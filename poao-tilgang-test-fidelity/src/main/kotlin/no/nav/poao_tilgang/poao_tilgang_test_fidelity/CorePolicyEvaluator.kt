package no.nav.poao_tilgang.poao_tilgang_test_fidelity

import com.fasterxml.jackson.databind.JsonNode
import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType
import no.nav.poao_tilgang.api_core_mapper.ApiCoreMapper
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.poao_tilgang_test_core.NavContext
import no.nav.poao_tilgang.poao_tilgang_test_core.PolicyEvaluator
import no.nav.poao_tilgang.poao_tilgang_test_core.PolicyEvaluatorFactory

class CorePolicyEvaluator(
	private val policies: Policies,
	private val apiCoreMapper: ApiCoreMapper
) : PolicyEvaluator {
	override fun evaluate(policyId: PolicyId, policyInput: JsonNode): DecisionDto {
		val policy = apiCoreMapper.mapToPolicyInput(policyId, policyInput)
		val decision = policies.policyResolver.evaluate(policy).decision
		return decision.toDecisionDto()
	}
}

class CorePolicyEvaluatorFactory : PolicyEvaluatorFactory {
	override fun create(navContext: NavContext): PolicyEvaluator {
		val policies = Policies(navContext)
		return CorePolicyEvaluator(
			policies = policies,
			apiCoreMapper = ApiCoreMapper(policies.providers.adGruppeProvider)
		)
	}
}

private fun Decision.toDecisionDto(): DecisionDto {
	return when (this) {
		is Decision.Permit -> DecisionDto(
			type = DecisionType.PERMIT,
			message = null,
			reason = null
		)
		is Decision.Deny -> DecisionDto(
			type = DecisionType.DENY,
			message = message,
			reason = reason.name
		)
	}
}

