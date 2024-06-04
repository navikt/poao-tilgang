FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /app
COPY /application/target/poao-tilgang-app.jar app.jar
EXPOSE 8080
CMD ["app.jar"]