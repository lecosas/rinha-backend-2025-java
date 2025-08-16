FROM eclipse-temurin:24-jre-alpine

# Install socat for Unix socket bridge
RUN apk add --no-cache socat

# Create socket directory
RUN mkdir -p /sockets && chmod 777 /sockets

WORKDIR /app

COPY target/*.jar app.jar
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

EXPOSE 8080



#CMD rm -f /sockets/${SOCKET_NAME}.sock

#CMD socat UNIX-LISTEN:/sockets/${SOCKET_NAME}.sock,fork TCP:127.0.0.1:8080 & \
#    chmod 777 /sockets/${SOCKET_NAME}.sock & \
#    java -jar -XX:+UseContainerSupport -XX:+UseSerialGC -XX:MaxRAMPercentage=75 app.jar

ENTRYPOINT ["/app/entrypoint.sh"]
#CMD ["java", "-XX:+UseContainerSupport", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
