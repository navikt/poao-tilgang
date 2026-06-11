package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.tilgangsmaskin.Avvisningskode
import no.nav.poao_tilgang.application.client.tilgangsmaskin.TilgangmaskinClient
import no.nav.poao_tilgang.application.client.tilgangsmaskin.TilgangmaskinResult
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider
import org.springframework.stereotype.Component

@Component
class TilgangmaskinProviderImpl(
    private val tilgangmaskinClient: TilgangmaskinClient
) : TilgangmaskinProvider {

    override fun evaluerKompletteRegler(norskIdent: String, navIdent: NavIdent): Decision {
        val result = tilgangmaskinClient.evaluerKompletteRegler(norskIdent, navIdent)

		return when (result) {
			TilgangmaskinResult.Godkjent -> Decision.Permit
			is TilgangmaskinResult.Avvist -> Decision.Deny(
				result.begrunnelse,
				mapAvvisningskodeTilDenyReason(result)
			)
		}
    }

	override fun evaluerKjerneregler(norskIdent: String, navIdent: NavIdent): Decision {
		val result = tilgangmaskinClient.evaluerKjerneregler(norskIdent, navIdent)

		return when (result) {
			TilgangmaskinResult.Godkjent -> Decision.Permit
			is TilgangmaskinResult.Avvist -> Decision.Deny(
				result.begrunnelse,
				mapAvvisningskodeTilDenyReason(result)
			)
		}
	}

    private fun mapAvvisningskodeTilDenyReason(avvist: TilgangmaskinResult.Avvist): DecisionDenyReason {
        return when (avvist.title) {
            Avvisningskode.AVVIST_GEOGRAFISK -> DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
            Avvisningskode.AVVIST_FORTROLIG_ADRESSE -> DecisionDenyReason.IKKE_TILGANG_TIL_FORTROLIG_BRUKER
            Avvisningskode.AVVIST_STRENGT_FORTROLIG_ADRESSE -> DecisionDenyReason.IKKE_TILGANG_TIL_STRENGT_FORTROLIG_BRUKER
            Avvisningskode.AVVIST_STRENGT_FORTROLIG_UTLAND -> DecisionDenyReason.IKKE_TILGANG_TIL_STRENGT_FORTROLIG_UTLAND_BRUKER
            Avvisningskode.AVVIST_SKJERMING -> DecisionDenyReason.IKKE_TILGANG_TIL_SKJERMET_PERSON
            Avvisningskode.AVVIST_UKJENT_BOSTED -> DecisionDenyReason.UKLAR_TILGANG_MANGLENDE_INFORMASJON
            else -> DecisionDenyReason.POLICY_IKKE_IMPLEMENTERT
        }
    }
}

