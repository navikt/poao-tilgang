package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import com.fasterxml.jackson.core.type.TypeReference
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api.dto.response.EvaluatePoliciesResponse
import no.nav.poao_tilgang.api.dto.response.PolicyEvaluationResultDto
import no.nav.poao_tilgang.api_core_mapper.ApiCoreMapper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.poao_tilgang_test_core.Policies

class PolicyController(val policies: Policies, baspath: String) : ResponseDefinitionTransformer() {
	val apiCoreMapper = ApiCoreMapper(policies.providers.adGruppeProvider)

	val path = "$baspath/api/v1/policy/evaluate"
	override fun getName(): String {
		return "policyController"
	}

	override fun transform(
		request: Request,
		responseDefinition: ResponseDefinition,
		files: FileSource?,
		parameters: Parameters?
	): ResponseDefinition {
		val bodyAsString = request.bodyAsString
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

	override fun applyGlobally(): Boolean {
		return false
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
