package no.nav.poao_tilgang.application.controller.internal

import no.nav.common.abac.AbacClient
import no.nav.poao_tilgang.core.provider.ToggleProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class HealthChecksPoaoTilgang(
	private val abacClient: AbacClient,
): HealthIndicator {
	private val logger: Logger = LoggerFactory.getLogger(this::class.java)
	override fun health(): Health {
		/*
		Vi har avhengigheter til:
		* Axsys
		* Microsoft graph api
		* Norg
		* Pdl
		* Skjermet Person
		* Veilarbarena
		Men de ulike policyene har forskjellige avhengigheter.
		TODO Vurder om vi skal ha noen eller alle avhengigheter inn i healthcheck.
		NB Ingen av klientene våre har innebygget støtte for ping, så det må i så fall implementeres.
		 */
		return Health.up().build()
	}
}
