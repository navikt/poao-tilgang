package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetMedSperrePolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.testAdGrupper
import no.nav.poao_tilgang.core.policy.test_utils.MockTimer
import no.nav.poao_tilgang.core.provider.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilNavEnhetMedSperrePolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val navEnhetTilgangProvider = mockk<NavEnhetTilgangProvider>()

	private val abacProvider = mockk<AbacProvider>()

	private lateinit var navEnhetMedSperrePolicy: NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl

	private lateinit var oppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicyImpl

	private val navAnsattAzureId = UUID.randomUUID()

	private val navIdent = "Z1234"

	private val navEnhetId = "1234"

	private val mockTimer = MockTimer()
	private val toggleProvider = mockk<ToggleProvider>()


	@BeforeEach
	internal fun setUp() {
		every { toggleProvider.brukAbacDecision() } returns false

		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns testAdGrupper

		every {
			adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		} returns navIdent

		oppfolgingPolicy = NavAnsattTilgangTilOppfolgingPolicyImpl(adGruppeProvider)
		navEnhetMedSperrePolicy = NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl(navEnhetTilgangProvider, adGruppeProvider, abacProvider, mockTimer, toggleProvider, oppfolgingPolicy)
	}

	@Test
	fun `skal returnere "permit" hvis NAV ansatt har rollen 0000-GA-aktivitesplan_kvp`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			testAdGrupper.aktivitetsplanKvp
		)

		val decision = navEnhetMedSperrePolicy.harTilgang(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "permit" hvis tilgang til enhet`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging)

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns listOf(
			NavEnhetTilgang(navEnhetId, "test", emptyList())
		)

		val decision = navEnhetMedSperrePolicy.harTilgang(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere "deny" hvis ikke tilgang til enhet`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(adGruppeProvider.hentTilgjengeligeAdGrupper().modiaOppfolging)

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns emptyList()

		val decision = navEnhetMedSperrePolicy.harTilgang(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Deny(
			"Har ikke tilgang til NAV enhet med sperre",
			DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
		)
	}

	@Test
	fun `skal returnere "deny" hvis ikke tilgang til modia oppfølging`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns emptyList()

		every {
			navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
		} returns listOf(
			NavEnhetTilgang(navEnhetId, "test", emptyList())
		)
		val decision = navEnhetMedSperrePolicy.harTilgang(NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(navAnsattAzureId, navEnhetId))

		decision shouldBe Decision.Deny(
			"NAV-ansatt mangler tilgang til AD-gruppen \"0000-GA-Modia-Oppfolging\"",
			DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

}
