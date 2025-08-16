#!/bin/sh
set -e

# Remove old socket if it exists
rm -f /sockets/${SOCKET_NAME}.sock

# Start socat in foreground
# Docker prefers a single foreground process, so we will use exec to start Java after socat
socat UNIX-LISTEN:/sockets/${SOCKET_NAME}.sock,fork TCP:127.0.0.1:8080 &

# Wait for the socket to appear
while [ ! -S /sockets/${SOCKET_NAME}.sock ]; do
    sleep 0.1
done

# Ensure socket is writable by all
chmod 777 /sockets/${SOCKET_NAME}.sock

# Start Spring Boot app in foreground
exec java -jar -XX:+UseContainerSupport -XX:+UseSerialGC -XX:MaxRAMPercentage=75 /app/app.jar
