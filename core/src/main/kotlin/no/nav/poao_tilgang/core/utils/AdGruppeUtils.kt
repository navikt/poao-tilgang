package no.nav.poao_tilgang.core.utils

import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason

object AdGruppeUtils {

	fun harTilgangTilAdGruppe(tilgangTil: AdGruppe, navAnsattGrupper: List<AdGruppe>): Decision {
		val harTilgang = navAnsattGrupper.any { it.id == tilgangTil.id }

		return if (harTilgang) Decision.Permit else Decision.Deny(
			message = "NAV ansatt mangler tilgang til AD gruppen \"${tilgangTil.navn}\"",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)

	}

	fun harTilgangTilEnAvAdGruppene(tilgangTil: List<AdGruppe>, navAnsattGrupper: List<AdGruppe>): Decision {
		val harTilgang = navAnsattGrupper.any { gruppe -> tilgangTil.any { gruppe.id == it.id } }

		return if (harTilgang) Decision.Permit else Decision.Deny(
			message = "NAV ansatt mangler tilgang til en av AD gruppene ${navAnsattGrupper.map { it.navn }}",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}
}
