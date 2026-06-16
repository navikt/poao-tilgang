package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider
import no.nav.poao_tilgang.core.utils.Timer
import java.time.Duration

/**
 * Etter modell av ABAC https://confluence.adeo.no/pages/viewpage.action?pageId=202371160
 */
class NavAnsattTilgangTilEksternBrukerKjernereglerPolicyImpl(
	private val tilgangmaskinProvider: TilgangmaskinProvider,
	private val adGruppeProvider: AdGruppeProvider,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
	private val navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
	private val timer: Timer,
) : NavAnsattTilgangTilEksternBrukerKjernereglerPolicy {

	override val name = "NavAnsattTilgangTilEksternBrukerKjerneregler"

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerKjernereglerPolicy.Input): Decision {
		return harTilgang(input)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilEksternBrukerKjernereglerPolicy.Input): Decision {
		val startTime = System.currentTimeMillis()

		val harTilgangEgen = harTilgangEgen(input)

		timer.record(
			"app.poao-tilgang.NavAnsattTilgangTilEksternBrukerKjerneregler.egen",
			Duration.ofMillis(System.currentTimeMillis() - startTime)
		)
		return harTilgangEgen
	}

	private fun harTilgangEgen(input: NavAnsattTilgangTilEksternBrukerKjernereglerPolicy.Input): Decision {
		val (navAnsattAzureId, tilgangType, norskIdent) = input
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)

		// sjekker kjerneregler via tilgangsmaskinen
		tilgangmaskinProvider.evaluerKjerneregler(norskIdent, navIdent).whenDeny { return it }

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
