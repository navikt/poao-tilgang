package no.nav.poao_tilgang.core.domain

enum class DecisionDenyReason {
	MANGLER_TILGANG_TIL_AD_GRUPPE,
	POLICY_IKKE_IMPLEMENTERT,
	IKKE_TILGANG_FRA_ABAC,
	IKKE_TILGANG_TIL_NAV_ENHET,
	UKLAR_TILGANG_MANGLENDE_INFORMASJON,
	EKSTERN_BRUKER_HAR_IKKE_TILGANG,
}
