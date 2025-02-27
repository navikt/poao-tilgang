package no.nav.poao_tilgang.core.domain

interface PolicyInput

interface PolicyInputWithNorskIdent : PolicyInput {
	val norskIdent: NorskIdent
	/* Copy fra kotlin sin data class er ikke tilgjengelig via interfaces, så lager den eksplisitt */
	fun withIdent(norskIdent: NorskIdent): PolicyInputWithNorskIdent
}
