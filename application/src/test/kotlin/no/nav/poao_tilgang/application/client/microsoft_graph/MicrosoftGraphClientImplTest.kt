package no.nav.poao_tilgang.application.client.microsoft_graph

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*

class MicrosoftGraphClientImplTest {

	companion object {
		private val mockServer = MockHttpServer()

		@BeforeAll
		@JvmStatic
		fun start() {
			mockServer.start()
		}
	}

	@AfterEach
	fun reset() {
		mockServer.reset()
	}

	@Test
	fun `hentAdGrupper - skal lage riktig request og parse respons`() {
		val client = MicrosoftGraphClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val adGruppeId = UUID.randomUUID()

		mockServer.handleRequest(
			response = MockResponse()
				.setBody(
					"""
					{
						"@odata.context": "https://graph.microsoft.com/v1.0/${"$"}metadata#directoryObjects(id,displayName)",
						"value": [
							{
								"@odata.type": "#microsoft.graph.group",
								"id": "$adGruppeId",
								"displayName": "Test"
							}
						]
					}
				""".trimIndent()
				)
		)

		val adGrupper = client.hentAdGrupper(listOf(adGruppeId))

		adGrupper.first().id shouldBe adGruppeId
		adGrupper.first().name shouldBe "Test"

		val request = mockServer.latestRequest()

		request.path shouldBe "/v1.0/directoryObjects/getByIds?\$select=id,displayName"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"

		val expectedRequestJson = """
			{"ids":["$adGruppeId"],"types":["group"]}
		""".trimIndent()

		request.body.readUtf8() shouldBe expectedRequestJson
	}

	@Test
	fun `hentAdGrupperForNavAnsatt - skal lage riktig request og parse respons`() {
		val client = MicrosoftGraphClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val navAnsattAzureId = UUID.randomUUID()
		val adGroupAzureId = UUID.randomUUID()

		mockServer.handleRequest(
			response = MockResponse()
				.setBody(
					"""
						{
							"@odata.context": "https://graph.microsoft.com/v1.0/${"$"}metadata#Collection(Edm.String)",
							"value": [
								"$adGroupAzureId"
							]
						}
					""".trimIndent()
				)
		)

		val adGrupper = client.hentAdGrupperForNavAnsatt(navAnsattAzureId)

		adGrupper.first() shouldBe adGroupAzureId

		val request = mockServer.latestRequest()

		request.path shouldBe "/v1.0/users/$navAnsattAzureId/getMemberGroups"
		request.method shouldBe "POST"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"

		val expectedRequestJson = """
			{"securityEnabledOnly":true}
		""".trimIndent()

		request.body.readUtf8() shouldBe expectedRequestJson
	}

	@Test
	fun `hentAzureIdMedNavIdent - skal lage riktig request og parse respons`() {
		val client = MicrosoftGraphClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val navIdent = "Z1234"
		val navAnsattAzureId = UUID.randomUUID()

		mockServer.handleRequest(
			response = MockResponse()
				.setBody(
					"""
						{
							"@odata.context": "https://graph.microsoft.com/v1.0/${"$"}metadata#users(id)",
							"value": [
								{
									"id": "$navAnsattAzureId"
								}
							]
						}
					""".trimIndent()
				)
		)

		val azureId = client.hentAzureIdMedNavIdent(navIdent)

		azureId shouldBe navAnsattAzureId

		val request = mockServer.latestRequest()

		request.path shouldBe "/v1.0/users?\$select=id&\$count=true&\$filter=onPremisesSamAccountName%20eq%20%27$navIdent%27"
		request.method shouldBe "GET"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"
		request.getHeader("ConsistencyLevel") shouldBe "eventual"

		request.body.readUtf8() shouldBe ""
	}

	@Test
	fun `hentNavIdentMedAzureId - skal lage riktig request og parse respons`() {
		val client = MicrosoftGraphClientImpl(
			baseUrl = mockServer.serverUrl(),
			tokenProvider = { "TOKEN" },
		)

		val navAnsattAzureId = UUID.randomUUID()
		val expectedNavIdent = "Z1234"

		mockServer.handleRequest(
			response = MockResponse()
				.setBody(
					"""
						{
							"@odata.context": "https://graph.microsoft.com/v1.0/${"$"}metadata#users(id)",
							"value": [
								{
									"onPremisesSamAccountName": "$expectedNavIdent"
								}
							]
						}
					""".trimIndent()
				)
		)

		val navIdent = client.hentNavIdentMedAzureId(navAnsattAzureId)

		navIdent shouldBe expectedNavIdent

		val request = mockServer.latestRequest()

		request.path shouldBe "/v1.0/users?\$select=onPremisesSamAccountName&\$filter=id%20eq%20%27$navAnsattAzureId%27"
		request.method shouldBe "GET"
		request.getHeader("Authorization") shouldBe "Bearer TOKEN"

		request.body.readUtf8() shouldBe ""
	}

}
