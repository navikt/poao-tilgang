package no.nav.poao_tilgang.application.client.ao_oppfolgingskontor

import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.core.domain.NavEnhetId

interface AoKontorClient {
	fun hentBrukerOppfolgingsenhetId(fnr: Fnr): NavEnhetId?
}
