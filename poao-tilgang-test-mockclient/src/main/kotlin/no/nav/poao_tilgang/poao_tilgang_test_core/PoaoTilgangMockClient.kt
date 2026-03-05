package no.nav.poao_tilgang.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.api.dto.response.TilgangsattributterResponse
import no.nav.poao_tilgang.client.api.ApiResult
import no.nav.poao_tilgang.client.api.ResponseDataApiException
import no.nav.poao_tilgang.poao_tilgang_test_core.NavContext
import no.nav.poao_tilgang.poao_tilgang_test_core.PolicyEvaluator
import no.nav.poao_tilgang.poao_tilgang_test_core.PolicyEvaluators
import java.util.*

internal object ClientObjectMapper {
	val objectMapper: ObjectMapper = ObjectMapper()
		.registerKotlinModule()
		.registerModule(JavaTimeModule())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
class PoaoTilgangMockClient(
	val navContext: NavContext = NavContext(),
	private val policyEvaluator: PolicyEvaluator = PolicyEvaluators.load(navContext)
): PoaoTilgangClient {

	override fun evaluatePolicy(input: PolicyInput): ApiResult<Decision> {
		val request = PolicyRequest(
			requestId = UUID.randomUUID(),
			policyInput = input
		)

		val evaluatePolicy = evaluatePolicy(request)
		return ApiResult.success(evaluatePolicy)
	}

	override fun evaluatePolicies(requests: List<PolicyRequest>): ApiResult<List<PolicyResult>> {
		val decisions = requests.map {
			PolicyResult(it.requestId, evaluatePolicy(it))
		}
		return ApiResult.success(decisions)
	}


	private fun evaluatePolicy(input: PolicyRequest): Decision {
		val requestDto = toRequestDto(input)
		val policyInput = ClientObjectMapper.objectMapper.valueToTree<JsonNode>(requestDto.policyInput)
		val decisionDto = policyEvaluator.evaluate(requestDto.policyId, policyInput)
		return decisionDto.toDecision()
	}

	override fun hentAdGrupper(navAnsattAzureId: UUID): ApiResult<List<AdGruppe>> {
		val adGrupper = navContext.navAnsatt.get(navAnsattAzureId)?.adGrupper ?: emptySet()

		val map = adGrupper.map { adGruppe ->
			AdGruppe(adGruppe.id, adGruppe.navn)
		}
		return ApiResult.success(map)
	}

	override fun erSkjermetPerson(norskIdent: NorskIdent): ApiResult<Boolean> {
		val eksternBruker = navContext.privatBrukere.get(norskIdent) ?: return ApiResult.failure<Boolean>(
			ResponseDataApiException("Brukern finnes ikke")
		)

		return ApiResult.success(eksternBruker.erSkjermet)
	}

	override fun erSkjermetPerson(norskeIdenter: List<NorskIdent>): ApiResult<Map<NorskIdent, Boolean>> {
		val toMap = navContext.erSkjermetPerson(norskeIdenter)
		return ApiResult.success(toMap)
	}

	override fun hentTilgangsAttributter(norskIdent: NorskIdent): ApiResult<TilgangsattributterResponse> {
		return ApiResult.success(TilgangsattributterResponse(
			diskresjonskode = Diskresjonskode.STRENGT_FORTROLIG_UTLAND,
			skjermet = navContext.erSkjermetPerson(listOf(norskIdent))[norskIdent]!!,
			kontor = "1234"
		))
	}
}

private fun DecisionDto.toDecision(): Decision {
	return when (type) {
		DecisionType.PERMIT -> Decision.Permit
		DecisionType.DENY -> {
			val message = message
			val reason = reason

			check(message != null) { "message cannot be null" }
			check(reason != null) { "reason cannot be null" }

			Decision.Deny(message, reason)
		}
	}
}
