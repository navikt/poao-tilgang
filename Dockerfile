FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app
COPY /application/target/poao-tilgang-app.jar app.jar
ENV TZ="Europe/Oslo"
EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_PROXY_OPTIONS -jar app.jar"]
