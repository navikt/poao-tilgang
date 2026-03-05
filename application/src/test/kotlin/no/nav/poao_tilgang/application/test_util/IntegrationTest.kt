package no.nav.poao_tilgang.application.test_util

import no.nav.poao_tilgang.application.Application
import no.nav.poao_tilgang.application.config.MyApplicationRunner
import no.nav.poao_tilgang.application.test_util.mock_clients.*
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application::class])
@ActiveProfiles("test")
open class IntegrationTest {
	@MockitoBean
	lateinit var myApplicationRunner: MyApplicationRunner

	@LocalServerPort
	private var port: Int = 0

	private val client = OkHttpClient.Builder()
		.readTimeout(Duration.ofMinutes(15)) // For setting debug breakpoints without the connection being killed
		.build()

	companion object {
		// Servers are initialized exactly once for the entire test suite lifetime via the companion
		// object initializer (equivalent to Java static initializer). Re-creating them in @BeforeAll
		// would start them on new ports for every test class while the shared Spring context still
		// holds HTTP clients wired to the original ports, causing connection failures.
		val mockOAuthServer: MockOAuthServer
		val mockMicrosoftGraphHttpServer: MockMicrosoftGraphHttpServer
		val mockSkjermetPersonHttpServer: MockSkjermetPersonHttpServer
		val mockAoKontorHttpServer: MockAoKontorHttpServer
		val mockPdlPipHttpServer: MockPdlPipHttpServer
		val mockNorgHttpServer: MockNorgHttpServer
		val mockTilgangsmaskinHttpServer: MockTilgangsmaskinHttpServer
		val mockMachineToMachineHttpServer: MockMachineToMachineHttpServer

		init {
			mockOAuthServer = MockOAuthServer()
			mockMicrosoftGraphHttpServer = MockMicrosoftGraphHttpServer()
			mockSkjermetPersonHttpServer = MockSkjermetPersonHttpServer()
			mockAoKontorHttpServer = MockAoKontorHttpServer()
			mockPdlPipHttpServer = MockPdlPipHttpServer()
			mockNorgHttpServer = MockNorgHttpServer()
			mockTilgangsmaskinHttpServer = MockTilgangsmaskinHttpServer()
			mockMachineToMachineHttpServer = MockMachineToMachineHttpServer()

			mockSkjermetPersonHttpServer.start()
			System.setProperty("SKJERMET_PERSON_URL", mockSkjermetPersonHttpServer.serverUrl())
			System.setProperty("SKJERMET_PERSON_SCOPE", "api://test.nom.skjermede-personer-pip/.default")
			mockMicrosoftGraphHttpServer.start()
			System.setProperty("MICROSOFT_GRAPH_URL", mockMicrosoftGraphHttpServer.serverUrl())
			System.setProperty("MICROSOFT_GRAPH_SCOPE", "https://graph.microsoft.com/.default")
			mockAoKontorHttpServer.start()
			System.setProperty("AO_KONTOR_URL", mockAoKontorHttpServer.serverUrl())
			System.setProperty("AO_KONTOR_SCOPE", "api://test.dab.ao-kontor/.default")
			mockTilgangsmaskinHttpServer.start()
			System.setProperty("TILGANGSMASKIN_URL", mockTilgangsmaskinHttpServer.serverUrl())
			System.setProperty("TILGANGSMASKIN_SCOPE", "api://test.tilgangsmaskin.populasjonstilgangskontroll/.default")
			mockPdlPipHttpServer.start()
			System.setProperty("PDLPIP_URL", mockPdlPipHttpServer.serverUrl())
			System.setProperty("PDLPIP_SCOPE", "api://test.pdl.pdl-pip-api/.default")
			mockNorgHttpServer.start()
			System.setProperty("NORG_URL", mockNorgHttpServer.serverUrl())

			mockOAuthServer.start()
			System.setProperty("AZURE_APP_WELL_KNOWN_URL", mockOAuthServer.getDiscoveryUrl())
			System.setProperty("AZURE_APP_CLIENT_ID", "test")
			mockMachineToMachineHttpServer.start()
			System.setProperty("AZURE_APP_JWK", MockMachineToMachineHttpServer.jwk)
			System.setProperty(
				"AZURE_OPENID_CONFIG_TOKEN_ENDPOINT",
				mockMachineToMachineHttpServer.serverUrl() + MockMachineToMachineHttpServer.tokenPath
			)

			setupAdGrupperIder()
		}

		@BeforeAll
		@JvmStatic
		fun setupMockServers() {
			// Servers and system properties are already configured in the companion object initializer.
			// This hook is intentionally left empty; it exists so Spring can finalize context setup
			// after system properties have been set.
		}

		private fun setupAdGrupperIder() {
			System.setProperty("AD_GRUPPE_ID_FORTROLIG_ADRESSE", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_STRENGT_FORTROLIG_ADRESSE", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_MODIA_ADMIN", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_MODIA_OPPFOLGING", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_MODIA_GENERELL", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_GOSYS_NASJONAL", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_GOSYS_UTVIDBAR_TIL_NASJONAL", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_SYFO_SENSITIV", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_EGNE_ANSATTE", UUID.randomUUID().toString())
			System.setProperty("AD_GRUPPE_ID_AKTIVITETSPLAN_KVP", UUID.randomUUID().toString())
			System.setProperty("NAIS_APP_NAME", "poao-tilgang")
		}

		@JvmStatic
		@AfterAll
		fun close() {
			// Intentionally empty: servers are kept alive for the entire test suite so that the
			// shared Spring context can keep using the same URLs across all test classes.
			// They will be cleaned up automatically when the JVM exits.
		}
	}

	@AfterEach
	fun reset() {
		mockMicrosoftGraphHttpServer.reset()
		mockSkjermetPersonHttpServer.reset()
//		mockAoKontorHttpServer.reset()
		mockTilgangsmaskinHttpServer.reset()
		mockPdlPipHttpServer.reset()
		mockNorgHttpServer.reset()
	}

	fun serverUrl() = "http://localhost:$port"

	fun sendRequest(
		method: String,
		path: String,
		body: RequestBody? = null,
		headers: Map<String, String> = emptyMap()
	): Response {
		val reqBuilder = Request.Builder()
			.url("${serverUrl()}$path")
			.method(method, body)

		headers.forEach {
			reqBuilder.addHeader(it.key, it.value)
		}

		return client.newCall(reqBuilder.build()).execute()
	}

	fun mockTilgangsMaskinPermit(navIdent: NavIdent) {
		mockTilgangsmaskinHttpServer.mockGodkjent(navIdent)
	}

	fun mockPersonData(
		norskIdent: NorskIdent,
		brukersEnhet: NavEnhetId,
		kommuneNr: String,
		erSkjermet: Boolean = false,
		gammelIdent: NorskIdent? = null
	) {
		mockPdlPipHttpServer.mockBrukerInfo(
			norskIdent = norskIdent,
			gtKommune = kommuneNr,
			gammelIdent = gammelIdent
		)

		mockSkjermetPersonHttpServer.mockErSkjermet(
			mapOf(
				norskIdent to erSkjermet
			)
		)
		mockNorgHttpServer.mockTilhorendeEnhet(kommuneNr, brukersEnhet)
		mockAoKontorHttpServer.mockOppfolgingsenhet(norskIdent,brukersEnhet, brukersEnhet)
	}

	fun mockRolleTilganger(navIdent: String, navAnsattId: UUID, adGrupper: List<AdGruppe>) {
		mockMicrosoftGraphHttpServer.mockHentAzureIdMedNavIdentResponse(navIdent, navAnsattId)
		mockMicrosoftGraphHttpServer.mockHentNavIdentMedAzureIdResponse(navAnsattId, navIdent)
		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(navAnsattId, adGrupper.map { it.id })
		mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(adGrupper)
	}
}
