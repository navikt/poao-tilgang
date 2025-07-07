package no.nav.poao_tilgang.client_core

import no.nav.poao_tilgang.api.dto.request.ErSkjermetPersonBulkRequest
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.HentAdGrupperForBrukerRequest
import no.nav.poao_tilgang.api.dto.request.Request
import no.nav.poao_tilgang.api.dto.response.EvaluatePoliciesResponse
import no.nav.poao_tilgang.api.dto.response.HentAdGrupperForBrukerResponse
import no.nav.poao_tilgang.api.dto.response.TilgangsattributterResponse
import no.nav.poao_tilgang.client_core.api.ApiResult
import java.util.*

interface PoaoTilgangClient {

	/**
	 * Evaluer en policy med gitt input, sjekk {@link no.nav.poao_tilgang.client.PolicyInput.kt} for hvilke policies som er tilgjengelig
	 */
	fun evaluatePolicy(input: PolicyInput): ApiResult<Decision>

	/**
	 * Evaluer flere policies, sjekk {@link no.nav.poao_tilgang.client.PolicyInput.kt} for hvilke policies som er tilgjengelig
	 */
	fun evaluatePolicies(requests: List<PolicyRequest>): ApiResult<List<PolicyResult>>

	/**
	 * Henter alle Azure AD-grupper til en NAV Ansatt ved bruk av objekt IDen til den ansatte
	 */
	fun hentAdGrupper(navAnsattAzureId: UUID): ApiResult<List<AdGruppe>>

	/**
	 * Henter om en enkelt person er skjermet. Skjermet person var tidligere kjent som "egen ansatt"
	 */
	fun erSkjermetPerson(norskIdent: NorskIdent): ApiResult<Boolean>

	/**
	 * Henter om flere personer er skjermet. Skjermet person var tidligere kjent som "egen ansatt"
	 */
	fun erSkjermetPerson(norskeIdenter: List<NorskIdent>): ApiResult<Map<NorskIdent, Boolean>>

	/**
	 * Henter diskresjonskode, skjerming og enhet som tilgang sjekkes mot
	 *
	 * @param norskIdent Norsk identifikasjonsnummer for personen
	 * @return ApiResult som inneholder TilgangsattributterResponse
	 */
	fun hentTilgangsAttributter(norskIdent: NorskIdent): ApiResult<TilgangsattributterResponse>

	interface BodyParser {
		fun parsePolicyRequestsBody(body: String): ApiResult<EvaluatePoliciesResponse>
		fun parseHentTilgangsAttributterBody(body: String): ApiResult<TilgangsattributterResponse>
		fun parseErSkjermetPersonBody(body: String): ApiResult<Map<NorskIdent, Boolean>>
		fun parseHentAdGrupper(body: String): ApiResult<HentAdGrupperForBrukerResponse>
	}
	interface Serializer {
		fun <I> serializeEvaluatePolicies(body: EvaluatePoliciesRequest<I>): String
		fun serializeHentAdGrupper(body: HentAdGrupperForBrukerRequest): String
		fun serializeErSkjermet(body: ErSkjermetPersonBulkRequest): String
	}
}

fun serializerFrom(serialize: (body: Request) -> String): PoaoTilgangClient.Serializer = object: PoaoTilgangClient.Serializer {
	override fun <I> serializeEvaluatePolicies(body: EvaluatePoliciesRequest<I>): String = serialize(body)
	override fun serializeHentAdGrupper(body: HentAdGrupperForBrukerRequest): String = serialize(body)
	override fun serializeErSkjermet(body: ErSkjermetPersonBulkRequest): String = serialize(body)
}

typealias NorskIdent = String

data class PolicyRequest(
	val requestId: UUID, // Unique pr PolicyRequest, used to find the matching PolicyResult
	val policyInput: PolicyInput
)

data class PolicyResult(
	val requestId: UUID, // Set to the value of requestId from PolicyRequest
	val decision: Decision
)

data class AdGruppe(
	val id: UUID,
	val navn: String // Ex: 0000-ga-123
)
