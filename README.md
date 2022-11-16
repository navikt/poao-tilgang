# Felles tilgangskontroll for Produktområde Arbeidsoppfølging

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=bugs)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=code_smells)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_poao-tilgang&metric=security_rating)](https://sonarcloud.io/dashboard?id=navikt_poao-tilgang)

API for tilgangskontroll med regler som er felles for Produktområde Arbeidsoppfølging.
Har også en HTTP-klient som kan trekkes inn som en avhengighet i applikasjoner for å kalle API'et.

## Bruk av HTTP-klient

### 1.Legg til jitpack som repository
Det er anbefalt å legge til Jitpack til slutt for å først søke igjennom andre repositories for avhengigheter.

Maven:
```xml
    <repositories>
        <!-- Legger til central eksplisitt for prioritet over jitpack -->
        <repository>
            <id>central</id>
            <url>https://repo.maven.apache.org/maven2</url>
        </repository>
        <repository>
            <id>jitpack</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
```

Gradle:
```groovy
repositories {
    // Legger til central eksplisitt for prioritet over jitpack
    mavenCentral()
    maven { url "https://jitpack.io" }
}
```

### 2. Legg til dependency for klienten 

Hent siste versjon av klienten på https://github.com/navikt/poao-tilgang/releases og legg til avhengigheten.
NB: Jitpack bygger avhengigheter lazy, dvs når noen først prøver å laste avhengigheten ned. Hvis du er den første som laster 
ned en ny versjon, så kan det potensielt ta noen minutter før avhengigheten er bygget ferdig. 

Maven:
```xml
<dependency>
    <groupId>com.github.navikt.poao-tilgang</groupId>
    <artifactId>client</artifactId>
    <version>YYYY.MM.DD_HH.mm-SHA</version>
</dependency>
```

Gradle:
```groovy
dependencies {
    implementation 'com.github.navikt.poao-tilgang:client:YYYY.MM.DD_HH.mm-SHA'
}
```

### 3. Opprett en instans av klienten

```kotlin
val client = PoaoTilgangCachedClient(
    PoaoTilgangHttpClient(
        baseUrl = "http://poao-tilgang.poao.svc.cluster.local", // or use "https://poao-tilgang(.dev).intern.nav.no" if your sending the request from dev-fss/prod-fss
        tokenProvider = { "machine-to-machine token" }
    )
)
```
