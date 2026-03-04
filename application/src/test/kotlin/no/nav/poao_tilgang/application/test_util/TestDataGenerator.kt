package no.nav.poao_tilgang.application.test_util

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

/**
 * Generates unique test data to prevent cache collisions between tests
 * when the Spring context is shared across the test suite.
 */
object TestDataGenerator {

	private val counter = AtomicInteger(0)

	private fun next() = counter.incrementAndGet()

	/** Generates a unique NAV-ident on the format Z followed by 6 digits, e.g. Z100001 */
	fun navIdent(): String = "Z%06d".format(next())

	/** Generates a unique 11-digit Norwegian national identity number */
	fun norskIdent(): String = "%011d".format(next())

	/** Generates a unique 4-digit NAV enhet id */
	fun navEnhetId(): String = "%04d".format(next() % 9000 + 1000)

	/** Generates a unique NAV ansatt Azure AD UUID */
	fun navAnsattId(): UUID = UUID.randomUUID()
}

