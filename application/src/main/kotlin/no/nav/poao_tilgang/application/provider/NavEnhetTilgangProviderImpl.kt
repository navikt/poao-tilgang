package no.nav.poao_tilgang.application.provider

import io.getunleash.DefaultUnleash
import io.micrometer.core.annotation.Timed
import no.nav.poao_tilgang.application.client.axsys.AxsysClient
import no.nav.poao_tilgang.application.utils.HENT_ENHETSTILGANGER_FRA_AD_OG_LOGG_DIFF
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgang
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.Integer.parseInt

@Component
open class NavEnhetTilgangProviderImpl(
	private val axsysClient: AxsysClient,
	private val adGruppeProvider: AdGruppeProvider,
	private val defaultUnleash: DefaultUnleash
) : NavEnhetTilgangProvider {

	private val logger = LoggerFactory.getLogger(javaClass)

	@Timed(
		value = "nav_enhet_tilgang_provider.hent_enhet_tilganger",
		histogram = true,
		percentiles = [0.5, 0.95, 0.99],
		extraTags = ["type", "provider"]
	)
	override fun hentEnhetTilganger(navIdent: NavIdent): List<NavEnhetTilgang> {
		return if (defaultUnleash.isEnabled(HENT_ENHETSTILGANGER_FRA_AD_OG_LOGG_DIFF)) {
			val enhetTilgangerFraAxsys = hentEnhetTilgangerFraAxsys(navIdent)

			// 2025-07-09: Her sammenlignes og logges bare eventuell differanse mellom gammel (Axsys) og ny
			// (vha. AD-grupper) måte å hente enhetstilganger på. Axsys er fortsatt "fasit".
			try {
				val enhetTilgangerFraADGrupper = hentEnhetTilgangerFraADGrupper(navIdent)

				val unikeEnhetTilgangerFraAxsysSortert =
					enhetTilgangerFraAxsys.map(NavEnhetTilgang::enhetId).toSortedSet()
				val unikeEnhetTilgangerFraADGrupperSortert = enhetTilgangerFraADGrupper.toSortedSet()

				if (unikeEnhetTilgangerFraAxsysSortert == unikeEnhetTilgangerFraADGrupperSortert) {
					logger.info("Enhettilganger er identiske mellom Axsys og AD-grupper.")
				} else {
					logger.warn("Enhettilganger er ikke identiske mellom Axsys og AD-grupper.")
				}
			} catch (e: NavEnhetIdValideringException) {
				logger.warn(
					"Kunne ikke hente enhettilganger fra AD-grupper. Årsak: validering feilet for en eller flere utlede NavEnhetId-er.",
					e
				)
			}

			enhetTilgangerFraAxsys
		} else {
			hentEnhetTilgangerFraAxsys(navIdent)
		}
	}

	private fun hentEnhetTilgangerFraAxsys(navIdent: NavIdent): List<NavEnhetTilgang> {
		return axsysClient.hentTilganger(navIdent)
			.map {
				NavEnhetTilgang(
					enhetId = it.enhetId,
					enhetNavn = it.enhetNavn,
					temaer = it.temaer
				)
			}.also {
//				secureLog.info("Axsys , hentTilganger for navIdent: $navIdent, result: $it")
			}
	}

	/**
	 * 2025-07-09: Tilgang til enhet skal migreres fra Axsys til Entra ID
	 * og det er laget egne AD-grupper per enhet. Disse bruker et prefiks ([AD_GRUPPE_ENHET_PREFIKS])
	 * for å kunne skille de fra andre AD-grupper.
	 *
	 * Enhetsnummeret må derfor utledes fra AD-gruppens navn.
	 *
	 * @see <a href="https://nav-it.slack.com/archives/CDKEM1HC5/p1746512543818079">Axsys skrus av - end of life 01.10.2025 | Slack - #axsys</a>
	 * @see <a href="https://nav-ea.public360online.com/locator.aspx?name=Earchive.Document.Details.EArchive&module=Document&subtype=17&recno=1742220">Avvikling av Axsys (001-ADR-IT Axsys) | Public 360 - Arkitekturbeslutninger</a>
	 */
	private fun hentEnhetTilgangerFraADGrupper(navIdent: NavIdent): List<NavEnhetId> {
		val navIdentAzureId = adGruppeProvider.hentAzureIdMedNavIdent(navIdent)
		val adGrupperMedNavn = adGruppeProvider.hentAdGrupper(navIdentAzureId)

		return adGrupperMedNavn
			.map(AdGruppe::navn)
			.filter { it.startsWith(AD_GRUPPE_ENHET_PREFIKS) }
			.map(::tilNavEnhetId)
	}

	companion object {
		private const val AD_GRUPPE_ENHET_PREFIKS = "0000-GA-ENHET_"
		private const val NAV_ENHET_ID_LENGDE = 4

		fun tilNavEnhetId(adGruppeNavn: String): NavEnhetId {
			return adGruppeNavn
				.uppercase()
				.substringAfter(AD_GRUPPE_ENHET_PREFIKS)
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
	}

	internal data class NavEnhetIdValideringException(val melding: String) : RuntimeException(melding)
}

