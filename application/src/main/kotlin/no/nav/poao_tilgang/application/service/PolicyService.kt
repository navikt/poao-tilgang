package no.nav.poao_tilgang.application.service

import no.nav.poao_tilgang.application.domain.PolicyEvaluationRequest
import no.nav.poao_tilgang.application.domain.PolicyEvaluationResult
import no.nav.poao_tilgang.application.exception.InvalidPolicyRequestException
import no.nav.poao_tilgang.application.utils.SecureLog
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.policy.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class PolicyService(
	private val eksternBrukerPolicy: EksternBrukerPolicy,
	private val fortroligBrukerPolicy: FortroligBrukerPolicy,
	private val modiaPolicy: ModiaPolicy,
	private val skjermetPersonPolicy: SkjermetPersonPolicy,
	private val strengtFortroligBrukerPolicy: StrengtFortroligBrukerPolicy
) {

	fun evaluatePolicyRequest(request: PolicyEvaluationRequest): PolicyEvaluationResult {
		val policyResult = evaluate(request.input)

		secureLogPolicyResult(
			requestId = request.requestId,
			policyName = policyResult.policyName,
			policyInput = request.input,
			decision = policyResult.decision
		)

		return PolicyEvaluationResult(request.requestId, policyResult.decision)
	}

	private fun secureLogPolicyResult(
		requestId: UUID,
		policyName: String,
		policyInput: PolicyInput,
		decision: Decision
	) {
		val logLine = listOfNotNull(
			logValueWithDescription("policy", policyName),
			logValueWithDescription("input", policyInput),
			logValueWithDescription("decision", decision.type),
			logValueWithDescription("requestId", requestId),
			logValueWithDescription("denyMessage", if (decision is Decision.Deny) decision.message else null),
			logValueWithDescription("denyReason", if (decision is Decision.Deny) decision.reason else null),
		).joinToString(" ")

		SecureLog.secureLog.info(logLine)
	}

	private fun logValueWithDescription(label: String, value: Any?): String? {
		return value?.let { "$label=$it" }
	}

	private fun evaluate(input: PolicyInput): PolicyResult {
		return when(input) {
			is EksternBrukerPolicy.Input -> PolicyResult(
				EksternBrukerPolicy.name,
				eksternBrukerPolicy.evaluate(input)
			)
			is FortroligBrukerPolicy.Input -> PolicyResult(
				FortroligBrukerPolicy.name,
				fortroligBrukerPolicy.evaluate(input)
			)
			is ModiaPolicy.Input -> PolicyResult(
				ModiaPolicy.name,
				modiaPolicy.evaluate(input)
			)
			is SkjermetPersonPolicy.Input -> PolicyResult(
				SkjermetPersonPolicy.name,
				skjermetPersonPolicy.evaluate(input)
			)
			is StrengtFortroligBrukerPolicy.Input -> PolicyResult(
				StrengtFortroligBrukerPolicy.name,
				strengtFortroligBrukerPolicy.evaluate(input)
			)
			else -> throw InvalidPolicyRequestException("Ukjent policy for ${input.javaClass.canonicalName}")
		}
	}

	private data class PolicyResult(
		val policyName: String,
		val decision: Decision
	)

}

