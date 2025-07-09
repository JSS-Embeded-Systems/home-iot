FROM ubuntu:latest
LABEL authors="nils"

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "app.jar"]