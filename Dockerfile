FROM gcr.io/distroless/java21-debian12
ENV TZ="Europe/Oslo"
COPY /application/target/poao-tilgang-app.jar app.jar
CMD ["app.jar"]