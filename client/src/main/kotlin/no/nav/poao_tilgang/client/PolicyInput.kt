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

data class NavAnsattTilgangTilModiaPolicyInput(
	val navAnsattAzureId: UUID
) : PolicyInput()

data class EksternBrukerTilgangTilEksternBrukerPolicyInput(
	val rekvirentNorskIdent: String, // Den som ber om tilgang
	val ressursNorskIdent: String // Den som bes tilgang om
) : PolicyInput()
