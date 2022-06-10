package no.nav.poao_tilgang.domain

import java.util.*

typealias AdGruppeNavn = String

data class AdGruppe(
	val id: UUID,
	val name: AdGruppeNavn
)
