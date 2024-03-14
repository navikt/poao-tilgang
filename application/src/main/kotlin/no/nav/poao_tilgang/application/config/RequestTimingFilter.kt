package no.nav.poao_tilgang.application.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class RequestTimingFilter : Filter {
	private val log = LoggerFactory.getLogger(javaClass)
	private val xRequestTime = "x_request_time_ms"
	@Throws(IOException::class, ServletException::class)
	override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
		if (request !is HttpServletRequest || response !is HttpServletResponse) {
			throw ServletException("RequestTimingFilter supports only HTTP requests")
		}

		if (isInternalRequest(request)) {
			chain.doFilter(request, response)
			return
		}
		val startTime = System.currentTimeMillis()
		try {
			log.debug("Request received: {} {}", request.method, request.requestURI)
			chain.doFilter(request, response)
		} finally {
			val duration = System.currentTimeMillis() - startTime
			if (duration > 250) {
				MDC.put(xRequestTime, duration.toString())
				log.debug("Slow request detected: {} {} ({}ms)", request.method, request.requestURI, duration)
				MDC.remove(xRequestTime)
			}
		}
	}

	private fun isInternalRequest(httpServletRequest: HttpServletRequest): Boolean {
		return httpServletRequest.requestURI.contains("/internal/")
	}
}
