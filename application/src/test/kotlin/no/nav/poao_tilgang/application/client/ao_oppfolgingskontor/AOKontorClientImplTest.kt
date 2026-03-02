package no.nav.poao_tilgang.application.client.ao_oppfolgingskontor

import io.kotest.matchers.shouldBe
import no.nav.common.types.identer.Fnr
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
		val client = AoKontorClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val personRequestJSON = JsonUtils.toJsonString(GraphqlRequest(Variables("987654")))

		mockServer.mockOppfolgingsenhet("1234", null)

		val oppfolgingsenhetId = client.hentBrukerOppfolgingsenhetId(Fnr.of("987654"))

		oppfolgingsenhetId shouldBe "1234"

		val request = mockServer.latestRequest()

		request.path shouldBe "/graphql"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"
		request.body.readUtf8() shouldBe personRequestJSON
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
