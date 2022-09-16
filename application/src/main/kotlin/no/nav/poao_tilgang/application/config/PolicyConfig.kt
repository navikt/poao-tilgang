package no.nav.poao_tilgang.application.config

import no.nav.common.abac.Pep
import no.nav.poao_tilgang.application.policy.AbacEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.policy.impl.FortroligBrukerPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.ModiaPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.SkjermetPersonPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.StrengtFortroligBrukerPolicyImpl
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PolicyConfig {

	@Bean
	open fun eksternBrukerPolicy(pep: Pep): EksternBrukerPolicy {
		return AbacEksternBrukerPolicy(pep)
	}

	@Bean
	open fun fortroligBrukerPolicy(adGruppeProvider: AdGruppeProvider): FortroligBrukerPolicy {
		return FortroligBrukerPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun modiaPolicy(adGruppeProvider: AdGruppeProvider): ModiaPolicy {
		return ModiaPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun skjermetPersonPolicy(adGruppeProvider: AdGruppeProvider): SkjermetPersonPolicy {
		return SkjermetPersonPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun strengtFortroligBrukerPolicy(adGruppeProvider: AdGruppeProvider): StrengtFortroligBrukerPolicy {
		return StrengtFortroligBrukerPolicyImpl(adGruppeProvider)
	}

}
