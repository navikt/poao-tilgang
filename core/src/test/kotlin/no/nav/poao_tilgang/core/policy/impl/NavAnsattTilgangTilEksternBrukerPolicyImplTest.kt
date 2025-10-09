package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.TilgangType.LESE
import no.nav.poao_tilgang.core.domain.TilgangType.SKRIVE
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.policy.test_utils.MockTimer
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilEksternBrukerPolicyImplTest {

	private val navIdent = "Z1234"
	private val navAnsattAzureId = UUID.randomUUID()
	private val norskIdent = "63546454"

	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy =
		mockk<NavAnsattTilgangTilAdressebeskyttetBrukerPolicy>()
	private val navAnsattTilgangTilSkjermetPersonPolicy = mockk<NavAnsattTilgangTilSkjermetPersonPolicy>()
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy = mockk<NavAnsattTilgangTilEksternBrukerNavEnhetPolicy>()
	private val navAnsattTilgangTilOppfolgingPolicy = mockk<NavAnsattTilgangTilOppfolgingPolicy>()
	private val navAnsattTilgangTilModiaGenerellPolicy = mockk<NavAnsattTilgangTilModiaGenerellPolicy>()
	private val adGruppeProvider = mockk<AdGruppeProvider>()

	private val mockTimer = MockTimer()

	private val policy = NavAnsattTilgangTilEksternBrukerPolicyImpl(
		navAnsattTilgangTilAdressebeskyttetBrukerPolicy,
		navAnsattTilgangTilSkjermetPersonPolicy,
		navAnsattTilgangTilEksternBrukerNavEnhetPolicy,
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
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(any())
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(any())
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(any())
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
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(any())
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(any())
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(any())
			navAnsattTilgangTilOppfolgingPolicy.evaluate(any())
		}

		decision shouldBe Decision.Permit
	}

	@Test
	internal fun `harTilgang should return DENY if adressebeskyttet is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			adressebeskyttetBrukerPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if skjermetPerson is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			skjermetPersonPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if eksternBrukerNavEnhet is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			adressebeskyttetBrukerPolicyDecision = deny(message),
			skjermetPersonPolicyDecision = deny(message),
			eksternBrukerNavEnhetPolicyDecision = deny(message),
			oppfolgingPolicyDecision = deny(message),
			modiaGenerellPolicyDecision = deny(message)
		)

		val decision =
			policy.harTilgang(NavAnsattTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, SKRIVE, norskIdent))

		decision shouldBe deny(message)
	}

	@Test
	internal fun `harTilgang should return DENY if tilgangTilOppfolging is DENY`() {
		val message = UUID.randomUUID().toString()

		mockDecision(
			oppfolgingPolicyDecision = deny(message)
		)

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
		adressebeskyttetBrukerPolicyDecision: Decision = Decision.Permit,
		skjermetPersonPolicyDecision: Decision = Decision.Permit,
		eksternBrukerNavEnhetPolicyDecision: Decision = Decision.Permit,
		oppfolgingPolicyDecision: Decision = Decision.Permit,
		modiaGenerellPolicyDecision: Decision = Decision.Permit
	) {

		every {
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
				NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
					navAnsattAzureId, norskIdent
				)
			)
		} returns adressebeskyttetBrukerPolicyDecision

		every {
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
				NavAnsattTilgangTilSkjermetPersonPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns skjermetPersonPolicyDecision

		every {
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns eksternBrukerNavEnhetPolicyDecision

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
