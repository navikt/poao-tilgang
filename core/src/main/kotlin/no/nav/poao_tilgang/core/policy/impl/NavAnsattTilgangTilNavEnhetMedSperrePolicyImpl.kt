package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilNavEnhetMedSperrePolicy
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilOppfolgingPolicy
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProviderV2
import no.nav.poao_tilgang.core.utils.Timer
import no.nav.poao_tilgang.core.utils.has
import java.time.Duration

class NavAnsattTilgangTilNavEnhetMedSperrePolicyImpl(
	private val navEnhetTilgangProviderV2: NavEnhetTilgangProviderV2,
	private val adGruppeProvider: AdGruppeProvider,
	private val timer: Timer,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy
) : NavAnsattTilgangTilNavEnhetMedSperrePolicy {

	private val aktivitetsplanKvp = adGruppeProvider.hentTilgjengeligeAdGrupper().aktivitetsplanKvp

	private val denyDecision = Decision.Deny(
		message = "Har ikke tilgang til NAV enhet med sperre",
		reason = DecisionDenyReason.IKKE_TILGANG_TIL_NAV_ENHET
	)

	override val name = "NavAnsattTilgangTilNavEnhetMedSperre"

	override fun evaluate(input: NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input): Decision {
		return harTilgang(input)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input): Decision {
		val startTime = System.currentTimeMillis()
		adGruppeProvider.hentAdGrupper(input.navAnsattAzureId)
			.has(aktivitetsplanKvp)
			.whenPermit { return it }

		val navIdent = adGruppeProvider.hentNavIdentMedAzureId(input.navAnsattAzureId)

		// Sjekk av adgruppe modiaoppfølging er egentlig ikke del av sjekken for tilgang til enhet
		// https://confluence.adeo.no/display/ABAC/Tilgang+til+enhet
		// MEN
		//  Sjekk av 'tilgang til oppfølging' kicker inn pga resource_type == "no.nav.abac.attributter.resource.felles.enhet"
		// https://confluence.adeo.no/pages/viewpage.action?pageId=202371312
		navAnsattTilgangTilOppfolgingPolicy.evaluate(NavAnsattTilgangTilOppfolgingPolicy.Input(input.navAnsattAzureId))
			.whenDeny {
				return it
			}

		val harTilgangTilEnhet = navEnhetTilgangProviderV2.hentEnhetTilganger(navIdent).any { input.navEnhetId == it }

		timer.record(
			"app.poao-tilgang.NavAnsattTilgangTilNavEnhetMedSperre.egen",
			Duration.ofMillis(System.currentTimeMillis() - startTime)
		)

		return if (harTilgangTilEnhet) Decision.Permit else denyDecision
	}

}
