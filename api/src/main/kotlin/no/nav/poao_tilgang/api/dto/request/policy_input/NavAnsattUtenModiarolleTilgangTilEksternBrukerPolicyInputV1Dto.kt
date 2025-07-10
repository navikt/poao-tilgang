package no.nav.poao_tilgang.api.dto.request.policy_input

import java.util.UUID

data class NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInputV1Dto(
	val navIdent: UUID,
	val norskIdent: String
): RequestPolicyInput()
