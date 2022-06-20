package no.nav.poao_tilgang.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.UrlUtils.joinPaths
import no.nav.poao_tilgang.api.dto.HarTilgangTilModiaRequest
import no.nav.poao_tilgang.client.ClientObjectMapper.objectMapper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class TilgangHttpClient(
	private val baseUrl: String,
	private val authTokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient()
) : TilgangClient {

	override fun harVeilederTilgangTilModia(navIdent: String): Decision {
		val requestJson = objectMapper.writeValueAsString(HarTilgangTilModiaRequest(navIdent))

		val url = joinPaths(baseUrl, "/api/v1/tilgang/modia")

		val request = Request.Builder().url(url).post(requestJson.toRequestBody("application/json".toMediaType()))
			.header("Authorization", "Bearer ${authTokenProvider()}").build()

		return client.newCall(request).execute().let { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Feilende kall med statuskode ${response.code} mot $url")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			objectMapper.readValue<TilgangResponseDto>(body).decision.let { responseToDecision(it) }
		}
	}

	private data class TilgangResponseDto(val decision: DecisionResponseDto)
	private data class DecisionResponseDto(val type: String, val message: String?, val reason: String?)

	private fun responseToDecision(response: DecisionResponseDto): Decision {
		return when (Decision.Type.values().first { it.decision == response.type }) {
			Decision.Type.PERMIT -> Decision.Permit
			Decision.Type.DENY -> {
				check(response.message != null) { "message cannot be null" }
				check(response.reason != null) { "reason cannot be null" }
				Decision.Deny(response.message, DecisionDenyReason.values().first { it.reason == response.reason })
			}
		}
	}
}
