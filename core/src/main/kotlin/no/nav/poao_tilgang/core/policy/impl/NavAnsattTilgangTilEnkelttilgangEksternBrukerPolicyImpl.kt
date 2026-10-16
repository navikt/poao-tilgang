package no.nav.poao_tilgang.core.policy.impl

import java.time.Duration
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaGenerellPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider
import no.nav.poao_tilgang.core.utils.Timer

/**
 * Veileder må være medlem av "0000-GA-enkelttilgang" og bruker kan ikke ha adressebeskyttelse eller være skjermet.
 */
class NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicyImpl(
	private val tilgangmaskinProvider: TilgangmaskinProvider,
	private val adGruppeProvider: AdGruppeProvider,
	private val navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
	private val timer: Timer,
) : NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy {

	override val name = "NavAnsattTilgangTilEnkelttilgangEksternBruker"

	override fun evaluate(input: NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy.Input): Decision {
		return harTilgang(input)
	}

	internal fun harTilgang(input: NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy.Input): Decision {
		val startTime = System.currentTimeMillis()

		val harTilgangEgen = harTilgangEgen(input)

		timer.record(
			"app.poao-tilgang.NavAnsattTilgangTilEnkelttilgangEksternBruker.egen",
			Duration.ofMillis(System.currentTimeMillis() - startTime)
		)
		return harTilgangEgen
	}

	private fun harTilgangEgen(input: NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, norskIdent) = input
		// sjekker lesetilgang
		navAnsattTilgangTilModiaGenerellPolicy.evaluate(
			NavAnsattTilgangTilModiaGenerellPolicy.Input(navAnsattAzureId)
		).whenDeny { return it }

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)

		// sjekker kjerneregler via tilgangsmaskinen
		tilgangmaskinProvider.evaluerKjerneregler(norskIdent, navIdent).whenDeny { return it }

		val enkelttilgang = adGruppeProvider.hentTilgjengeligeAdGrupper().enkelttilgang
		// sjekker enkelttilgang
		adGruppeProvider.hentAdGrupper(navAnsattAzureId).find { it.id == enkelttilgang.id }
			?: return Decision.Deny("Veileder har ikke tilgang til enkelttilgang", DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)

		return Decision.Permit
	}
}
