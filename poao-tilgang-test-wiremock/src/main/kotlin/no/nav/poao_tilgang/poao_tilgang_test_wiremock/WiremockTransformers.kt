package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import no.nav.poao_tilgang.api.dto.request.ErSkjermetPersonBulkRequest
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.api.dto.request.HentAdGrupperForBrukerRequest
import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilModiaPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.response.AdGruppeDto
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.EvaluatePoliciesResponse
import no.nav.poao_tilgang.api.dto.response.HentAdGrupperForBrukerResponse
import no.nav.poao_tilgang.api.dto.response.PolicyEvaluationResultDto
import no.nav.poao_tilgang.api.dto.response.TilgangResponse
import no.nav.poao_tilgang.poao_tilgang_test_core.NavContext
import no.nav.poao_tilgang.poao_tilgang_test_core.NorskIdent
import no.nav.poao_tilgang.poao_tilgang_test_core.PolicyEvaluator
import no.nav.poao_tilgang.poao_tilgang_test_core.PolicyEvaluators
import kotlin.reflect.KFunction1


class WiremockTransformers(
	val navContext: NavContext = NavContext(),
	private val policyEvaluator: PolicyEvaluator = PolicyEvaluators.load(navContext),
	baspath : String
) {
	val skjermetPerson = Response("skjermetPerson", "$baspath/api/v1/skjermet-person", ::kjermetPerson, ErSkjermetPersonBulkRequest::class.java)
	val adgroupController = Response("adgroupController", "$baspath/api/v1/ad-gruppe", ::getAdGrupper, HentAdGrupperForBrukerRequest::class.java)
	val tilgangsKontroller = Response("tilgangsKontroller", "$baspath/api/v1/tilgang/modia", ::harTilgang, HarTilgangTilModiaRequest::class.java)
	val polecyController = PolicyController(policyEvaluator, baspath)

	val listOfExtension = arrayOf(skjermetPerson, adgroupController, polecyController, tilgangsKontroller)

	private fun kjermetPerson(model: ErSkjermetPersonBulkRequest): Map<NorskIdent, Boolean> {
		return navContext.erSkjermetPerson(model.norskeIdenter)
	}
	private fun harTilgang(harTilgangTilModiaRequest: HarTilgangTilModiaRequest): TilgangResponse {
		val navIdent = harTilgangTilModiaRequest.navIdent
		val navAnsatt = navContext.navAnsatt.get(navIdent)
			?: return TilgangResponse(DecisionDto(DecisionType.DENY, "ikke satt i mock", "Ikke ansatt"))

		val policyInput = NavAnsattTilgangTilModiaPolicyInputV1Dto(navAnsatt.azureObjectId)
		val inputJson = ClientObjectMapper.objectMapper.valueToTree<JsonNode>(policyInput)
		val decisionDto = policyEvaluator.evaluate(PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_V1, inputJson)
		return TilgangResponse(decisionDto)

	}
	private fun getAdGrupper(model: HentAdGrupperForBrukerRequest): HentAdGrupperForBrukerResponse? {
		return navContext
			.navAnsatt.get(model.navAnsattAzureId)
			?.adGrupper
			?.map {
				AdGruppeDto(
					it.id,
					it.navn
				)
			}
	}
}



class PolicyController(private val policyEvaluator: PolicyEvaluator, baspath: String) : ResponseDefinitionTransformer() {
	val path = "$baspath/api/v1/policy/evaluate"
	override fun getName(): String {
		return "policyController"
	}

	override fun transform(
		request: Request,
		responseDefinition: ResponseDefinition,
		files: FileSource?,
		parameters: Parameters?
	): ResponseDefinition {
		val bodyAsString = request.bodyAsString
		val readValue = ClientObjectMapper.objectMapper.readValue(
			bodyAsString,
			object : TypeReference<EvaluatePoliciesRequest<JsonNode>>() {})

		val response = response(readValue)


		return ResponseDefinitionBuilder()
			.withHeader("Content-Type", "application/json")
			.withStatus(200)
			.withBody(ClientObjectMapper.objectMapper.writeValueAsString(response))
			.build()

	}

	private fun response(requestDto: EvaluatePoliciesRequest<JsonNode>): EvaluatePoliciesResponse {
		val results = requestDto.requests.map {
			val decision = policyEvaluator.evaluate(it.policyId, it.policyInput)
			PolicyEvaluationResultDto(it.requestId, decision)
		}
		return EvaluatePoliciesResponse(results)
	}

	override fun applyGlobally(): Boolean {
		return false
	}
}

class Response<T>(
	private val name: String,
	val path: String,
	val responsFunc: KFunction1<T, Any?>,
	val requestBody: Class<T>
): ResponseDefinitionTransformer()   {
	override fun getName(): String {
		return name
	}

	override fun transform(
		request: Request,
		responseDefinition: ResponseDefinition,
		files: FileSource?,
		parameters: Parameters?
	): ResponseDefinition {

		val bodyAsString = request.bodyAsString
		val requestDto = ClientObjectMapper.objectMapper.readValue(bodyAsString, requestBody)
		val response = responsFunc(requestDto)

		return ResponseDefinitionBuilder()
			.withHeader("Content-Type", "application/json")
			.withStatus(200)
			.withBody(ClientObjectMapper.objectMapper.writeValueAsString(response))
			.build()

	}

	override fun applyGlobally(): Boolean {
		return false
	}

}

internal object ClientObjectMapper {
	val objectMapper: ObjectMapper = ObjectMapper()
		.registerKotlinModule()
		.registerModule(JavaTimeModule())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
