package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInputV1Dto
import java.util.UUID

class NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_NAV_IDENT_LESETILGANG_TIL_EKSTERN_BRUKER_V1
}
