package no.nav.poao_tilgang.application.provider

import io.getunleash.DefaultUnleash
import no.nav.poao_tilgang.application.utils.BRUK_ENHETSTILGANGER_FRA_AD
import no.nav.poao_tilgang.core.provider.ToggleProvider
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class ToggleProviderImpl(private val unleashClient: DefaultUnleash) : ToggleProvider {
	override fun brukAbacDecision(): Boolean {
		return false
	}

	override fun logAbacDecisionDiff(): Boolean {
		return false
	}
}
