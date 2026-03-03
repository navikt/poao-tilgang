package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.Decision

interface TilgangmaskinProvider {
    fun evaluerKjerneregler(norskIdent: String): Decision
}

