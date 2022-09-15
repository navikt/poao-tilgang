package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.domain.PolicyName

/**
 * Sjekker om en NAV ansatt har tilgang til å behandle fortrolig brukere (kode 7)
 */
interface FortroligBrukerPolicy : Policy<FortroligBrukerPolicy.Input> {

	companion object : PolicyName {
		override val name = "HarNavAnsattTilgangTilFortroligBruker"
	}

	data class Input (
		val navIdent: NavIdent
	) : PolicyInput

}
