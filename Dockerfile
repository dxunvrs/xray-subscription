FROM eclipse-temurin:21-jdk AS builder
WORKDIR /build

COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 12258

ENTRYPOINT ["java", "-jar", "app.jar"]