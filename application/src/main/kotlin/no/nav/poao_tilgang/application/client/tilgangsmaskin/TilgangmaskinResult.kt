package no.nav.poao_tilgang.application.client.tilgangsmaskin

sealed class TilgangmaskinResult {

	object Godkjent : TilgangmaskinResult()

	data class Avvist(
		val type: String,
		val title: Avvisningskode,
		val status: Int,
		val instance: String,
		val brukerIdent: String,
		val navIdent: String,
		val begrunnelse: String,
		val traceId: String,
		val kanOverstyres: Boolean
	) : TilgangmaskinResult()
}

enum class Avvisningskode {
	AVVIST_STRENGT_FORTROLIG_ADRESSE,
	AVVIST_STRENGT_FORTROLIG_UTLAND,
	AVVIST_AVDD,
	AVVIST_PERSON_UTLAND,
	AVVIST_SKJERMING,
	AVVIST_FORTROLIG_ADRESSE,
	AVVIST_UKJENT_BOSTED,
	AVVIST_GEOGRAFISK,
	AVVIST_HABILITET
}

