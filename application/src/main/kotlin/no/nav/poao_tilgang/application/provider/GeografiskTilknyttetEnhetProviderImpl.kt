package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.norg.NorgClient
import no.nav.poao_tilgang.application.client.pdl_pip.GeografiskTilknytning
import no.nav.poao_tilgang.application.client.pdl_pip.GeografiskTilknytningType
import no.nav.poao_tilgang.application.client.pdl_pip.PdlPipClient
import no.nav.poao_tilgang.core.domain.Diskresjonskode

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import org.springframework.stereotype.Component

@Component
class GeografiskTilknyttetEnhetProviderImpl(
	private val pdlpipClient: PdlPipClient,
	private val norgClient: NorgClient
) : GeografiskTilknyttetEnhetProvider {

	companion object {
		val DEFAULT_GT_IF_NO_GT_FOUND = "460101" // Nav Bergen Nord (tilfeldig valgt - enheten vil alltid bli Vikafossen
	}

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
		    val geografiskTilknytningNr = bruker.geografiskTilknytning?.let { utledGeografiskTilknytningNr(it) }

		    if (geografiskTilknytningNr != null ||
		        (adressebeskyttelse == Diskresjonskode.STRENGT_FORTROLIG || adressebeskyttelse == Diskresjonskode.STRENGT_FORTROLIG_UTLAND)) {
		        norgClient.hentTilhorendeEnhet(geografiskTilknytningNr ?: DEFAULT_GT_IF_NO_GT_FOUND, skjermet, adressebeskyttelse)
		    } else {
		        null
		    }
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
