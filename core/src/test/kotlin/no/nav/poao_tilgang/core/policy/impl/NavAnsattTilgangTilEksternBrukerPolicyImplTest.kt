package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.TilgangType.LESE
import no.nav.poao_tilgang.core.domain.TilgangType.SKRIVE
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaGenerellPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilOppfolgingPolicy
import no.nav.poao_tilgang.core.policy.test_utils.MockTimer
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilEksternBrukerPolicyImplTest {

	private val navAnsattAzureId = UUID.randomUUID()
	private val navIdent = "G441122"
	private val norskIdent = "63546454"

	private val tilgangsMaskinProvider = mockk<TilgangmaskinProvider>()
	private val adGruppeProvider = mockk<AdGruppeProvider>()
	private val navAnsattTilgangTilOppfolgingPolicy = mockk<NavAnsattTilgangTilOppfolgingPolicy>()
	private val navAnsattTilgangTilModiaGenerellPolicy = mockk<NavAnsattTilgangTilModiaGenerellPolicy>()

	private val mockTimer = MockTimer()

	private val policy = NavAnsattTilgangTilEksternBrukerPolicyImpl(
		tilgangsMaskinProvider,
		adGruppeProvider,
		navAnsattTilgangTilOppfolgingPolicy,
		navAnsattTilgangTilModiaGenerellPolicy,
		mockTimer
	)

	@Test
	internal fun `harTilgang should return PERMIT for LESE`() {
		mockDecision()

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, LESE, norskIdent))

		verifyAll {
			tilgangsMaskinProvider.evaluerKompletteRegler(norskIdent, navIdent)
			navAnsattTilgangTilModiaGenerellPolicy.evaluate(any())
		}

		decision shouldBe Decision.Permit
	}

	@Test
	internal fun `harTilgang should return PERMIT for SKRIVE`() {
		mockDecision()

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		verifyAll {
			tilgangsMaskinProvider.evaluerKompletteRegler(norskIdent, navIdent)
			navAnsattTilgangTilOppfolgingPolicy.evaluate(any())
		}

		decision shouldBe Decision.Permit
	}


	@Test
	internal fun `harTilgang should return DENY if tilgangsMaskinDecision is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(tilgangsMaskinDecision = deny(message))

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if tilgangType is SKRIVE and tilgangTilOppfolging is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			oppfolgingPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if tilgangType is LESE and tilgangTilmodiaGenerell is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			modiaGenerellPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, LESE, norskIdent))

		decision shouldBe deny(message)
	}

	private fun deny(
		message: String = "TEST",
		reason: DecisionDenyReason = DecisionDenyReason.POLICY_IKKE_IMPLEMENTERT
	): Decision.Deny {
		return Decision.Deny(
			message = message,
			reason = reason
		)
	}

	private fun mockDecision(
		tilgangsMaskinDecision: Decision = Decision.Permit,
		oppfolgingPolicyDecision: Decision = Decision.Permit,
		modiaGenerellPolicyDecision: Decision = Decision.Permit
	) {

		every { adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId) } returns navIdent

		every {
			tilgangsMaskinProvider.evaluerKompletteRegler(norskIdent, navIdent)
		} returns tilgangsMaskinDecision

		every {
			navAnsattTilgangTilOppfolgingPolicy.evaluate(
				NavAnsattTilgangTilOppfolgingPolicy.Input(navAnsattAzureId)
			)
		} returns oppfolgingPolicyDecision

		every {
			navAnsattTilgangTilModiaGenerellPolicy.evaluate(
				NavAnsattTilgangTilModiaGenerellPolicy.Input(navAnsattAzureId)
			)
		} returns modiaGenerellPolicyDecision
	}
}
