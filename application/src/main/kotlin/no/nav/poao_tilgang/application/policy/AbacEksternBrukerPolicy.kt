package no.nav.poao_tilgang.application.policy

import no.nav.common.abac.Pep
import no.nav.common.abac.domain.request.ActionId
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.EksternBrukerPolicy

class AbacEksternBrukerPolicy(
	private val pep: Pep
) : EksternBrukerPolicy {

	override val name = "AbacHarNavAnsattTilgangTilEksternBruker"

	override fun evaluate(input: EksternBrukerPolicy.Input): Decision {
		val harTilgang = pep.harVeilederTilgangTilPerson(
			NavIdent.of(input.navIdent),
			ActionId.WRITE,
			Fnr.of(input.norskIdent)
		)

		return if (harTilgang) Decision.Permit else Decision.Deny(
			"Deny fra ABAC",
			DecisionDenyReason.IKKE_TILGANG_FRA_ABAC
		)
	}

}
