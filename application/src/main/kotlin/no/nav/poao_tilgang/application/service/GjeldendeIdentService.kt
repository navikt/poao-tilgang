package no.nav.poao_tilgang.application.service

import no.nav.poao_tilgang.application.client.pdl_pip.PdlPipClient
import no.nav.poao_tilgang.core.domain.NorskIdent
import org.springframework.stereotype.Service

interface GjeldendeIdentProvider: (NorskIdent) -> NorskIdent

@Service
class GjeldendeIdentService(
	val pdlpipClient: PdlPipClient
): GjeldendeIdentProvider {

	/* Gitt en gammel ident typ dnr, bytt det ut med nyeste ident typ fnr og gjør tilgangskontroll mot dette istedet */
	override fun invoke(ident: NorskIdent): NorskIdent {
		val brukerInfo = pdlpipClient.hentBrukerInfo(ident)
		if (brukerInfo == null) return ident

		return brukerInfo.identer.identer
			.singleOrNull { it.historisk == false }?.ident ?: ident
	}

}
