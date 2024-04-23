package no.nav.poao_tilgang.application.client.axsys

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.application.utils.SecureLog.secureLog
import java.time.Duration

private typealias NavIdent = String

class CachedAxsysClient(
	private val axsysClient: AxsysClient
) : AxsysClient {

	private val cache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofHours(1))
		.build<NavIdent, List<EnhetTilgang>> { navIdent ->
			axsysClient.hentTilganger(navIdent)
		}

	override fun hentTilganger(navIdent: String) : List<EnhetTilgang> {
		return cache.get(navIdent) ?: throw IllegalStateException("Fant ikke brukerident")
			.also {
			secureLog.info("Axsys response from cache, hentTilganger for navIdent: $navIdent, result: $it")
		}
	}

}
