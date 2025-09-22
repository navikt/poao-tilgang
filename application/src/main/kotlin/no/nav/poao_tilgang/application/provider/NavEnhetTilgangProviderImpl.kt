package no.nav.poao_tilgang.application.provider

import io.micrometer.core.annotation.Timed
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn.ENHET_PREFIKS
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProviderV2
import org.springframework.stereotype.Component
import java.lang.Integer.parseInt

@Component
open class NavEnhetTilgangProviderV2Impl(
	private val adGruppeProvider: AdGruppeProvider,
) : NavEnhetTilgangProviderV2 {
	@Timed(
		value = "nav_enhet_tilgang_provider_v2.hent_enhet_tilganger",
		histogram = true,
		percentiles = [0.5, 0.95, 0.99],
		extraTags = ["type", "provider"]
	)
	override fun hentEnhetTilganger(navIdent: NavIdent): Set<NavEnhetId> {
		val navIdentAzureId = adGruppeProvider.hentAzureIdMedNavIdent(navIdent)
		val adGrupperMedNavn = adGruppeProvider.hentAdGrupper(navIdentAzureId)

		return adGrupperMedNavn
			.map(AdGruppe::navn)
			.filter { it.startsWith(ENHET_PREFIKS) }
			.map(::tilNavEnhetId)
			.toSet()
	}
}

private const val NAV_ENHET_ID_LENGDE = 4

fun tilNavEnhetId(adGruppeNavn: String): NavEnhetId {
	if (!adGruppeNavn.startsWith(AD_GRUPPE_ENHET_PREFIKS)) {
		throw NavEnhetIdValideringException("Ugyldig format: ${adGruppeNavn}. Forventet format: \"$AD_GRUPPE_ENHET_PREFIKS\"-prefiks etterfulgt av fire siffer.")
	}

	return adGruppeNavn
		.uppercase()
		.substringAfter(ENHET_PREFIKS)
		.let(::tilValidertNavEnhetId)
}

private fun tilValidertNavEnhetId(navEnhetId: String): NavEnhetId {
	if (navEnhetId.length != NAV_ENHET_ID_LENGDE) throw NavEnhetIdValideringException("Ugyldig lengde: ${navEnhetId.length}. Forventet: $NAV_ENHET_ID_LENGDE.")
	if (
		try {
			parseInt(navEnhetId)
			false
		} catch (_: NumberFormatException) {
			true
		}
	) throw NavEnhetIdValideringException("Ugyldige tegn: ${navEnhetId.length}. Forventet: 4 siffer.")

	return navEnhetId
}

data class NavEnhetIdValideringException(val melding: String) : RuntimeException(melding)
