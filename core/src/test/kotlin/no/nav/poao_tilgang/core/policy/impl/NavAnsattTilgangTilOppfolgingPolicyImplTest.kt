package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilOppfolgingPolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.randomGruppe
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.testAdGrupper
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilOppfolgingPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private lateinit var policy: NavAnsattTilgangTilOppfolgingPolicy

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns TestAdGrupper.testAdGrupper

		policy = NavAnsattTilgangTilOppfolgingPolicyImpl(adGruppeProvider)
	}


	@Test
	fun `should return "permit" if access to 0000-GA-Modia-Oppfolging`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			testAdGrupper.modiaOppfolging,
			randomGruppe
		)

		val decision = policy.evaluate(NavAnsattTilgangTilOppfolgingPolicy.Input(navIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if not access to 0000-GA-Modia-Oppfolging`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			randomGruppe
		)

		val decision = policy.evaluate(NavAnsattTilgangTilOppfolgingPolicy.Input(navIdent))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV ansatt mangler tilgang til AD gruppen \"0000-GA-Modia-Oppfolging\""
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}

}

