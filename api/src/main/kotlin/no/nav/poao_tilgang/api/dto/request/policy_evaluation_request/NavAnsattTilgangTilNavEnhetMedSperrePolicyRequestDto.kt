package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto
import java.util.*

class NavAnsattTilgangTilNavEnhetMedSperrePolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1
}
