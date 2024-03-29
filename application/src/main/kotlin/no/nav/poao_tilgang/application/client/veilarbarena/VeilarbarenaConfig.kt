package no.nav.poao_tilgang.application.client.veilarbarena

import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.poao_tilgang.application.config.ApplicationConfig.Companion.APPLICATION_NAME
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class VeilarbarenaConfig {

	@Value("\${veilarbarena.url}")
	lateinit var url: String

	@Value("\${veilarbarena.scope}")
	lateinit var scope: String

	@Bean
	open fun veilarbarenaClient(machineToMachineTokenClient: MachineToMachineTokenClient): VeilarbarenaClient {
		return VeilarbarenaClientImpl(
			baseUrl = url,
			tokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(scope) },
			consumerId = APPLICATION_NAME
		)
	}

}
