package no.nav.poao_tilgang.core.provider

interface ToggleProvider {
	fun brukAbacDecision(): Boolean
	fun logAbacDecisionDiff(): Boolean
	fun brukEntraIdSomFasitForEnhetstilgang(): Boolean
}
