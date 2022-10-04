package no.nav.poao_tilgang.core.policy.test_utils

import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn
import no.nav.poao_tilgang.core.domain.AdGrupper
import java.util.*

object TestAdGrupper {

	val grupper = AdGrupper(
        fortroligAdresse = AdGruppe(
            UUID.fromString("97690ad9-d423-4c1f-9885-b01fb9f9feab"),
            AdGruppeNavn.FORTROLIG_ADRESSE
        )
    )


}
