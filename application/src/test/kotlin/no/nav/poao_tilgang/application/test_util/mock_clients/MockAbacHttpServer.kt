package no.nav.poao_tilgang.application.test_util.mock_clients

import no.nav.poao_tilgang.application.test_util.MockHttpServer
import okhttp3.mockwebserver.MockResponse

class MockAbacHttpServer : MockHttpServer() {

	fun mockPermit() {
		handleRequest(
			matchPath = "/",
			matchMethod = "POST",
			response = MockResponse()
				.setBody(
					"""
						{
						  "Response": {
						    "Decision": "Permit",
						    "Status": {
						      "StatusCode": {
						        "Value": "urn:oasis:names:tc:xacml:1.0:status:ok",
						        "StatusCode": {
						          "Value": "urn:oasis:names:tc:xacml:1.0:status:ok"
						        }
						      }
						    }
						  }
						}
					""".trimIndent()
				)
		)
	}

	fun mockDeny() {
		handleRequest(
			matchPath = "/",
			matchMethod = "POST",
			response = MockResponse()
				.setBody(
					"""
						{
						  "Response": {
						    "Decision": "Deny",
						    "Status": {
						      "StatusCode": {
						        "Value": "urn:oasis:names:tc:xacml:1.0:status:ok",
						        "StatusCode": {
						          "Value": "urn:oasis:names:tc:xacml:1.0:status:ok"
						        }
						      }
						    },
						    "AssociatedAdvice": {
						      "Id": "no.nav.abac.advices.reason.deny_reason",
						      "AttributeAssignment": [
						        {
						          "AttributeId": "no.nav.abac.advice.fritekst",
						          "Value": "Ikke tilgang",
						          "Category": "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject",
						          "DataType": "http://www.w3.org/2001/XMLSchema#string"
						        },
						        {
						          "AttributeId": "no.nav.abac.attributter.adviceorobligation.cause",
						          "Value": "cause",
						          "Category": "urn:oasis:names:tc:xacml:3.0:attribute-category:environment",
						          "DataType": "http://www.w3.org/2001/XMLSchema#string"
						        },
						        {
						          "AttributeId": "no.nav.abac.attributter.adviceorobligation.deny_policy",
						          "Value": "deny_policy",
						          "Category": "urn:oasis:names:tc:xacml:3.0:attribute-category:environment",
						          "DataType": "http://www.w3.org/2001/XMLSchema#string"
						        },
						        {
						          "AttributeId": "no.nav.abac.attributter.adviceorobligation.deny_rule",
						          "Value": "deny_rule",
						          "Category": "urn:oasis:names:tc:xacml:3.0:attribute-category:environment",
						          "DataType": "http://www.w3.org/2001/XMLSchema#string"
						        }
						      ]
						    }
						  }
						}
					""".trimIndent()
				)
		)
	}

}
