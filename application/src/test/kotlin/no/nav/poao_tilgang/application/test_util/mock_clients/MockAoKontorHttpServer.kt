package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NavEnhetId
import okhttp3.mockwebserver.MockResponse

class MockAoKontorHttpServer : MockHttpServer() {

	fun mockOppfolgingsenhet(mockOppfolgingsenhet: NavEnhetId, mockGtEnhet: NavEnhetId?) {
		val response = MockResponse()
			.setBody(
				"""
					{
						"data": {
							"kontorTilhorigheter": {
								"arbeidsoppfolging": { "kontorId": "$mockOppfolgingsenhet" },
								"geografiskTilknytning": { "kontorId": "$mockGtEnhet" }
							}
						}
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/graphql",
			matchMethod = "POST",
			response = response
		)
	}

	fun mockIngenOppfolgingsenhet(fnr: Fnr) {
		val response = MockResponse()
			.setBody("""
				{
					"data": {
						"kontorTilhorigheter": {
							"arbeidsoppfolging": null,
							"geografiskTilknytning": null
						}
					}
				}
			""".trimIndent())

		handleRequest(
			matchPath = "/graphql",
			matchMethod = "POST",
			matchBodyContains = fnr.get(),
			response = response
		)
	}

}
