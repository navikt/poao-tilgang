package no.nav.poao_tilgang.application.provider

import com.github.benmanes.caffeine.cache.Caffeine
import no.nav.poao_tilgang.application.client.microsoft_graph.MicrosoftGraphClient
import no.nav.poao_tilgang.application.utils.CacheUtils.tryCacheFirstNotNull
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGruppeNavn
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class AdGruppeProviderImpl(
	private val microsoftGraphClient: MicrosoftGraphClient,
	@Value("\${ad-gruppe-id.fortrolig-adresse}") private val adGruppeIdFortroligAdresse: UUID
) : AdGruppeProvider {

	private val tilgjengligeAdGrupper = AdGrupper(
		fortroligAdresse = AdGruppe(adGruppeIdFortroligAdresse, AdGruppeNavn.FORTROLIG_ADRESSE)
	)


	private val navIdentToAzureIdCache = Caffeine.newBuilder()
		.maximumSize(10_000)
		.build<String, AzureObjectId>()

	// TODO: Bruk heller List<UUID> for å redusere minnebruk
	private val navAnsattAzureIdToAdGroupsCache = Caffeine.newBuilder()
		.expireAfterWrite(Duration.ofMinutes(15))
		.maximumSize(10_000)
		.build<AzureObjectId, List<AdGruppe>>()

	private val adGruppeIdToAdGruppeCache = Caffeine.newBuilder()
		.maximumSize(1000)
		.build<AzureObjectId, AdGruppe>()

	override fun hentAdGrupper(navIdent: String): List<AdGruppe> {
		val azureId = hentAzureIdWithCache(navIdent)

		return hentAdGrupperForNavAnsattWithCache(azureId)
	}

	override fun hentAdGrupper(azureId: AzureObjectId): List<AdGruppe> {
		return hentAdGrupperForNavAnsattWithCache(azureId)
	}

	override fun hentTilgjengeligeAdGrupper(): AdGrupper {
		return tilgjengligeAdGrupper
	}

	private fun hentAdGrupperForNavAnsattWithCache(azureId: AzureObjectId): List<AdGruppe> {
		return tryCacheFirstNotNull(navAnsattAzureIdToAdGroupsCache, azureId) {
			val gruppeIder = microsoftGraphClient.hentAdGrupperForNavAnsatt(azureId)

			hentAdGrupperWithCache(gruppeIder)
		}
	}

	private fun hentAdGrupperWithCache(adGruppeIder: List<AzureObjectId>): List<AdGruppe> {
		val cachedGroups = mutableListOf<AdGruppe>()
		val missingGroups = mutableListOf<AzureObjectId>()

		adGruppeIder.forEach {
			val gruppe = adGruppeIdToAdGruppeCache.getIfPresent(it)

			if (gruppe != null) {
				cachedGroups.add(gruppe)
			} else {
				missingGroups.add(it)
			}
		}

		if (missingGroups.isEmpty()) {
			return cachedGroups
		}

		val adGrupper = microsoftGraphClient.hentAdGrupper(missingGroups)

		adGrupper.forEach {
			val gruppe = AdGruppe(it.id, it.name)

			adGruppeIdToAdGruppeCache.put(it.id, gruppe)
			cachedGroups.add(gruppe)
		}

		return cachedGroups
	}

	private fun hentAzureIdWithCache(navIdent: String): AzureObjectId {
		return tryCacheFirstNotNull(navIdentToAzureIdCache, navIdent) {
			microsoftGraphClient.hentAzureIdForNavAnsatt(
				navIdent
			)
		}
	}

}
