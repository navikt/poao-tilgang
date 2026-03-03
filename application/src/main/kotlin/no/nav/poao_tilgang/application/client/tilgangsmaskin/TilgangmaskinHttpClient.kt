package no.nav.poao_tilgang.application.client.tilgangsmaskin

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.application.utils.JsonUtils.objectMapper
import no.nav.poao_tilgang.client.NorskIdent
import no.nav.poao_tilgang.client.api.*
import no.nav.poao_tilgang.client.api.ApiResult.Companion.failure
import no.nav.poao_tilgang.client.api.ApiResult.Companion.success
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class TilgangmaskinHttpClient(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient()
) : TilgangmaskinClient {

	private val jsonMediaType = "application/json".toMediaType()

	override fun evaluerKjerneregler(norskIdent: NorskIdent): ApiResult<TilgangmaskinResult> {
		val request = Request.Builder()
			.url(joinPaths(baseUrl, "/api/v1/kjerne"))
			.post(objectMapper.writeValueAsString(norskIdent).toRequestBody(jsonMediaType))
			.header("Authorization", "Bearer ${tokenProvider()}")
			.build()

		return try {
			client.newCall(request).execute().use { response ->
				when (response.code) {
					204 -> success(TilgangmaskinResult.Godkjent)
					403 -> {
						val body = response.body?.string()
						if (body != null) {
							try {
								val avvist = objectMapper.readValue<TilgangmaskinResult.Avvist>(body)
								success(avvist)
							} catch (e: Exception) {
								failure(ResponseDataApiException("Kunne ikke parse avvisningsrespons fra tilgangsmaskin: ${e.message}"))
							}
						} else {
							success(
								TilgangmaskinResult.Avvist(
									type = null,
									title = null,
									status = 403,
									instance = null,
									brukerIdent = null,
									navIdent = null,
									begrunnelse = null,
									traceId = null,
									kanOverstyres = null
								)
							)
						}
					}
					else -> failure(BadHttpStatusApiException(response.code, response.body?.string()))
				}
			}
		} catch (e: Throwable) {
			when (e) {
				is IOException -> failure(NetworkApiException(e))
				else -> failure(UnspecifiedApiException(e))
			}
		}
	}
}
