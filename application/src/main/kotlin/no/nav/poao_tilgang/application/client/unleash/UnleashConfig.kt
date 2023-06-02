package no.nav.poao_tilgang.application.client.unleash

import no.nav.common.featuretoggle.UnleashClient
import no.nav.common.featuretoggle.UnleashClientImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean


@Configuration
open class UnleashConfig {
	@Bean
	open fun unleashClient(@Value("\${unleash.url}") url: String): UnleashClient {
		return UnleashClientImpl(url, "poao-tilgang")
	}
}
