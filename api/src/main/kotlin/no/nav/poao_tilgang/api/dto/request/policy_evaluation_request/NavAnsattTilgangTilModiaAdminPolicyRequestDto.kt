package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilModiaAdminPolicyInputV1Dto
import java.util.*

class NavAnsattTilgangTilModiaAdminPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattTilgangTilModiaAdminPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_ADMIN_V1
}
