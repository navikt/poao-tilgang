package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider

class NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider,
	private val tilgangsmaskinProvider: TilgangmaskinProvider,
) : NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy {

	override val name = "NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy"

	override fun evaluate(input: NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navAnsattAzureId, norskIdent) = input
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)

		tilgangsmaskinProvider.evaluerKompletteRegler(norskIdent, navIdent)
			.whenDeny { return it }

		return Decision.Permit
	}
}
