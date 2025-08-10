FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar
EXPOSE 8080

CMD ["java", "-XX:+UseContainerSupport", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
