package no.nav.poao_tilgang.service

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.poao_tilgang.domain.AdGruppe
import no.nav.poao_tilgang.domain.Decision
import no.nav.poao_tilgang.domain.DecisionReasonType
import org.junit.jupiter.api.Test
import java.util.*

class TilgangTilModiaPolicyServiceTest {
	private val adGruppeService = mockk<AdGruppeService>()
	private val tilgangTilModiaPolicyService = TilgangTilModiaPolicyService(adGruppeService)

	private val ident = "Z999999"

	@Test
	internal fun `permit med modia-generell rolle`() {
		gittAdGrupper("0000-ga-bd06_modiagenerelltilgang")
		tilgangTilModiaPolicyService.sjekkTilgang(ident) shouldBe Decision.Permit
	}

	@Test
	internal fun `permit med modia-oppfolging rolle`() {
		gittAdGrupper("0000-ga-modia-oppfolging")
		tilgangTilModiaPolicyService.sjekkTilgang(ident) shouldBe Decision.Permit
	}

	@Test
	internal fun `permit med modia-syfo rolle`() {
		gittAdGrupper("0000-ga-syfo-sensitiv")
		tilgangTilModiaPolicyService.sjekkTilgang(ident) shouldBe Decision.Permit
	}

	@Test
	internal fun `permit med gyldig rolle og annen rolle`() {
		gittAdGrupper("0000-ga-bd06_modiagenerelltilgang", "annen-rolle")
		tilgangTilModiaPolicyService.sjekkTilgang(ident) shouldBe Decision.Permit
	}

	@Test
	internal fun `deny om rolle manger`() {
		gittAdGrupper("annen-rolle")
		tilgangTilModiaPolicyService.sjekkTilgang(ident) shouldBe Decision.Deny(
			"Veileder har ikke tilgang til Modia.", DecisionReasonType.IKKE_TILGANG_TIL_MODIA
		)
	}

	@Test
	internal fun `deny om ingen roller`() {
		gittAdGrupper()
		tilgangTilModiaPolicyService.sjekkTilgang(ident) shouldBe Decision.Deny(
			"Veileder har ikke tilgang til Modia.", DecisionReasonType.IKKE_TILGANG_TIL_MODIA
		)
	}

	private fun gittAdGrupper(vararg adGrupper: String) {
		every { adGruppeService.hentAdGrupper(ident) } returns adGrupper.map { AdGruppe(UUID.randomUUID(), it) }
	}
}
