package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto
import java.util.UUID

class NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_NAV_IDENT_SKRIVETILGANG_TIL_EKSTERN_BRUKER_V1
}
