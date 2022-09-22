package no.nav.poao_tilgang.core.policy.impl

import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason
import no.nav.poao_tilgang.core.policy.NavAnsattTilgangTilEksternBrukerPolicy
import no.nav.poao_tilgang.core.provider.AbacProvider

class NavAnsattTilgangTilEksternBrukerPolicyImpl(
	private val abacProvider: AbacProvider,
) : NavAnsattTilgangTilEksternBrukerPolicy {

	override val name = "NavAnsattTilgangTilEksternBruker"

	override fun evaluate(input: NavAnsattTilgangTilEksternBrukerPolicy.Input): Decision {
		val (navIdent, norskIdent) = input

		val harTilgang = abacProvider.harVeilederTilgangTilPerson(navIdent, norskIdent)

		return if (harTilgang) Decision.Permit else Decision.Deny(
			"Deny fra ABAC",
			DecisionDenyReason.IKKE_TILGANG_FRA_ABAC
		)

//		val diskresjonskode = diskresjonskodeProvider.hentDiskresjonskode(norskIdent)
//
//		if (diskresjonskode == Diskresjonskode.STRENGT_FORTROLIG) {
//			strengtFortroligBrukerPolicy.evaluate(
//				StrengtFortroligBrukerPolicy.Input(navIdent)
//			).let {
//				if (it is Decision.Deny) return it
//			}
//		} else if (diskresjonskode == Diskresjonskode.FORTROLIG) {
//			fortroligBrukerPolicy.evaluate(
//				FortroligBrukerPolicy.Input(navIdent)
//			).let {
//				if (it is Decision.Deny) return it
//			}
//		}
//
//		return Decision.Deny(
//			message = "Policy er ikke implementert",
//			reason = DecisionDenyReason.POLICY_NOT_IMPLEMENTED
//		)
	}

}