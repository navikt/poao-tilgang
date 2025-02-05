package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.norg.NorgClient
import no.nav.poao_tilgang.application.client.pdl_pip.GeografiskTilknytning
import no.nav.poao_tilgang.application.client.pdl_pip.GeografiskTilknytningType
import no.nav.poao_tilgang.application.client.pdl_pip.PdlPipClient

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import org.springframework.stereotype.Component

@Component
class GeografiskTilknyttetEnhetProviderImpl(
	private val pdlpipClient: PdlPipClient,
	private val norgClient: NorgClient
) : GeografiskTilknyttetEnhetProvider {

	override fun hentGeografiskTilknyttetEnhet(norskIdent: NorskIdent): NavEnhetId? {
		val brukerInfo = pdlpipClient.hentBrukerInfo(norskIdent)
		return brukerInfo?.geografiskTilknytning
			?.let { utledGeografiskTilknytningNr(it) }
			?.let { norgClient.hentTilhorendeEnhet(it) }
	}

	override fun hentGeografiskTilknyttetEnhet(norskIdent: NorskIdent, skjermet: Boolean): NavEnhetId? {
		val brukerInfo = pdlpipClient.hentBrukerInfo(norskIdent)

		return brukerInfo?.let { bruker ->
			val adressebeskyttelse = bruker.person.adressebeskyttelse?.firstOrNull()?.tilDiskresjonskode()
			bruker.geografiskTilknytning
				?.let { utledGeografiskTilknytningNr(it) }
				?.let { norgClient.hentTilhorendeEnhet(it, skjermet, adressebeskyttelse) }
		}
	}

	private fun utledGeografiskTilknytningNr(geografiskTilknytning: GeografiskTilknytning): String? {
		return when (geografiskTilknytning.gtType) {
			GeografiskTilknytningType.BYDEL -> geografiskTilknytning.gtBydel
			GeografiskTilknytningType.KOMMUNE -> geografiskTilknytning.gtKommune
			else -> null
		}
	}
}
