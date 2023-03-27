package no.nav.poao_tilgang.core.policy.impl

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AbacProvider
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.asyncLogDecisionDiff
import no.nav.poao_tilgang.core.utils.AbacDecisionDiff.toAbacDecision
import java.time.Duration

class NavIdentTilgangTilEksternBrukerPolicyImpl(
	private val abacProvider: AbacProvider,
	private val navAnsattTilgangTilAdressebeskyttetBrukerPolicy: NavAnsattTilgangTilAdressebeskyttetBrukerPolicy,
	private val navAnsattTilgangTilSkjermetPersonPolicy: NavAnsattTilgangTilSkjermetPersonPolicy,
	private val navAnsattTilgangTilEksternBrukerNavEnhetPolicy: NavAnsattTilgangTilEksternBrukerNavEnhetPolicy,
	private val navAnsattTilgangTilOppfolgingPolicy: NavAnsattTilgangTilOppfolgingPolicy,
	private val navAnsattTilgangTilModiaGenerellPolicy: NavAnsattTilgangTilModiaGenerellPolicy,
	private val adGruppeProvider: AdGruppeProvider,
	private val meterRegistry: MeterRegistry
) : NavIdentTilgangTilEksternBrukerPolicy {

	override val name = "NavAnsattNavIdentTilgangTilEksternBrukerPolicy"

	override fun evaluate(input: NavIdentTilgangTilEksternBrukerPolicy.Input): Decision {
		val harTilgangAbac = harTilgangAbac(input)

		asyncLogDecisionDiff(name, input, ::harTilgang, harTilgangAbac)

		return harTilgangAbac
	}

	private fun harTilgangAbac(input: NavIdentTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navIdent, tilgangType, norskIdent) = input

		val timer: Timer = meterRegistry.timer("app.poao-tilgang.NavIdentTilgangTilEksternBruker")
		val startTime=System.currentTimeMillis();

		val harTilgang = abacProvider.harVeilederTilgangTilPerson(navIdent, tilgangType, norskIdent)

		timer.record(Duration.ofMillis(System.currentTimeMillis()-startTime))

		return toAbacDecision(harTilgang)
	}

	// Er ikke private slik at vi kan teste implementasjonen
	internal fun harTilgang(input: NavIdentTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navIdent, tilgangType, norskIdent) = input
		val navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(navIdent)

		navAnsattTilgangTilAdressebeskyttetBrukerPolicy.evaluate(
			NavAnsattTilgangTilAdressebeskyttetBrukerPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilSkjermetPersonPolicy.evaluate(
			NavAnsattTilgangTilSkjermetPersonPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		navAnsattTilgangTilEksternBrukerNavEnhetPolicy.evaluate(
			NavAnsattTilgangTilEksternBrukerNavEnhetPolicy.Input(
				navAnsattAzureId = navAnsattAzureId,
				norskIdent = norskIdent
			)
		).whenDeny { return it }

		when (tilgangType) {
			TilgangType.LESE ->
				navAnsattTilgangTilModiaGenerellPolicy.evaluate(
					NavAnsattTilgangTilModiaGenerellPolicy.Input(navAnsattAzureId)
				).whenDeny { return it }

			TilgangType.SKRIVE ->
				navAnsattTilgangTilOppfolgingPolicy.evaluate(
					NavAnsattTilgangTilOppfolgingPolicy.Input(navAnsattAzureId)
				).whenDeny { return it }
		}

		return Decision.Permit
	}

}
