package no.nav.poao_tilgang.application.client.pdl_pip

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstNullable
import no.nav.poao_tilgang.application.utils.SecureLog.secureLog
import no.nav.poao_tilgang.core.domain.NorskIdent
import java.time.Duration

class CachedPdlpipClient(
	private val pdlPipClient: PdlPipClient
) : PdlPipClient {

	private val norskIdentToBrukerInfoCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofHours(1))
		.build<NorskIdent, BrukerInfo>()

	override fun hentBrukerInfo(brukerIdent: String): BrukerInfo? {
		return tryCacheFirstNullable(norskIdentToBrukerInfoCache, brukerIdent) {
			pdlPipClient.hentBrukerInfo(brukerIdent)
		}.also {
			secureLog.info("PdlPip response, hentPerson for fnr: $brukerIdent, body: $it")
		}
	}

}
