package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleFortroligBrukerePolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.AdGruppeUtils.harTilgangTilAdGruppe

class NavAnsattBehandleFortroligBrukerePolicyImpl(
	private val adGruppeProvider: AdGruppeProvider
) : NavAnsattBehandleFortroligBrukerePolicy {

	override val name = "NavAnsattBehandleFortroligBrukere"

	override fun evaluate(input: NavAnsattBehandleFortroligBrukerePolicy.Input): Decision {
		val (fortroligAdresse) = adGruppeProvider.hentTilgjengeligeAdGrupper()

		val adGruppper = adGruppeProvider.hentAdGrupper(input.navIdent)

		return harTilgangTilAdGruppe(fortroligAdresse, adGruppper)
	}
}
