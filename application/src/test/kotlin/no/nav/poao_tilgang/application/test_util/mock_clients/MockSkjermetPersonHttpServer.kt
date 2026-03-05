package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.skjermet_person.SkjermetPersonClientImpl.ErSkjermet
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.mockwebserver.MockResponse

class MockSkjermetPersonHttpServer : MockHttpServer() {

	val skjermedePersoner: MutableMap<NorskIdent, Boolean> = mutableMapOf()
	fun mockErSkjermet(skjerming: Map<NorskIdent, Boolean>) {
		if (skjerming.keys.any { skjermedePersoner.keys.contains(it) } ) {
			throw IllegalStateException("Kan ikke mocke ident ${skjerming.keys} skjerming for samme person flere ganger, det ødelegger for paralellitet i testene")
		}
		skjermedePersoner.putAll(skjerming)

		handleRequest(
			matchPath = "/skjermetBulk",
			matchMethod = "POST",
			responseCreator = { _, body ->
				val personer = fromJsonString<ErSkjermet.Request>(body.value!!).personidenter
					.map { ident -> ident to skjermedePersoner[ident] }
					.toMap()
				MockResponse()
					.setBody(toJsonString(personer))
			},
		)
	}

}
