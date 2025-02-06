package no.nav.poao_tilgang.application.controller

import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.api.dto.response.TilgangsattributterResponse
import no.nav.poao_tilgang.application.service.AuthService
import no.nav.poao_tilgang.application.utils.Issuer
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import no.nav.poao_tilgang.core.provider.SkjermetPersonProvider
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import no.nav.poao_tilgang.api_core_mapper.toApiDto
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider

@RestController
@RequestMapping("/api/v1/tilgangsattributter")
class TilgangsattributtController(
	private val authService: AuthService,
	private val skjermetPersonProvider: SkjermetPersonProvider,
	private val geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider,
	private val diskresjonskodeProvider: DiskresjonskodeProvider,
	private val oppfolgingsenhetProvider: OppfolgingsenhetProvider
) {

	@ProtectedWithClaims(issuer = Issuer.AZURE_AD)
	@PostMapping
	fun tilgangsattributter(@RequestBody norskIdent: NorskIdent): TilgangsattributterResponse {
		authService.verifyRequestIsMachineToMachine()
		val erSkjermetPerson = skjermetPersonProvider.erSkjermetPerson(norskIdent)
		val kontor = oppfolgingsenhetProvider.hentOppfolgingsenhet(norskIdent)?:
			geografiskTilknyttetEnhetProvider.hentGeografiskTilknyttetEnhet(norskIdent, erSkjermetPerson)

		val diskresjonskode = diskresjonskodeProvider.hentDiskresjonskode(norskIdent)?.toApiDto()?: Diskresjonskode.UGRADERT
		return TilgangsattributterResponse(
			kontor = kontor,
			skjermet = erSkjermetPerson,
			diskresjonskode = diskresjonskode
		)
	}
}
