package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentTilgangTilNavEnhetPolicyInputV1Dto
import java.util.UUID

class NavAnsattNavIdentTilgangTilNavEnhetPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattNavIdentTilgangTilNavEnhetPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_NAV_IDENT_TILGANG_TIL_NAV_ENHET_V1
}
