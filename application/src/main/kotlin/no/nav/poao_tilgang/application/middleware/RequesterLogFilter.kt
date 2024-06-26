package no.nav.poao_tilgang.application.middleware

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.MDC
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse

class RequesterLogFilter(
	private val tokenValidationContextHolder: TokenValidationContextHolder
) : Filter {

	private val requesterLabelName = "requester"

	override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
		val azpName: String? = tokenValidationContextHolder
			.getTokenValidationContext()
			.anyValidClaims
			?.getStringClaim("azp_name")

		try {
			if (azpName == null) {
				MDC.remove(requesterLabelName)
			} else {
				MDC.put(requesterLabelName, azpName)
			}

			chain.doFilter(req, res)
		} finally {
			MDC.remove(requesterLabelName)
		}
	}

}
