# =========================================================================
# Multi-Stage Build Dockerfile for Zynpath Spring Boot Backend v4
# =========================================================================

# Stage 1: Build JAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy Maven descriptor
COPY pom.xml .

# Pre-fetch dependencies (cached layer)
RUN mvn dependency:go-offline -B || true

# Copy source tree and compile production artifact
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime base with maintained Eclipse Temurin 17 JRE
FROM eclipse-temurin:17-jre-jammy AS runtime

# Set operational environment flags
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Install curl for native container healthcheck
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Create non-root dedicated application user & group
RUN groupadd -g 10001 zynpath && \
    useradd -u 10001 -g zynpath -m -s /bin/bash zynpath

WORKDIR /app

# Copy executable jar produced by builder stage
COPY --from=builder /build/target/zynpath-backend-*.jar app.jar

# Enforce secure ownership
RUN chown -R zynpath:zynpath /app

# Switch to non-root execution
USER zynpath:zynpath

# Expose internal service port
EXPOSE 8080

# Native healthcheck using Spring Boot Actuator probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1

# Launch application with container-aware JVM flags
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
