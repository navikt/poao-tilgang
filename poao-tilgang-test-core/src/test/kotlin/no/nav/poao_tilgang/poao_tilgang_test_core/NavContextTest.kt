package no.nav.poao_tilgang.poao_tilgang_test_core

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerPolicy
import org.junit.jupiter.api.Test

class NavContextTest {
	val navContext = NavContext()
	val providers = Policies(navContext)
	val policyResolver = providers.policyResolver

	@Test
	fun `veileder skal ha tilgang til bruker`() {
		val nyEksternBruker = navContext.privatBrukere.ny()
		val veileder = navContext.navAnsatt.nyFor(nyEksternBruker)

		val input = NavAnsattTilgangTilEksternBrukerPolicy.Input(
			navAnsattAzureId = veileder.azureObjectId,
			tilgangType = TilgangType.SKRIVE,
			norskIdent = nyEksternBruker.norskIdent
		)

		val result = policyResolver.evaluate(input)

		result.decision shouldBe Decision.Permit
	}

	@Test
	fun `veileder skal ha tilgang til bruker når bruker opprettes med fnr`() {
		val nyEksternBruker = navContext.privatBrukere.ny("10048835000")
		val veileder = navContext.navAnsatt.nyFor(nyEksternBruker)

		val input = NavAnsattTilgangTilEksternBrukerPolicy.Input(
			navAnsattAzureId = veileder.azureObjectId,
			tilgangType = TilgangType.SKRIVE,
			norskIdent = nyEksternBruker.norskIdent
		)

		val result = policyResolver.evaluate(input)

		result.decision shouldBe Decision.Permit
	}

	@Test
	fun `nks skal ha tilgang til bruker`() {
		val nyEksternBruker = navContext.privatBrukere.ny()
		val nks = navContext.navAnsatt.nyNksAnsatt()

		val input = NavAnsattTilgangTilEksternBrukerPolicy.Input(
			navAnsattAzureId = nks.azureObjectId,
			tilgangType = TilgangType.SKRIVE,
			norskIdent = nyEksternBruker.norskIdent
		)

		val result = policyResolver.evaluate(input)

		result.decision shouldBe Decision.Permit
	}

	@Test
	fun `tilgangsmaskinprodvider - brukers veileder skal ha tilgang til bruker`() {
		val nyEksternBruker = navContext.privatBrukere.ny()
		val veileder = navContext.navAnsatt.nyFor(nyEksternBruker)
		val tilgangsmaskinProvider = TilgangmaskinProviderImpl(navContext)

		val result = tilgangsmaskinProvider.evaluerKompletteRegler(nyEksternBruker.norskIdent, veileder.navIdent)

		result.isPermit shouldBe true
	}

	@Test
	fun `tilgangsmaskinprodvider - annen veileder har ikke tilgang til bruker`() {
		val nyEksternBruker = navContext.privatBrukere.ny()
		val annenEksternBruker = navContext.privatBrukere.ny()
		val veileder = navContext.navAnsatt.nyFor(annenEksternBruker)
		val tilgangsmaskinProvider = TilgangmaskinProviderImpl(navContext)

		val result = tilgangsmaskinProvider.evaluerKompletteRegler(nyEksternBruker.norskIdent, veileder.navIdent)

		result.isDeny shouldBe true
	}
}
