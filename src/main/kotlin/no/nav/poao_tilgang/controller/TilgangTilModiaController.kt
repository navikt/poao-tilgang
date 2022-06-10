package no.nav.poao_tilgang.controller

import no.nav.poao_tilgang.domain.Decision
import no.nav.poao_tilgang.service.AuthService
import no.nav.poao_tilgang.service.TilgangTilModiaPolicyService
import no.nav.poao_tilgang.utils.Issuer
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tilgang-til-modia")
class TilgangTilModiaController(
	private val authService: AuthService,
	private val tilgangTilModiaPolicyService: TilgangTilModiaPolicyService
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun harTilgang(@RequestBody tilgangTilModiaRequest: TilgangTilModiaRequest): TilgangTilModiaResponse {
		authService.verifyRequestIsMachineToMachine()
		val decision = tilgangTilModiaPolicyService.sjekkTilgang(navIdent = tilgangTilModiaRequest.navIdent)
		return TilgangTilModiaResponse(harTilgang = decision.type == Decision.Type.PERMIT)
	}
}

data class TilgangTilModiaRequest(
	val navIdent: String
)

data class TilgangTilModiaResponse(val harTilgang: Boolean)
