package no.nav.poao_tilgang.application.client.norg

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.application.test_util.TestDataGenerator
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

	@AfterEach
	fun reset() {
		mockServer.reset()
	}

	@Test
	fun `hentTilhorendeEnhet skal lage riktig request og parse response`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		val gt = TestDataGenerator.geografiskTilknytning()
		val vanligEnhet = TestDataGenerator.navEnhetId()

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = vanligEnhet)

		val tilhorendeEnhet = client.hentTilhorendeEnhet(gt)

		tilhorendeEnhet shouldBe vanligEnhet

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/$gt"
		request.method shouldBe "GET"
	}
	@Test
	fun `hentTilhorendeEnhet skal gi spesialenhet hvis personen har streng fortrolig adresse`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		val gt = TestDataGenerator.geografiskTilknytning()
		val skjermetEnhet = TestDataGenerator.navEnhetId()
		val gradertEnhet = TestDataGenerator.navEnhetId()
		val vanligEnhet = TestDataGenerator.navEnhetId()

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = skjermetEnhet, skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = gradertEnhet, gradering = Diskresjonskode.STRENGT_FORTROLIG_UTLAND)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = vanligEnhet)

		val tilhorendeEnhet = client.hentTilhorendeEnhet(gt, diskresjonskode = no.nav.poao_tilgang.core.domain.Diskresjonskode.STRENGT_FORTROLIG_UTLAND)

		tilhorendeEnhet shouldBe gradertEnhet

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/$gt"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet skal gi spesialenhet hvis personen er egen ansatt`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)
		val gt = TestDataGenerator.geografiskTilknytning()
		val skjermetEnhet = TestDataGenerator.navEnhetId()
		val gradertEnhet = TestDataGenerator.navEnhetId()
		val vanligEnhet = TestDataGenerator.navEnhetId()

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = skjermetEnhet, skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = gradertEnhet, gradering = Diskresjonskode.UGRADERT)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = vanligEnhet)

		val tilhorendeEnhet = client.hentTilhorendeEnhet(gt, skjermet = true)

		tilhorendeEnhet shouldBe skjermetEnhet

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/$gt"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet skal prioritere adressebeskyttelse hvis personen både har strengt fortrolig adresse og er egen ansatt`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		val gt = TestDataGenerator.geografiskTilknytning()
		val skjermetEnhet = TestDataGenerator.navEnhetId()
		val gradertEnhet = TestDataGenerator.navEnhetId()
		val vanligEnhet = TestDataGenerator.navEnhetId()

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = skjermetEnhet, skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = gradertEnhet, gradering = Diskresjonskode.STRENGT_FORTROLIG)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = vanligEnhet)

		val tilhorendeEnhet = client.hentTilhorendeEnhet(gt, diskresjonskode = no.nav.poao_tilgang.core.domain.Diskresjonskode.STRENGT_FORTROLIG_UTLAND)

		tilhorendeEnhet shouldBe gradertEnhet

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/$gt"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet skal gi egen-ansatt-enhet hvis personen er egen ansatt`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		val gt = TestDataGenerator.geografiskTilknytning()
		val skjermetEnhet = TestDataGenerator.navEnhetId()
		val gradertEnhet = TestDataGenerator.navEnhetId()
		val vanligEnhet = TestDataGenerator.navEnhetId()

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = skjermetEnhet, skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = gradertEnhet, gradering = Diskresjonskode.UGRADERT)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = vanligEnhet)

		val tilhorendeEnhet = client.hentTilhorendeEnhet(gt, skjermet = true)

		tilhorendeEnhet shouldBe skjermetEnhet

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/$gt"
		request.method shouldBe "GET"
	}

	@Test
	fun `hentTilhorendeEnhet skal gi vikafossen hvis personen har diskresjonskode, selv om GT er null`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		val gt = TestDataGenerator.geografiskTilknytning()
		val skjermetEnhet = TestDataGenerator.navEnhetId()
		val gradertEnhet = TestDataGenerator.navEnhetId()
		val vanligEnhet = TestDataGenerator.navEnhetId()

		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = skjermetEnhet, skjermet = true)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = gradertEnhet, gradering = Diskresjonskode.UGRADERT)
		mockServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = vanligEnhet)

		val tilhorendeEnhet = client.hentTilhorendeEnhet(gt, skjermet = true)

		tilhorendeEnhet shouldBe skjermetEnhet

		val request = mockServer.latestRequest()

		request.path shouldStartWith  "/norg2/api/v1/enhet/navkontor/$gt"
		request.method shouldBe "GET"
	}


	@Test
	fun `hentTilhorendeEnhet skal returnere null hvis Norg returnerer 404`() {
		val client = NorgHttpClient(
			baseUrl = mockServer.serverUrl()
		)

		val gt = TestDataGenerator.geografiskTilknytning()

		mockServer.mockIngenTilhorendeEnhet(gt)

		val geografiskTilknyttetEnhet = client.hentTilhorendeEnhet(gt)

		geografiskTilknyttetEnhet shouldBe null
	}
}
