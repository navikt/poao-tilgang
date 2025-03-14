package no.nav.poao_tilgang.client

import java.util.*

enum class TilgangType {
	LESE, SKRIVE
}

sealed class PolicyInput

data class NavAnsattTilgangTilEksternBrukerPolicyInput(
	val navAnsattAzureId: UUID,
	val tilgangType: TilgangType,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInput(
	val navIdent: String,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInput(
	val navIdent: String,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattTilgangTilModiaPolicyInput(
	val navAnsattAzureId: UUID
) : PolicyInput()

data class NavAnsattTilgangTilModiaAdminPolicyInput(
	val navAnsattAzureId: UUID
) : PolicyInput()

data class EksternBrukerTilgangTilEksternBrukerPolicyInput(
	val rekvirentNorskIdent: String, // Den som ber om tilgang
	val ressursNorskIdent: String // Den som bes tilgang om
) : PolicyInput()

data class NavAnsattTilgangTilNavEnhetPolicyInput(
	val navAnsattAzureId: UUID,
	val navEnhetId: String
) : PolicyInput()

data class NavAnsattNavIdentTilgangTilNavEnhetPolicyInput(
	val navIdent: String,
	val navEnhetId: String
) : PolicyInput()

data class NavAnsattBehandleStrengtFortroligBrukerePolicyInput(
	val navAnsattAzureId: UUID,
) : PolicyInput()

data class NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInput(
	val navIdent: String,
) : PolicyInput()

data class NavAnsattBehandleFortroligBrukerePolicyInput(
	val navAnsattAzureId: UUID,
) : PolicyInput()

data class NavAnsattNavIdentBehandleFortroligBrukerePolicyInput(
	val navIdent: String,
) : PolicyInput()

data class NavAnsattTilgangTilNavEnhetMedSperrePolicyInput(
	val navAnsattAzureId: UUID,
	val navEnhetId: String
) : PolicyInput()

data class NavAnsattBehandleSkjermedePersonerPolicyInput(
	val navAnsattAzureId: UUID,
) : PolicyInput()

data class NavAnsattNavIdentBehandleSkjermedePersonerPolicyInput(
	val navIdent: String,
) : PolicyInput()

data class NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInput (
	val navIdent: UUID,
	val norskIdent: String
) : PolicyInput()
