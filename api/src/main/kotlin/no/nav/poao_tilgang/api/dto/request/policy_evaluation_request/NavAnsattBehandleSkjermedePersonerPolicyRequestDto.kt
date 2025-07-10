package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto
import java.util.*

class NavAnsattBehandleSkjermedePersonerPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER_V1
}
