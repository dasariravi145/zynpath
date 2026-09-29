# =========================================================================
# Multi-Stage Build Dockerfile for Zynpath Spring Boot Backend
# =========================================================================

# Runtime base with maintained Eclipse Temurin 17 JRE
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

# Copy executable jar produced by 'mvn clean package -DskipTests'
ARG JAR_FILE=target/zynpath-backend-*.jar
COPY ${JAR_FILE} app.jar

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
