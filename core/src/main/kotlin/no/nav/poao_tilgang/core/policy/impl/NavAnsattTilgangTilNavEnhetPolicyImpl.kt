package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilOppfolgingPolicy
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import no.nav.poao_tilgang.core.provider.ToggleProvider
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.asyncLogDecisionDiff
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.toAbacDecision
import no.nav.poao_tilgang.core.utils.Timer
import no.nav.poao_tilgang.core.utils.has
import org.slf4j.LoggerFactory
import java.time.Duration

/**
 * Etter modell av https://confluence.adeo.no/display/ABAC/Tilgang+til+enhet
 */
class NavAnsattTilgangTilNavEnhetPolicyImpl(
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider,
	private val adGruppeProvider: AdGruppeProvider,
	private val abacProvider: AbacProvider,
	private val timer: Timer,
	private val toggleProvider: ToggleProvider,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy
) : NavAnsattTilgangTilNavEnhetPolicy {

	private val modiaAdmin = adGruppeProvider.hentTilgjengeligeAdGrupper().modiaAdmin

	private val denyDecision = Decision.Deny(
		message = "Har ikke tilgang til NAV enhet",
		reason = DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
	)

	override val name = "NavAnsattTilgangTilNavEnhet"

	val secureLog = LoggerFactory.getLogger("SecureLog")

	override fun evaluate(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		return if (toggleProvider.brukAbacDecision()) {
			val harTilgangAbacDesicion = harTilgangAbac(input)
			if (toggleProvider.logAbacDecisionDiff()) {
				asyncLogDecisionDiff(name, input, ::harTilgang, { _ ->harTilgangAbacDesicion })
			}
			harTilgangAbacDesicion
		} else {
			val resultat = harTilgang(input)
			if (toggleProvider.logAbacDecisionDiff()) {
				asyncLogDecisionDiff(name, input, { _ -> resultat }, ::harTilgangAbac)
			}
			resultat
		}
	}

	private fun harTilgangAbac(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		val startTime = System.currentTimeMillis()

		val harTilgangAbac = abacProvider.harVeilederTilgangTilNavEnhet(navIdent, input.navEnhetId)

		timer.record("app.poao-tilgang.NavAnsattTilgangTilNavEnhet", Duration.ofMillis(System.currentTimeMillis() - startTime))

		return toAbacDecision(harTilgangAbac)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {
		val startTime = System.currentTimeMillis()
		val harTilgangEgen = harTilgangEgen(input)
		timer.record("app.poao-tilgang.NavAnsattTilgangTilNavEnhet", Duration.ofMillis(System.currentTimeMillis() - startTime))
		return harTilgangEgen
	}

	private fun harTilgangEgen(input: NavAnsattTilgangTilNavEnhetPolicy.Input): Decision {

		val adGrupper = adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
		adGrupper.has(modiaAdmin).whenPermit {
			secureLog.info("Tilgang gitt basert paa 0000-GA-Modia_Admin")
			// TODO tror denne skal audit-logges
			return it
		}
		// Sjekk av adgruppe modiaoppfølging er egentlig ikke del av sjekken for tilgang til enhet
		// https://confluence.adeo.no/display/ABAC/Tilgang+til+enhet
		// MEN
		//  Sjekk av 'tilgang til oppfølging' kicker inn pga resource_type == "no.nav.abac.attributter.resource.felles.enhet"
		// https://confluence.adeo.no/pages/viewpage.action?pageId=202371312

		// TODO Slutte med denne sjekken hvis fag er enige (bør ikke trenge tilgang til modia_oppfølging for å ha tilgang til enhet)

		navAnsattTilgangTilOppfolgingPolicy.evaluate(NavAnsattTilgangTilOppfolgingPolicy.Input(input.navAnsattAzureId)).whenDeny {
			return it
		}

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
			.any { input.navEnhetId == it.enhetId }

		return if (harTilgangTilEnhet) return Decision.Permit else denyDecision
	}

}
