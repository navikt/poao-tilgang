package no.nav.poao_tilgang.test_util

import no.nav.common.token_client.client.MachineToMachineTokenClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestConfig {

	@Bean
	fun machineToMachineTokenClient(): MachineToMachineTokenClient {
		return MachineToMachineTokenClient { "TOKEN" }
	}

}
