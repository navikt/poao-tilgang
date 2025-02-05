package no.nav.poao_tilgang.api.dto.response

data class TilgangsattributterResponse(
	/* Standard enhet fåes ved å mappe fra gt + diskresjonsgkode + skjermet -> enhet via norg sitt nav-kontor endepunkt
	* Standard som i IKKE overstyrt. */
	val standardEnhet: String?,
	val skjermet: Boolean,
	val diskresjonskode: Diskresjonskode?,
)

enum class Diskresjonskode {
	STRENGT_FORTROLIG_UTLAND, // kode 19
	STRENGT_FORTROLIG, // kode 6
	FORTROLIG, // kode 7
	UGRADERT
}
