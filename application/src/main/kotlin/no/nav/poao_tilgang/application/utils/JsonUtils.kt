package no.nav.poao_tilgang.application.utils

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.PolicyEvaluationRequestDto

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY, // The discriminator is a property in the JSON object
	property = "policyId",              // The name of the discriminator property
	visible = true                      // Makes 'policyId' available for deserialization on subclasses
)
@JsonSubTypes(
	// Map the string value from the JSON to the concrete class
	Type(value = NavAnsattTilgangTilModiaPolicyRequestDto::class, name = "NAV_ANSATT_TILGANG_TIL_MODIA_V1"),
	Type(value = EksternBrukerTilgangTilEksternBrukerPolicyRequestDto::class, name = "EKSTERN_BRUKER_TILGANG_TIL_EKSTERN_BRUKER_V1"),
	Type(value = NavAnsattBehandleFortroligBrukerePolicyRequestDto::class, name = "NAV_ANSATT_BEHANDLE_FORTROLIG_BRUKERE_V1"),
	Type(value = NavAnsattBehandleSkjermedePersonerPolicyRequestDto::class, name = "NAV_ANSATT_BEHANDLE_SKJERMEDE_PERSONER_V1"),
	Type(value = NavAnsattBehandleStrengtFortroligBrukerePolicyRequestDto::class, name = "NAV_ANSATT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1"),
	Type(value = NavAnsattNavIdentBehandleFortroligBrukerePolicyRequestDto::class, name = "NAV_ANSATT_NAV_IDENT_BEHANDLE_FORTROLIG_BRUKERE_V1"),
	Type(value = NavAnsattNavIdentBehandleSkjermedePersonerPolicyRequestDto::class, name = "NAV_ANSATT_NAV_IDENT_BEHANDLE_SKJERMEDE_PERSONER_V1"),
	Type(value = NavAnsattNavIdentBehandleStrengtFortroligBrukerePolicyRequestDto::class, name = "NAV_ANSATT_NAV_IDENT_BEHANDLE_STRENGT_FORTROLIG_BRUKERE_V1"),
	Type(value = NavAnsattNavIdentLesetilgangTilEksternBrukerPolicyRequestDto::class, name = "NAV_ANSATT_NAV_IDENT_LESETILGANG_TIL_EKSTERN_BRUKER_V1"),
	Type(value = NavAnsattNavIdentSkrivetilgangTilEksternBrukerPolicyRequestDto::class, name = "NAV_ANSATT_NAV_IDENT_SKRIVETILGANG_TIL_EKSTERN_BRUKER_V1"),
	Type(value = NavAnsattNavIdentTilgangTilNavEnhetPolicyRequestDto::class, name = "NAV_ANSATT_NAV_IDENT_TILGANG_TIL_NAV_ENHET_V1"),
	Type(value = NavAnsattTilgangTilEksternBrukerPolicyRequestDto::class, name = "NAV_ANSATT_TILGANG_TIL_EKSTERN_BRUKER_V2"),
	Type(value = NavAnsattTilgangTilModiaAdminPolicyRequestDto::class, name = "NAV_ANSATT_TILGANG_TIL_MODIA_ADMIN_V1"),
	Type(value = NavAnsattTilgangTilModiaPolicyRequestDto::class, name = "NAV_ANSATT_TILGANG_TIL_MODIA_V1"),
	Type(value = NavAnsattTilgangTilNavEnhetMedSperrePolicyRequestDto::class, name = "NAV_ANSATT_TILGANG_TIL_NAV_ENHET_MED_SPERRE_V1"),
	Type(value = NavAnsattTilgangTilNavEnhetPolicyRequestDto::class, name = "NAV_ANSATT_TILGANG_TIL_NAV_ENHET_V1"),
	Type(value = NavAnsattUtenModiarolleTilgangTilEksternBrukerPolicyRequestDto::class, name = "NAV_ANSATT_UTEN_MODIAROLLE_TILGANG_TIL_EKSTERN_BRUKER_V1"),
)
abstract class PolicyEvaluationRequestMixIn

object JsonUtils {

	//TODO burde denne flyttes til api-core-mapper? da har vi bare en instans av objectmapper
	//eksisiterer også en instangs av objectmapper i api-core-mapper
	val objectMapper: ObjectMapper = ObjectMapper()
		.addMixIn(PolicyEvaluationRequestDto::class.java, PolicyEvaluationRequestMixIn::class.java)
		.registerKotlinModule()
		.registerModule(JavaTimeModule())
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

	inline fun <reified T> fromJsonString(jsonStr: String): T {
		return objectMapper.readValue(jsonStr)
	}

	fun toJsonString(any: Any): String {
		return objectMapper.writeValueAsString(any)
	}

}
