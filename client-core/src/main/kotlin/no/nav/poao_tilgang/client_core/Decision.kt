package no.nav.poao_tilgang.client_core

import no.nav.poao_tilgang.api.dto.response.DecisionDto
import no.nav.poao_tilgang.api.dto.response.DecisionType

sealed class Decision(val type: Type) {

	enum class Type {
		DENY,
		PERMIT
	}

	val isPermit: Boolean get() = type == Type.PERMIT

	val isDeny: Boolean get() = type == Type.DENY

	object Permit : Decision(Type.PERMIT)

	data class Deny(val message: String, val reason: String) : Decision(Type.DENY)

}

fun DecisionDto.toDecision(): Decision {
	return when (this.type) {
		DecisionType.PERMIT -> Decision.Permit
		DecisionType.DENY -> {
			val message = this.message
			val reason = this.reason

			check(message != null) { "message cannot be null" }
			check(reason != null) { "reason cannot be null" }

			Decision.Deny(message, reason)
		}
	}
}
