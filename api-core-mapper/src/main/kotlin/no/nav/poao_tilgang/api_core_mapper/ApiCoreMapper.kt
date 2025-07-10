package no.nav.poao_tilgang.api_core_mapper

import no.nav.poao_tilgang.api.dto.request.policy_input.*
import no.nav.poao_tilgang.core.domain.PolicyInput
import no.nav.poao_tilgang.core.domain.TilgangType
import no.nav.poao_tilgang.core.policy.*
import no.nav.poao_tilgang.core.provider.AdGruppeProvider

class ApiCoreMapper(private val adGruppeProvider: AdGruppeProvider) {
	fun mapToPolicyInput(dto: RequestPolicyInput): PolicyInput {
		return when (dto) {
			is NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto -> {
				NavAnsattTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent),
					norskIdent = dto.norskIdent,
					tilgangType = TilgangType.SKRIVE
				)
			}
			is NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInputV1Dto -> {
				NavAnsattTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent),
					norskIdent = dto.norskIdent,
					tilgangType = TilgangType.LESE
				)
			}
			is NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto -> {
				NavAnsattTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId,
					norskIdent = dto.norskIdent,
					tilgangType = when (dto.tilgangType) {
						no.nav.poao_tilgang.api.dto.request.TilgangType.LESE -> TilgangType.LESE
						no.nav.poao_tilgang.api.dto.request.TilgangType.SKRIVE -> TilgangType.SKRIVE
					}
				)
			}
			is NavAnsattTilgangTilModiaPolicyInputV1Dto -> {
				NavAnsattTilgangTilModiaPolicy.Input(dto.navAnsattAzureId)
			}
			is NavAnsattTilgangTilModiaAdminPolicyInputV1Dto -> {
				NavAnsattTilgangTilModiaAdminPolicy.Input(dto.navAnsattAzureId)
			}
			is EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto -> {
				EksternBrukerTilgangTilEksternBrukerPolicy.Input(
					rekvirentNorskIdent = dto.rekvirentNorskIdent,
					ressursNorskIdent = dto.ressursNorskIdent
				)
			}
			is NavAnsattTilgangTilNavEnhetPolicyInputV1Dto-> {
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navEnhetId = dto.navEnhetId,
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}
			is NavAnsattNavIdentTilgangTilNavEnhetPolicyInputV1Dto -> {
				NavAnsattTilgangTilNavEnhetPolicy.Input(
					navEnhetId = dto.navEnhetId,
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent)
				)
			}
			is NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto -> {
				NavAnsattBehandleStrengtFortroligBrukerePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}
			is NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInputV1Dto -> {
				NavAnsattBehandleStrengtFortroligBrukerePolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent)
				)
			}
			is NavAnsattBehandleFortroligBrukerePolicyInputV1Dto -> {
				NavAnsattBehandleFortroligBrukerePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}
			is NavAnsattNavIdentBehandleFortroligBrukerePolicyInputV1Dto -> {
				NavAnsattBehandleFortroligBrukerePolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent)
				)
			}
			is NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto -> {
				NavAnsattTilgangTilNavEnhetMedSperrePolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId,
					navEnhetId = dto.navEnhetId,
				)
			}
			is NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto -> {
				NavAnsattBehandleSkjermedePersonerPolicy.Input(
					navAnsattAzureId = dto.navAnsattAzureId
				)
			}

			is NavAnsattNavIdentBehandleSkjermedePersonerPolicyInputV1Dto -> {
				NavAnsattBehandleSkjermedePersonerPolicy.Input(
					navAnsattAzureId = adGruppeProvider.hentAzureIdMedNavIdent(dto.navIdent)
				)
			}
			is NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInputV1Dto -> {
				NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicy.Input(
					navAnsattAzureId = dto.navIdent,
					norskIdent = dto.norskIdent
				)
			}
		}
	}
}
fun no.nav.poao_tilgang.core.domain.Diskresjonskode.toApiDto(): no.nav.poao_tilgang.api.dto.response.Diskresjonskode {
	return when (this) {
		no.nav.poao_tilgang.core.domain.Diskresjonskode.STRENGT_FORTROLIG_UTLAND -> no.nav.poao_tilgang.api.dto.response.Diskresjonskode.STRENGT_FORTROLIG_UTLAND
		no.nav.poao_tilgang.core.domain.Diskresjonskode.STRENGT_FORTROLIG -> no.nav.poao_tilgang.api.dto.response.Diskresjonskode.STRENGT_FORTROLIG
		no.nav.poao_tilgang.core.domain.Diskresjonskode.FORTROLIG -> no.nav.poao_tilgang.api.dto.response.Diskresjonskode.FORTROLIG
		no.nav.poao_tilgang.core.domain.Diskresjonskode.UGRADERT -> no.nav.poao_tilgang.api.dto.response.Diskresjonskode.UGRADERT
	}
}
