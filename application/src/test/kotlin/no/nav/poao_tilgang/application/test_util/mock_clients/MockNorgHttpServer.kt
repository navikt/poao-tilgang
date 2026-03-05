package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NavEnhetId
import okhttp3.mockwebserver.MockResponse

class MockNorgHttpServer : MockHttpServer() {

	val gtTilEnhetMock: MutableMap<Triple<String, Boolean?, Diskresjonskode?>, NavEnhetId> = mutableMapOf()
	fun mockTilhorendeEnhet(geografiskTilknytning: String, tilhorendeEnhet: NavEnhetId, skjermet: Boolean? = null, gradering: Diskresjonskode? = null) {
		val mockArgs = Triple(geografiskTilknytning, skjermet, gradering)
		if (gtTilEnhetMock.containsKey(mockArgs)) throw RuntimeException("Samme gt ble mocked flere ganger gt:${geografiskTilknytning} skjermet:${skjermet} gradering:${gradering}, det ødelegger for paralellitet i testene")

		gtTilEnhetMock[mockArgs] = tilhorendeEnhet

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
			matchQueryParam = mutableMapOf<String, String>().apply {
				skjermet?.let { put("skjermet", it.toString()) }
				gradering?.let {
					if (it == Diskresjonskode.STRENGT_FORTROLIG || it == Diskresjonskode.STRENGT_FORTROLIG_UTLAND) {
						put("disk", "SPSF")
					}
				}
			}.takeIf { it.isNotEmpty() }
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
