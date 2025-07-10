package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilNavEnhetPolicyInputV1Dto
import java.util.UUID

class NavAnsattTilgangTilNavEnhetPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattTilgangTilNavEnhetPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1
}
