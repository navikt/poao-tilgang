package no.nav.poao_tilgang.api.dto.request.policy_input

import java.util.*

data class NavAnsattTilgangTilModiaAdminPolicyInputV1Dto (
	val navAnsattAzureId: UUID
): RequestPolicyInput()
