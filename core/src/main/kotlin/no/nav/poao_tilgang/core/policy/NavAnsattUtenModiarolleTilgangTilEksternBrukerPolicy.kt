package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.domain.Policy
import no.nav.poao_tilgang.core.domain.PolicyInputWithNorskIdent

interface NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy : Policy<NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input> {

	data class Input(
		val navAnsattAzureId: AzureObjectId,
		override val norskIdent: NorskIdent
	) : PolicyInputWithNorskIdent {
		override fun withIdent(norskIdent: NorskIdent): PolicyInputWithNorskIdent {
			return this.copy(norskIdent = norskIdent)
		}
	}
}
