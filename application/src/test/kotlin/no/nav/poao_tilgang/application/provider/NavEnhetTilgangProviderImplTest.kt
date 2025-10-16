package no.nav.poao_tilgang.application.provider

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class NavEnhetTilgangProviderImplTest {

	@ParameterizedTest
	@MethodSource("gyldigeAdGruppeTilEnhetMappinger")
	fun `skal returnere validert NavEnhetId`(adGruppeTilEnhetMapping: Pair<String, String>) {
		val (first, second) = adGruppeTilEnhetMapping
		val validertNavEnhetId = tilNavEnhetId(first)
		assertEquals(second, validertNavEnhetId)
	}

	@ParameterizedTest
	@ValueSource(
		strings = [
			"0000-GA-ENHET_ABCD",
			"1234-GA-ENHET_1234",
			"0000-GA-ENHET_12345",
			"0000-GA-ENHET_$%&#",
			"0000",
			"0000-GA-ENHET",
			"0000-GA-ENHET_",
			"0000-GA-ENHET_0",
			"0000-GA-ENHET_1",
			"0000-GA-ENHET_12",
			"0000-GA-ENHET_123",
			"0000-GA-ENHET_123%",
			"0000-GA-ENHET_12%4",
			"0000-GA-ENHET_1!34",
			"0000-GA-ENHET__1234",
			"",
			" "
		]
	)
	fun `skal feile validering`(ugyldigAdGruppe: String) {
		assertThrows<NavEnhetIdValideringException> {
			tilNavEnhetId(
				ugyldigAdGruppe
			)
		}
	}

	companion object {
		@JvmStatic
		fun gyldigeAdGruppeTilEnhetMappinger(): List<Pair<String, String>> {
			return listOf(
				"0000-GA-ENHET_1234" to "1234",
				"0000-GA-ENHET_0123" to "0123",
				"0000-GA-ENHET_9090" to "9090"
			)
		}
	}
}
