package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.test_util.CapturedRequest
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.mockwebserver.MockResponse
import java.util.concurrent.ConcurrentHashMap

class MockAoKontorHttpServer : MockHttpServer() {
	override val mockServerName: String
		get() = "Ao-kontor"

	val mockedUsers: MutableSet<NorskIdent> = mutableSetOf()
	private val capturedRequests = ConcurrentHashMap<NorskIdent, CapturedRequest>()

	fun takeRequest(norskIdent: NorskIdent, timeoutMs: Long = 2000): CapturedRequest {
		val deadline = System.currentTimeMillis() + timeoutMs
		while (System.currentTimeMillis() < deadline) {
			capturedRequests.remove(norskIdent)?.let { return it }
			Thread.sleep(50)
		}
		throw IllegalStateException("No captured AO-kontor request for norskIdent '$norskIdent' within ${timeoutMs}ms. Available: ${capturedRequests.keys}")
	}

	fun mockOppfolgingsenhet(norskIdent: NorskIdent, mockOppfolgingsenhet: NavEnhetId, mockGtEnhet: NavEnhetId?) {
		if (mockedUsers.contains(norskIdent)) throw IllegalStateException("Kan ikke mocke samme bruker flere ganger, det ødelegger paralellitet")
		mockedUsers.add(norskIdent)

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
			response = response,
			matchBodyContains = "\"fnr\":\"${norskIdent}\"",
			onRequestCaptured = { capturedRequests[norskIdent] = it }
		)
	}

	fun mockIngenOppfolgingsenhet(fnr: Fnr) {
		val ident = fnr.get()
		if (mockedUsers.contains(ident)) throw IllegalStateException("Kan ikke mocke samme bruker flere ganger, det ødelegger paralellitet")
		mockedUsers.add(ident)

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
			matchBodyContains = "\"fnr\":\"${ident}\"",
			onRequestCaptured = { capturedRequests[ident] = it },
			response = response
		)
	}

}
