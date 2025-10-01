# Java 24 runtime image (matches your build JDK)
FROM eclipse-temurin:24-jre

# Set working directory inside the container
WORKDIR /app

# Copy your locally built JAR file into the container
COPY target/*.jar app.jar

# Expose the port your Spring app uses (adjust if needed)
EXPOSE 3000

# Start the application
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]