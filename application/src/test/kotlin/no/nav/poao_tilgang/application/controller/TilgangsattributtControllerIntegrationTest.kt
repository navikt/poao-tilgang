package no.nav.poao_tilgang.application.controller

import io.kotest.matchers.shouldBe
import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.api.dto.response.TilgangsattributterResponse
import no.nav.poao_tilgang.application.client.pdl_pip.Gradering
import no.nav.poao_tilgang.application.client.veilarbarena.PersonRequest
import no.nav.poao_tilgang.application.utils.JsonUtils
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class TilgangsattributtControllerIntegrationTest : IntegrationTest() {
	private val logger = LoggerFactory.getLogger(TilgangsattributtControllerIntegrationTest::class.java)
	@Test
	fun `tilgangsattributter - should return correct response for skjermet og fortrolig`() {
		val norskIdent = "12345678910"
		val geografiskTilknytning = "434576"
		val enhet = "9999"
		val gradering = Gradering.FORTROLIG // DTO fra pdl
		val disk = Diskresjonskode.FORTROLIG // Response DTO fra poao-tilgang
		val erSkjermetPerson = true

		mockVeilarbarenaHttpServer.mockIngenOppfolgingsenhet(PersonRequest(Fnr.of(norskIdent)))
		mockPdlPipHttpServer.mockBrukerInfo(norskIdent, gradering, gtKommune = geografiskTilknytning)
		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(norskIdent to erSkjermetPerson))
		mockNorgHttpServer.mockTilhorendeEnhet(geografiskTilknytning, enhet, erSkjermetPerson, disk)

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/tilgangsattributter",
			body = norskIdent.toRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}")
		)
		val expectedResponse = TilgangsattributterResponse(
			kontor = enhet,
			skjermet = erSkjermetPerson,
			diskresjonskode = disk
		)

		response.code shouldBe 200
		response.body?.string() shouldBe JsonUtils.toJsonString(expectedResponse)
	}

	@Test
	fun `tilgangsattributter - should return correct response for ikke skjermet eller fortrolig`() {
		val norskIdent = "12345678910"
		val geografiskTilknytning = "434576"
		val enhet = "9999"
		val gradering = Gradering.UGRADERT // DTO fra pdl
		val disk = Diskresjonskode.UGRADERT // Response DTO fra poao-tilgang
		val erSkjermetPerson = false

		mockVeilarbarenaHttpServer.mockIngenOppfolgingsenhet(PersonRequest(Fnr.of(norskIdent)))
		mockPdlPipHttpServer.mockBrukerInfo(norskIdent, gradering, gtKommune = geografiskTilknytning)
		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(norskIdent to erSkjermetPerson))
		mockNorgHttpServer.mockTilhorendeEnhet(geografiskTilknytning, enhet, erSkjermetPerson, disk)

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/tilgangsattributter",
			body = norskIdent.toRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}")
		)
		val expectedResponse = TilgangsattributterResponse(
			kontor = enhet,
			skjermet = erSkjermetPerson,
			diskresjonskode = disk
		)

		response.code shouldBe 200
		response.body?.string() shouldBe JsonUtils.toJsonString(expectedResponse)
	}
	@Test
	fun `tilgangsattributter - should return arena kontor when present`() {
		val norskIdent = "12345678910"
		val geografiskTilknytning = "434576"
		val enhet = "9999"
		val gradering = Gradering.UGRADERT // DTO fra pdl
		val disk = Diskresjonskode.UGRADERT // Response DTO fra poao-tilgang
		val erSkjermetPerson = false

		mockVeilarbarenaHttpServer.mockOppfolgingsenhet(enhet)
		mockPdlPipHttpServer.mockBrukerInfo(norskIdent, gradering, gtKommune = geografiskTilknytning)
		mockSkjermetPersonHttpServer.mockErSkjermet(mapOf(norskIdent to erSkjermetPerson))

		val response = sendRequest(
			method = "POST",
			path = "/api/v1/tilgangsattributter",
			body = norskIdent.toRequestBody(),
			headers = mapOf("Authorization" to "Bearer ${mockOAuthServer.issueAzureAdM2MToken()}")
		)
		val expectedResponse = TilgangsattributterResponse(
			kontor = enhet,
			skjermet = erSkjermetPerson,
			diskresjonskode = disk
		)

		response.code shouldBe 200
		response.body?.string() shouldBe JsonUtils.toJsonString(expectedResponse)
	}
}
