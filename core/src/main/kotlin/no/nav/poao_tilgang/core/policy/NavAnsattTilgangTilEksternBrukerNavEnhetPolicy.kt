package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*

/**
 * Sjekker om NAV ansatt har tilgang til brukers oppfølgingsenhet eller brukers geografiske enhet
 *
 */
@Deprecated("Skal ikke lenger bruker direkte, men man skal bruke tilgangsmaskinClient der man har behov for denne")
interface NavAnsattTilgangTilEksternBrukerNavEnhetPolicy: Policy<NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input> {

	data class Input(
		val navAnsattAzureId: AzureObjectId,
		override val norskIdent: NorskIdent
	) : PolicyInputWithNorskIdent {
		override fun withIdent(norskIdent: NorskIdent): PolicyInputWithNorskIdent {
			return this.copy(norskIdent = norskIdent)
		}
	}

}


