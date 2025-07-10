package no.nav.poao_tilgang.poao_tilgang_test_wiremock

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import no.nav.poao_tilgang.api.dto.request.EvaluatePoliciesRequest
import no.nav.poao_tilgang.api.dto.request.policy_evaluation_request.NavAnsattTilgangTilModiaPolicyRequestDto
import no.nav.poao_tilgang.api.dto.request.policy_input.NavAnsattTilgangTilModiaPolicyInputV1Dto
import no.nav.poao_tilgang.client.ClientObjectMapper
import no.nav.poao_tilgang.client_core.Decision
import no.nav.poao_tilgang.client_core.NavAnsattTilgangTilModiaPolicyInput
import no.nav.poao_tilgang.client.PoaoTilgangOkHttpClient
import no.nav.poao_tilgang.client_core.PoaoTilgangClient
import no.nav.poao_tilgang.poao_tilgang_test_core.NavAnsatt
import no.nav.poao_tilgang.poao_tilgang_test_core.tilgjengligeAdGrupper
import org.junit.jupiter.api.Test
import java.util.UUID


class PoaoWireMockTest {
	val managedWiermock = PoaoTilgangWiremock()
	val baseUrl = managedWiermock.wireMockServer.baseUrl()
	val navContext = managedWiermock.navContext

	val poaoTilgangHttpClient: PoaoTilgangClient = PoaoTilgangOkHttpClient(baseUrl, { "kake" })


	@Test
	fun skjermet_person() {
		val privatBruker = navContext.privatBrukere.ny()
		val erSkjermetPersonFalse = poaoTilgangHttpClient.erSkjermetPerson(privatBruker.norskIdent)
		erSkjermetPersonFalse.get() shouldBe false

		privatBruker.erSkjermet = true
		val erSkjermetPersonTrue = poaoTilgangHttpClient.erSkjermetPerson(privatBruker.norskIdent)
		erSkjermetPersonTrue.get() shouldBe true

	}

	@Test
	fun skal_hente_adGrupper() {
		val nyNksAnsatt = navContext.navAnsatt.nyNksAnsatt()
		val anttal_roller = nyNksAnsatt.adGrupper.size
		val hentAdGrupper = poaoTilgangHttpClient.hentAdGrupper(nyNksAnsatt.azureObjectId)
		hentAdGrupper.get()!!.size shouldBe nyNksAnsatt.adGrupper.size

		nyNksAnsatt.adGrupper.add(tilgjengligeAdGrupper.aktivitetsplanKvp)
		val hentAdGrupper_pluss_1 = poaoTilgangHttpClient.hentAdGrupper(nyNksAnsatt.azureObjectId)
		hentAdGrupper_pluss_1.get()!!.size shouldBe nyNksAnsatt.adGrupper.size

		withClue("sjekk at vi har lagt til i modellen") {
			nyNksAnsatt.adGrupper.size shouldBe  anttal_roller +1
		}
	}


	@Test
	fun skal_evaluere_polecy() {
		val nyNksAnsatt = navContext.navAnsatt.nyNksAnsatt()
		val premitDesicion =
			poaoTilgangHttpClient.evaluatePolicy(NavAnsattTilgangTilModiaPolicyInput(nyNksAnsatt.azureObjectId))

		premitDesicion.get() shouldBe Decision.Permit

		val utenTilgang = NavAnsatt()
		navContext.navAnsatt.add(utenTilgang)

		poaoTilgangHttpClient.evaluatePolicy(NavAnsattTilgangTilModiaPolicyInput(utenTilgang.azureObjectId)).get()?.isDeny shouldBe true
	}

	@Test
	fun `skal serializere evaluatepolicyDto`() {
		val request = EvaluatePoliciesRequest(
			requests = listOf(
				NavAnsattTilgangTilModiaPolicyRequestDto(
					requestId = UUID.randomUUID(),
					policyInput = NavAnsattTilgangTilModiaPolicyInputV1Dto(
						navAnsattAzureId = navContext.navAnsatt.nyNksAnsatt().azureObjectId
					)
				)
			)
		)
		val objectMapper = ClientObjectMapper.objectMapper
		val string = objectMapper.writeValueAsString(request)
		objectMapper.readValue<EvaluatePoliciesRequest>(string)
	}

}
