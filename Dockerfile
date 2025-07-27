# Stage 1: Build with GraalVM Native Image (ARM64 or AMD64)
FROM vegardit/graalvm-maven:latest-java24 as builder

# Install required tools
#RUN gu install native-image

WORKDIR /app

# Copy only necessary files to speed up Docker builds
COPY . .

# Build native image using Maven
RUN ./mvnw -Pnative native:compile

RUN ls -l target

CMD ["./target/rinha-backend-2025-lecosas"]
#
#RUN ls -l
#
#RUN ls -l /app
#
#RUN ls -l /app/target
#
## Stage 2: Create minimal runtime container
#FROM arm64v8/alpine:3.22
#
#RUN ls -l /
#
#WORKDIR /app
#
## Copy binary from previous stage
#COPY --from=builder /app/target/rinha-backend-2025-lecosas app
#
#RUN ls -l
#
## Expose port
#EXPOSE 8080
#
## Run binary
#CMD ["./app"]


#FROM arm64v8/alpine:3.22
##FROM linux/arm64/v8
#
#WORKDIR /app
#
#COPY target/rinha-backend-2025-lecosas app
#
#EXPOSE 8080
#
#ENTRYPOINT ["/app/app"]

# Copy the pre-built JAR file

#COPY target/rinha-backend-2025-lecosas app

# Expose the application port


#ENTRYPOINT ["/app"]

# Optimize JVM memory settings
#CMD ["java", "-XX:+UseContainerSupport", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
