package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import no.nav.poao_tilgang.api.dto.request.ErSkjermetPersonBulkRequest
import no.nav.poao_tilgang.api.dto.request.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.api.dto.request.HentAdGrupperForBrukerRequest
import no.nav.poao_tilgang.api.dto.response.AdGruppeDto
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.HentAdGrupperForBrukerResponse
import no.nav.poao_tilgang.api.dto.response.TilgangResponse
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaPolicy
import no.nav.poao_tilgang.poao_tilgang_test_core.Policies
import kotlin.reflect.KFunction1


class WiremockTransformers(val policies: Policies = Policies(), baspath : String) {
	val navContext = policies.navContext

	val skjermetPerson = ResponseTransformer("skjermetPerson", "$baspath/api/v1/skjermet-person", ::kjermetPerson, ErSkjermetPersonBulkRequest::class.java)
	val adgroupController = ResponseTransformer("adgroupController", "$baspath/api/v1/ad-gruppe", ::getAdGrupper, HentAdGrupperForBrukerRequest::class.java)
	val tilgangsKontroller = ResponseTransformer("tilgangsKontroller", "$baspath/api/v1/tilgang/modia", ::harTilgang, HarTilgangTilModiaRequest::class.java)
	val polecyController = PolicyController(policies, baspath)

	val listOfExtension = arrayOf(skjermetPerson, adgroupController, polecyController, tilgangsKontroller)

	private fun kjermetPerson(model: ErSkjermetPersonBulkRequest): Map<NorskIdent, Boolean> {
		return navContext.erSkjermetPerson(model.norskeIdenter)
	}
	private fun harTilgang(harTilgangTilModiaRequest: HarTilgangTilModiaRequest): TilgangResponse {
		val navIdent = harTilgangTilModiaRequest.navIdent
		val navAnsatt = navContext.navAnsatt.get(navIdent)
			?: return TilgangResponse(DecisionDto(DecisionType.DENY, "ikke satt i mock", "Ikke ansatt"))


		val evaluate =  policies.navAnsattTilgangTilModiaPolicy.evaluate(NavAnsattTilgangTilModiaPolicy.Input(navAnsatt.azureObjectId))
		val decisionDto = evaluate.toDecisionDto()
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

class ResponseTransformer<T>(
	private val name: String,
	val path: String,
	val responsFunc: KFunction1<T, Any?>,
	val requestBody: Class<T>
): ResponseDefinitionTransformerV2   {
	override fun applyGlobally() = false
	override fun getName() = name
	override fun transform(serverEvent: ServeEvent?): ResponseDefinition? {
		val bodyAsString = serverEvent?.request?.bodyAsString ?: return null
		val requestDto = WiremockClientObjectMapper.objectMapper.readValue(bodyAsString, requestBody)
		val response = responsFunc(requestDto)

		return ResponseDefinitionBuilder()
			.withHeader("Content-Type", "application/json")
			.withStatus(200)
			.withBody(WiremockClientObjectMapper.objectMapper.writeValueAsString(response))
			.build()
	}
}
