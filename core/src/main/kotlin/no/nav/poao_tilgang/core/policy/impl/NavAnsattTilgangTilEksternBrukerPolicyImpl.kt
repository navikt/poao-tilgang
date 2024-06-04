package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.ToggleProvider
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.asyncLogDecisionDiff
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.toAbacDecision
import no.nav.poao_tilgang.core.utils.Timer
import java.time.Duration

/**
 * Etter modell av ABAC https://confluence.adeo.no/pages/viewpage.action?pageId=202371160
 */
class NavAnsattTilgangTilEksternBrukerPolicyImpl(
	private val abacProvider: AbacProvider,
	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy: NavAnsattTilgangTilAdressebeskyttetBrukerPolicy,
	private val navAnsattTilgangTilSkjermetPersonPolicy: NavAnsattTilgangTilSkjermetPersonPolicy,
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
	private val navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
	private val adGruppeProvider: AdGruppeProvider,
	private val timer: Timer,
	private val toggleProvider: ToggleProvider,
) : NavAnsattTilgangTilEksternBrukerPolicy {

	override val name = "NavAnsattTilgangTilEksternBruker"

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		return if (toggleProvider.brukAbacDecision()) {
			val harTilgangAbac = harTilgangAbac(input)
			asyncLogDecisionDiff(name, input, ::harTilgang, { _ -> harTilgangAbac })

			harTilgangAbac
		} else {
			val result = harTilgang(input)
			asyncLogDecisionDiff(name, input, { _ -> result }, ::harTilgangAbac)

			result
		}
	}

	private fun harTilgangAbac(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, tilgangType, norskIdent) = input

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)

		val startTime=System.currentTimeMillis()

		val harTilgang = abacProvider.harVeilederTilgangTilPerson(navIdent, tilgangType, norskIdent)

		timer.record("app.poao-tilgang.NavAnsattTilgangTilEksternBruker", Duration.ofMillis(System.currentTimeMillis()-startTime))

		return toAbacDecision(harTilgang)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val startTime=System.currentTimeMillis()

		val harTilgangEgen = harTilgangEgen(input)

		timer.record("app.poao-tilgang.NavAnsattTilgangTilEksternBruker.egen", Duration.ofMillis(System.currentTimeMillis()-startTime))
		return harTilgangEgen
	}

	private fun harTilgangEgen(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, tilgangType, norskIdent) = input
		// FP-Adressebeskyttelse
		navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
			NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }
		// FP-Skjermede NAV ansatte
		navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
			NavAnsattTilgangTilSkjermetPersonPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		// Sjekker ikke Kontorsperre når vi ber om tilgang til bruker

		// organisatorisk og geografisk tilgang + tilgang enhet
		navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		// tilgang oppfølging
		when (tilgangType) {
			TilgangType.LESE ->
				navAnsattTilgangTilModiaGenerellPolicy.evaluate(
					NavAnsattTilgangTilModiaGenerellPolicy.Input(navAnsattAzureId)
				).whenDeny { return it }

			TilgangType.SKRIVE ->
				navAnsattTilgangTilOppfolgingPolicy.evaluate(
					NavAnsattTilgangTilOppfolgingPolicy.Input(navAnsattAzureId)
				).whenDeny { return it }
		}

		return Decision.Permit
	}

}
