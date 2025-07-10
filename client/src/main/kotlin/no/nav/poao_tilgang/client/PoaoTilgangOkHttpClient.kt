package no.nav.poao_tilgang.client

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.poao_tilgang.api.dto.response.*
import no.nav.poao_tilgang.client.ClientObjectMapper.objectMapper
import no.nav.poao_tilgang.client_core.NorskIdent
import no.nav.poao_tilgang.client_core.PoaoTilgangClient
import no.nav.poao_tilgang.client_core.PoaoTilgangHttpClient
import no.nav.poao_tilgang.client_core.api.ApiResult
import no.nav.poao_tilgang.client_core.api.ApiResult.Companion.failure
import no.nav.poao_tilgang.client_core.api.ApiResult.Companion.success
import no.nav.poao_tilgang.client_core.api.BadHttpStatusApiException
import no.nav.poao_tilgang.client_core.api.NetworkApiException
import no.nav.poao_tilgang.client_core.api.ResponseDataApiException
import no.nav.poao_tilgang.client_core.api.UnspecifiedApiException
import no.nav.poao_tilgang.client_core.serializerFrom
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

val jsonMediaType = "application/json".toMediaType()
fun sendRequestUsingOkHttp(tokenProvider: () -> String, client: OkHttpClient): (
	fullUrl: String,
	method: String,
	body: String?,
) ->  ApiResult<String> = { fullUrl, method, body ->
	val request = Request.Builder()
		.url(fullUrl)
		.method(method, body?.toRequestBody(jsonMediaType))
		.header("Authorization", "Bearer ${tokenProvider()}")
		.build()

	try {
		client.newCall(request).execute().use { response ->
//			val responseBody = response.body?.string()
			if (!response.isSuccessful) {
				return@use failure(BadHttpStatusApiException(response.code, response.body?.string()))
			}

			response.body?.string()?.let { success(it) }
				?: failure(ResponseDataApiException.missingBody())
		}
	} catch (e: Throwable) {
		when (e) {
			is IOException -> failure(NetworkApiException(e))
			else -> failure(UnspecifiedApiException(e))
		}
	}
}

internal inline fun <reified D> parseBody(body: String): ApiResult<D> {
	return try {
		success(objectMapper.readValue(body))
	} catch (e: Throwable) {
		failure(ResponseDataApiException(e.message ?: "Unknown error"))
	}
}

object JacksonSerializer: PoaoTilgangClient.BodyParser {
	override fun parsePolicyRequestsBody(body: String): ApiResult<EvaluatePoliciesResponse>  = parseBody(body)
	override fun parseHentTilgangsAttributterBody(body: String): ApiResult<TilgangsattributterResponse> = parseBody(body)
	override fun parseErSkjermetPersonBody(body: String): ApiResult<Map<NorskIdent, Boolean>> = parseBody(body)
	override fun parseHentAdGrupper(body: String): ApiResult<HentAdGrupperForBrukerResponse> = parseBody(body)
}

class PoaoTilgangOkHttpClient(
	private val baseUrl: String,
	private val tokenProvider: () -> String,
	private val client: OkHttpClient = RestClient.baseClient(),
	private val poaoTilgangHttpClient: PoaoTilgangHttpClient = PoaoTilgangHttpClient(
		baseUrl = baseUrl,
		httpFetch = sendRequestUsingOkHttp(tokenProvider, client),
		bodyParser = JacksonSerializer,
		serializer = serializerFrom { body -> objectMapper.writeValueAsString(body) }
	)
) : PoaoTilgangClient by poaoTilgangHttpClient


