package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.tilgangsmaskin.Avvisningskode
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse

class MockTilgangsmaskinHttpServer : MockHttpServer() {

	fun mockGodkjent(navIdent: String) {
		val response = MockResponse().setResponseCode(204)

		handleRequest(
			matchPath = "/api/v1/ccf/komplett/$navIdent",
			matchMethod = "POST",
			response = response
		)
	}

	fun mockAvvist(
		navIdent: String,
		brukerIdent: String,
		avvisningskode: Avvisningskode,
		begrunnelse: String = "Ikke tilgang",
		kanOverstyres: Boolean = false
	) {
		val response = MockResponse()
			.setResponseCode(403)
			.setBody(
				"""
					{
						"type": "about:blank",
						"title": "$avvisningskode",
						"status": 403,
						"instance": "/api/v1/ccf/komplett/$navIdent",
						"brukerIdent": "$brukerIdent",
						"navIdent": "$navIdent",
						"begrunnelse": "$begrunnelse",
						"traceId": "test-trace-id",
						"kanOverstyres": $kanOverstyres
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/api/v1/ccf/komplett/$navIdent",
			matchMethod = "POST",
			response = response
		)
	}

	fun mockFeil(navIdent: String, statusCode: Int = 500) {
		val response = MockResponse().setResponseCode(statusCode)

		handleRequest(
			matchPath = "/api/v1/ccf/komplett/$navIdent",
			matchMethod = "POST",
			response = response
		)
	}
}

