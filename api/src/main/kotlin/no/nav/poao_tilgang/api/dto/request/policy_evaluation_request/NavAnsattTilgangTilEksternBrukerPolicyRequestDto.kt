package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto
import java.util.UUID

class NavAnsattTilgangTilEksternBrukerPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2
}
