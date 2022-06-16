package no.nav.poao_tilgang.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.api.dto.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.client.KotlinObjectMapper.objectMapper
import no.nav.poao_tilgang.core.domain.Decision
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class TilgangHttpClient(val baseUrl: String, val authToken: () -> String) : TilgangClient {

	private val client: OkHttpClient = RestClient.baseClient()

	override fun harVeilederTilgangTilModia(navIdent: String): Decision {
		val requestJson = objectMapper.writeValueAsString(HarTilgangTilModiaRequest(navIdent))


		val request = Request.Builder()
			.url(joinPaths(baseUrl, "/api/v1/tilgang/modia"))
			.post(requestJson.toRequestBody("application/json".toMediaType()))
			.header("Authorization", "Bearer ${authToken()}")
			.build()

		return client.newCall(request).execute().let { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Feilende kall med statuskode ${response.code} mot $baseUrl")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			objectMapper.readValue(body)
		}
	}
}
