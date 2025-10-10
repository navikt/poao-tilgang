package no.nav.poao_tilgang.core.provider

import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NavIdent

@Deprecated(
	message = "Utlisting av NavEnhetTilgang er ikke lenger mulig. Se NavEnhetTilgangProviderV2 for alternativ.",
	level = DeprecationLevel.WARNING,
	replaceWith = ReplaceWith(
		expression = "NavEnhetTilgangProviderV2",
		imports = ["no.nav.poao_tilgang.core.provider.NavEnhetTilgangProviderV2"]
	)
)
interface NavEnhetTilgangProvider {

	@Deprecated(
		message = "Utlisting av NavEnhetTilgang er ikke lenger mulig. Se NavEnhetTilgangProviderV2.hentEnhetTilganger for alternativ.",
		level = DeprecationLevel.WARNING,
		replaceWith = ReplaceWith("NavEnhetTilgangProviderV2.hentEnhetTilganger(navIdent)")
	)
	fun hentEnhetTilganger(navIdent: NavIdent): List<NavEnhetTilgang>

}

interface NavEnhetTilgangProviderV2 {
	fun hentEnhetTilganger(navIdent: NavIdent): Set<NavEnhetId>
}

data class NavEnhetTilgang(
	val enhetId: NavEnhetId,
	val enhetNavn: String,
	val temaer: List<String>
)
