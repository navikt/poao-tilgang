package no.nav.poao_tilgang.application.client.ao_oppfolgingskontor

import io.micrometer.core.annotation.Timed
import no.nav.common.rest.client.RestClient.baseClient
import no.nav.common.rest.client.RestUtils.MEDIA_TYPE_JSON
import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.utils.JsonUtils
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.core.domain.NavEnhetId
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.doubleArrayOf

open class AoKontorClientImpl(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val httpClient: OkHttpClient = baseClient(),
): AoKontorClient {

	@Timed("ao_oppfolgingskontor_client.hent_bruker_oppfolgingsenhet_id", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "client"])
	override fun hentBrukerOppfolgingsenhetId(fnr: Fnr): NavEnhetId? {
		val requestPayload = JsonUtils.toJsonString(GraphqlRequest(Variables(fnr.get())))
		val requestBody = requestPayload.toRequestBody(MEDIA_TYPE_JSON)

		val url = "$baseUrl/graphql".toHttpUrl().newBuilder().build()

		val request = Request.Builder()
			.url(url)
			.addHeader("Authorization", "Bearer ${tokenProvider()}")
			.post(requestBody)
			.build()

		httpClient.newCall(request).execute().use { response ->
			if (!response.isSuccessful) {
				throw RuntimeException("Klarte ikke å hente status fra ao-oppfolgingskontor. Status: ${response.code}")
			}
			val body = response.body?.string() ?: throw RuntimeException("Body is missing")
			val response = fromJsonString<GraphqlResponse>(body)

			if (!response.errors.isNullOrEmpty()) {
				throw RuntimeException("Klarte ikke å hente status fra ao-oppfolgingskontor. Status: ${response.errors.joinToString(separator = ",") { it.message }}")
			}
			if (response.data == null) {
				throw RuntimeException("Klarte ikke å hente status fra ao-oppfolgingskontor. Ingen data funnet i response")
			}
			val tilhorigheter = response.data.kontorTilhorigheter
			return tilhorigheter.arbeidsoppfolging?.kontorId ?: tilhorigheter.geografiskTilknytning?.kontorId
		}
	}
}

val kontorQuery = """
	query(${'$'}fnr: String!) {
	  kontorTilhorigheter(ident: ${'$'}fnr) {
	    arbeidsoppfolging { kontorId }
	    geografiskTilknytning { kontorId }
	  }
	}
""".trimIndent()

data class Variables(
	val fnr: String,
)
data class GraphqlRequest(
	val variables: Variables,
	val query: String = kontorQuery,
)

data class Location(
	val line: Int,
	val column: Int,
)
data class Error(
	val message: String,
	val path: String,
	val locations: List<Location>
)
data class KontorMedId(
	val kontorId: String,
)
data class KontorTilhørigheter(
	val arbeidsoppfolging: KontorMedId?,
	val geografiskTilknytning: KontorMedId?,
)
data class Data(
	val kontorTilhorigheter: KontorTilhørigheter
)
data class GraphqlResponse(
	val data: Data? = null,
	val errors: List<Error>? = null,
)
