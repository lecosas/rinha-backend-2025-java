# Use a minimal Java 21 JRE image
#FROM eclipse-temurin:21-jre-alpine
FROM eclipse-temurin:24-jre-alpine

# Install curl for healthchecks
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Copy the pre-built JAR file
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Optimize JVM memory settings
CMD ["java", "-XX:+UseContainerSupport", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
