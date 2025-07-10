package no.nav.poao_tilgang.client_core

import no.nav.poao_tilgang.api.dto.request.ErSkjermetPersonBulkRequest
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.HentAdGrupperForBrukerRequest
import no.nav.poao_tilgang.api.dto.request.policy_input.RequestPolicyInput
import no.nav.poao_tilgang.api.dto.response.PolicyEvaluationResultDto
import no.nav.poao_tilgang.api.dto.response.TilgangsattributterResponse
import no.nav.poao_tilgang.client_core.api.ApiResult
import no.nav.poao_tilgang.client_core.api.ApiResult.Companion.failure
import no.nav.poao_tilgang.client_core.api.ApiResult.Companion.success
import no.nav.poao_tilgang.client_core.api.ResponseDataApiException
import java.util.UUID

typealias HttpFetch = (fullUrl: String, method: String, body: String?) -> ApiResult<String>

class PoaoTilgangHttpClient(
	private val baseUrl: String,
	private val httpFetch: HttpFetch,
	private val bodyParser: PoaoTilgangClient.BodyParser,
	private val serializer: PoaoTilgangClient.Serializer,
): PoaoTilgangClient {
	private fun joinUrlParts(vararg parts: String): String {
		return parts.joinToString("/") { it.trim('/') }
	}

	private inline fun <reified D> catchError(parseBody: () -> ApiResult<D>): ApiResult<D> {
		return try {
			parseBody()
		} catch (e: Throwable) {
			failure(ResponseDataApiException(e.message ?: "Unknown error"))
		}
	}

	override fun evaluatePolicy(input: PolicyInput): ApiResult<Decision> {
		return evaluatePolicies(listOf(
				PolicyRequest(
					requestId = UUID.randomUUID(),
					policyInput = input
				)
			)
		)
			.flatMap { data ->
				data.firstOrNull()?.decision?.let { success(it) }
					?: failure(ResponseDataApiException("Mangler result for policy evaluation request"))
			}
	}

	override fun evaluatePolicies(requests: List<PolicyRequest>): ApiResult<List<PolicyResult>> {
		return sendPolicyRequests(requests)
			.map { data ->
				data.map { PolicyResult(it.requestId, it.decision.toDecision()) }
			}
	}

	override fun hentAdGrupper(navAnsattAzureId: UUID): ApiResult<List<AdGruppe>> {
		return serializer.serializeHentAdGrupper(HentAdGrupperForBrukerRequest(navAnsattAzureId))
			.let { jsonPayload -> httpFetch(
				joinUrlParts(baseUrl,"/api/v1/ad-gruppe"),
				"POST",
				jsonPayload)
			}
			.flatMap { catchError { bodyParser.parseHentAdGrupper(it) } }
			.map { adGrupper ->
				adGrupper.map { adGruppe -> AdGruppe(adGruppe.id, adGruppe.name) }
			}
	}

	override fun erSkjermetPerson(norskIdent: NorskIdent): ApiResult<Boolean> {
		return erSkjermetPerson(listOf(norskIdent))
			.flatMap { data ->
				data[norskIdent]?.let { success(it) }
					?: failure(ResponseDataApiException("Mangler data om skjermet person"))
			}
	}

	override fun erSkjermetPerson(norskeIdenter: List<NorskIdent>): ApiResult<Map<NorskIdent, Boolean>> {
		val requestJson = serializer.serializeErSkjermet(ErSkjermetPersonBulkRequest(norskeIdenter))

		return httpFetch(
			joinUrlParts(baseUrl, "/api/v1/skjermet-person"),
			"POST",
			requestJson
		).flatMap { catchError { bodyParser.parseErSkjermetPersonBody(it) } }
	}

	override fun hentTilgangsAttributter(norskIdent: NorskIdent): ApiResult<TilgangsattributterResponse> {
		return httpFetch(
			joinUrlParts(baseUrl, "/api/v1/tilgangsattributter"),
			"POST",
			norskIdent
		).flatMap { catchError { bodyParser.parseHentTilgangsAttributterBody(it) } }
	}

	private fun sendPolicyRequests(requests: List<PolicyRequest>): ApiResult<List<PolicyEvaluationResultDto>> {
		return requests.map { it.toRequestDto() }
			.let { serializer.serializeEvaluatePolicies(EvaluatePoliciesRequest(it)) }
			.let { jsonPayload -> httpFetch(
				joinUrlParts(baseUrl, "/api/v1/policy/evaluate"),
				"POST",
				jsonPayload)
			}
			.map { catchError { bodyParser.parsePolicyRequestsBody(it) } }
			.flatMap { it -> it.map { it.results } }
	}
}

