package no.nav.poao_tilgang.application.provider

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.client.veilarbarena.PersonRequest
import no.nav.poao_tilgang.application.client.veilarbarena.VeilarbarenaClient
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import org.junit.jupiter.api.Test

class OppfolgingsenhetProviderImplTest {

	val veilarbarenaClient = mockk<VeilarbarenaClient>()
	val oppfolgingsenhetProvider = OppfolgingsenhetProviderImpl(veilarbarenaClient)

	@Test
	fun `Skal cache resultat ved oppslag og bruke resultat som fallback ved feil`() {
		val navEnhetId: NavEnhetId = "0123"
		val norskIdent: NorskIdent = "012345678901"
		val personRequest = PersonRequest(Fnr(norskIdent))
		every { veilarbarenaClient.hentBrukerOppfolgingsenhetId(personRequest) } returns navEnhetId
		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent) shouldBe navEnhetId
		every { veilarbarenaClient.hentBrukerOppfolgingsenhetId(personRequest) } throws RuntimeException("Klarte ikke å hente status fra veilarbarena. Status: 500")
		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent) shouldBe navEnhetId
	}

	@Test
	fun `Skal ikke cache null-resultat og fallback ved feil skal propagere feil`() {
		val norskIdent: NorskIdent = "012345678901"
		val personRequest = PersonRequest(Fnr(norskIdent))
		every { veilarbarenaClient.hentBrukerOppfolgingsenhetId(personRequest) } returns null
		oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent) shouldBe null
		every { veilarbarenaClient.hentBrukerOppfolgingsenhetId(personRequest) } throws RuntimeException("Klarte ikke å hente status fra veilarbarena. Status: 500")
		val exception = shouldThrow<RuntimeException> { oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent) }
		exception.message shouldBe "Klarte ikke å hente status fra veilarbarena. Status: 500"
	}

}
