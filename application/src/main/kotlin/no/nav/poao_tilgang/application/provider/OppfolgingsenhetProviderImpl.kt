package no.nav.poao_tilgang.application.provider

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.common.types.identer.Fnr
import no.nav.poao_tilgang.application.client.veilarbarena.PersonRequest
import no.nav.poao_tilgang.application.client.veilarbarena.VeilarbarenaClient
import no.nav.poao_tilgang.application.utils.SecureLog.secureLog
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OppfolgingsenhetProviderImpl(
	private val veilarbarenaClient: VeilarbarenaClient
) : OppfolgingsenhetProvider {

	private val norskIdentToOppfolgingsenhetCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofHours(4))
		.build<NorskIdent, NavEnhetId>()

	override fun hentOppfolgingsenhet(norskIdent: NorskIdent): NavEnhetId? {
		val personRequest = PersonRequest(Fnr.of(norskIdent))
		return runCatching {
			veilarbarenaClient.hentBrukerOppfolgingsenhetId(personRequest)
		}.onSuccess {
			if (it != null) norskIdentToOppfolgingsenhetCache.put(norskIdent, it)
		}.onFailure {
			secureLog.warn("Feil under kall til veilarbarenaClient.hentBrukerOppfolgingsenhetId for norskIdent: ${personRequest.fnr}. Forsøker fallback til cache")
		}.recover {
			norskIdentToOppfolgingsenhetCache.getIfPresent(norskIdent) ?: throw it
		}
			.getOrNull()
			.also {
//				secureLog.info("Veilarbarena , hentOppfolgingsEnhetId for norskIdent: ${personRequest.fnr}, result: $it")
			}
	}

}
