package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.core.provider.ToggleProvider
import org.springframework.stereotype.Component

@Component
class ToggleProviderTestImpl : ToggleProvider {
	override fun brukAbacDecision(): Boolean {
		return false
	}
}
