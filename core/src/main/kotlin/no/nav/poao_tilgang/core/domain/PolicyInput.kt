package no.nav.poao_tilgang.core.domain

interface PolicyInput

interface PolicyInputWithNorskIdent : PolicyInput {
	val norskIdent: NorskIdent
	fun byttIdent(norskIdent: NorskIdent): PolicyInputWithNorskIdent
}
