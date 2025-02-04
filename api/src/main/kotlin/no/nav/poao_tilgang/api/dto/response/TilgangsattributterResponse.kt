package no.nav.poao_tilgang.api.dto.response

import java.util.*
data class TilgangsattributterResponse(
	val geografiskTilknytning: String?,
	val skjermet: Boolean,
	val diskresjonskode: Diskresjonskode?,
)

enum class Diskresjonskode {
	STRENGT_FORTROLIG_UTLAND, // kode 19
	STRENGT_FORTROLIG, // kode 6
	FORTROLIG, // kode 7
	UGRADERT
}
