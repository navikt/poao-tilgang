package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.core.provider.ToggleProvider
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class ToggleProviderImpl(/*private val unleashClient: UnleashClient*/) : ToggleProvider {
	override fun brukAbacDecision(): Boolean {
		return false
//		return !unleashClient.isEnabled("poao-tilgang.use-poao-tilgang-decision")
	}

	override fun logAbacDecisionDiff(): Boolean {
		return false
	}
}
