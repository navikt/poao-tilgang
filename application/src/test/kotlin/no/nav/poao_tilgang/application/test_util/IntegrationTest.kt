package no.nav.poao_tilgang.application.test_util

import no.nav.common.featuretoggle.UnleashClient
import no.nav.poao_tilgang.application.Application
import no.nav.poao_tilgang.application.client.axsys.EnhetTilgang
import no.nav.poao_tilgang.application.config.MyApplicationRunner
import no.nav.poao_tilgang.application.test_util.mock_clients.*
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.NavEnhetId
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
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.*

@ExtendWith(SpringExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [Application::class])
@ActiveProfiles("test")
open class IntegrationTest {
	@MockBean
	lateinit var myApplicationRunner: MyApplicationRunner

	@MockBean
	lateinit var mockUnleashHttpServer: UnleashClient

	@LocalServerPort
	private var port: Int = 0

	private val client = OkHttpClient.Builder()
		.readTimeout(Duration.ofMinutes(15)) // For setting debug breakpoints without the connection being killed
		.build()

	companion object {
		lateinit var mockOAuthServer : MockOAuthServer
		lateinit var mockMicrosoftGraphHttpServer : MockMicrosoftGraphHttpServer
		lateinit var mockSkjermetPersonHttpServer : MockSkjermetPersonHttpServer
		lateinit var mockAxsysHttpServer : MockAxsysHttpServer
		lateinit var mockAbacHttpServer : MockAbacHttpServer
		lateinit var mockVeilarbarenaHttpServer : MockVeilarbarenaHttpServer
		//lateinit var mockPdlHttpServer : MockPdlHttpServer
		lateinit var mockPdlPipHttpServer : MockPdlPipHttpServer
		lateinit var mockNorgHttpServer : MockNorgHttpServer
		lateinit var mockMachineToMachineHttpServer : MockMachineToMachineHttpServer

		@BeforeAll
		@JvmStatic
		fun setupMockServers() {
			setupClients()
			setupAdGrupperIder()

			mockOAuthServer.start()
			System.setProperty("AZURE_APP_WELL_KNOWN_URL", mockOAuthServer.getDiscoveryUrl())
			System.setProperty("AZURE_APP_CLIENT_ID", "test")


			mockMachineToMachineHttpServer.start()
			System.setProperty("AZURE_APP_JWK", MockMachineToMachineHttpServer.jwk)
			System.setProperty(
				"AZURE_OPENID_CONFIG_TOKEN_ENDPOINT",
				mockMachineToMachineHttpServer.serverUrl() + MockMachineToMachineHttpServer.tokenPath
			)
		}


		private fun setupClients() {
			mockOAuthServer = MockOAuthServer()
			mockMicrosoftGraphHttpServer = MockMicrosoftGraphHttpServer()
			mockSkjermetPersonHttpServer = MockSkjermetPersonHttpServer()
			mockAxsysHttpServer = MockAxsysHttpServer()
			mockAbacHttpServer = MockAbacHttpServer()
			mockVeilarbarenaHttpServer = MockVeilarbarenaHttpServer()
			mockPdlPipHttpServer = MockPdlPipHttpServer()
			mockNorgHttpServer = MockNorgHttpServer()
			mockMachineToMachineHttpServer = MockMachineToMachineHttpServer()

			mockSkjermetPersonHttpServer.start()
			System.setProperty("SKJERMET_PERSON_URL", mockSkjermetPersonHttpServer.serverUrl())
			System.setProperty("SKJERMET_PERSON_SCOPE", "api://test.nom.skjermede-personer-pip/.default")

			mockMicrosoftGraphHttpServer.start()
			System.setProperty("MICROSOFT_GRAPH_URL", mockMicrosoftGraphHttpServer.serverUrl())
			System.setProperty("MICROSOFT_GRAPH_SCOPE", "https://graph.microsoft.com/.default")


			mockAxsysHttpServer.start()
			System.setProperty("AXSYS_URL", mockAxsysHttpServer.serverUrl())
			System.setProperty("AXSYS_SCOPE", "api://test.org.axsys/.default")


			mockAbacHttpServer.start()
			System.setProperty("ABAC_URL", mockAbacHttpServer.serverUrl())
			System.setProperty("ABAC_SCOPE", "api://test.pto.abac-veilarb-proxy/.default")

			mockVeilarbarenaHttpServer.start()
			System.setProperty("VEILARBARENA_URL", mockVeilarbarenaHttpServer.serverUrl())
			System.setProperty("VEILARBARENA_SCOPE", "api://test.pto.veilarbarena/.default")

			mockPdlPipHttpServer.start()
			System.setProperty("PDLPIP_URL", mockPdlPipHttpServer.serverUrl())
			System.setProperty("PDLPIP_SCOPE", "api://test.pdl.pdl-pip-api/.default")

			mockNorgHttpServer.start()
			System.setProperty("NORG_URL", mockNorgHttpServer.serverUrl())

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
		fun close(): Unit {
			mockMicrosoftGraphHttpServer.close()
			mockSkjermetPersonHttpServer.close()
			mockAxsysHttpServer.close()
			mockAbacHttpServer.close()
			mockVeilarbarenaHttpServer.close()
			mockPdlPipHttpServer.close()
			mockNorgHttpServer.close()
		}
	}

	@AfterEach
	fun reset() {
		mockMicrosoftGraphHttpServer.reset()
		mockSkjermetPersonHttpServer.reset()
		mockAxsysHttpServer.reset()
		mockAbacHttpServer.reset()
		mockVeilarbarenaHttpServer.reset()
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

	fun mockPersonData(norskIdent: NorskIdent, brukersEnhet: NavEnhetId, kommuneNr: String, erSkjermet: Boolean = false) {
		mockPdlPipHttpServer.mockBrukerInfo(
			norskIdent = norskIdent,
			gtKommune = kommuneNr
		)

		mockSkjermetPersonHttpServer.mockErSkjermet(
			mapOf(
				norskIdent to false
			)
		)
		mockNorgHttpServer.mockTilhorendeEnhet(kommuneNr, brukersEnhet)
		mockVeilarbarenaHttpServer.mockOppfolgingsenhet(brukersEnhet)
	}

	fun mockRolleTilganger(navIdent: String, navAnsattId: UUID, adGrupper: List<AdGruppe>) {
		mockMicrosoftGraphHttpServer.mockHentAzureIdMedNavIdentResponse(navIdent, navAnsattId)

		mockMicrosoftGraphHttpServer.mockHentNavIdentMedAzureIdResponse(navAnsattId, navIdent)

		mockMicrosoftGraphHttpServer.mockHentAdGrupperForNavAnsatt(navAnsattId, adGrupper.map { it.id })

		mockMicrosoftGraphHttpServer.mockHentAdGrupperResponse(adGrupper)
	}

	fun mockEnhetsTilganger(navIdent: String, enhetsTilganger: List<EnhetTilgang>) {
		mockAxsysHttpServer.mockHentTilgangerResponse(navIdent, enhetsTilganger)
	}


}
