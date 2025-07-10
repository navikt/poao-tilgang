package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInputV1Dto
import java.util.UUID

class NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_NAV_IDENT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1
}
