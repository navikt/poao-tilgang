package no.nav.poao_tilgang.client

import no.nav.poao_tilgang.api.dto.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.rest.JsonUtils.toJsonString

class TilgangHttpClient : TilgangClient {
	override fun harVeilederTilgangTilModia(navIdent: String): Decision {
		val requestJson = toJsonString(HarTilgangTilModiaRequest(navIdent))

		TODO("Not yet implemented")
	}
}
