package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NavEnhetId
import okhttp3.mockwebserver.MockResponse

class MockNorgHttpServer : MockHttpServer() {

	fun mockTilhorendeEnhet(geografiskTilknytning: String, tilhorendeEnhet: NavEnhetId, skjermet: Boolean? = false, gradering: Diskresjonskode? = null) {
		val response = MockResponse()
			.setBody(
				"""
					{
						"enhetNr": "$tilhorendeEnhet"
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/norg2/api/v1/enhet/navkontor/$geografiskTilknytning",
			matchMethod = "GET",
			response = response,
			matchQueryParam = if(skjermet != null && gradering != null) mapOf(
				"skjermet" to skjermet.toString(),
				"disk" to if (gradering == Diskresjonskode.STRENGT_FORTROLIG) "SPSF" else ""
			) else null
		)
	}

	fun mockIngenTilhorendeEnhet(geografiskTilknytning: String) {
		val response = MockResponse()
			.setResponseCode(404)

		handleRequest(
			matchPath = "/norg2/api/v1/enhet/navkontor/$geografiskTilknytning",
			matchMethod = "GET",
			response = response
		)
	}
}
