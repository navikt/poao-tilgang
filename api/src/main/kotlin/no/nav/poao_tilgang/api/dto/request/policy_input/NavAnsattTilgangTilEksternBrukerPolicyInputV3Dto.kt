package no.nav.poao_tilgang.api.dto.request.policy_input

import no.nav.poao_tilgang.api.dto.request.TilgangType
import java.util.*

data class NavAnsattTilgangTilEksternBrukerPolicyInputV3Dto(
	val navAnsattNavIdent: String,
	val tilgangType: TilgangType,
	val norskIdent: String
)
