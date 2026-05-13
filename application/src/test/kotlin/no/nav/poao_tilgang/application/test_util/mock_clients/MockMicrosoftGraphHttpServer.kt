package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClientImpl
import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClientImpl.HentAdGrupper
import no.nav.poao_tilgang.application.test_util.CapturedRequest
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NavIdent
import okhttp3.mockwebserver.MockResponse
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class MockMicrosoftGraphHttpServer : MockHttpServer() {

	private val capturedRequests = ConcurrentHashMap<String, CapturedRequest>()

	fun takeAdGrupperRequest(adGruppeId: UUID, timeoutMs: Long = 2000): CapturedRequest = pollCaptured(adGruppeId.toString(), timeoutMs)
	fun takeAdGrupperForNavAnsattRequest(navAnsattAzureId: UUID, timeoutMs: Long = 2000): CapturedRequest = pollCaptured(navAnsattAzureId.toString(), timeoutMs)
	fun takeAzureIdMedNavIdentRequest(navIdent: NavIdent, timeoutMs: Long = 2000): CapturedRequest = pollCaptured(navIdent, timeoutMs)
	fun takeNavIdentMedAzureIdRequest(navAnsattAzureId: AzureObjectId, timeoutMs: Long = 2000): CapturedRequest = pollCaptured(navAnsattAzureId.toString(), timeoutMs)

	private fun pollCaptured(key: String, timeoutMs: Long): CapturedRequest {
		val deadline = System.currentTimeMillis() + timeoutMs
		while (System.currentTimeMillis() < deadline) {
			capturedRequests.remove(key)?.let { return it }
			Thread.sleep(50)
		}
		throw IllegalStateException("No captured MicrosoftGraph request for key '$key' within ${timeoutMs}ms. Available: ${capturedRequests.keys}")
	}

	val mockedAdGrupper: MutableMap<UUID, AdGruppe> = mutableMapOf()
	fun mockHentAdGrupperResponse(grupper: List<AdGruppe>) {
		mockedAdGrupper.putAll(grupper.map { it.id to it })

		val response: (List<UUID>) -> MockResponse = { adGruppeIds ->
			val adGrupper = adGruppeIds.mapNotNull { mockedAdGrupper[it] }
			MockResponse()
				.setBody(
					toJsonString(
						HentAdGrupper.Response(
							adGrupper.map { HentAdGrupper.Response.AdGruppe(it.id, it.navn) }
						)
					)
				)
		}

		handleRequest(
			matchPath = "/v1.0/directoryObjects/getByIds?\$select=id,displayName",
			matchMethod = "POST",
			onRequestCaptured = { captured ->
				// Capture under first queried AD gruppe ID
				captured.body?.let { body ->
					val ids = fromJsonString<HentAdGrupper.Request>(body).ids
					ids.firstOrNull()?.let { id -> capturedRequests[id.toString()] = captured }
				}
			},
			responseCreator = { _, body ->
				val queriedAdGrupperIds = fromJsonString<HentAdGrupper.Request>(body.value!!).ids
				response(queriedAdGrupperIds)
			}
		)
	}


	fun mockHentAdGrupperForNavAnsatt(navAnsattAzureId: UUID, gruppeIder: List<AzureObjectId>) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentAdGrupperForNavAnsatt.Response(
						value = gruppeIder
					)
				)
			)

		handleRequest(
			matchPath = "/v1.0/users/${navAnsattAzureId}/getMemberGroups",
			matchMethod = "POST",
			onRequestCaptured = { capturedRequests[navAnsattAzureId.toString()] = it },
			response = response
		)
	}

	fun mockHentAzureIdMedNavIdentResponse(navIdent: NavIdent, navAnsattAzureId: AzureObjectId) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentAzureIdMedNavIdent.Response(listOf(
						MicrosoftGraphClientImpl.HentAzureIdMedNavIdent.Response.UserData(navAnsattAzureId)
					))
				)
			)

		handleRequest(
			matchPath = "/v1.0/users?\$select=id&\$count=true&\$filter=onPremisesSamAccountName%20eq%20%27$navIdent%27",
			matchMethod = "GET",
			onRequestCaptured = { capturedRequests[navIdent] = it },
			response = response
		)
	}
	fun mockHentNavIdentMedAzureIdResponse(navAnsattAzureId: AzureObjectId, navIdent: NavIdent) {
		val response = MockResponse()
			.setBody(
				toJsonString(
					MicrosoftGraphClientImpl.HentNavIdentMedAzureId.Response(listOf(
						MicrosoftGraphClientImpl.HentNavIdentMedAzureId.Response.UserData(navIdent)
					))
				)
			)

		handleRequest(
			matchPath = "/v1.0/users?\$select=onPremisesSamAccountName&\$filter=id%20eq%20%27$navAnsattAzureId%27",
			matchMethod = "GET",
			onRequestCaptured = { capturedRequests[navAnsattAzureId.toString()] = it },
			response = response
		)
	}

}
