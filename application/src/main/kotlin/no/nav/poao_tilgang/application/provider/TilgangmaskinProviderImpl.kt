package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.tilgangsmaskin.Avvisningskode
import no.nav.poao_tilgang.application.client.tilgangsmaskin.TilgangmaskinClient
import no.nav.poao_tilgang.application.client.tilgangsmaskin.TilgangmaskinResult
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.provider.TilgangmaskinProvider
import no.nav.poao_tilgang.core.utils.SecureLog.secureLog
import org.springframework.stereotype.Component

@Component
class TilgangmaskinProviderImpl(
    private val tilgangmaskinClient: TilgangmaskinClient
) : TilgangmaskinProvider {

    override fun evaluerKjerneregler(norskIdent: String): Decision {
        val result = tilgangmaskinClient.evaluerKjerneregler(norskIdent)

        return result.fold(
            onSuccess = { tilgangmaskinResult ->
                when (tilgangmaskinResult) {
                    is TilgangmaskinResult.Godkjent -> Decision.Permit
                    is TilgangmaskinResult.Avvist -> {
                        secureLog.info("Tilgangsmaskin avviste tilgang for bruker: begrunnelse=${tilgangmaskinResult.begrunnelse}, title=${tilgangmaskinResult.title}")
                        Decision.Deny(
                            message = tilgangmaskinResult.begrunnelse ?: "Tilgang avvist av tilgangsmaskinen",
                            reason = mapAvvisningskodeTilDenyReason(tilgangmaskinResult)
                        )
                    }
                }
            },
            onFailure = { error ->
                secureLog.error("Feil ved kall til tilgangsmaskinen: ${error.message}", error)
                Decision.Deny(
                    message = "Kunne ikke evaluere tilgang: ${error.message}",
                    reason = DecisionDenyReason.UKLAR_TILGANG_MANGLENDE_INFORMASJON
                )
            }
        )
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

