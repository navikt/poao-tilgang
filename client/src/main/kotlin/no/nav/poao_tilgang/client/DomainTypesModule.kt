package no.nav.poao_tilgang.client

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import no.nav.poao_tilgang.core.domain.Decision
import no.nav.poao_tilgang.core.domain.DecisionDenyReason

class DomainTypesModule : SimpleModule() {

	init {
		this.addDeserializer(Decision::class.java, DecisionDeserialiser())
	}

	class DecisionDeserialiser : StdDeserializer<Decision>(Decision::class.java) {
		override fun deserialize(jsonParser: JsonParser, context: DeserializationContext): Decision {
			val node = jsonParser.codec.readTree<JsonNode>(jsonParser)
			return when (Decision.Type.valueOf(node.get("type").asText())) {
				Decision.Type.PERMIT ->
					Decision.Permit
				Decision.Type.DENY ->
					Decision.Deny(
						message = node.get("message").asText(),
						reason = DecisionDenyReason.valueOf(node.get("reason").asText())
					)
			}
		}
	}
}
