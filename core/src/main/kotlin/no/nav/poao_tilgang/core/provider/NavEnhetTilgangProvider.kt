package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NavIdent

interface NavEnhetTilgangProvider {
	fun hentEnhetTilganger(navIdent: NavIdent): List<NavEnhetId>
}
