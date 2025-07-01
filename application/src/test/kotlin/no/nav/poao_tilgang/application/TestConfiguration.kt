package no.nav.poao_tilgang.application

import io.getunleash.DefaultUnleash
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
open class TestConfiguration {
	@Bean
	open fun defaultUnleash(): DefaultUnleash? {
		return Mockito.mock(DefaultUnleash::class.java)
	}
}
