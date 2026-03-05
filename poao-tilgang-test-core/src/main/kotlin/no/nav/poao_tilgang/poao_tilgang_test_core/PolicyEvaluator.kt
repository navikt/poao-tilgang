package no.nav.poao_tilgang.poao_tilgang_test_core

import com.fasterxml.jackson.databind.JsonNode
import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.response.DecisionDto

interface PolicyEvaluator {
	fun evaluate(policyId: PolicyId, policyInput: JsonNode): DecisionDto
}

interface PolicyEvaluatorFactory {
	fun create(navContext: NavContext): PolicyEvaluator
}

object PolicyEvaluators {
	fun load(navContext: NavContext): PolicyEvaluator {
		val loader = java.util.ServiceLoader.load(PolicyEvaluatorFactory::class.java)
		val factory = loader.iterator().asSequence().firstOrNull()
		return factory?.create(navContext)
			?: error("No PolicyEvaluatorFactory found. Add poao-tilgang-test-fidelity to the classpath.")
	}
}

