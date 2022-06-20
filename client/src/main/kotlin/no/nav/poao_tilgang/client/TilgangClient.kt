package no.nav.poao_tilgang.client

import no.nav.poao_tilgang.api.dto.DecisionDto

interface TilgangClient {
	fun harVeilederTilgangTilModia(navIdent: String): DecisionDto
}
