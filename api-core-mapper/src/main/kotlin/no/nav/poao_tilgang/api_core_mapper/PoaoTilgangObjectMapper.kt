package no.nav.poao_tilgang.api_core_mapper

import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.cfg.EnumFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule

object PoaoTilgangObjectMapper {
	val objectMapper: JsonMapper = JsonMapper.builder()
		.addModule(KotlinModule.Builder().build())
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
		.build()
}
