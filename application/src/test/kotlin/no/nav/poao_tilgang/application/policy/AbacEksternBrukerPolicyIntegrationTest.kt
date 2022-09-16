package no.nav.poao_tilgang.application.policy

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.application.utils.RestUtils.toJsonRequestBody
import org.junit.jupiter.api.Test
import java.util.*

class AbacEksternBrukerPolicyIntegrationTest : IntegrationTest() {

	@Test
	fun `should return decision permit if receives permit from ABAC`() {
		val navIdent = "Z1235"
		val norskIdent = "6456532"
		val requestId = UUID.randomUUID()

		mockAbacHttpServer.mockPermit()

		val response = sendRequest(
			path = "/api/v1/policy/evaluate",
			method = "POST",
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}"),
			body = """
				{"requests": [
					{
						"requestId": "$requestId",
						"policyInput": {"navIdent": "$navIdent", "norskIdent": "$norskIdent"},
						"policyId": "EKSTERN_BRUKER_V1"
					}
				]}
			""".trimIndent().toJsonRequestBody()
		)

		val expectedJson = """
			{"results":[{"requestId":"$requestId","decision":{"type":"PERMIT","message":null,"reason":null}}]}
		""".trimIndent()

		response.body?.string() shouldBe expectedJson
	}

	@Test
	fun `should return decision deny if receives deny from ABAC`() {
		val navIdent = "Z1235"
		val norskIdent = "6456532"
		val requestId = UUID.randomUUID()

		mockAbacHttpServer.mockDeny()

		val response = sendRequest(
			path = "/api/v1/policy/evaluate",
			method = "POST",
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}"),
			body = """
				{"requests": [
					{
						"requestId": "$requestId",
						"policyInput": {"navIdent": "$navIdent", "norskIdent": "$norskIdent"},
						"policyId": "EKSTERN_BRUKER_V1"
					}
				]}
			""".trimIndent().toJsonRequestBody()
		)

		val expectedJson = """
			{"results":[{"requestId":"$requestId","decision":{"type":"DENY","message":"Deny fra ABAC","reason":"IKKE_TILGANG_FRA_ABAC"}}]}
		""".trimIndent()

		response.body?.string() shouldBe expectedJson
	}

}
