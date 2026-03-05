package no.nav.poao_tilgang.application.provider

import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.application.client.pdl_pip.GeografiskTilknytningType
import no.nav.poao_tilgang.application.client.pdl_pip.Gradering
import no.nav.poao_tilgang.application.provider.GeografiskTilknyttetEnhetProviderImpl.Companion.DEFAULT_GT_IF_NO_GT_FOUND
import no.nav.poao_tilgang.application.test_util.IntegrationTest
import no.nav.poao_tilgang.application.test_util.TestDataGenerator
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GeografiskTilknyttetEnhetProviderImplIntegrationTest : IntegrationTest() {

	@Autowired
	lateinit var geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider

	private val navVikaFossen = "2103"

	@Test
	fun `henter tilhørende enhet basert på geografisk tilknyting kommune`() {
		val norskIdent = TestDataGenerator.norskIdent()
		val navEnhet = TestDataGenerator.navEnhetId()
		val gt = TestDataGenerator.geografiskTilknytning()
		mockPdlPipHttpServer.mockBrukerInfo(
			norskIdent = norskIdent, gtType = GeografiskTilknytningType.KOMMUNE, gtKommune = gt
		)
		mockNorgHttpServer.mockTilhorendeEnhet(geografiskTilknytning = gt, tilhorendeEnhet = navEnhet)

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknyttetEnhet(norskIdent) shouldBe navEnhet
	}

	@Test
	fun `henter tilhørende enhet basert på geografisk tilknyting bydel`() {
		val navEnhet = TestDataGenerator.navEnhetId()
		val gtBydel = TestDataGenerator.geografiskTilknytningBydel()
		val norskIdent = TestDataGenerator.norskIdent()
		mockPdlPipHttpServer.mockBrukerInfo(
			norskIdent = norskIdent, gtType = GeografiskTilknytningType.BYDEL, gtBydel = gtBydel
		)
		mockNorgHttpServer.mockTilhorendeEnhet(geografiskTilknytning = gtBydel, tilhorendeEnhet = navEnhet)

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknyttetEnhet(norskIdent) shouldBe navEnhet
	}

	@Test
	fun `kan ikke hente enhet basert på geografisk tilknyting utland`() {
		val norskIdent = TestDataGenerator.norskIdent()
		mockPdlPipHttpServer.mockBrukerInfo(
			norskIdent = norskIdent, gtType = GeografiskTilknytningType.UTLAND
		)

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknyttetEnhet(norskIdent) shouldBe null
	}

	@Test
	fun `hvis geografisk tilknyting utland skal likevel få vikafossen dersom diskreskjonskode STRENGT_FORTROLIG_UTLAND`() {
		val norskIdent = TestDataGenerator.norskIdent()
		mockPdlPipHttpServer.mockBrukerInfo(
			gradering = Gradering.STRENGT_FORTROLIG_UTLAND,
			norskIdent = norskIdent, gtType = GeografiskTilknytningType.UTLAND
		)

		mockNorgHttpServer.mockTilhorendeEnhet(DEFAULT_GT_IF_NO_GT_FOUND, tilhorendeEnhet = navVikaFossen, gradering = Diskresjonskode.STRENGT_FORTROLIG_UTLAND)

		// NB denne logikken fungerer kun på metoden som tar inn skjermet parameteret
		geografiskTilknyttetEnhetProvider.hentGeografiskTilknyttetEnhet(norskIdent, skjermet = false) shouldBe navVikaFossen
	}

	@Test
	fun `skal returnere null dersom tilhørende enhet for geografisk tilknyting ikke finnes`() {
		val norskIdent = TestDataGenerator.norskIdent()
		val gt = TestDataGenerator.geografiskTilknytning()
		mockPdlPipHttpServer.mockBrukerInfo(
			norskIdent = norskIdent, gtType = GeografiskTilknytningType.KOMMUNE, gtKommune = gt
		)

		mockNorgHttpServer.mockIngenTilhorendeEnhet(gt)

		geografiskTilknyttetEnhetProvider.hentGeografiskTilknyttetEnhet(norskIdent) shouldBe null
	}
}
