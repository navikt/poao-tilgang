package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.RequestPolicyInput
import java.util.UUID

sealed class PolicyEvaluationRequestDto(
    val requestId: UUID,
) {
	abstract val policyId: PolicyId
	abstract val policyInput: RequestPolicyInput
}
