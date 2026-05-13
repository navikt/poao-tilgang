package no.nav.poao_tilgang.poao_tilgang_test_core

import no.nav.poao_tilgang.core.domain.NavEnhetId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

private val navidener: MutableSet<String> = ConcurrentHashMap.newKeySet()

fun nyNavIdent(): String {
	val navIdent = ('A'..'Z').random() + (100000..999999).random().toString()
	if (!navidener.add(navIdent)) {
		return nyNavIdent()
	}
	return navIdent
}

private val enhetCounter = AtomicInteger(1000)

fun nyNavEnhet(): NavEnhetId {
	val value = enhetCounter.getAndIncrement()
	if (value > 9999) error("Ran out of unique nav enhet IDs")
	return value.toString()
}
