package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput

/**
 * Sjekker om en NAV ansatt har rollen Modia Admin
 */
interface NavAnsattTilgangTilModiaAdminPolicy : Policy<NavAnsattTilgangTilModiaAdminPolicy.Input> {

	data class Input (
		val navAnsattAzureId: AzureObjectId
	) : PolicyInput

}
