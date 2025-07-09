# Use Java 21 runtime only (no compiler needed)
FROM openjdk:21-slim

# Set working directory inside the container
WORKDIR /app

# Copy your locally built JAR file into the container
COPY target/*.jar app.jar

# Expose the port your Spring app uses (adjust if needed)
EXPOSE 3000

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]