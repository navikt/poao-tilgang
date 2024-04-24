package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.pdl_pip.Adressebeskyttelse
import no.nav.poao_tilgang.application.client.pdl_pip.Gradering
import no.nav.poao_tilgang.application.client.pdl_pip.PdlPipClient
import no.nav.poao_tilgang.application.utils.SecureLog.secureLog
import no.nav.poao_tilgang.core.domain.Diskresjonskode
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider
import org.springframework.stereotype.Service

@Service
class DiskresjonskodeProviderImpl(
	private val pdlPipClient: PdlPipClient
) : DiskresjonskodeProvider {

	override fun hentDiskresjonskode(norskIdent: String): Diskresjonskode? {
		return pdlPipClient.hentBrukerInfo(norskIdent)
			?.person
			?.adressebeskyttelse?.firstOrNull()?.let { tilDiskresjonskode(it) }
			.also {
				secureLog.info("PdlPip , hentDiskresjonskode for fnr: $norskIdent, result: $it")
			}
	}

	private fun tilDiskresjonskode(adressebeskyttelse: Adressebeskyttelse): Diskresjonskode {
		return when(adressebeskyttelse.gradering) {
			Gradering.FORTROLIG -> Diskresjonskode.FORTROLIG
			Gradering.STRENGT_FORTROLIG -> Diskresjonskode.STRENGT_FORTROLIG
			Gradering.STRENGT_FORTROLIG_UTLAND -> Diskresjonskode.STRENGT_FORTROLIG_UTLAND
		}
	}

}
