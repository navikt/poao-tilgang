package no.nav.poao_tilgang.api.dto.request.policy_input

import java.util.UUID

data class NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicyInputV1Dto(
	val navAnsattAzureId: UUID,
	val norskIdent: String
)
