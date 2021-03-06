package no.nav.poao_tilgang.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.client.microsoft_graph.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.test_util.IntegrationTest
import no.nav.poao_tilgang.utils.RestUtils.toJsonRequestBody
import okhttp3.Response
import org.junit.jupiter.api.Test
import java.util.*

class TilgangControllerIntegrationTest : IntegrationTest() {

	@Test
	fun `harTilgangTilModia - should return 401 when not authenticated`() {
		val response = sendTilgangTilModiaRequest("Z1234")

		response.code shouldBe 401
	}

	@Test
	fun `harTilgangTilModia - should return 403 when not machine-to-machine request`() {
		val response = sendTilgangTilModiaRequest("Z1234") { oAuthServer.issueAzureAdToken() }

		response.code shouldBe 403
	}

	@Test
	fun `harTilgangTilModia - should return 'deny' if not member of any ad group`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(listOf())

		val response = sendTilgangTilModiaRequest(navIdent) { oAuthServer.issueAzureAdM2MToken() }

		expectDeny(
			response= response,
			message = "NAV ansatt mangler tilgang til en av AD gruppene [0000-ga-bd06_modiagenerelltilgang, 0000-ga-modia-oppfolging, 0000-ga-syfo-sensitiv]",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)
	}

	@Test
	fun `harTilgangTilModia - should return 'deny' if not member of correct ad group`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(listOf("Gruppe1", "Gruppe2"))

		val response = sendTilgangTilModiaRequest(navIdent) { oAuthServer.issueAzureAdM2MToken() }

		expectDeny(
			response= response,
			message = "NAV ansatt mangler tilgang til en av AD gruppene [0000-ga-bd06_modiagenerelltilgang, 0000-ga-modia-oppfolging, 0000-ga-syfo-sensitiv]",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of modiagenerelltilgang`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(listOf(AdGrupper.MODIA_GENERELL))

		val response = sendTilgangTilModiaRequest(navIdent) { oAuthServer.issueAzureAdM2MToken() }

		expectPermit(response)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of modia-oppfolging`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(listOf(AdGrupper.MODIA_OPPFOLGING))

		val response = sendTilgangTilModiaRequest(navIdent) { oAuthServer.issueAzureAdM2MToken() }

		expectPermit(response)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of syfo-sensitiv`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(listOf(AdGrupper.SYFO_SENSITIV))

		val response = sendTilgangTilModiaRequest(navIdent) { oAuthServer.issueAzureAdM2MToken() }

		expectPermit(response)
	}

	@Test
	fun `harTilgangTilModia - should return 'permit' if member of correct role and other`() {
		val navIdent = "Z12371"

		mockAdGrupperResponse(listOf(AdGrupper.MODIA_OPPFOLGING, "Gruppe2"))

		val response = sendTilgangTilModiaRequest(navIdent) { oAuthServer.issueAzureAdM2MToken() }

		expectPermit(response)
	}

	private fun mockAdGrupperResponse(adGrupperNavn: List<String>) {
		val adGrupper = adGrupperNavn.map { AdGruppe(UUID.randomUUID(), it) }

		mockMicrosoftGraphHttpClient.enqueueHentAzureIdForNavAnsattResponse(UUID.randomUUID())

		mockMicrosoftGraphHttpClient.enqueueHentAdGrupperForNavAnsatt(adGrupper.map { it.id })

		if (adGrupper.isNotEmpty()) {
			mockMicrosoftGraphHttpClient.enqueueHentAdGrupperResponse(adGrupper)
		}
	}

	private fun sendTilgangTilModiaRequest(navIdent: String, token: (() -> String)? = null): Response {
		val headers = if (token != null) mapOf("Authorization" to "Bearer ${token()}") else mapOf()
		return sendRequest(
			method = "POST",
			path = "/api/v1/tilgang/modia",
			body = """{"navIdent": "$navIdent"}""".toJsonRequestBody(),
			headers = headers
		)
	}

	private fun expectPermit(response: Response) {
		val expectedJson = """
			{"decision":{"type":"PERMIT","message":null,"reason":null}}
		""".trimIndent()

		response.code shouldBe 200
		response.body?.string() shouldBe expectedJson
	}

	private fun expectDeny(response: Response, message: String, reason: DecisionDenyReason) {
		val expectedJson = """
			{"decision":{"type":"DENY","message":"$message","reason":"$reason"}}
		""".trimIndent()

		response.code shouldBe 200
		response.body?.string() shouldBe expectedJson
	}
}
