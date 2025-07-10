package no.nav.poao_tilgang.api.dto.request.policy_evaluation_request

import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInputV1Dto
import java.util.UUID

class NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyRequestDto(
	requestId: UUID,
	override val policyInput: NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInputV1Dto
) : PolicyEvaluationRequestDto(requestId) {
	override val policyId: PolicyId = PolicyId.NAV_ANSATT_UTEN_MODIAROLLE_TILGANG_TIL_EKSTERN_BRUKER_V1
}
