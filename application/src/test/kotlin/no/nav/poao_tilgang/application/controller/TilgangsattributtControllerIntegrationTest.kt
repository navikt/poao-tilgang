package no.nav.poao_tilgang.application.controller

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.api.dto.response.TilgangsattributterResponse
import no.nav.poao_tilgang.application.client.pdl_pip.Gradering
import no.nav.poao_tilgang.application.utils.JsonUtils
//import no.nav.poao_tilgang.core.domain.Diskresjonskode
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class TilgangsattributtControllerIntegrationTest : IntegrationTest() {
	private val logger = LoggerFactory.getLogger(TilgangsattributtControllerIntegrationTest::class.java)
	@Test
	fun `tilgangsattributter - should return correct response`() {
		val norskIdent = "12345678910"
		val geografiskTilknytning = "4345"
		val enhet = "9999"
		val gradering = Gradering.FORTROLIG // DTO fra pdl
		val disk = Diskresjonskode.FORTROLIG // Response DTO fra poao-tilgang
		val erSkjermetPerson = true

		mockPdlPipHttpServer.mockBrukerInfo(norskIdent, gradering, gtKommune = geografiskTilknytning)
		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(norskIdent to erSkjermetPerson))
		mockNorgHttpServer.mockTilhorendeEnhet(geografiskTilknytning, enhet, erSkjermetPerson, disk)

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/tilgangsattributter",
			body = norskIdent.toRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}")
		)
		logger.info("Response code: ${response.code}")
		logger.info("Response body: ${response.body?.string()}")
		val expectedResponse = TilgangsattributterResponse(
			standardEnhet = geografiskTilknytning,
			skjermet = erSkjermetPerson,
			diskresjonskode = Diskresjonskode.STRENGT_FORTROLIG
		)

		response.code shouldBe 200
		response.body?.string() shouldBe JsonUtils.toJsonString(expectedResponse)
	}
}
