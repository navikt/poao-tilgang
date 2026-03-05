package no.nav.poao_tilgang.poao_tilgang_test_core

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.poao_tilgang.client.Decision
import no.nav.poao_tilgang.client.NavAnsattTilgangTilEksternBrukerPolicyInput
import no.nav.poao_tilgang.client.PoaoTilgangMockClient
import no.nav.poao_tilgang.client.TilgangType
import org.junit.jupiter.api.Test

class PolicyEvaluatorServiceLoaderTest {

	@Test
	fun `ServiceLoader finner CorePolicyEvaluatorFactory fra fidelity-modulen`() {
		val loader = java.util.ServiceLoader.load(PolicyEvaluatorFactory::class.java)
		val factory = loader.iterator().asSequence().firstOrNull()
		factory shouldNotBe null
	}

	@Test
	fun `PolicyEvaluators load returnerer fungerende evaluator`() {
		val navContext = NavContext()
		val evaluator = PolicyEvaluators.load(navContext)
		evaluator shouldNotBe null
	}

	@Test
	fun `full fidelity evaluering via mock client gir korrekt resultat`() {
		val navContext = NavContext()
		val client = PoaoTilgangMockClient(navContext)

		val bruker = navContext.privatBrukere.ny()
		val ansatt = navContext.navAnsatt.nyFor(bruker)

		val result = client.evaluatePolicy(
			NavAnsattTilgangTilEksternBrukerPolicyInput(
				navAnsattAzureId = ansatt.azureObjectId,
				tilgangType = TilgangType.SKRIVE,
				norskIdent = bruker.norskIdent
			)
		).get()!!

		result shouldBe Decision.Permit
	}
}

