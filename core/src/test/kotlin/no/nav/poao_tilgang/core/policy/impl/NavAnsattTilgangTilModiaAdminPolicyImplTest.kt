package no.nav.poao_tilgang.core.policy.impl

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaAdminPolicy
import no.nav.poao_tilgang.core.policy.test_utils.TestAdGrupper
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class NavAnsattTilgangTilModiaAdminPolicyImplTest {

	private val adGruppeProvider = mockk<AdGruppeProvider>()
	private lateinit var policy: NavAnsattTilgangTilModiaAdminPolicyImpl
	private val navAnsattAzureId = UUID.randomUUID()

	@BeforeEach
	internal fun setUp() {
		every {
			adGruppeProvider.hentTilgjengeligeAdGrupper()
		} returns TestAdGrupper.testAdGrupper

		policy = NavAnsattTilgangTilModiaAdminPolicyImpl(adGruppeProvider)
	}

	@Test
	fun `should return permit if access to 0000-GA-Modia_AdminTilgang`() {
		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			TestAdGrupper.testAdGrupper.modiaAdmin,
			TestAdGrupper.randomGruppe
		)

		policy.evaluate(NavAnsattTilgangTilModiaAdminPolicy.Input(navAnsattAzureId)) shouldBe Decision.Permit
	}

	@Test
	fun `should return deny if missing access to ad groups`() {

		every {
			adGruppeProvider.hentAdGrupper(navAnsattAzureId)
		} returns listOf(
			TestAdGrupper.randomGruppe
		)

		val decision = policy.evaluate(NavAnsattTilgangTilModiaAdminPolicy.Input(navAnsattAzureId))

		decision.type shouldBe Decision.Type.DENY

		if (decision is Decision.Deny) {
			decision.message shouldBe "Har ikke tilgang til rollen 0000-GA-Modia_Admin"
			decision.reason shouldBe DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		}
	}
}
