package no.nav.poao_tilgang.client_core

import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.PolicyEvaluationRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.EksternBrukerTilgangTilEksternBrukerPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattBehandleFortroligBrukerePolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattBehandleSkjermedePersonerPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattBehandleStrengtFortroligBrukerePolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattNavIdentBehandleFortroligBrukerePolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattNavIdentBehandleSkjermedePersonerPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattNavIdentTilgangTilNavEnhetPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattTilgangTilEksternBrukerPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattTilgangTilModiaAdminPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattTilgangTilModiaPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattTilgangTilNavEnhetMedSperrePolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattTilgangTilNavEnhetPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_input.EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattBehandleFortroligBrukerePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleFortroligBrukerePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleSkjermedePersonerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattNavIdentTilgangTilNavEnhetPolicyInputV1Dto
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


fun PolicyRequest.toRequestDto(): PolicyEvaluationRequestDto {
	return when (this.policyInput) {

		is NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInput -> NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent,
				norskIdent = this.policyInput.norskIdent,
			),
		)

		is NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInput -> NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent,
				norskIdent = this.policyInput.norskIdent,
			),
		)

		is NavAnsattTilgangTilEksternBrukerPolicyInput -> NavAnsattTilgangTilEksternBrukerPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilEksternBrukerPolicyInputV2Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId,
				norskIdent = this.policyInput.norskIdent,
				tilgangType = when (this.policyInput.tilgangType) {
					TilgangType.LESE -> no.nav.poao_tilgang.api.dto.request.TilgangType.LESE
					TilgangType.SKRIVE -> no.nav.poao_tilgang.api.dto.request.TilgangType.SKRIVE
				}
			)
		)

		is NavAnsattTilgangTilModiaPolicyInput -> NavAnsattTilgangTilModiaPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilModiaPolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId,
			)
		)

		is EksternBrukerTilgangTilEksternBrukerPolicyInput -> EksternBrukerTilgangTilEksternBrukerPolicyRequestDto(
			requestId = this.requestId,
			policyInput = EksternBrukerTilgangTilEksternBrukerPolicyInputV1Dto(
				rekvirentNorskIdent = this.policyInput.rekvirentNorskIdent,
				ressursNorskIdent = this.policyInput.ressursNorskIdent
			)
		)

		is NavAnsattTilgangTilNavEnhetPolicyInput -> NavAnsattTilgangTilNavEnhetPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilNavEnhetPolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId,
				navEnhetId = this.policyInput.navEnhetId
			)
		)

		is NavAnsattNavIdentTilgangTilNavEnhetPolicyInput -> NavAnsattNavIdentTilgangTilNavEnhetPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentTilgangTilNavEnhetPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent,
				navEnhetId = this.policyInput.navEnhetId
			)
		)

		is NavAnsattBehandleStrengtFortroligBrukerePolicyInput -> NavAnsattBehandleStrengtFortroligBrukerePolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattBehandleStrengtFortroligBrukerePolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId
			)
		)

		is NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInput -> NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyInputV1Dto(
				navIdent = this.policyInput.navIdent
			)
		)

		is NavAnsattBehandleFortroligBrukerePolicyInput -> NavAnsattBehandleFortroligBrukerePolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattBehandleFortroligBrukerePolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId
			)
		)

		is NavAnsattNavIdentBehandleFortroligBrukerePolicyInput -> NavAnsattNavIdentBehandleFortroligBrukerePolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentBehandleFortroligBrukerePolicyInputV1Dto(
				navIdent = this.policyInput.navIdent
			)
		)

		is NavAnsattTilgangTilNavEnhetMedSperrePolicyInput -> NavAnsattTilgangTilNavEnhetMedSperrePolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilNavEnhetMedSperrePolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId,
				navEnhetId = this.policyInput.navEnhetId
			)
		)

		is NavAnsattBehandleSkjermedePersonerPolicyInput -> NavAnsattBehandleSkjermedePersonerPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattBehandleSkjermedePersonerPolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId
			)
		)

		is NavAnsattNavIdentBehandleSkjermedePersonerPolicyInput -> NavAnsattNavIdentBehandleSkjermedePersonerPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattNavIdentBehandleSkjermedePersonerPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent
			)
		)

		is NavAnsattTilgangTilModiaAdminPolicyInput -> NavAnsattTilgangTilModiaAdminPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattTilgangTilModiaAdminPolicyInputV1Dto(
				navAnsattAzureId = this.policyInput.navAnsattAzureId
			)
		)

		is NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInput -> NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyRequestDto(
			requestId = this.requestId,
			policyInput = NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyInputV1Dto(
				navIdent = this.policyInput.navIdent,
				norskIdent = this.policyInput.norskIdent
			)
		)
	}
}
