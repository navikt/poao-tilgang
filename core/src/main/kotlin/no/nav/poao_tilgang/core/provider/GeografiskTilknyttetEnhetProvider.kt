package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent

interface GeografiskTilknyttetEnhetProvider {

	/* Tar ikke hensyn til om bruker er skjermet, usikker på hva man får ved kode 6 eller kode 7 */
	fun hentGeografiskTilknyttetEnhet(norskIdent: NorskIdent): NavEnhetId?

	/* Tar med skjerming og diskresjonskode som parameter til nav-kontor kallet til NORG */
	fun hentGeografiskTilknyttetEnhet(norskIdent: NorskIdent, skjermet: Boolean): NavEnhetId?
}
