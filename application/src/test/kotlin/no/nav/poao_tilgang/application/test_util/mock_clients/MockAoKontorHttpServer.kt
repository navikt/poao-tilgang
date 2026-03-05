package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.application.test_util.TestDataGenerator.norskIdent
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.mockwebserver.MockResponse

class MockAoKontorHttpServer : MockHttpServer() {
	override val mockServerName: String
		get() = "Ao-kontor"

	val mockedUsers: MutableSet<NorskIdent> = mutableSetOf()

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
			matchBodyContains = "\"fnr\":\"${norskIdent}\""
		)
	}

	fun mockIngenOppfolgingsenhet(fnr: Fnr) {
		if (mockedUsers.contains(fnr.get())) throw IllegalStateException("Kan ikke mocke samme bruker flere ganger, det ødelegger paralellitet")
		mockedUsers.add(fnr.get())

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
			matchBodyContains = "\"fnr\":\"${fnr.get()}\"" ,
			response = response
		)
	}

}
