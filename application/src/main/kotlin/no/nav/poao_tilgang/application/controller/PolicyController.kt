package no.nav.poao_tilgang.application.controller

import com.github.benmanes.caffeine.cache.Caffeine
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.cache.CaffeineStatsCounter
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.PolicyEvaluationRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_input.RequestPolicyInput
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.EvaluatePoliciesResponse
import no.nav.poao_tilgang.api.dto.response.PolicyEvaluationResultDto
import no.nav.poao_tilgang.api_core_mapper.ApiCoreMapper
import no.nav.poao_tilgang.application.domain.PolicyEvaluationRequest
import no.nav.poao_tilgang.application.domain.PolicyEvaluationResult
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.service.GjeldendeIdentService
import no.nav.poao_tilgang.application.service.PolicyService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.domain.PolicyInputWithNorskIdent
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration


@RestController
@RequestMapping("/api/v1/policy")
class PolicyController(
	private val authService: AuthService,
	private val policyService: PolicyService,
	private val gjeldendeIdentService: GjeldendeIdentService,
	private val apiCoreMapper: ApiCoreMapper,
	private val meterRegistry: MeterRegistry
) {
	// Caching Decisions for 10 seconds, since same Policies are usually evaluated a number of times when veilarbpersonflate loads a new user
	private val decisionCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofSeconds(10))
		.maximumSize(10_000)
		.recordStats {
			CaffeineStatsCounter(
				meterRegistry,
				"policyDecisions"
			)
		}
		.build<PolicyInput, Decision>()

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/evaluate")
	fun evaluatePolicies(@RequestBody evaluatePoliciesRequest: EvaluatePoliciesRequest): EvaluatePoliciesResponse {
		authService.verifyRequestIsMachineToMachine()

		val evaluations = evaluatePoliciesRequest.requests
			.map { evaluateRequest(it) }

		return EvaluatePoliciesResponse(evaluations)
	}


	private fun evaluateRequest(request: PolicyEvaluationRequestDto): PolicyEvaluationResultDto {
		val policyInput = apiCoreMapper
			.mapToPolicyInput(request.policyInput)
			.let {
				when (it) {
					is PolicyInputWithNorskIdent -> byttTilGjeldendeIdent(it)
					else -> it
				}
			}

		val cachedDecision = decisionCache.getIfPresent(policyInput)
		val result = if (cachedDecision == null) {
			val evaluation = policyService.evaluatePolicyRequest(PolicyEvaluationRequest(request.requestId, policyInput))
			decisionCache.put(policyInput, evaluation.decision)
			evaluation
		} else {
			PolicyEvaluationResult(request.requestId, cachedDecision)
		}

		return PolicyEvaluationResultDto(request.requestId, toDecisionDto(result.decision))
	}

	private fun toDecisionDto(decision: Decision): DecisionDto {
		return when (decision) {
			is Decision.Permit -> DecisionDto(
				type = DecisionType.PERMIT,
				message = null,
				reason = null
			)
			is Decision.Deny -> DecisionDto(
				type = DecisionType.DENY,
				message = decision.message,
				reason = decision.reason.name
			)
		}
	}

	private fun byttTilGjeldendeIdent(input: PolicyInputWithNorskIdent): PolicyInputWithNorskIdent {
		val norskIdent = this.gjeldendeIdentService(input.norskIdent)
		return input.withIdent(norskIdent)
	}

}

