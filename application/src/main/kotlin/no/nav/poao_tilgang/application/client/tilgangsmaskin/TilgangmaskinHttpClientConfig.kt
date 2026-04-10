package no.nav.poao_tilgang.application.client.tilgangsmaskin

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TilgangmaskinHttpClientConfig {

	@Value("\${tilgangsmaskin.url}")
	lateinit var url: String

	@Value("\${tilgangsmaskin.scope}")
	lateinit var scope: String

	@Bean
	open fun tilgangmaskinClient(machineToMachineTokenClient: MachineToMachineTokenClient): TilgangmaskinClient {
		return TilgangmaskinHttpClient(
			baseUrl = url,
			tokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(scope) },
		)
	}
}

