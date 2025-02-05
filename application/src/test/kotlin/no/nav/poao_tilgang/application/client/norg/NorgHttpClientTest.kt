package no.nav.poao_tilgang.application.client.norg

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.application.test_util.mock_clients.MockNorgHttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class NorgHttpClientTest {

	companion object {
		private val mockServer = MockNorgHttpServer()

		@BeforeAll
		@JvmStatic
		fun start() {
			mockServer.start()
		}
	}

	private val navVikaFossen = "2103"

	@AfterEach
	fun reset() {
		mockServer.reset()
	}

	@Test
	fun `hentTilhorendeEnhet skal lage riktig request og parse response`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "4321")

		val tilhorendeEnhet = client.hentTilhorendeEnhet("12345")

		tilhorendeEnhet shouldBe "4321"

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/12345"
		request.method shouldBe "GET"
	}
	@Test
	fun `hentTilhorendeEnhet skal gi spesialenhet hvis personen har streng fortrolig adresse`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "4321")
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "4331", skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = navVikaFossen, gradering = Diskresjonskode.STRENGT_FORTROLIG_UTLAND)

		val tilhorendeEnhet = client.hentTilhorendeEnhet("12345", diskresjonskode = no.nav.poao_tilgang.core.domain.Diskresjonskode.STRENGT_FORTROLIG_UTLAND)

		tilhorendeEnhet shouldBe "1024"

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/12345"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet skal gi spesialenhet hvis personen er egen ansatt`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "4321")
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "5321", skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "1024", gradering = Diskresjonskode.UGRADERT)

		val tilhorendeEnhet = client.hentTilhorendeEnhet("12345", skjermet = true)

		tilhorendeEnhet shouldBe "5321"

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/12345"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet skal prioritere adressebeskyttelse hvis personen både har strengt fortrolig adresse og er egen ansatt`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "4321")
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "5321", skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = navVikaFossen, gradering = Diskresjonskode.STRENGT_FORTROLIG)

		val tilhorendeEnhet = client.hentTilhorendeEnhet("12345", diskresjonskode = no.nav.poao_tilgang.core.domain.Diskresjonskode.STRENGT_FORTROLIG_UTLAND)

		tilhorendeEnhet shouldBe navVikaFossen

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/12345"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet skal gi egen-ansatt-enhet hvis personen er egen ansatt`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "4321")
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "5321", skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "1024", gradering = Diskresjonskode.UGRADERT)

		val tilhorendeEnhet = client.hentTilhorendeEnhet("12345", skjermet = true)

		tilhorendeEnhet shouldBe "5321"

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/12345"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet skal gi vikafossen hvis personen har diskresjonskode, selv om GT er null`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "4321")
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "5321", skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = "12345", tilhorendeEnhet = "1024", gradering = Diskresjonskode.UGRADERT)

		val tilhorendeEnhet = client.hentTilhorendeEnhet("12345", skjermet = true)

		tilhorendeEnhet shouldBe "5321"

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/12345"
		request.method shouldBe "GET"
	}


	@Test
	fun `hentTilhorendeEnhet skal returnere null hvis Norg returnerer 404`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		mockServer.mockIngenTilhorendeEnhet("23456")

		val geografiskTilknyttetEnhet = client.hentTilhorendeEnhet("23456")

		geografiskTilknyttetEnhet shouldBe null
	}
}
