package no.nav.poao_tilgang.core.domain

interface Policy<I : PolicyInput> {

	fun evaluate(input: I): Decision

}
