package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaPolicy
import no.nav.poao_tilgang.core.policy.test_utils.MockTimer
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper.testAdGrupper
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider
import org.junit.jupiter.api.Test

class NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicyImplTest {

	private val navAnsattAzureId = UUID.randomUUID()
	private val navIdent = "G441122"
	private val norskIdent = "63546454"

	private val tilgangsMaskinProvider = mockk<TilgangmaskinProvider>()
	private val adGruppeProvider = mockk<AdGruppeProvider>()
	private val navAnsattTilgangTilModiaPolicy = mockk<NavAnsattTilgangTilModiaPolicy>()

	private val mockTimer = MockTimer()

	private val policy = NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicyImpl(
		tilgangsMaskinProvider,
		adGruppeProvider,
		navAnsattTilgangTilModiaPolicy,
		mockTimer
	)

	@Test
	internal fun `harTilgang svarer DENY hvis tilgangsMaskinDecision er DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(tilgangsMaskinDecision = deny(message))

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang svarer DENY hvis tilgangTilModia er DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			modiaPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang svarer DENY hvis mangler rollen for enkelttilgang`() {
		val message = "Veileder har ikke tilgang til enkelttilgang"

		mockDecision(
			harEnkelttilgang = false
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang svarer PERMIT hvis har rollen for enkelttilgang og har kjernetilgang til bruker`() {
		mockDecision()
		val decision =
			policy.harTilgang(NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Permit
	}

	private fun deny(
		message: String = "TEST",
		reason: DecisionDenyReason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
	): Decision.Deny {
		return Decision.Deny(
			message = message,
			reason = reason
		)
	}

	private fun mockDecision(
		tilgangsMaskinDecision: Decision = Decision.Permit,
		modiaPolicyDecision: Decision = Decision.Permit,
		harEnkelttilgang: Boolean = true,
	) {
		every { adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId) } returns navIdent
		every { adGruppeProvider.hentTilgjengeligeAdGrupper() } returns testAdGrupper
		if (harEnkelttilgang) {
			every { adGruppeProvider.hentAdGrupper(navAnsattAzureId) } returns listOf(
				testAdGrupper.enkelttilgang,
				testAdGrupper.modiaGenerell,
			)
		} else {
			every { adGruppeProvider.hentAdGrupper(navAnsattAzureId) } returns listOf(
				testAdGrupper.modiaGenerell,
			)
		}

		every {
			tilgangsMaskinProvider.evaluerKjerneregler(norskIdent, navIdent)
		} returns tilgangsMaskinDecision

		every {
			navAnsattTilgangTilModiaPolicy.evaluate(
				NavAnsattTilgangTilModiaPolicy.Input(navAnsattAzureId)
			)
		} returns modiaPolicyDecision
	}
}
