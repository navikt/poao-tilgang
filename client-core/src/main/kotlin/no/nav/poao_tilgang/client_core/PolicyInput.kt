package no.nav.poao_tilgang.client_core

import no.nav.poao_tilgang.api.dto.request.PolicyEvaluationRequestDto
import no.nav.poao_tilgang.api.dto.request.PolicyId
import no.nav.poao_tilgang.api.dto.request.policy_input.EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattBehandleFortroligBrukerePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleFortroligBrukerePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleSkjermedePersonerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilModiaAdminPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilModiaPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilNavEnhetPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInputV1Dto
import java.util.*

enum class TilgangType {
	LESE, SKRIVE
}

sealed class PolicyInput

data class NavAnsattTilgangTilEksternBrukerPolicyInput(
	val navAnsattAzureId: UUID,
	val tilgangType: TilgangType,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInput(
	val navIdent: String,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInput(
	val navIdent: String,
	val norskIdent: String
) : PolicyInput()

data class NavAnsattTilgangTilModiaPolicyInput(
	val navAnsattAzureId: UUID
) : PolicyInput()

data class NavAnsattTilgangTilModiaAdminPolicyInput(
	val navAnsattAzureId: UUID
) : PolicyInput()

data class EksternBrukerTilgangTilEksternBrukerPolicyInput(
	val rekvirentNorskIdent: String, // Den som ber om tilgang
	val ressursNorskIdent: String // Den som bes tilgang om
) : PolicyInput()

data class NavAnsattTilgangTilNavEnhetPolicyInput(
	val navAnsattAzureId: UUID,
	val navEnhetId: String
) : PolicyInput()

data class NavAnsattNavIdentTilgangTilNavEnhetPolicyInput(
	val navIdent: String,
	val navEnhetId: String
) : PolicyInput()

data class NavAnsattBehandleStrengtFortroligBrukerePolicyInput(
	val navAnsattAzureId: UUID,
) : PolicyInput()

data class NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInput(
	val navIdent: String,
) : PolicyInput()

data class NavAnsattBehandleFortroligBrukerePolicyInput(
	val navAnsattAzureId: UUID,
) : PolicyInput()

data class NavAnsattNavIdentBehandleFortroligBrukerePolicyInput(
	val navIdent: String,
) : PolicyInput()

data class NavAnsattTilgangTilNavEnhetMedSperrePolicyInput(
	val navAnsattAzureId: UUID,
	val navEnhetId: String
) : PolicyInput()

data class NavAnsattBehandleSkjermedePersonerPolicyInput(
	val navAnsattAzureId: UUID,
) : PolicyInput()

data class NavAnsattNavIdentBehandleSkjermedePersonerPolicyInput(
	val navIdent: String,
) : PolicyInput()

data class NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInput (
	val navIdent: UUID,
	val norskIdent: String
) : PolicyInput()


fun PolicyRequest.toRequestDto(): PolicyEvaluationRequestDto<Any> {
	return when (this.policyInput) {

		is NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent,
				norskIdent = this.policyInput.norskIdent,

				),
			policyId = PolicyId.NAV_ANSATT_NAV_IDENT_SKRIVETILGANG_TIL_EKSTERN_BRUKER_V1
		)

		is NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent,
				norskIdent = this.policyInput.norskIdent,

				),
			policyId = PolicyId.NAV_ANSATT_NAV_IDENT_LESETILGANG_TIL_EKSTERN_BRUKER_V1
		)

		is NavAnsattTilgangTilEksternBrukerPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId,
				norskIdent = this.policyInput.norskIdent,
				tilgangType = when (this.policyInput.tilgangType) {
					TilgangType.LESE -> no.nav.poao_tilgang.api.dto.request.TilgangType.LESE
					TilgangType.SKRIVE -> no.nav.poao_tilgang.api.dto.request.TilgangType.SKRIVE
				}
			),
			policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2
		)

		is NavAnsattTilgangTilModiaPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilModiaPolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId,
			),
			policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_V1
		)

		is EksternBrukerTilgangTilEksternBrukerPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto(
				rekvirentNorskIdent = this.policyInput.rekvirentNorskIdent,
				ressursNorskIdent = this.policyInput.ressursNorskIdent
			),
			policyId = PolicyId.EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1
		)

		is NavAnsattTilgangTilNavEnhetPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilNavEnhetPolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId,
				navEnhetId = this.policyInput.navEnhetId
			),
			policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1
		)

		is NavAnsattNavIdentTilgangTilNavEnhetPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentTilgangTilNavEnhetPolicyInput(
				navIdent = this.policyInput.navIdent,
				navEnhetId = this.policyInput.navEnhetId
			),
			policyId = PolicyId.NAV_ANSATT_NAV_IDENT_TILGANG_TIL_NAV_ENHET_V1
		)

		is NavAnsattBehandleStrengtFortroligBrukerePolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId
			),
			policyId = PolicyId.NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1
		)

		is NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInputV1Dto(
				navIdent = this.policyInput.navIdent
			),
			policyId = PolicyId.NAV_ANSATT_NAV_IDENT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1
		)

		is NavAnsattBehandleFortroligBrukerePolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattBehandleFortroligBrukerePolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId
			),
			policyId = PolicyId.NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1
		)

		is NavAnsattNavIdentBehandleFortroligBrukerePolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentBehandleFortroligBrukerePolicyInputV1Dto(
				navIdent = this.policyInput.navIdent
			),
			policyId = PolicyId.NAV_ANSATT_NAV_IDENT_BEHANDLE_FORTROLIG_BRUKERE_V1
		)

		is NavAnsattTilgangTilNavEnhetMedSperrePolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId,
				navEnhetId = this.policyInput.navEnhetId
			),
			policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1
		)

		is NavAnsattBehandleSkjermedePersonerPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId
			),
			policyId = PolicyId.NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER_V1
		)

		is NavAnsattNavIdentBehandleSkjermedePersonerPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentBehandleSkjermedePersonerPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent
			),
			policyId = PolicyId.NAV_ANSATT_NAV_IDENT_BEHANDLE_SKJERMEDE_PERSONER_V1
		)

		is NavAnsattTilgangTilModiaAdminPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilModiaAdminPolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId
			),
			policyId = PolicyId.NAV_ANSATT_TILGANG_TIL_MODIA_ADMIN_V1
		)

		is NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInput -> PolicyEvaluationRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent,
				norskIdent = this.policyInput.norskIdent
			),
			policyId = PolicyId.NAV_ANSATT_UTEN_MODIAROLLE_TILGANG_TIL_EKSTERN_BRUKER_V1
		)
	}
}
