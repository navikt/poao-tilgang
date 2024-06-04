FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /app
COPY /application/target/poao-tilgang-app.jar app.jar
ENV JAVA_OPTS="-Dhttp.proxyHost=webproxy.nais -Dhttps.proxyHost=webproxy.nais -Dhttp.proxyPort=8088 -Dhttps.proxyPort=8088 '-Dhttp.nonProxyHosts=localhost|127.0.0.1|10.254.0.1|*.local|*.adeo.no|*.nav.no|*.aetat.no|*.devillo.no|*.oera.no|*.nais.io|*.aivencloud.com|*.intern.dev.nav.no'"
EXPOSE 8080
CMD ["app.jar"]