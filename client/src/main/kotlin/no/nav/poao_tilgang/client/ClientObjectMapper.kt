package no.nav.poao_tilgang.client

import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.cfg.EnumFeature
import tools.jackson.databind.json.JsonMapper

internal object ClientObjectMapper {
	val objectMapper: JsonMapper = JsonMapper.builder()
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
		.build()
}
