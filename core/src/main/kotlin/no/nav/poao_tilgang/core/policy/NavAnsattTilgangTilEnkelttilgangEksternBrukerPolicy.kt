package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInputWithNorskIdent

/**
 * Sjekker om en NAV ansatt har tilgang til å gi seg selv midlertidig tilgang (enkelttilgang) til en gitt bruker.
 */
interface NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy : Policy<NavAnsattTilgangTilEnkelttilgangEksternBrukerPolicy.Input> {

	data class Input(
		val navAnsattAzureId: AzureObjectId,
		override val norskIdent: NorskIdent
	) : PolicyInputWithNorskIdent {
		override fun withIdent(norskIdent: NorskIdent): PolicyInputWithNorskIdent {
			return this.copy(norskIdent = norskIdent)
		}
	}
}
