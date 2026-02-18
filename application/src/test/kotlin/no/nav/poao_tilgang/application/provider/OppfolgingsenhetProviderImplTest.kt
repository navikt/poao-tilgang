package no.nav.poao_tilgang.application.provider

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.client.ao_oppfolgingskontor.AoKontorClient
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import org.junit.jupiter.api.Test

class OppfolgingsenhetProviderImplTest {

	val aoKontorClient = mockk<AoKontorClient>()
	val oppfolgingsenhetProvider = OppfolgingsenhetProviderImpl(aoKontorClient)

	@Test
	fun `Skal cache resultat ved oppslag og bruke resultat som fallback ved feil`() {
		val navEnhetId: NavEnhetId = "0123"
		val norskIdent: NorskIdent = "012345678901"
		val personRequest = Fnr(norskIdent)
		every { aoKontorClient.hentBrukerOppfolgingsenhetId(personRequest) } returns navEnhetId
		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent) shouldBe navEnhetId
		every { aoKontorClient.hentBrukerOppfolgingsenhetId(personRequest) } throws RuntimeException("Klarte ikke å hente status fra veilarbarena. Status: 500")
		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent) shouldBe navEnhetId
	}

	@Test
	fun `Skal ikke cache null-resultat og fallback ved feil skal propagere feil`() {
		val norskIdent: NorskIdent = "012345678901"
		val personRequest = Fnr(norskIdent)
		every { aoKontorClient.hentBrukerOppfolgingsenhetId(personRequest) } returns null
		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent) shouldBe null
		every { aoKontorClient.hentBrukerOppfolgingsenhetId(personRequest) } throws RuntimeException("Klarte ikke å hente status fra veilarbarena. Status: 500")
		val exception = shouldThrow<RuntimeException> { oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent) }
		exception.message shouldBe "Klarte ikke å hente status fra veilarbarena. Status: 500"
	}

}
