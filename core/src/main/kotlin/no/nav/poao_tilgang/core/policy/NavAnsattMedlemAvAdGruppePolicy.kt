package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInput
import java.util.*

/**
 * Sjekker om en NAV ansatt er medlen av AD-gruppe
 */
interface NavAnsattMedlemAvAdGruppePolicy : Policy<NavAnsattMedlemAvAdGruppePolicy.Input> {

	data class Input (
		val navAnsattAzureId: AzureObjectId,
		val adGruppeId: UUID
	) : PolicyInput

}
