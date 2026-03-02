package no.nav.poao_tilgang.application.client.ao_oppfolgingskontor

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AoKontorConfig {

	@Value("\${ao-kontor.url}")
	lateinit var url: String

	@Value("\${ao-kontor.scope}")
	lateinit var scope: String

	@Bean
	open fun aoKontorClient(machineToMachineTokenClient: MachineToMachineTokenClient): AoKontorClient {
		return AoKontorClientImpl(
			baseUrl = url,
			tokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(scope) },
		)
	}
}
