package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto
import java.util.UUID

class EksternBrukerTilgangTilEksternBrukerPolicyRequestDto(
	requestId: UUID,
	override val policyInput: EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1
}
