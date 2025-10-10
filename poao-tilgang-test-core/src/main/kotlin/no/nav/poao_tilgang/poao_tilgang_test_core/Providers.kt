package no.nav.poao_tilgang.poao_tilgang_test_core

import no.nav.poao_tilgang.core.domain.*
import no.nav.poao_tilgang.core.provider.*


data class Providers(
	val navContext: NavContext = NavContext(),
	val skjermetPersonProvider: SkjermetPersonProvider = SkjermetPersonProviderImpl(navContext),
	val oppfolgingsenhetProvider: OppfolgingsenhetProvider = OppfolgingsenhetProviderImpl(navContext),
	val navEnhetTilgangProviderV2: NavEnhetTilgangProviderV2 = NavEnhetTilgangProviderV2Impl(navContext),
	val geografiskTilknyttetEnhetProvider: GeografiskTilknyttetEnhetProvider = GeografiskTilknyttetEnhetProviderImpl(
		navContext
	),
	val diskresjonskodeProvider: DiskresjonskodeProvider = DiskresjonskodeProviderImpl(navContext),
	val adGruppeProvider: AdGruppeProvider = AdGruppeProviderImpl(navContext),
)

class SkjermetPersonProviderImpl(private val navContext: NavContext) : SkjermetPersonProvider {
	override fun erSkjermetPerson(norskIdent: String): Boolean {
		return navContext.privatBrukere.get(norskIdent)?.erSkjermet ?: true
	}

	override fun erSkjermetPerson(norskeIdenter: List<String>): Map<String, Boolean> {
		return norskeIdenter.map { it to erSkjermetPerson(it) }.toMap()
	}
}

class OppfolgingsenhetProviderImpl(private val navContext: NavContext) : OppfolgingsenhetProvider {
	override fun hentOppfolgingsenhet(norskIdent: NorskIdent): NavEnhetId? {
		return navContext.privatBrukere.get(norskIdent)?.oppfolgingsenhet
	}
}

class NavEnhetTilgangProviderV2Impl(private val navContext: NavContext) : NavEnhetTilgangProviderV2 {
	override fun hentEnhetTilganger(navIdent: NavIdent): Set<NavEnhetId> {
		return navContext.navAnsatt.get(navIdent)?.enheter?.map { it.enhetId }?.toSet() ?: emptySet()
	}
}

class GeografiskTilknyttetEnhetProviderImpl(private val navContext: NavContext) : GeografiskTilknyttetEnhetProvider {
	override fun hentGeografiskTilknyttetEnhet(norskIdent: NorskIdent): NavEnhetId? {
		return navContext.privatBrukere.get(norskIdent)?.oppfolgingsenhet //for enklere oppset
	}

	override fun hentGeografiskTilknyttetEnhet(
		norskIdent: NorskIdent,
		skjermet: Boolean
	): NavEnhetId? {
		return navContext.privatBrukere.get(norskIdent)?.oppfolgingsenhet
	}
}

class DiskresjonskodeProviderImpl(private val navContext: NavContext) : DiskresjonskodeProvider {
	override fun hentDiskresjonskode(norskIdent: String): Diskresjonskode? {
		return navContext.privatBrukere.get(norskIdent)?.diskresjonskode
	}
}

class AdGruppeProviderImpl(private val navContext: NavContext) : AdGruppeProvider {
	override fun hentAdGrupper(navAnsattAzureId: AzureObjectId): List<AdGruppe> {
		return navContext.navAnsatt.get(navAnsattAzureId)?.adGrupper?.toList() ?: emptyList()
	}

	override fun hentNavIdentMedAzureId(navAnsattAzureId: AzureObjectId): NavIdent {
		return navContext.navAnsatt.get(navAnsattAzureId)!!.navIdent
	}

	override fun hentAzureIdMedNavIdent(navIdent: NavIdent): AzureObjectId {
		return navContext.navAnsatt.get(navIdent)!!.azureObjectId
	}

	override fun hentTilgjengeligeAdGrupper(): AdGrupper {
		return tilgjengligeAdGrupper
	}
}
