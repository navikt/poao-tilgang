package no.nav.poao_tilgang.application.client.tilgangsmaskin

import no.nav.poao_tilgang.client.NorskIdent
import no.nav.poao_tilgang.client.api.ApiResult

interface TilgangmaskinClient {
	fun evaluerKjerneregler(norskIdent: NorskIdent): ApiResult<TilgangmaskinResult>
}
