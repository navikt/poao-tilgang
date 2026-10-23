package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.DisplayName
import no.nav.poao_tilgang.core.domain.NavIdent

interface SaksbehandlerNavnProvider {
	fun hentNavnForNavIdent(navIdent: NavIdent): DisplayName
}
