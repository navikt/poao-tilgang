package no.nav.poao_tilgang.application.client.pdl_pip

import io.micrometer.core.annotation.Timed
import no.nav.common.rest.client.RestClient
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.SecureLog
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

open class PdlPipClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val httpClient: OkHttpClient = RestClient.baseClient(),
): PdlPipClient {

	private val IDENT_PARAM_NAME: String = "ident"

	@Timed(
		"pdl_pip_http_client.hent_person",
		histogram = true,
		percentiles = [0.5, 0.95, 0.99],
		extraTags = ["type", "client"]
	)
	override fun hentBrukerInfo(
		brukerIdent: NorskIdent,
	): BrukerInfo? {

		val request = Request.Builder()
			.url("$baseUrl/api/v1/person")
			.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.invoke())
			.header(IDENT_PARAM_NAME, brukerIdent)
			.build()

		httpClient.newCall(request).execute().use { response ->
			if (response.code == 404) {
				return null
			}
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente personinfo fra pdl-pip. Status: ${response.code}")
			}

			val body = response.body?.string() ?: throw RuntimeException("Body is missing")

			SecureLog.secureLog.info("PdlPip response, hentPerson for fnr: $brukerIdent, body: $body")

			val brukerInfoResponse = fromJsonString<BrukerInfo>(body)

			return brukerInfoResponse
		}
	}
}
