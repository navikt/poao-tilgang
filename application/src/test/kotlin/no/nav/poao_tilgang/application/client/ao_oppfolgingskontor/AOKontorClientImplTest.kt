package no.nav.poao_tilgang.application.client.ao_oppfolgingskontor

import io.kotest.matchers.shouldBe
import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.test_util.TestDataGenerator
import no.nav.poao_tilgang.application.test_util.mock_clients.MockAoKontorHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class AOKontorClientImplTest {

	companion object {
		private val mockServer = MockAoKontorHttpServer()

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
	fun `hentBrukerOppfolgingsenhetId skal lage riktig request og parse respons`() {
		val norskIdent = TestDataGenerator.norskIdent()
		val enhetId = TestDataGenerator.navEnhetId()
		val client = AoKontorClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val personRequestJSON = JsonUtils.toJsonString(GraphqlRequest(Variables(norskIdent)))

		mockServer.mockOppfolgingsenhet(norskIdent, enhetId, null)

		val oppfolgingsenhetId = client.hentBrukerOppfolgingsenhetId(Fnr.of(norskIdent))

		oppfolgingsenhetId shouldBe enhetId

		val captured = mockServer.takeRequest(norskIdent)

		captured.request.path shouldBe "/graphql"
		captured.request.method shouldBe "POST"
		captured.request.getHeader("Authorization") shouldBe "Bearer TOKEN"
		captured.body shouldBe personRequestJSON
	}

	@Test
	fun `hentBrukerOppfolgingsenhetId skal returnere null hvis ao-kontor returnerer kontor`() {
		val client = AoKontorClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val personRequest = Fnr.of("987654")
		mockServer.mockIngenOppfolgingsenhet(personRequest)

		val oppfolgingsenhetId = client.hentBrukerOppfolgingsenhetId(personRequest)

		oppfolgingsenhetId shouldBe null
	}

}
