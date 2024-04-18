package no.nav.poao_tilgang.application.client.pdl_pip

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PdlPipClientImplTest {

	companion object {
		private val mockServer = MockHttpServer()

		@BeforeAll
		@JvmStatic
		fun start() {
			mockServer.start()
		}
	}

	@AfterEach
	fun reset() {
		mockServer.reset()
	}

	private val client = PdlPipClientImpl(mockServer.serverUrl(), { "TOKEN" })

	@Test
	fun `hentBrukerInfo - gyldig respons - skal lage riktig request og parse response`() {
		val brukerIdent = "43534534"

		mockServer.handleRequest(
			matchPath = "/api/v1/person",
			matchMethod = "GET",
			matchHeaders = mapOf("ident" to brukerIdent),
			response = MockResponse()
				.setBody("""
				{
				  "person": {
					"adressebeskyttelse": [
					  {
						"gradering": "FORTROLIG"
					  }
					],
					"foedsel": [
					  {
						"foedselsdato": "1911-01-01"
					  }
					],
					"doedsfall": [
					  {
						"doedsdato": "1977-07-07"
					  }
					],
					"familierelasjoner": [
					  {
						"relatertPersonsIdent": "11223344550"
					  }
					]
				  },
				  "identer": {
					"identer": [
					  {
						"ident": "1234567890123",
						"historisk": false,
						"gruppe": "AKTORID"
					  },
					  {
						"ident": "9876543210987",
						"historisk": true,
						"gruppe": "AKTORID"
					  },
					  {
						"ident": "12345678901",
						"historisk": false,
						"gruppe": "AKTORID"
					  },
					  {
						"ident": "98765432109",
						"historisk": true,
						"gruppe": "AKTORID"
					  }
					]
				  },
				  "geografiskTilknytning": {
					"gtType": "KOMMUNE",
					"gtKommune": "0570",
					"gtBydel": null				  }
				}
				""".trimIndent())
		)

		val brukerInfo = client.hentBrukerInfo(brukerIdent)

		brukerInfo?.person?.adressebeskyttelse?.firstOrNull()?.gradering shouldBe Gradering.FORTROLIG
		brukerInfo?.geografiskTilknytning shouldBe GeografiskTilknytning(
			gtType = GeografiskTilknytningType.KOMMUNE,
			gtKommune = "0570",
			gtBydel = null
		)

		val request = mockServer.latestRequest()

		request.path shouldBe "/api/v1/person"
		request.method shouldBe "GET"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"
	}

	@Test
	fun `hentBrukerInfo - tom adressebeskyttelse - skal lage riktig request og parse response`() {
		val brukerIdent = "43534534"

		mockServer.handleRequest(
			response = MockResponse()
				.setBody("""
				{
				  "person": {
					"adressebeskyttelse": null,
					"foedsel": [
					  {
						"foedselsdato": "1911-01-01"
					  }
					],
					"doedsfall": [
					  {
						"doedsdato": "1977-07-07"
					  }
					],
					"familierelasjoner": [
					  {
						"relatertPersonsIdent": "11223344550"
					  }
					]
				  },
				  "identer": {
					"identer": [
					  {
						"ident": "1234567890123",
						"historisk": false,
						"gruppe": "AKTORID"
					  },
					  {
						"ident": "9876543210987",
						"historisk": true,
						"gruppe": "AKTORID"
					  },
					  {
						"ident": "12345678901",
						"historisk": false,
						"gruppe": "AKTORID"
					  },
					  {
						"ident": "98765432109",
						"historisk": true,
						"gruppe": "AKTORID"
					  }
					]
				  },
				  "geografiskTilknytning": {
					"gtType": "KOMMUNE",
					"gtKommune": "0570",
					"gtBydel": null				  }
				}
				""".trimIndent())
		)

		val brukerInfo = client.hentBrukerInfo(brukerIdent)

		brukerInfo?.person?.adressebeskyttelse?.firstOrNull() shouldBe null
		brukerInfo?.geografiskTilknytning shouldBe GeografiskTilknytning(
			gtType = GeografiskTilknytningType.KOMMUNE,
			gtKommune = "0570",
			gtBydel = null
		)

		val request = mockServer.latestRequest()

		request.path shouldBe "/api/v1/person"
		request.method shouldBe "GET"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"
	}
}
