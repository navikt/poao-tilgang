package no.nav.poao_tilgang.poao_tilgang_test_core

import no.nav.poao_tilgang.core.policy.impl.*
import no.nav.poao_tilgang.core.utils.Timer
import java.time.Duration

class TimerService():Timer {
	override fun record(name: String, duration: Duration, vararg tags: String) {
		//bare for test
	}

	override fun <T> measure(name: String, vararg tags: String, method: () -> T): T {
		return method()
	}

}

data class Policies(
	val navContext: NavContext = NavContext(),
	val providers: Providers = Providers(navContext),
	val timer: Timer= TimerService(),
	val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicyImpl = NavAnsattTilgangTilOppfolgingPolicyImpl(
		providers.adGruppeProvider
	),
	val navAnsattTilgangTilNavEnhetMedSperrePolicy: NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl = NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl(
		providers.navEnhetTilgangProvider,
		providers.adGruppeProvider,
		providers.abacProvider,
		timer,
		providers.toggleProvider,
		navAnsattTilgangTilOppfolgingPolicy
	),
	val navAnsattTilgangTilEksternBrukerNavEnhetPolicy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl = NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl(
		providers.oppfolgingsenhetProvider,
		providers.geografiskTilknyttetEnhetProvider,
		providers.adGruppeProvider,
		providers.navEnhetTilgangProvider,
	),
	val navAnsattTilgangTilNavEnhetPolicy: NavAnsattTilgangTilNavEnhetPolicyImpl = NavAnsattTilgangTilNavEnhetPolicyImpl(
		providers.navEnhetTilgangProvider,
		providers.adGruppeProvider,
		providers.abacProvider,
		timer,
		providers.toggleProvider,
		navAnsattTilgangTilOppfolgingPolicy
	),
	val eksternBrukerTilgangTilEksternBrukerPolicy: EksternBrukerTilgangTilEksternBrukerPolicyImpl = EksternBrukerTilgangTilEksternBrukerPolicyImpl(),
	val navAnsattBehandleStrengtFortroligUtlandBrukerePolicy: NavAnsattBehandleStrengtFortroligUtlandBrukerePolicyImpl = NavAnsattBehandleStrengtFortroligUtlandBrukerePolicyImpl(
		providers.adGruppeProvider
	),
	val navAnsattBehandleFortroligBrukerePolicy: NavAnsattBehandleFortroligBrukerePolicyImpl = NavAnsattBehandleFortroligBrukerePolicyImpl(
		providers.adGruppeProvider
	),
	val navAnsattTilgangTilModiaPolicy: NavAnsattTilgangTilModiaPolicyImpl = NavAnsattTilgangTilModiaPolicyImpl(
		providers.adGruppeProvider
	),
	val navAnsattBehandleSkjermedePersonerPolicy: NavAnsattBehandleSkjermedePersonerPolicyImpl = NavAnsattBehandleSkjermedePersonerPolicyImpl(
		providers.adGruppeProvider
	),
	val navAnsattBehandleStrengtFortroligBrukerePolicy: NavAnsattBehandleStrengtFortroligBrukerePolicyImpl = NavAnsattBehandleStrengtFortroligBrukerePolicyImpl(
		providers.adGruppeProvider
	),
	val navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicyImpl = NavAnsattTilgangTilModiaGenerellPolicyImpl(
		providers.adGruppeProvider
	),
	val navAnsattTilgangTilModiaAdminPolicy: NavAnsattTilgangTilModiaAdminPolicyImpl = NavAnsattTilgangTilModiaAdminPolicyImpl(
		providers.adGruppeProvider
	),
	val navAnsattTilgangTilSkjermetPersonPolicy: NavAnsattTilgangTilSkjermetPersonPolicyImpl = NavAnsattTilgangTilSkjermetPersonPolicyImpl(
		providers.skjermetPersonProvider,
		navAnsattBehandleSkjermedePersonerPolicy
	),
	val navAnsattTilgangTilAdressebeskyttetBrukerPolicy: NavAnsattTilgangTilAdressebeskyttetBrukerPolicyImpl = NavAnsattTilgangTilAdressebeskyttetBrukerPolicyImpl(
		providers.diskresjonskodeProvider,
		navAnsattBehandleFortroligBrukerePolicy,
		navAnsattBehandleStrengtFortroligBrukerePolicy,
		navAnsattBehandleStrengtFortroligUtlandBrukerePolicy,
	),
	val navAnsattTilgangTilEksternBrukerPolicy: NavAnsattTilgangTilEksternBrukerPolicyImpl = NavAnsattTilgangTilEksternBrukerPolicyImpl(
		providers.abacProvider,
		navAnsattTilgangTilAdressebeskyttetBrukerPolicy,
		navAnsattTilgangTilSkjermetPersonPolicy,
		navAnsattTilgangTilEksternBrukerNavEnhetPolicy,
		navAnsattTilgangTilOppfolgingPolicy,
		navAnsattTilgangTilModiaGenerellPolicy,
		providers.adGruppeProvider,
		timer,
		providers.toggleProvider,
		),
	val policyResolver: PolicyResolver = PolicyResolver(
		navAnsattTilgangTilEksternBrukerPolicy,
			navAnsattTilgangTilModiaPolicy,
			eksternBrukerTilgangTilEksternBrukerPolicy,
			navAnsattTilgangTilNavEnhetPolicy,
			navAnsattBehandleStrengtFortroligBrukerePolicy,
			navAnsattBehandleFortroligBrukerePolicy,
			navAnsattTilgangTilNavEnhetMedSperrePolicy,
			navAnsattBehandleSkjermedePersonerPolicy,
			navAnsattTilgangTilModiaAdminPolicy,
			timer,
			providers.toggleProvider,
	)
) {
}

