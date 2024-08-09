package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilModiaAdminPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.has

class NavAnsattTilgangTilModiaAdminPolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattTilgangTilModiaAdminPolicy {

	private val tilgangTilModiaAdmin = adGruppeProvider.hentTilgjengeligeAdGrupper().modiaAdmin

	override val name = "NavAnsattTilgangTilModiaAdminPolicy"
	override fun evaluate(input: NavAnsattTilgangTilModiaAdminPolicy.Input): Decision {
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.has(tilgangTilModiaAdmin)
			.whenPermit { return it }

		return Decision.Deny(
			message = "Har ikke tilgang til rollen 0000-GA-Modia_Admin",
			reason = DecisionDenyReason.MANGLER_TILGANG_TIL_AD_GRUPPE
		)
	}
}
