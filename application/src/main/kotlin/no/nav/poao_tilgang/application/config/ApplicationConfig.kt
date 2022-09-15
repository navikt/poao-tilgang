package no.nav.poao_tilgang.application.config

import no.nav.common.abac.*
import no.nav.common.log.LogFilter
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableJwtTokenValidation
open class ApplicationConfig {

	@Profile("default")
	@Bean
	open fun machineToMachineTokenClient(): MachineToMachineTokenClient {
		return AzureAdTokenClientBuilder.builder()
			.withNaisDefaults()
			.buildMachineToMachineTokenClient()
	}

	@Bean
	open fun logFilterRegistrationBean(): FilterRegistrationBean<LogFilter> {
		val registration = FilterRegistrationBean<LogFilter>()
		registration.filter = LogFilter(
			"poao-tilgang", EnvironmentUtils.isDevelopment().orElse(false)
		)
		registration.order = 1
		registration.addUrlPatterns("/*")
		return registration
	}

	@Bean
	open fun abacClient(): AbacClient {
		val client = AbacHttpClient(
			"",
			{""}
		)

		return AbacCachedClient(client)
	}

	@Bean
	open fun pep(abacClient: AbacClient): Pep {
		return VeilarbPep(
			"poao-tilgang",
			abacClient,
			null,
			null
		)
	}

}
