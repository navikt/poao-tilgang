package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*

/**
 * Sjekker om en NAV ansatt har tilgang til å behandle informasjon om en ekstern bruker.
 */
interface NavIdentTilgangTilEksternBrukerPolicy : Policy<NavIdentTilgangTilEksternBrukerPolicy.Input> {

	data class Input(
		val navIdent: NavIdent,
		val tilgangType: TilgangType,
		val norskIdent: NorskIdent
	) : PolicyInput

}
