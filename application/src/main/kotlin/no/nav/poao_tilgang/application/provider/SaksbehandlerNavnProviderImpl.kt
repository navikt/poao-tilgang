package no.nav.poao_tilgang.application.provider

import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClient
import no.nav.poao_tilgang.core.domain.DisplayName
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.provider.SaksbehandlerNavnProvider

class SaksbehandlerNavnProviderImpl(
	private val microsoftGraphClient: MicrosoftGraphClient
) : SaksbehandlerNavnProvider {
	override fun hentNavnForNavIdent(navIdent: NavIdent): DisplayName {
		return microsoftGraphClient.hentNavnForNavIdent(navIdent)
	}
}
