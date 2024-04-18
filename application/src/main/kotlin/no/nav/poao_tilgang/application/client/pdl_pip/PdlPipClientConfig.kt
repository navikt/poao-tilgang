package no.nav.poao_tilgang.application.client.pdl_pip

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PdlPipClientConfig(
	@Value("\${pdlpip.url}") val pdlpipUrl: String,
	@Value("\${pdlpip.scope}") val pdlpipScope: String,
) {

	@Bean
	open fun pdlpipClient(machineToMachineTokenClient: MachineToMachineTokenClient): PdlPipClient {
		val client = PdlPipClientImpl(
			baseUrl = pdlpipUrl,
			tokenProvider = { machineToMachineTokenClient.createMachineToMachineToken(pdlpipScope) }
		)
		return CachedPdlpipClient(client)
	}
}
