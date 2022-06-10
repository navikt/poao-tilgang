package no.nav.poao_tilgang.service

import no.nav.poao_tilgang.domain.Decision
import no.nav.poao_tilgang.domain.DecisionReasonType
import org.springframework.stereotype.Service

@Service
class TilgangTilModiaPolicyService(private val adGruppeService: AdGruppeService) {
	fun sjekkTilgang(navIdent: String): Decision {
		val modiaRoller =
			listOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")

		val adGrupper = adGruppeService.hentAdGrupper(navIdent)

		return if (modiaRoller.intersect(adGrupper.map { it.name }.toSet()).isNotEmpty()) {
			Decision.Permit
		} else {
			Decision.Deny(
				message = "Veileder har ikke tilgang til Modia.",
				reason = DecisionReasonType.IKKE_TILGANG_TIL_MODIA
			)
		}
	}
}
