package no.nav.poao_tilgang.application.utils

import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue

object JsonUtils {

	//TODO burde denne flyttes til api-core-mapper? da har vi bare en instans av objectmapper
	//eksisiterer også en instangs av objectmapper i api-core-mapper
	val objectMapper: JsonMapper = JsonMapper.builder()
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.build()

	inline fun <reified T> fromJsonString(jsonStr: String): T {
		return objectMapper.readValue(jsonStr)
	}

	fun toJsonString(any: Any): String {
		return objectMapper.writeValueAsString(any)
	}

}
