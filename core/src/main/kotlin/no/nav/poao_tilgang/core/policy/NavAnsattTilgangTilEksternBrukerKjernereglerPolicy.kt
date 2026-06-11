package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInputWithNorskIdent
import no.nav.poao_tilgang.core.domain.TilgangType

/**
 * Sjekker om en NAV ansatt har tilgang til å behandle informasjon om en ekstern bruker. Skal brukes for personer som
 * ikke er hovedpart i saken, f.eks. brukers barn.
 */
interface NavAnsattTilgangTilEksternBrukerKjernereglerPolicy : Policy<NavAnsattTilgangTilEksternBrukerKjernereglerPolicy.Input> {

	data class Input(
		val navAnsattAzureId: AzureObjectId,
		val tilgangType: TilgangType,
		override val norskIdent: NorskIdent
	) : PolicyInputWithNorskIdent {
		override fun withIdent(norskIdent: NorskIdent): PolicyInputWithNorskIdent {
			return this.copy(norskIdent = norskIdent)
		}
	}
}
