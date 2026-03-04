package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.NavIdent

interface TilgangmaskinProvider {
    fun evaluerKompletteRegler(norskIdent: String, navIdent: NavIdent): Decision
}

