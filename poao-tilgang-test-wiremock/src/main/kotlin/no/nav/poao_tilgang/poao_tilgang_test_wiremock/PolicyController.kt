package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import com.fasterxml.jackson.core.type.TypeReference
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformerV2
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.EvaluatePoliciesResponse
import no.nav.poao_tilgang.api.dto.response.PolicyEvaluationResultDto
import no.nav.poao_tilgang.api_core_mapper.ApiCoreMapper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.poao_tilgang_test_core.Policies

class PolicyController(val policies: Policies, baspath: String) : ResponseDefinitionTransformerV2 {
	val apiCoreMapper = ApiCoreMapper(policies.providers.adGruppeProvider)
	val path = "$baspath/api/v1/policy/evaluate"
	override fun getName() = "policyController"
	override fun applyGlobally() = false

	override fun transform(
		serveEvent: ServeEvent?
	): ResponseDefinition {
		val bodyAsString = serveEvent?.request?.bodyAsString
		val readValue = WiremockClientObjectMapper.objectMapper.readValue(
			bodyAsString,
			object : TypeReference<EvaluatePoliciesRequest>() {})

		val response = response(readValue)

		return ResponseDefinitionBuilder()
			.withHeader("Content-Type", "application/json")
			.withStatus(200)
			.withBody(WiremockClientObjectMapper.objectMapper.writeValueAsString(response))
			.build()

	}

	private fun response(requestDto: EvaluatePoliciesRequest): EvaluatePoliciesResponse {
		val a = requestDto.requests.map {
			val kake = apiCoreMapper.mapToPolicyInput(it.policyInput)
			val evaluate = policies.policyResolver.evaluate(kake)
			val value = evaluate.decision.toDecisionDto()

			PolicyEvaluationResultDto(it.requestId,  value)
		}
		return EvaluatePoliciesResponse(a)
	}
}

fun Decision.toDecisionDto(): DecisionDto {
	return when (this) {
		is Decision.Permit -> DecisionDto(
			DecisionType.PERMIT,
			null, null
		)
		is Decision.Deny -> DecisionDto(
			DecisionType.DENY,
			this.message,
			this.reason.name,
		)
	}
}
