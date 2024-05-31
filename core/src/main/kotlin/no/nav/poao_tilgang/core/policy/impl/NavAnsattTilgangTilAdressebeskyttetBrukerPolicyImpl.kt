package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.Diskresjonskode
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleFortroligBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilAdressebeskyttetBrukerPolicy
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider

class NavAnsattTilgangTilAdressebeskyttetBrukerPolicyImpl(
	private val diskresjonskodeProvider: DiskresjonskodeProvider,
	private val navAnsattBehandleFortroligBrukerePolicy: NavAnsattBehandleFortroligBrukerePolicy,
	private val navAnsattBehandleStrengtFortroligBrukerePolicy: NavAnsattBehandleStrengtFortroligBrukerePolicy,
	private val navAnsattBehandleStrengtFortroligUtlandBrukerePolicy: NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy,
) : NavAnsattTilgangTilAdressebeskyttetBrukerPolicy {

	override val name = "NavAnsattTilgangTilAdresseBesyttetBruker"
	//TODO: Bør vi implementere muligheten for å ta inn en liste av fnr og sette begrensning på 20? Dette skjer i abac.
	override fun evaluate(input: NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input): Decision {
		val diskresjonskode = diskresjonskodeProvider.hentDiskresjonskode(input.norskIdent)
			?: return Decision.Permit

		return when (diskresjonskode) {
			Diskresjonskode.FORTROLIG -> navAnsattBehandleFortroligBrukerePolicy.evaluate(
				NavAnsattBehandleFortroligBrukerePolicy.Input(input.navAnsattAzureId)
			)
			Diskresjonskode.STRENGT_FORTROLIG -> navAnsattBehandleStrengtFortroligBrukerePolicy.evaluate(
				NavAnsattBehandleStrengtFortroligBrukerePolicy.Input(input.navAnsattAzureId)
			)
			Diskresjonskode.STRENGT_FORTROLIG_UTLAND -> navAnsattBehandleStrengtFortroligUtlandBrukerePolicy.evaluate(
				NavAnsattBehandleStrengtFortroligUtlandBrukerePolicy.Input(input.navAnsattAzureId)
			)
			Diskresjonskode.UGRADERT -> Decision.Permit
		}
	}


}
