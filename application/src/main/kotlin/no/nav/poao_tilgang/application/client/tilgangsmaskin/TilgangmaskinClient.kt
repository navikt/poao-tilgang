package no.nav.poao_tilgang.application.client.tilgangsmaskin

import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.NorskIdent

interface TilgangmaskinClient {
	fun evaluerKompletteRegler(norskIdent: NorskIdent, navIdent: NavIdent): TilgangmaskinResult
}
