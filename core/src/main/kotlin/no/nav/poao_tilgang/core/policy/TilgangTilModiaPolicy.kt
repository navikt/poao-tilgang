package no.nav.poao_tilgang.core.policy

import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class TilgangTilModiaPolicy(
	private val adGruppeProvider: AdGruppeProvider
) : Policy<NavIdent>(PolicyType.TILGANG_TIL_MODIA) {

	companion object {
		private val tilgangTilModiaGrupper = listOf(
			AdGrupper.MODIA_GENERELL,
			AdGrupper.MODIA_OPPFOLGING,
			AdGrupper.SYFO_SENSITIV
		)

		private val denyDecision = Decision.Deny(
			message = "NAV ansatt mangler tilgang til en av AD gruppene $tilgangTilModiaGrupper",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}

	override fun harTilgang(input: NavIdent): Decision {
		val adGruppper = adGruppeProvider.hentAdGrupper(input)

		val harTilgang = adGruppper.any { tilgangTilModiaGrupper.contains(it.name) }

		return if (harTilgang) Decision.Permit else denyDecision
	}

}
