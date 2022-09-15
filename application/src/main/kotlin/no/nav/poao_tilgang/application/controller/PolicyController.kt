package no.nav.poao_tilgang.application.controller

import com.fasterxml.jackson.databind.JsonNode
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.PolicyEvaluationRequestDto
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.EvaluatePoliciesResponse
import no.nav.poao_tilgang.api.dto.response.PolicyEvaluationResultDto
import no.nav.poao_tilgang.application.domain.PolicyEvaluationRequest
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.service.PolicyService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonNode
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.policy.*
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/policy")
class PolicyController(
	private val authService: AuthService,
	private val policyService: PolicyService,
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping("/evaluate")
	fun evaluatePolicies(@RequestBody evaluatePoliciesRequest: EvaluatePoliciesRequest): EvaluatePoliciesResponse {
		authService.verifyRequestIsMachineToMachine()

		val evaluations = evaluatePoliciesRequest.requests
			.map { evaluateRequest(it) }

		return EvaluatePoliciesResponse(evaluations)
	}

	private fun evaluateRequest(request: PolicyEvaluationRequestDto): PolicyEvaluationResultDto {
		val policyInput = mapToPolicyInput(request.policyName, request.policyInput)

		val result = policyService.evaluatePolicyRequest(
			PolicyEvaluationRequest(request.requestId, policyInput)
		)

		return PolicyEvaluationResultDto(result.requestId, toDecisionDto(result.decision))
	}

	private fun mapToPolicyInput(policyName: String, policyInput: JsonNode): PolicyInput {
		return when (policyName) {
			EksternBrukerPolicy.name -> fromJsonNode<EksternBrukerPolicy.Input>(policyInput)
			FortroligBrukerPolicy.name -> fromJsonNode<FortroligBrukerPolicy.Input>(policyInput)
			ModiaPolicy.name -> fromJsonNode<ModiaPolicy.Input>(policyInput)
			SkjermetPersonPolicy.name -> fromJsonNode<SkjermetPersonPolicy.Input>(policyInput)
			StrengtFortroligBrukerPolicy.name -> fromJsonNode<StrengtFortroligBrukerPolicy.Input>(policyInput)
			else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ukjent policy $policyName")
		}
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

}

