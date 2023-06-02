package no.nav.poao_tilgang.application.test_util.mock_clients


import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse
class MockUnleashHttpServer: MockHttpServer() {
	fun mockTilhorendeEnhet() {
		val response = MockResponse()
			.setBody(
				"""
					{
						"enhetNr": "1"
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/norg2/api/v1/enhet/navkontor/2",
			matchMethod = "GET",
			response = response
		)
	}

	fun mockIngenTilhorendeEnhet(geografiskTilknytning: String) {
		val response = MockResponse()
			.setResponseCode(404)

		handleRequest(
			matchPath = "/norg2/api/v1/enhet/navkontor/1",
			matchMethod = "GET",
			response = response
		)
	}
}
