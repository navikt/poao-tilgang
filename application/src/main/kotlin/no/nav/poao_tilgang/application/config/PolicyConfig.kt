package no.nav.poao_tilgang.application.config

import no.nav.poao_tilgang.application.service.TimerService
import no.nav.poao_tilgang.core.policy.EksternBrukerTilgangTilEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleFortroligBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleSkjermedePersonerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilAdressebeskyttetBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerKjernereglerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaAdminPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaGenerellPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetMedSperrePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilOppfolgingPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilSkjermetPersonPolicy
import no.nav.poao_tilgang.core.policy.NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy
import no.nav.poao_tilgang.core.policy.impl.EksternBrukerTilgangTilEksternBrukerPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattBehandleFortroligBrukerePolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattBehandleSkjermedePersonerPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattBehandleStrengtFortroligBrukerePolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattBehandleStrengtFortroligUtlandBrukerePolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilAdressebeskyttetBrukerPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilEksternBrukerKjernereglerPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilEksternBrukerPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilModiaAdminPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilModiaGenerellPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilModiaPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilNavEnhetPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilOppfolgingPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattTilgangTilSkjermetPersonPolicyImpl
import no.nav.poao_tilgang.core.policy.impl.NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyImpl
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProviderV2
import no.nav.poao_tilgang.core.provider.SkjermetPersonProvider
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PolicyConfig {

	@Bean
	open fun navAnsattTilgangTilEksternBrukerPolicy(
		tilgangmaskinProvider: TilgangmaskinProvider,
		adGruppeProvider: AdGruppeProvider,
		navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
		navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
		timerService: TimerService,
	): NavAnsattTilgangTilEksternBrukerPolicy {
		return NavAnsattTilgangTilEksternBrukerPolicyImpl(
			tilgangmaskinProvider,
			adGruppeProvider,
			navAnsattTilgangTilOppfolgingPolicy,
			navAnsattTilgangTilModiaGenerellPolicy,
			timerService,
		)
	}

	@Bean
	open fun navAnsattTilgangTilEksternBrukerKjernereglerPolicy(
		tilgangmaskinProvider: TilgangmaskinProvider,
		adGruppeProvider: AdGruppeProvider,
		navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
		navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
		timerService: TimerService,
	): NavAnsattTilgangTilEksternBrukerKjernereglerPolicy {
		return NavAnsattTilgangTilEksternBrukerKjernereglerPolicyImpl(
			tilgangmaskinProvider,
			adGruppeProvider,
			navAnsattTilgangTilOppfolgingPolicy,
			navAnsattTilgangTilModiaGenerellPolicy,
			timerService,
		)
	}

	@Bean
	open fun navAnsattTilgangTilEnkelttilgangEksternBrukerPolicy(
		tilgangmaskinProvider: TilgangmaskinProvider,
		adGruppeProvider: AdGruppeProvider,
		navAnsattTilgangTilModiaPolicy: NavAnsattTilgangTilModiaPolicy,
		timerService: TimerService,
	): NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy {
		return NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicyImpl(
			tilgangmaskinProvider,
			adGruppeProvider,
			navAnsattTilgangTilModiaPolicy,
			timerService,
		)
	}

	@Bean
	open fun navAnsattTilgangTilAdressebeskyttetBrukerPolicy(
		diskresjonskodeProvider: DiskresjonskodeProvider,
		navAnsattBehandleFortroligBrukerePolicy: NavAnsattBehandleFortroligBrukerePolicy,
		navAnsattBehandleStrengtFortroligBrukerePolicy: NavAnsattBehandleStrengtFortroligBrukerePolicy,
		navAnsattBehandleStrengtFortroligUtlandBrukerePolicy: NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy
	): NavAnsattTilgangTilAdressebeskyttetBrukerPolicy {
		return NavAnsattTilgangTilAdressebeskyttetBrukerPolicyImpl(
			diskresjonskodeProvider,
			navAnsattBehandleFortroligBrukerePolicy,
			navAnsattBehandleStrengtFortroligBrukerePolicy,
			navAnsattBehandleStrengtFortroligUtlandBrukerePolicy
		)
	}

	@Bean
	open fun navAnsattTilgangTilNavEnhetMedSperrePolicy(
		navEnhetTilgangProviderV2: NavEnhetTilgangProviderV2,
		adGruppeProvider: AdGruppeProvider,
		timerService: TimerService,
		navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy
	): NavAnsattTilgangTilNavEnhetMedSperrePolicy {
		return NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl(
			navEnhetTilgangProviderV2,
			adGruppeProvider,
			timerService,
			navAnsattTilgangTilOppfolgingPolicy,
		)
	}

	@Bean
	open fun navAnsattTilgangTilSkjermetPersonPolicy(
		skjermetPersonProvider: SkjermetPersonProvider,
		navAnsattBehandleSkjermedePersonerPolicy: NavAnsattBehandleSkjermedePersonerPolicy
	): NavAnsattTilgangTilSkjermetPersonPolicy {
		return NavAnsattTilgangTilSkjermetPersonPolicyImpl(
			skjermetPersonProvider, navAnsattBehandleSkjermedePersonerPolicy
		)
	}

	@Bean
	open fun tilgangTilNavEnhetPolicy(
		navEnhetTilgangProviderV2: NavEnhetTilgangProviderV2,
		adGruppeProvider: AdGruppeProvider,
		timerService: TimerService,
		navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy
	): NavAnsattTilgangTilNavEnhetPolicy {
		return NavAnsattTilgangTilNavEnhetPolicyImpl(
			navEnhetTilgangProviderV2,
			adGruppeProvider,
			timerService,
			navAnsattTilgangTilOppfolgingPolicy
		)
	}

	@Bean
	open fun eksternBrukerTilgangTilEksternBrukerPolicy(): EksternBrukerTilgangTilEksternBrukerPolicy {
		return EksternBrukerTilgangTilEksternBrukerPolicyImpl()
	}

	@Bean
	open fun navAnsattBehandleStrengtFortroligUtlandBrukerePolicy(
		adGruppeProvider: AdGruppeProvider
	): NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy {
		return NavAnsattBehandleStrengtFortroligUtlandBrukerePolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattBehandleFortroligBrukerePolicy(adGruppeProvider: AdGruppeProvider): NavAnsattBehandleFortroligBrukerePolicy {
		return NavAnsattBehandleFortroligBrukerePolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattTilgangTilModiaPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattTilgangTilModiaPolicy {
		return NavAnsattTilgangTilModiaPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattBehandleSkjermedePersonerPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattBehandleSkjermedePersonerPolicy {
		return NavAnsattBehandleSkjermedePersonerPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattBehandleStrengtFortroligBrukerePolicy(adGruppeProvider: AdGruppeProvider): NavAnsattBehandleStrengtFortroligBrukerePolicy {
		return NavAnsattBehandleStrengtFortroligBrukerePolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattTilgangTilOppfolgingPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattTilgangTilOppfolgingPolicy {
		return NavAnsattTilgangTilOppfolgingPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattTilgangTilModiaGenerellPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattTilgangTilModiaGenerellPolicy {
		return NavAnsattTilgangTilModiaGenerellPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattTilgangTilModiaAdminPolicy(adGruppeProvider: AdGruppeProvider): NavAnsattTilgangTilModiaAdminPolicy {
		return NavAnsattTilgangTilModiaAdminPolicyImpl(adGruppeProvider)
	}

	@Bean
	open fun navAnsattUtenModiarolleTilgangTilEksternBrukerPolicy(
		adGruppeProvider: AdGruppeProvider,
		tilgangsmaskinProvider: TilgangmaskinProvider
	): NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy {
		return NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyImpl(
			adGruppeProvider,
			tilgangsmaskinProvider
		)
	}
}
