package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleFortroligBrukerePolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattBehandleFortroligBrukerePolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val policy = NavAnsattBehandleFortroligBrukerePolicyImpl(adGruppeProvider)

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns TestAdGrupper.grupper
	}

	@Test
	fun `should return "permit" if access to 0000-GA-Fortrolig_Adresse`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			TestAdGrupper.grupper.fortroligAdresse,
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		val decision = policy.evaluate(NavAnsattBehandleFortroligBrukerePolicy.Input(navIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `should return "deny" if not access to 0000-GA-Fortrolig_Adresse`() {
		val navIdent = "Z1234"

		every {
			adGruppeProvider.hentAdGrupper(navIdent)
		} returns listOf(
			AdGruppe(UUID.randomUUID(), "some-other-group"),
		)

		val decision = policy.evaluate(NavAnsattBehandleFortroligBrukerePolicy.Input(navIdent))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "NAV ansatt mangler tilgang til AD gruppen \"0000-GA-Fortrolig_Adresse\""
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}

}

