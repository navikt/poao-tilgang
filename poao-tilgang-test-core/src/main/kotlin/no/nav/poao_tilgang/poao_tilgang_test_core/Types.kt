package no.nav.poao_tilgang.poao_tilgang_test_core

import java.util.UUID

typealias AzureObjectId = UUID

typealias NavIdent = String

typealias NorskIdent = String

typealias NavEnhetId = String

typealias Diskresjonskode = no.nav.poao_tilgang.api.dto.response.Diskresjonskode

data class NavEnhetTilgang(
	val enhetId: NavEnhetId,
	val enhetNavn: String,
	val temaer: List<String>
)

data class AdGruppe(
	val id: UUID,
	val navn: String
)

data class AdGrupper(
	val fortroligAdresse: AdGruppe,
	val strengtFortroligAdresse: AdGruppe,
	val modiaAdmin: AdGruppe,
	val modiaOppfolging: AdGruppe,
	val modiaGenerell: AdGruppe,
	val gosysNasjonal: AdGruppe,
	val gosysUtvidbarTilNasjonal: AdGruppe,
	val syfoSensitiv: AdGruppe,
	val egneAnsatte: AdGruppe,
	val aktivitetsplanKvp: AdGruppe
)

object AdGruppeNavn {
	const val MODIA_ADMIN = "0000-GA-Modia_Admin"
	const val MODIA_OPPFOLGING = "0000-GA-Modia-Oppfolging"
	const val MODIA_GENERELL = "0000-GA-BD06_ModiaGenerellTilgang"
	const val STRENGT_FORTROLIG_ADRESSE = "0000-GA-Strengt_Fortrolig_Adresse"
	const val FORTROLIG_ADRESSE = "0000-GA-Fortrolig_Adresse"
	const val GOSYS_NASJONAL = "0000-GA-GOSYS_NASJONAL"
	const val GOSYS_UTVIDBAR_TIL_NASJONAL = "0000-GA-GOSYS_UTVIDBAR_TIL_NASJONAL"
	const val SYFO_SENSITIV = "0000-GA-SYFO-SENSITIV"
	const val EGNE_ANSATTE = "0000-GA-Egne_ansatte"
	const val AKTIVITETSPLAN_KVP = "0000-GA-aktivitesplan_kvp"
	const val ENHET_PREFIKS = "0000-GA-ENHET_"
}
