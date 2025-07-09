FROM ubuntu:latest
LABEL authors="nils"

FROM eclipse-temurin:17-jdk as builder
WORKDIR /build

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "app.jar"]