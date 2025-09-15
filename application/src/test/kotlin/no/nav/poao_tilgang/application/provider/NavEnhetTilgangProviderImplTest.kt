package no.nav.poao_tilgang.application.provider

import io.getunleash.DefaultUnleash
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.types.identer.NavIdent
import no.nav.poao_tilgang.application.client.axsys.AxsysClient
import no.nav.poao_tilgang.application.utils.HENT_ENHETSTILGANGER_FRA_AD_OG_LOGG_DIFF
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class NavEnhetTilgangProviderImplTest {

	@Test
	fun `skal sammenligne enhettilganger`() {
		val axsysClient = mockk<AxsysClient>(relaxed = true)
		val adGruppeProvider = mockk<AdGruppeProvider>(relaxed = true)
		val defaultUnleash = mockk<DefaultUnleash>(relaxed = true)
		every { defaultUnleash.isEnabled(HENT_ENHETSTILGANGER_FRA_AD_OG_LOGG_DIFF) } returns true
		every { adGruppeProvider.hentAzureIdMedNavIdent("Z999999") } returns UUID.fromString("13c9c66f-8381-4d17-b2b4-625401f26083")
		val navEnhetTilgangProvider = NavEnhetTilgangProviderImpl(axsysClient, adGruppeProvider, defaultUnleash)

		navEnhetTilgangProvider.hentEnhetTilganger(NavIdent.of("Z999999").toString())

		verify(exactly = 1) { axsysClient.hentTilganger("Z999999") }
		verify(exactly = 1) { adGruppeProvider.hentAzureIdMedNavIdent("Z999999") }
		verify(exactly = 1) { adGruppeProvider.hentAdGrupper(UUID.fromString("13c9c66f-8381-4d17-b2b4-625401f26083")) }
	}

	@Test
	fun `skal ikke sammenligne enhettilganger`() {
		val axsysClient = mockk<AxsysClient>(relaxed = true)
		val adGruppeProvider = mockk<AdGruppeProvider>(relaxed = true)
		val defaultUnleash = mockk<DefaultUnleash>(relaxed = true)
		every { defaultUnleash.isEnabled(HENT_ENHETSTILGANGER_FRA_AD_OG_LOGG_DIFF) } returns false
		val navEnhetTilgangProvider = NavEnhetTilgangProviderImpl(axsysClient, adGruppeProvider, defaultUnleash)

		navEnhetTilgangProvider.hentEnhetTilganger(NavIdent.of("Z999999").toString())

		verify(exactly = 1) { axsysClient.hentTilganger("Z999999") }
		verify { adGruppeProvider wasNot Called }
	}

	@ParameterizedTest
	@ValueSource(
		strings = [
			"0000-GA-ENHET_1234",
			"0000-ga-enhet_1234"
		]
	)
	fun `skal returnere validert NavEnhetId`(gyldigAdGruppe: String) {
		val validertNavEnhetId = NavEnhetTilgangProviderImpl.tilNavEnhetId(gyldigAdGruppe)
		assertEquals("1234", validertNavEnhetId)
	}

	@ParameterizedTest
	@ValueSource(
		strings = [
			"0000-GA-ENHET_ABCD",
			"1234-GA-ENHET_1234",
			"0000-GA-ENHET_12345"
		]
	)
	fun `skal feile validering`(ugyldigAdGruppe: String) {
		assertThrows<NavEnhetTilgangProviderImpl.NavEnhetIdValideringException> {
			NavEnhetTilgangProviderImpl.tilNavEnhetId(
				ugyldigAdGruppe
			)
		}
	}
}
