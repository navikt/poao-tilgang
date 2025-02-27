package no.nav.poao_tilgang.application.client.pdl_pip

interface PdlPipClient {
	fun hentBrukerInfo(brukerIdent: String): BrukerInfo?
}

enum class IdentGruppe {
	AKTORID,
	NPID,
	FOLKEREGISTERIDENT,
}

data class Ident(
	val ident: String,
	val historisk: Boolean,
	val gruppe: IdentGruppe
)

data class Identer(
	val identer: List<Ident>
)

data class BrukerInfo(
	val person: Person,
	val identer: Identer,
	val geografiskTilknytning: GeografiskTilknytning?
)

enum class Gradering {
	STRENGT_FORTROLIG,
	FORTROLIG,
	STRENGT_FORTROLIG_UTLAND,
	UGRADERT
}

data class Adressebeskyttelse(
	val gradering: Gradering
)

data class GeografiskTilknytning(
	val gtType: GeografiskTilknytningType,
	val gtKommune: String? = null,
	val gtBydel: String? = null,
)

data class Person(
	val adressebeskyttelse: List<Adressebeskyttelse>?
)

enum class GeografiskTilknytningType {
	BYDEL, KOMMUNE, UDEFINERT, UTLAND
}
