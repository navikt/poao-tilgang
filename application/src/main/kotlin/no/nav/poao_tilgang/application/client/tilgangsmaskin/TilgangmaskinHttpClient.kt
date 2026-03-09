package no.nav.poao_tilgang.application.client.tilgangsmaskin

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.application.utils.JsonUtils.objectMapper
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.application.utils.RestUtils.authorization
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.OkHttpClient
import okhttp3.Request

class TilgangmaskinHttpClient(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient()
) : TilgangmaskinClient {

	override fun evaluerKompletteRegler(norskIdent: NorskIdent, navIdent: NavIdent): TilgangmaskinResult {
		val request = Request.Builder()
			.url(joinPaths(baseUrl, "/api/v1/ccf/komplett/${navIdent}"))
			.post(toJsonString(norskIdent).toJsonRequestBody())
			.authorization(tokenProvider)
			.build()

		return client.newCall(request).execute().use { response ->
			when (response.code) {
				204 -> TilgangmaskinResult.Godkjent
				403 -> {
					val body = response.body?.string()
					if (body != null) {
						objectMapper.readValue<TilgangmaskinResult.Avvist>(body)
					} else {
						throw RuntimeException("HTTP 403 fra tilgangsmaskinen uten body")
					}
				}
				else -> throw RuntimeException("Fikk uhåndtert statuskode ${response.code} fra tilgangsmaskinen, body=${response.body?.string()}")
			}
		}
	}
}
