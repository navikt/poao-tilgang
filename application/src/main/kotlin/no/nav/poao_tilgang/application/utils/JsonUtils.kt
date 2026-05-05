package no.nav.poao_tilgang.application.utils

import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import no.nav.poao_tilgang.api_core_mapper.PoaoTilgangObjectMapper

object JsonUtils {

	val objectMapper: JsonMapper = PoaoTilgangObjectMapper.objectMapper

	inline fun <reified T> fromJsonString(jsonStr: String): T {
		return objectMapper.readValue(jsonStr)
	}

	fun toJsonString(any: Any): String {
		return objectMapper.writeValueAsString(any)
	}

}
