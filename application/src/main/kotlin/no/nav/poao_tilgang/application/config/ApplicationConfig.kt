package no.nav.poao_tilgang.application.config

import io.getunleash.DefaultUnleash
import io.getunleash.util.UnleashConfig
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import no.nav.common.rest.filter.LogRequestFilter
import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.common.utils.Credentials
import no.nav.common.utils.EnvironmentUtils
import no.nav.common.utils.EnvironmentUtils.isProduction
import no.nav.common.utils.NaisUtils
import no.nav.poao_tilgang.application.controller.internal.HealthChecksPoaoTilgang
import no.nav.poao_tilgang.application.middleware.RequesterLogFilter
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@EnableJwtTokenValidation
open class ApplicationConfig {

	companion object {
		const val APPLICATION_NAME = "poao-tilgang"
	}

	@Bean
	open fun machineToMachineTokenClient(
		@Value("\${nais.env.azureAppClientId}") azureAdClientId: String,
		@Value("\${nais.env.azureOpenIdConfigTokenEndpoint}") azureTokenEndpoint: String,
		@Value("\${nais.env.azureAppJWK}") azureAdJWK: String
	): MachineToMachineTokenClient {
		return AzureAdTokenClientBuilder.builder()
			.withClientId(azureAdClientId)
			.withTokenEndpointUrl(azureTokenEndpoint)
			.withPrivateJwk(azureAdJWK)
			.buildMachineToMachineTokenClient()
	}

	@Bean
	open fun requesterLogFilterRegistrationBean(
		tokenValidationContextHolder: TokenValidationContextHolder
	): FilterRegistrationBean<RequesterLogFilter> {
		val registration = FilterRegistrationBean<RequesterLogFilter>()
		registration.filter = RequesterLogFilter(tokenValidationContextHolder)

		registration.order = 1
		registration.addUrlPatterns("/api/*")
		return registration
	}

	@Bean
	open fun logFilterRegistrationBean(): FilterRegistrationBean<LogRequestFilter> {
		val registration = FilterRegistrationBean<LogRequestFilter>()
		registration.filter = LogRequestFilter(
			APPLICATION_NAME, EnvironmentUtils.isDevelopment().orElse(false)
		)
		registration.order = 2
		registration.addUrlPatterns("/api/*")
		return registration
	}

	@Bean
	open fun requestTimingFilterRegistrationBean(): FilterRegistrationBean<RequestTimingFilter> {
		val registration = FilterRegistrationBean<RequestTimingFilter>()
		registration.filter = RequestTimingFilter()
		registration.order = 3
		registration.addUrlPatterns("/api/*")
		return registration
	}

	@Bean
	@Profile("fss")
	open fun serviceUserCredentials(): Credentials {
		return NaisUtils.getCredentials("service_user")
	}

	@Bean
	open fun healthChecks(): HealthChecksPoaoTilgang {
		return HealthChecksPoaoTilgang()
	}

	@Bean
	open fun meterRegistry(): MeterRegistry {
		return PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
	}

	@Bean
	open fun unleashClient(
		@Value("\${nais.env.unleash.url}") unleashUrl: String,
		@Value("\${nais.env.unleash.apiToken}") unleashApiToken: String,
		@Value("\${nais.env.podName}") podName: String
	): DefaultUnleash = DefaultUnleash(
		UnleashConfig.builder()
			.appName(APPLICATION_NAME)
			.instanceId(podName)
			.unleashAPI("$unleashUrl/api")
			.apiKey(unleashApiToken)
			.environment(if (isProduction().orElse(false)) "production" else "development")
			.synchronousFetchOnInitialisation(true)
			.build()
	)
}
