package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilAdressebeskyttetBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilSkjermetPersonPolicy
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyImplTest {

	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy =
		mockk<NavAnsattTilgangTilAdressebeskyttetBrukerPolicy>()
	private val navAnsattTilgangTilSkjermetPersonPolicy = mockk<NavAnsattTilgangTilSkjermetPersonPolicy>()
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy = mockk<NavAnsattTilgangTilEksternBrukerNavEnhetPolicy>()

	private val policy = NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyImpl (
		navAnsattTilgangTilAdressebeskyttetBrukerPolicy,
		navAnsattTilgangTilSkjermetPersonPolicy,
		navAnsattTilgangTilEksternBrukerNavEnhetPolicy,
	)

	private val navAnsattAzureId = UUID.randomUUID()

	@Test
	fun `skal returnere permit hvis bruker ikke har adressebeskyttelse`() {
		val norskIdent = "1235645644"

		every {
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
				NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
					navAnsattAzureId, norskIdent
				)
			)
		} returns Decision.Permit

		every {
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
				NavAnsattTilgangTilSkjermetPersonPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns Decision.Permit

		every {
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns Decision.Permit

		val decision = policy.evaluate(NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere Deny hvis navveileder ikke har tilgang til enhet`() {
		val norskIdent = "1235645644"

		every {
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
				NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
					navAnsattAzureId, norskIdent
				)
			)
		} returns Decision.Permit

		every {
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
				NavAnsattTilgangTilSkjermetPersonPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns Decision.Permit

		every {
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns Decision.Deny("", DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET)

		val decision = policy.evaluate(NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Deny("", DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET)
	}

	@Test
	fun `skal returnere Deny hvis navveileder ikke har tilgang til adressebeskyttelse og bruker er adressebeskyttet`() {
		val norskIdent = "1235645644"

		every {
			navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
				NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
					navAnsattAzureId, norskIdent
				)
			)
		} returns Decision.Deny("", DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)

		every {
			navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
				NavAnsattTilgangTilSkjermetPersonPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns Decision.Permit

		every {
			navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
				NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
					navAnsattAzureId = navAnsattAzureId,
					norskIdent = norskIdent
				)
			)
		} returns Decision.Permit

		val decision = policy.evaluate(NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Deny("", DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)
	}
}
