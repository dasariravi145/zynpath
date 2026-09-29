# Zynpath Local Backend Setup Guide

## 1. Prerequisites
- **Java**: Java 17 LTS (verified with Amazon Corretto 17.0.12)
- **Maven**: Apache Maven 3.9+ (`mvn`)
- **Git**: Working copy of the Zynpath repository

Verify environment:
```bash
java -version
mvn -version
```

---

## 2. Directory Structure
The Spring Boot backend lives in the `backend/` directory:
```
d:/Zynpath/backend/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/zynpath/backend/...
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       └── application-example.yml
    └── test/
        └── java/com/zynpath/backend/...
```

---

## 3. Configuration Profiles
- **Default Profile (`application.yml`)**: Port 8080, address `0.0.0.0` (allows local LAN and ADB reverse), context path `/`, API prefix `/api/v1`.
- **Development Profile (`application-dev.yml`)**: Verbose debug logging for Zynpath packages, Actuator health endpoints enabled.
- **Example Configuration (`application-example.yml`)**: Safe template showing configurable environment variables without committing any secrets.

---

## 4. Building and Running the Backend

### Build and Run Tests
From the `backend/` directory:
```bash
mvn clean test
```

### Compile Package (JAR)
```bash
mvn clean package -DskipTests=false
```

### Start the Local Server
```bash
# Using Maven Spring Boot plugin:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or running the packaged JAR:
java -jar target/zynpath-backend-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

The application starts on `http://localhost:8080`.

---

## 5. Verifying the Health Endpoint
The health endpoint is unauthenticated and available for verification:

### Using cURL:
```bash
curl -i http://localhost:8080/api/v1/health
```

### Expected JSON Response:
```json
{
  "status": "UP",
  "service": "Zynpath Backend",
  "version": "1.0.0-SNAPSHOT",
  "timestamp": 1727334200000,
  "environment": "dev"
}
```

---

## 6. Safe Development Principles
- **No Cloud Services Needed**: You do not need PostgreSQL, Cloud SQL, Redis, or Google Cloud access for local development in Phase 1.
- **Port Conflicts**: If port 8080 is in use, supply `--server.port=8081` or set `SERVER_PORT=8081` in your environment.
- **Stopping the Backend**: Use `Ctrl+C` in the terminal to gracefully shut down the Spring context.
