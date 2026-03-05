package no.nav.poao_tilgang.poao_tilgang_test_fidelity

import no.nav.poao_tilgang.api.dto.response.Diskresjonskode
import no.nav.poao_tilgang.core.domain.AdGruppe
import no.nav.poao_tilgang.core.domain.AdGrupper
import no.nav.poao_tilgang.core.domain.AzureObjectId
import no.nav.poao_tilgang.core.domain.Diskresjonskode as CoreDiskresjonskode
import no.nav.poao_tilgang.core.domain.NavEnhetId
import no.nav.poao_tilgang.core.domain.NavIdent
import no.nav.poao_tilgang.core.domain.NorskIdent
import no.nav.poao_tilgang.core.provider.AdGruppeProvider
import no.nav.poao_tilgang.core.provider.DiskresjonskodeProvider
import no.nav.poao_tilgang.core.provider.GeografiskTilknyttetEnhetProvider
import no.nav.poao_tilgang.core.provider.NavEnhetTilgangProviderV2
import no.nav.poao_tilgang.core.provider.OppfolgingsenhetProvider
import no.nav.poao_tilgang.core.provider.SkjermetPersonProvider
import no.nav.poao_tilgang.poao_tilgang_test_core.NavContext
import no.nav.poao_tilgang.poao_tilgang_test_core.tilgjengligeAdGrupper


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
	override fun hentDiskresjonskode(norskIdent: String): CoreDiskresjonskode? {
		return navContext.privatBrukere.get(norskIdent)?.diskresjonskode?.toCore()
	}
}

class AdGruppeProviderImpl(private val navContext: NavContext) : AdGruppeProvider {
	override fun hentAdGrupper(navAnsattAzureId: AzureObjectId): List<AdGruppe> {
		return navContext.navAnsatt.get(navAnsattAzureId)?.adGrupper?.map { it.toCore() } ?: emptyList()
	}

	override fun hentNavIdentMedAzureId(navAnsattAzureId: AzureObjectId): NavIdent {
		return navContext.navAnsatt.get(navAnsattAzureId)!!.navIdent
	}

	override fun hentAzureIdMedNavIdent(navIdent: NavIdent): AzureObjectId {
		return navContext.navAnsatt.get(navIdent)!!.azureObjectId
	}

	override fun hentTilgjengeligeAdGrupper(): AdGrupper {
		return tilgjengligeAdGrupper.toCore()
	}
}

private fun no.nav.poao_tilgang.poao_tilgang_test_core.AdGruppe.toCore(): AdGruppe {
	return AdGruppe(id, navn)
}

private fun no.nav.poao_tilgang.poao_tilgang_test_core.AdGrupper.toCore(): AdGrupper {
	return AdGrupper(
		fortroligAdresse = fortroligAdresse.toCore(),
		strengtFortroligAdresse = strengtFortroligAdresse.toCore(),
		modiaAdmin = modiaAdmin.toCore(),
		modiaOppfolging = modiaOppfolging.toCore(),
		modiaGenerell = modiaGenerell.toCore(),
		gosysNasjonal = gosysNasjonal.toCore(),
		gosysUtvidbarTilNasjonal = gosysUtvidbarTilNasjonal.toCore(),
		syfoSensitiv = syfoSensitiv.toCore(),
		egneAnsatte = egneAnsatte.toCore(),
		aktivitetsplanKvp = aktivitetsplanKvp.toCore()
	)
}

private fun Diskresjonskode.toCore(): CoreDiskresjonskode {
	return CoreDiskresjonskode.valueOf(name)
}

