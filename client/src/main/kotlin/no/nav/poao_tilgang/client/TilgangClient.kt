package no.nav.poao_tilgang.client

import no.nav.poao_tilgang.core.domain.Decision

interface TilgangClient {
	fun harVeilederTilgangTilModia(navIdent: String): Decision
}
