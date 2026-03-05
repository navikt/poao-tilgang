package no.nav.poao_tilgang.application.test_util

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.TimeUnit

open class MockHttpServer : Closeable {
	open val mockServerName: String = "<mock server without name>"

	private val server = MockWebServer()

	private val log = LoggerFactory.getLogger(javaClass)

	private var lastRequestCount = 0

	private val responseHandlers = mutableMapOf<(request: RecordedRequest, requestBody: Lazy<String?>) -> Boolean, ResponseCreator>()

	fun start() {
		try {
		    server.start()
			server.dispatcher = createResponseDispatcher()
		} catch (e: IllegalArgumentException) {
			log.info("${javaClass.simpleName} is already started")
		}
	}

	fun reset() {
		lastRequestCount = server.requestCount
		responseHandlers.clear()
		flushRequests()
	}

	fun serverUrl(): String {
		return server.url("").toString().removeSuffix("/")
	}

	fun addResponseHandler(requestMatcher: (req: RecordedRequest, requestBody: Lazy<String?>) -> Boolean, response: MockResponse) {
		responseHandlers[requestMatcher] = { _, _ -> response }
	}
	fun addResponseHandler(requestMatcher: (req: RecordedRequest, requestBody: Lazy<String?>) -> Boolean, responseCreator: ResponseCreator) {
		responseHandlers[requestMatcher] = responseCreator
	}

	/**
	 * Matches a request on a best effort basis. The matching is loose, so if you are mocking several requests, make sure
	 * you put the most specific matchers first.
	 *
	 * For example, if you have a request which matches on a path, and another request which matches on the same path and a query parameter,
	 * Put the query parameter matcher first.
	 */

	fun handleRequest(
		matchPath: String? = null,
		matchMethod: String? = null,
		matchHeaders: Map<String, String>? = null,
		matchBodyContains: String? = null,
		matchQueryParam: Map<String, String>? = null,
		responseCreator: ResponseCreator
	) {
		val requestMatcher: (req: RecordedRequest, requestBody: Lazy<String?>) -> Boolean = matcher@{ req, requestBody ->
			if (matchPath != null && (req.path?.startsWith(matchPath) != true))
				return@matcher false

			if (matchQueryParam != null) {
				val allParamsMatches = matchQueryParam.all { matchEntry ->
					req.requestUrl!!.queryParameterValues(matchEntry.key).contains(matchEntry.value) == true
				}
				if (!allParamsMatches) return@matcher false
			} else if (req.requestUrl!!.querySize > 0) {
				log.warn("Request has query parameters, but no matchQueryParam was provided. This may cause unexpected behavior.")
			}

			if (matchMethod != null && req.method != matchMethod)
				return@matcher false

			if (matchHeaders != null && !hasExpectedHeaders(req.headers, matchHeaders))
				return@matcher false

			if (matchBodyContains != null) {
				if (requestBody.value == null) throw RuntimeException("Request matching on body requires the request to have a body")
				if (!requestBody.value!!.contains(matchBodyContains)) {
					return@matcher false
				}
			}

			true
		}

		addResponseHandler(requestMatcher, responseCreator)
	}
	/* If mock setup does not require requeset input to create response they can just send the response as response param */
	fun handleRequest(
		matchPath: String? = null,
		matchMethod: String? = null,
		matchHeaders: Map<String, String>? = null,
		matchBodyContains: String? = null,
		matchQueryParam: Map<String, String>? = null,
		response: MockResponse
	) = handleRequest(matchPath, matchMethod, matchHeaders, matchBodyContains, matchQueryParam, { _, _ -> response })

	fun latestRequest(): RecordedRequest {
		return server.takeRequest()
	}

	fun requestCount(): Int {
		return server.requestCount - lastRequestCount
	}

	private fun createResponseDispatcher(): Dispatcher {
		return object : Dispatcher() {
			override fun dispatch(request: RecordedRequest): MockResponse {
				val body = lazy { request.body.readUtf8() }
				val response = responseHandlers.entries
					.find {
						val requestMatcher = it.key
						requestMatcher.invoke(request, body)
					}?.value
					?: throw IllegalStateException("No handler for ${mockServerName} path:${request.path} - body ${request.bodySize} :${request.body.readUtf8()}")

				log.info("Responding [${request.path}]: $response")

				return response(request, body)
			}
		}
	}

	private fun hasExpectedHeaders(requestHeaders: okhttp3.Headers, expectedHeaders: Map<String, String>): Boolean {
		var hasHeaders = true

		expectedHeaders.forEach { (name, value) ->
			if (requestHeaders[name] != value)
				hasHeaders = false
		}

		return hasHeaders
	}

	private fun flushRequests() {
		while (server.takeRequest(1, TimeUnit.NANOSECONDS) != null) {}
	}

	override fun close() {
		server.close()
	}
}

typealias ResponseCreator = (RecordedRequest, requestBody: Lazy<String?>) -> MockResponse

