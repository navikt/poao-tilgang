package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NavIdent

interface NavEnhetTilgangProvider {

	fun hentEnhetTilganger(navIdent: NavIdent): List<NavEnhetTilgang>

}

interface NavEnhetTilgangProviderV2 {
	fun hentEnhetTilganger(navIdent: NavIdent): Set<NavEnhetId>
}

data class NavEnhetTilgang(
	val enhetId: NavEnhetId,
	val enhetNavn: String,
	val temaer: List<String>
)
