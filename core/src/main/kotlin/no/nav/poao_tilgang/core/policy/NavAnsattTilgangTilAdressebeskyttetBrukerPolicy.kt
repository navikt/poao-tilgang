package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*

/**
 * Sjekker om en NAV ansatt har tilgang til bruker med adressebeskyttelse (diskresjonskode som f.eks kode6/7/19)
 */
interface NavAnsattTilgangTilAdressebeskyttetBrukerPolicy : Policy<NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input> {

	data class Input (
		val navAnsattAzureId: AzureObjectId,
		override val norskIdent: NorskIdent
	) : PolicyInputWithNorskIdent {
		override fun withIdent(norskIdent: NorskIdent): PolicyInputWithNorskIdent {
			return this.copy(norskIdent = norskIdent)
		}
	}

}
