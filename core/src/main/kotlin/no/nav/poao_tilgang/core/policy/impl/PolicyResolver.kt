package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.ToggleProvider
import no.nav.poao_tilgang.core.utils.Timer

class PolicyResolver(
	private val navAnsattTilgangTilEksternBrukerPolicy: NavAnsattTilgangTilEksternBrukerPolicy,
	private val navAnsattTilgangTilModiaPolicy: NavAnsattTilgangTilModiaPolicy,
	private val eksternBrukerTilgangTilEksternBrukerPolicy: EksternBrukerTilgangTilEksternBrukerPolicy,
	private val navAnsattTilgangTilNavEnhetPolicy: NavAnsattTilgangTilNavEnhetPolicy,
	private val navAnsattBehandleStrengtFortroligBrukerePolicy: NavAnsattBehandleStrengtFortroligBrukerePolicy,
	private val navAnsattBehandleFortroligBrukerePolicy: NavAnsattBehandleFortroligBrukerePolicy,
	private val navAnsattTiltangTilEnhetMedSperrePolicy: NavAnsattTilgangTilNavEnhetMedSperrePolicy,
	private val navAnsattBehandleSkjermedePersonerPolicy: NavAnsattBehandleSkjermedePersonerPolicy,
	private val navAnsattTilgangTilModiaAdminPolicy: NavAnsattTilgangTilModiaAdminPolicy,
	private val timer: Timer,
	private val toggleProvider: ToggleProvider,
) {
	fun evaluate(input: PolicyInput): PolicyResult {
		return when (input) {
			is NavAnsattTilgangTilEksternBrukerPolicy.Input -> evaluateWithName(input, navAnsattTilgangTilEksternBrukerPolicy)
			is NavAnsattTilgangTilModiaPolicy.Input -> evaluateWithName(input, navAnsattTilgangTilModiaPolicy)
			is EksternBrukerTilgangTilEksternBrukerPolicy.Input -> evaluateWithName(input, eksternBrukerTilgangTilEksternBrukerPolicy)
			is NavAnsattTilgangTilNavEnhetPolicy.Input -> evaluateWithName(input, navAnsattTilgangTilNavEnhetPolicy)
			is NavAnsattBehandleFortroligBrukerePolicy.Input -> evaluateWithName(input, navAnsattBehandleFortroligBrukerePolicy)
			is NavAnsattBehandleStrengtFortroligBrukerePolicy.Input -> evaluateWithName(input, navAnsattBehandleStrengtFortroligBrukerePolicy)
			is NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input -> evaluateWithName(input, navAnsattTiltangTilEnhetMedSperrePolicy)
			is NavAnsattBehandleSkjermedePersonerPolicy.Input -> evaluateWithName(input, navAnsattBehandleSkjermedePersonerPolicy)
			is NavAnsattTilgangTilModiaAdminPolicy.Input -> evaluateWithName(input, navAnsattTilgangTilModiaAdminPolicy)
			else -> throw PolicyNotImplementedException("Håndtering av policy ${input.javaClass.canonicalName} er ikke implementert")
		}
	}


	private fun <I : PolicyInput> evaluateWithName(input: I, policy: Policy<I>): PolicyResult {
		return timer.measure("app.poao-tilgang.policy.evaluate", "policy", policy.name, "brukAbacDesision", toggleProvider.brukAbacDecision().toString()) {
			val decision = policy.evaluate(input)
			PolicyResult(policy.name, decision)
		}

	}
}
