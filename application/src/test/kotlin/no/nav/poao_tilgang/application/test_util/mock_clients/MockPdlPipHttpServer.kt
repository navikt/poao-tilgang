package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.client.pdl_pip.GeografiskTilknytningType
import no.nav.poao_tilgang.application.client.pdl_pip.Gradering
import no.nav.poao_tilgang.application.test_util.MockHttpServer
import no.nav.poao_tilgang.core.domain.NorskIdent
import okhttp3.mockwebserver.MockResponse

class MockPdlPipHttpServer : MockHttpServer() {

	fun mockBrukerInfo(
		norskIdent: NorskIdent,
		gradering: Gradering? = null,
		gtType: GeografiskTilknytningType = GeografiskTilknytningType.KOMMUNE,
		gtKommune: String? = null,
		gtBydel: String? = null
	) {
		val response = MockResponse()
			.setBody(
				"""
					{
					  "person": {
					    "adressebeskyttelse": [
						${gradering?.let { """{"gradering":"${it.name}"}""".trimMargin() }?:""}
						],
					    "foedsel": [
					      {
					        "foedselsdato": "1980-01-01"
					      }
					    ],
					    "doedsfall": [
					    ],
					    "familierelasjoner": [
					      {
					        "relatertPersonsIdent": "11223344550"
					      }
					    ]
					  },
					  "identer": {
					    "identer": [
					      {
					        "ident": "${norskIdent}",
					        "historisk": false,
					        "gruppe": "FOLKEREGISTERIDENT"
					      },
					      {
					        "ident": "9876543210987",
					        "historisk": false,
					        "gruppe": "AKTORID"
					      }
					    ]
					  },
					  "geografiskTilknytning": {
					    "gtType": "${gtType.name}",
					    "gtKommune": ${gtKommune?.let { """"$it"""" }},
					    "gtBydel": ${gtBydel?.let { """"$it"""" }},
					    "gtLand": "NO",
					    "regel": "42"
					  }
					}
				""".trimIndent()
			)

		handleRequest(
			matchPath = "/api/v1/person",
			matchMethod = "GET",
			matchHeaders = mapOf("ident" to norskIdent),
			response = response
		)
	}

}
