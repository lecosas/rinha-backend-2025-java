# -------- Stage 1: Build native image --------
#FROM paketobuildpacks/builder:base as builder
#
## Environment variables for native image
#ENV BP_NATIVE_IMAGE=true
#ENV BP_JVM_VERSION=21
#
## Copy source code
#WORKDIR /workspace
#COPY . .
#
## Build native image
#RUN ./mvnw -ntp -Pnative -DskipTests spring-boot:build-image

#FROM ghcr.io/graalvm/graalvm-ce:21.3.0 as builder
#
## Install native-image and dependencies
#RUN gu install native-image
#
## Set working dir
#WORKDIR /app
#
## Copy project
#COPY . .
#
## Build native image
#RUN ./mvnw -Pnative native:compile -DskipTests


# Stage 1: Build with GraalVM Native Image (ARM64 or AMD64)
FROM vegardit/graalvm-maven:latest-java24 as builder

WORKDIR /app

## Copy only necessary files to speed up Docker builds
COPY . .

## Build native image using Maven
RUN ./mvnw -Pnative native:compile -DskipTests

#RUN ls -l target

#CMD ["./target/rinha-backend-2025-java"]
#
#RUN ls -l
#
#RUN ls -l /app
#
#RUN ls -l /app/target
#
## Stage 2: Create minimal runtime container
#FROM alpine:3.22
FROM debian:bookworm-slim

RUN apt-get update && apt-get install -y zlib1g && rm -rf /var/lib/apt/lists/*

#RUN apk add --no-cache libstdc++
#
#RUN ls -l
#
WORKDIR /app
#

## Copy binary from previous stage
COPY --from=builder /app/target/rinha-backend-2025-java-spring-graalvm app
#
#RUN ls -l
#
#RUN file ./app
#
#RUN addgroup -S appgroup && adduser -S appuser -G appgroup

#
## Expose port
EXPOSE 8080

#USER nonroot
#
## Run binary
CMD ["./app"]


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
