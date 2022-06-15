package no.nav.poao_tilgang.api.dto

import no.nav.poao_tilgang.core.domain.Decision

data class HarTilgangTilModiaRequest(
	val navIdent: String
)

data class TilgangResponse(
	val decision: Decision
)
