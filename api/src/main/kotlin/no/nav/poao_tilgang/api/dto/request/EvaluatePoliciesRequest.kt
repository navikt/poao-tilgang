package no.nav.poao_tilgang.api.dto.request

import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.PolicyEvaluationRequestDto

data class EvaluatePoliciesRequest (
	val requests: List<PolicyEvaluationRequestDto>
): Request
