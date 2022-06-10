package no.nav.poao_tilgang.domain

sealed class Decision(val type: Type) {

	enum class Type(val decision: String) {
		DENY("DENY"),
		PERMIT("PERMIT")
	}

	object Permit : Decision(Type.PERMIT) {
		override fun toString() = "Permit"
	}

	data class Deny(val message: String, val reason: DecisionReasonType) : Decision(Type.DENY)
}
