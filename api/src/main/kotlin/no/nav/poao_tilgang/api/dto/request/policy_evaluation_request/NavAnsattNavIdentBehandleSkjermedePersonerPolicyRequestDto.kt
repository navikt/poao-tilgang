package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleSkjermedePersonerPolicyInputV1Dto
import java.util.UUID

class NavAnsattNavIdentBehandleSkjermedePersonerPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattNavIdentBehandleSkjermedePersonerPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_NAV_IDENT_BEHANDLE_SKJERMEDE_PERSONER_V1
}
