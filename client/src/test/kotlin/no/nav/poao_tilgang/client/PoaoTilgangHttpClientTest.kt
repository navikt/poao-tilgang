package no.nav.poao_tilgang.client

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import no.nav.common.rest.client.RestClient
import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.api.dto.response.TilgangsattributterResponse
import no.nav.poao_tilgang.application.client.pdl_pip.Gradering
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.client.api.BadHttpStatusApiException
import no.nav.poao_tilgang.client.api.NetworkApiException
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.ENHET_PREFIKS
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import java.net.UnknownHostException
import java.time.Duration
import java.util.*

class PoaoTilgangHttpClientTest : IntegrationTest() {

	private val navIdent = "Z1235"
	private val norskIdent = "6456532"
	private val brukersEnhet = "0123"
	private val navAnsattId = UUID.randomUUID()

	private val fnr1 = "124253321"
	private val fnr2 = "654756834"


	lateinit var client: PoaoTilgangHttpClient

	@Autowired
	private lateinit var adGruppeProvider: AdGruppeProvider

	@BeforeEach
	fun setup() {
		client = PoaoTilgangHttpClient(
			serverUrl(),
			{ mockOAuthServer.issueAzureAdM2MToken() },
			RestClient.baseClientBuilder().readTimeout(Duration.ofMinutes(15)).build()
		)
	}

	@ParameterizedTest
	@EnumSource(TilgangType::class)
	fun `evaluatePolicy - should evaluate NavAnsattTilgangTilEksternBrukerPolicy V2`(tilgangType: TilgangType) {
		setupMocks(
			adGrupper = listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging,
				adGruppeProvider.hentTilgjengeligeAdGrupper().gosysNasjonal
			)
		)

		val decision =
			client.evaluatePolicy(NavAnsattTilgangTilEksternBrukerPolicyInput(navAnsattId, tilgangType, norskIdent))
				.getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy`() {
		setupMocks(adGrupper = listOf(AdGruppe(UUID.randomUUID(), "${ENHET_PREFIKS}0123")))
		val decision =
			client.evaluatePolicy(NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInput(navAnsattId, norskIdent))
				.getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate NavAnsattTilgangTilModiaPolicy`() {
		mockRolleTilganger(
			navIdent,
			navAnsattId,
			listOf(adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell)
		)

		val decision = client.evaluatePolicy(NavAnsattTilgangTilModiaPolicyInput(navAnsattId)).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate EksternBrukerTilgangTilEksternBrukerPolicy`() {
		val decision = client.evaluatePolicy(
			EksternBrukerTilgangTilEksternBrukerPolicyInput(
				rekvirentNorskIdent = "234",
				ressursNorskIdent = "234"
			)
		).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate NavAnsattHarTilgangTilNavEnhetPolicy`() {
		mockRolleTilganger(
			navIdent,
			navAnsattId,
			listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging,
				AdGruppe(UUID.randomUUID(), "${ENHET_PREFIKS}0123")
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattTilgangTilNavEnhetPolicyInput(
				navAnsattAzureId = navAnsattId,
				navEnhetId = "0123"
			)
		).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should evaluate NavAnsattHarTilgangTilNavEnhetMedSperrePolicy`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(adGruppeProvider.hentTilgjengeligeAdGrupper().aktivitetsplanKvp)
		)

		val decision = client.evaluatePolicy(
			NavAnsattTilgangTilNavEnhetMedSperrePolicyInput(
				navAnsattAzureId = navAnsattId,
				navEnhetId = "0123"
			)
		).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `hentAdGrupper - skal hente AD-grupper`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				AdGruppe(UUID.randomUUID(), "0000-ga-123"),
				AdGruppe(UUID.randomUUID(), "0000-ga-456")
			)
		)

		val adGrupper = client.hentAdGrupper(navAnsattId).getOrThrow()

		adGrupper shouldHaveSize 2
		adGrupper.any { it.navn == "0000-ga-123" } shouldBe true
		adGrupper.any { it.navn == "0000-ga-456" } shouldBe true
	}

	@Test
	fun `erSkjermetPerson - skal hente enkelt skjermet person`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				AdGruppe(UUID.randomUUID(), "0000-ga-123"),
				AdGruppe(UUID.randomUUID(), "0000-ga-456")
			)
		)

		mockSkjermetPersonHttpServer.mockErSkjermet(
			mapOf(
				fnr1 to true,
				fnr2 to false
			)
		)

		client.erSkjermetPerson(fnr1).getOrThrow() shouldBe true
		client.erSkjermetPerson(fnr2).getOrThrow() shouldBe false
	}

	@Test
	fun `erSkjermetPerson - skal hente bulk skjermet person`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				AdGruppe(UUID.randomUUID(), "0000-ga-123"),
				AdGruppe(UUID.randomUUID(), "0000-ga-456")
			)
		)

		mockSkjermetPersonHttpServer.mockErSkjermet(
			mapOf(
				fnr1 to true,
				fnr2 to false
			)
		)

		val erSkjermet = client.erSkjermetPerson(listOf(fnr1, fnr2)).getOrThrow()

		erSkjermet[fnr1] shouldBe true
		erSkjermet[fnr2] shouldBe false
	}

	@Test
	fun `skal returnere BadHttpStatusApiException for feilende status`() {
		val badClient = PoaoTilgangHttpClient(serverUrl(), { "" })

		val exception = badClient.erSkjermetPerson("34242").exception
		exception should beInstanceOf<BadHttpStatusApiException>()
		(exception as BadHttpStatusApiException).httpStatus shouldBe 401
		exception.responseBody shouldNotBe null
	}

	@Test
	fun `skal returnere NetworkApiException for netverk feil`() {
		val badClient = PoaoTilgangHttpClient("http://not-a-real-host", { "" })

		val exception = badClient.erSkjermetPerson("34242").exception

		exception should beInstanceOf<NetworkApiException>()
		exception?.cause should beInstanceOf<UnknownHostException>()
	}

	@Test
	fun `evaluatePolicy - should permit NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell,
				adGruppeProvider.hentTilgjengeligeAdGrupper().strengtFortroligAdresse
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattBehandleStrengtFortroligBrukerePolicyInput(
				navAnsattAzureId = navAnsattId
			)
		).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should deny NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattBehandleStrengtFortroligBrukerePolicyInput(
				navAnsattAzureId = navAnsattId
			)
		).getOrThrow()

		decision shouldBe Decision.Deny(
			"NAV-ansatt mangler tilgang til AD-gruppen \"0000-GA-Strengt_Fortrolig_Adresse\"",
			"MANGLER_TILGANG_TIL_AD_GRUPPE"
		)
	}

	@Test
	fun `evaluatePolicy - should permit NAV_ANSATT_TILGANG_TIL_MODIA_ADMIN_V1`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaAdmin,
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattTilgangTilModiaAdminPolicyInput(
				navAnsattAzureId = navAnsattId
			)
		).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should deny NAV_ANSATT_TILGANG_TIL_MODIA_ADMIN_V1`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattTilgangTilModiaAdminPolicyInput(
				navAnsattAzureId = navAnsattId
			)
		).getOrThrow()

		decision shouldBe Decision.Deny(
			"Har ikke tilgang til rollen 0000-GA-Modia_Admin",
			"MANGLER_TILGANG_TIL_AD_GRUPPE"
		)
	}

	@Test
	fun `evaluatePolicy - should permit NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell,
				adGruppeProvider.hentTilgjengeligeAdGrupper().fortroligAdresse
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattBehandleFortroligBrukerePolicyInput(
				navAnsattAzureId = navAnsattId
			)
		).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should deny NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattBehandleFortroligBrukerePolicyInput(
				navAnsattAzureId = navAnsattId
			)
		).getOrThrow()

		decision shouldBe Decision.Deny(
			"NAV-ansatt mangler tilgang til AD-gruppen \"0000-GA-Fortrolig_Adresse\"",
			"MANGLER_TILGANG_TIL_AD_GRUPPE"
		)
	}

	@Test
	fun `evaluatePolicy - should permit NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().egneAnsatte,
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattBehandleSkjermedePersonerPolicyInput(
				navAnsattAzureId = navAnsattId
			)
		).getOrThrow()

		decision shouldBe Decision.Permit
	}

	@Test
	fun `evaluatePolicy - should deny NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER`() {
		mockRolleTilganger(
			navIdent, navAnsattId, listOf(
				adGruppeProvider.hentTilgjengeligeAdGrupper().modiaGenerell
			)
		)

		val decision = client.evaluatePolicy(
			NavAnsattBehandleSkjermedePersonerPolicyInput(
				navAnsattAzureId = navAnsattId
			)
		).getOrThrow()

		decision shouldBe Decision.Deny(
			"NAV-ansatt mangler tilgang til en av AD-gruppene [0000-GA-Egne_ansatte]",
			reason = "MANGLER_TILGANG_TIL_AD_GRUPPE"
		)
	}

	@Test
	fun `tilgangsAttributter - skal hente tilgangsattributter`() {
		val norskIdent = "12345678910"
		val geografiskTilknytning = "434576"
		val kontorEnhet = "9999"
		val gradering = Gradering.STRENGT_FORTROLIG_UTLAND
		val erSkjermetPerson = false

		mockPdlPipHttpServer.mockBrukerInfo(norskIdent, gradering, gtKommune = geografiskTilknytning)
		mockVeilarbarenaHttpServer.mockOppfolgingsenhet(kontorEnhet)
		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(norskIdent to erSkjermetPerson))

		val tilgansAttributter = client.hentTilgangsAttributter(norskIdent).getOrThrow()

		tilgansAttributter shouldBe TilgangsattributterResponse(
			diskresjonskode = Diskresjonskode.STRENGT_FORTROLIG_UTLAND,
			skjermet = erSkjermetPerson,
			kontor = kontorEnhet
		)
	}

	private fun setupMocks(adGrupper: List<AdGruppe> = listOf(AdGruppe(UUID.randomUUID(), "0000-some-group"))) {
		mockPersonData(norskIdent, brukersEnhet)
		mockRolleTilganger(
			navIdent, navAnsattId, adGrupper
		)
	}
}
