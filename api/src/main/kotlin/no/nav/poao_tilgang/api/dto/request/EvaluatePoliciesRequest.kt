package no.nav.poao_tilgang.api.dto.request

import no.nav.poao_tilgang.api.dto.request.policy_input.PolicyInput

data class EvaluatePoliciesRequest<I: PolicyInput> (
	val requests: List<PolicyEvaluationRequestDto<I>>
): Request
