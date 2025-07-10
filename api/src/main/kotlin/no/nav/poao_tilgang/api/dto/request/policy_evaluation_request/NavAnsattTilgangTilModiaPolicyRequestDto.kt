package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilModiaPolicyInputV1Dto
import java.util.*

class NavAnsattTilgangTilModiaPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattTilgangTilModiaPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_V1
}
