package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.skjermet_person.SkjermetPersonClientImpl.ErSkjermet
import no.nav.poao_tilgang.application.test_util.CapturedRequest
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.application.utils.JsonUtils.fromJsonString
import no.nav.poao_tilgang.application.utils.JsonUtils.toJsonString
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.mockwebserver.MockResponse
import java.util.concurrent.ConcurrentHashMap

class MockSkjermetPersonHttpServer : MockHttpServer() {

	private val capturedRequests = ConcurrentHashMap<String, CapturedRequest>()

	fun takeRequest(captureKey: String, timeoutMs: Long = 2000): CapturedRequest {
		val deadline = System.currentTimeMillis() + timeoutMs
		while (System.currentTimeMillis() < deadline) {
			capturedRequests.remove(captureKey)?.let { return it }
			Thread.sleep(50)
		}
		throw IllegalStateException("No captured SkjermetPerson request for key '$captureKey' within ${timeoutMs}ms. Available: ${capturedRequests.keys}")
	}

	val skjermedePersoner: MutableMap<NorskIdent, Boolean> = mutableMapOf()
	fun mockErSkjermet(skjerming: Map<NorskIdent, Boolean>, captureKey: String? = null) {
		if (skjerming.keys.any { skjermedePersoner.keys.contains(it) } ) {
			throw IllegalStateException("Kan ikke mocke ident ${skjerming.keys} skjerming for samme person flere ganger, det ødelegger for paralellitet i testene")
		}
		skjermedePersoner.putAll(skjerming)

		handleRequest(
			matchPath = "/skjermetBulk",
			matchMethod = "POST",
			onRequestCaptured = captureKey?.let { key -> { captured -> capturedRequests[key] = captured } },
			responseCreator = { _, body ->
				val personer = fromJsonString<ErSkjermet.Request>(body.value!!).personidenter
					.map { ident -> ident to skjermedePersoner[ident] }
					.toMap()
				MockResponse()
					.setBody(toJsonString(personer))
			},
		)
	}

}
