package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleFortroligBrukerePolicyInputV1Dto
import java.util.UUID

class NavAnsattNavIdentBehandleFortroligBrukerePolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattNavIdentBehandleFortroligBrukerePolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_NAV_IDENT_BEHANDLE_FORTROLIG_BRUKERE_V1
}
