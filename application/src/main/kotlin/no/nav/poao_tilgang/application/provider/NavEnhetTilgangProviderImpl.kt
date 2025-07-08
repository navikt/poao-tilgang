package no.nav.poao_tilgang.application.provider

import io.getunleash.DefaultUnleash
import io.micrometer.core.annotation.Timed
import no.nav.poao_tilgang.application.client.axsys.AxsysClient
import no.nav.poao_tilgang.application.utils.HENT_ENHETSTILGANGER_FRA_AD_OG_LOGG_DIFF
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgang
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

const val AD_GRUPPE_ENHET_PREFIKS = "0000-GA-ENHET_"

@Component
class NavEnhetTilgangProviderImpl(
	private val axsysClient: AxsysClient,
	private val adGruppeProvider: AdGruppeProvider,
	private val defaultUnleash: DefaultUnleash
) : NavEnhetTilgangProvider {

	private val logger = LoggerFactory.getLogger(javaClass)

	@Timed(value = "nav_enhet_tilgang_provider.hent_enhet_tilganger", histogram = true, percentiles = [0.5, 0.95, 0.99], extraTags = ["type", "provider"])
	override fun hentEnhetTilganger(navIdent: NavIdent): List<NavEnhetTilgang> {
		return if (defaultUnleash.isEnabled(HENT_ENHETSTILGANGER_FRA_AD_OG_LOGG_DIFF)) {
			val enhetTilgangerFraADGrupper = hentEnhetTilgangerFraADGrupper(navIdent)
			val enhetTilgangerFraAxsys = hentEnhetTilgangerFraAxsys(navIdent)

			if (
				enhetTilgangerFraADGrupper.map { it.enhetId }.sorted() ==
				enhetTilgangerFraAxsys.map { it.enhetId }.sorted()
			) {
				logger.info("Enhettilganger er identiske mellom Axsys og AD.")
			} else {
				logger.info("Enhettilganger er ulike mellom Axsys og AD.")
			}

			enhetTilgangerFraAxsys
		} else {
			hentEnhetTilgangerFraAxsys(navIdent)
		}
	}

	/**
	 * 2025-07-07: Tilgang til enhet skal migreres fra Axsys til Entra ID
	 * og det er laget egne AD-grupper per enhet. Disse bruker et prefiks ([AD_GRUPPE_ENHET_PREFIKS])
	 * for å kunne skille de fra andre AD-grupper.
	 *
	 * Enhetsnummeret må derfor utledes fra AD-gruppens navn.
	 *
	 * @see <a href="https://nav-it.slack.com/archives/CDKEM1HC5/p1746512543818079">Axsys skrus av - end of life 01.10.2025 | Slack - #axsys</a>
	 * @see <a href="https://nav-ea.public360online.com/locator.aspx?name=Earchive.Document.Details.EArchive&module=Document&subtype=17&recno=1742220">Avvikling av Axsys (001-ADR-IT Axsys) | Public 360 - Arkitekturbeslutninger</a>
	 */
	private fun hentEnhetTilgangerFraADGrupper(navIdent: NavIdent): List<NavEnhetTilgang> {
		val navIdentAzureId = adGruppeProvider.hentAzureIdMedNavIdent(navIdent)
		val adGrupperMedNavn = adGruppeProvider.hentAdGrupper(navIdentAzureId)

		return adGrupperMedNavn.filter { it.navn.startsWith(AD_GRUPPE_ENHET_PREFIKS) }
			.map { NavEnhetTilgang(it.navn.substringAfter(AD_GRUPPE_ENHET_PREFIKS)) }
	}

	private fun hentEnhetTilgangerFraAxsys(navIdent: NavIdent): List<NavEnhetTilgang> =
		axsysClient.hentTilganger(navIdent)
			.map {
				NavEnhetTilgang(
					enhetId = it.enhetId
				)
			}.also {
				//				secureLog.info("Axsys , hentTilganger for navIdent: $navIdent, result: $it")
			}
}
