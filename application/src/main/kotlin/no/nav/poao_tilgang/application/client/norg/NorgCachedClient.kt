package no.nav.poao_tilgang.application.client.norg

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstNullable
import no.nav.poao_tilgang.core.domain.Diskresjonskode
import no.nav.poao_tilgang.core.domain.NavEnhetId

class NorgCachedClient(private val norgClient: NorgClient) : NorgClient {

    private val hentTilhorendeNavEnhetIdCache: Cache<String, NavEnhetId> = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofHours(1))
        .build()

	override fun hentTilhorendeEnhet(geografiskTilknytning: String, skjermet: Boolean?, diskresjonskode: Diskresjonskode?): NavEnhetId? {
		if (skjermet != true && diskresjonskode == null) {
			return tryCacheFirstNullable(hentTilhorendeNavEnhetIdCache, geografiskTilknytning) {
				return@tryCacheFirstNullable norgClient.hentTilhorendeEnhet(geografiskTilknytning, skjermet, diskresjonskode)
			}
		} else {
			return norgClient.hentTilhorendeEnhet(geografiskTilknytning, skjermet, diskresjonskode)
		}
	}
}
