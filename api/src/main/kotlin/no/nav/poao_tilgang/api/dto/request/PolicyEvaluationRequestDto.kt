package no.nav.poao_tilgang.api.dto.request

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class PolicyEvaluationRequestDto(
    val requestId: UUID,
    val policyInput: JsonNode,
    val policyName: String
)
