package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerNavEnhetPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProvider
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider
import no.nav.poao_tilgang.core.utils.hasAtLeastOne
import org.slf4j.LoggerFactory

class NavAnsattTilgangTilEksternBrukerNavEnhetPolicyImpl(
	private val oppfolgingsenhetProvider: OppfolgingsenhetProvider,
	private val geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider,
	private val adGruppeProvider: AdGruppeProvider,
	private val navEnhetTilgangProvider: NavEnhetTilgangProvider
) : NavAnsattTilgangTilEksternBrukerNavEnhetPolicy {

	private val secureLog = LoggerFactory.getLogger("SecureLog")
	private val nasjonalTilgangGrupperOgAdmin = adGruppeProvider.hentTilgjengeligeAdGrupper().let {
		listOf(
			it.gosysNasjonal,
			it.gosysUtvidbarTilNasjonal,
			it.modiaAdmin
		)
	}

	override val name = "NavAnsattTilgangTilEksternBrukerNavEnhetPolicy"

	val denyDecisionNotAccessToEnhet = Decision.Deny(
		message = "NavAnsatt har ikke tilgang til Brukers oppfølgingsenhet eller geografisk enhet",
		reason = DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
	)

	val denyDecisionEksternbrukerMissingEnhet = Decision.Deny(
		message = "Brukeren har ikke oppfølgingsenhet eller geografisk enhet",
		reason = DecisionDenyReason.UKLAR_TILGANG_MANGLENDE_INFORMASJON
	)

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input): Decision {
		val (navAnsattAzureId, norskIdent) = input

		// Hvis man har nasjonal tilgang så trengs det ikke sjekk på enhet tilgang
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.hasAtLeastOne(nasjonalTilgangGrupperOgAdmin)
			.whenPermit { return it }

		val gtEnhet = geografiskTilknyttetEnhetProvider.hentGeografiskTilknyttetEnhet(norskIdent)?.let { navEnhetId ->
			harTilgangTilEnhetForBruker(navAnsattAzureId, navEnhetId, "geografiskEnhet")
				.whenPermit { return it }
		}

		val oppfolgingsEnhet = oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)?.let { navEnhetId ->
			harTilgangTilEnhetForBruker(navAnsattAzureId, navEnhetId, "oppfolgingsEnhet")
				.whenPermit { return it }
		}
		if (gtEnhet == null && oppfolgingsEnhet == null) return denyDecisionEksternbrukerMissingEnhet
		else return denyDecisionNotAccessToEnhet
	}

	fun harTilgangTilEnhetForBruker(
		navAnsattAzureId: AzureObjectId,
		navEnhetId: NavEnhetId,
		typeEnhet: String
	): Decision {
		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(navAnsattAzureId)
		val harTilgangTilEnhet = navEnhetTilgangProvider.hentEnhetTilganger(navIdent)
			.any { navEnhetId == it.enhetId }
		secureLog.info("$name, harTilgangTilEnhet: $harTilgangTilEnhet, navEnhetForBruker: $navEnhetId, navident: $navIdent, azureId: $navAnsattAzureId, for type Enhet: $typeEnhet")
		return if (harTilgangTilEnhet) Decision.Permit else denyDecisionNotAccessToEnhet
	}
}
