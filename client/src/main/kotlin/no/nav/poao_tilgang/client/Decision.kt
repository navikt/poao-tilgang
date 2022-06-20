package no.nav.poao_tilgang.client

sealed class Decision(val type: Type) {

	enum class Type(val decision: String) {
		DENY("DENY"),
		PERMIT("PERMIT")
	}

	object Permit : Decision(Type.PERMIT)

	data class Deny(val message: String, val reason: String) : Decision(Type.DENY)

}
