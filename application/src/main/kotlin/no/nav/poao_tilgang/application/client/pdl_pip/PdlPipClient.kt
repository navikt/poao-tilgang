package no.nav.poao_tilgang.application.client.pdl_pip

interface PdlPipClient {
	fun hentBrukerInfo(brukerIdent: String): BrukerInfo?
}

data class BrukerInfo(
	val person: Person,
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
