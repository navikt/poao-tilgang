package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattBehandleFortroligBrukerePolicyInputV1Dto
import java.util.UUID

class NavAnsattBehandleFortroligBrukerePolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattBehandleFortroligBrukerePolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1
}
