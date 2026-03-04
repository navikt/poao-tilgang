package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyImplTest {
	private val adGruppeProvider = mockk<AdGruppeProvider>()
	private val tilgangsmaskinProvider = mockk<TilgangmaskinProvider>()

	private val policy = NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyImpl (
		adGruppeProvider,
		tilgangsmaskinProvider,
	)

	private val navAnsattAzureId = UUID.randomUUID()

	@Test
	fun `skal returnere permit hvis bruker ikke har adressebeskyttelse`() {
		val norskIdent = "1235645644"
		val navIdent = "A123123"

		every { adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId) } returns navIdent
		every { tilgangsmaskinProvider.evaluerKompletteRegler(norskIdent, navIdent) } returns Decision.Permit

		val decision = policy.evaluate(NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Permit
	}

	@Test
	fun `skal returnere Deny hvis navveileder ikke har tilgang til enhet`() {
		val norskIdent = "1235645644"
		val navIdent = "A123123"

		every { adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId) } returns navIdent
		every { tilgangsmaskinProvider.evaluerKompletteRegler(norskIdent, navIdent) } returns Decision.Deny("",
			DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET)

		val decision = policy.evaluate(NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Deny("", DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET)
	}

	@Test
	fun `skal returnere Deny hvis navveileder ikke har tilgang til adressebeskyttelse og bruker er adressebeskyttet`() {
		val norskIdent = "1235645644"
		val navIdent = "A123123"

		every { adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId) } returns navIdent
		every { tilgangsmaskinProvider.evaluerKompletteRegler(norskIdent, navIdent) } returns Decision.Deny("ikke greit",
			DecisionDenyReason.IKKE_TILGANG_TIL_STRENGT_FORTROLIG_BRUKER)

		val decision = policy.evaluate(NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input(navAnsattAzureId, norskIdent))

		decision shouldBe Decision.Deny("ikke greit", DecisionDenyReason.IKKE_TILGANG_TIL_STRENGT_FORTROLIG_BRUKER)
	}
}
