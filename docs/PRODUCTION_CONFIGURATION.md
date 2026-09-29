# Production Configuration Specification

## 1. Overview & Architecture Preservation
Zynpath’s backend is built as a Spring Boot 3 modular monolith running on Java 17+. The production architecture preserves this monolith design without decomposing into microservices or requiring distributed service meshes. All production deployments execute with the Spring profile `prod` activated (`SPRING_PROFILES_ACTIVE=prod`).

---

## 2. Configuration Profile Hierarchy
Spring Boot loads configuration hierarchically. Defaults defined in `application.yml` are selectively overridden by environment-specific profiles:

| Profile | File | Purpose | Security Posture |
|---|---|---|---|
| **Default** | `application.yml` | Shared baseline, fallback values, Actuator endpoints | Local development defaults |
| **Dev** | `application-dev.yml` | Local developer ergonomics, SQL logging, H2/local Postgres | Insecure development defaults permitted |
| **Test** | `application-test.yml` | Hermetic unit and integration testing, random port (`0`) | Mocked external services |
| **Staging** | `application-staging.yml` | Pre-production testing, integration with sandbox OAuth | Strict validation with test credentials |
| **Prod** | `application-prod.yml` | Hardened production environment, environment-driven | Zero hardcoded secrets, fail-fast validation |

---

## 3. Production Startup Validation (`ProductionStartupValidator`)
In production (`prod` profile active), the application executes [ProductionStartupValidator.java](file:///d:/Zynpath/backend/src/main/java/com/zynpath/backend/common/config/ProductionStartupValidator.java) immediately upon application ready event.

### Validated Invariants:
1. **Google OAuth Client ID:**
   - Must be configured (`zynpath.auth.google-client-id`).
   - Must NOT match placeholder strings: `dev-google-client-id.apps.googleusercontent.com`, `YOUR_GOOGLE_CLIENT_ID`, `test-client-id`, `placeholder`.
2. **CORS Allowed Origins:**
   - Must NOT contain wildcard (`*`) or `null` origins when running in production.
   - Must be explicitly enumerated HTTPS domains (e.g. `https://zynpath.app`).
3. **Session TTL:**
   - Session duration (`zynpath.auth.session-ttl-hours`) must be strictly positive (> 0).
4. **Failure Behavior:**
   - If any condition fails, the validator throws `IllegalStateException`, halting JVM initialization before accepting any HTTP or WebSocket traffic.

---

## 4. Database Connection Pool Configuration (HikariCP)
Zynpath uses HikariCP for high-throughput, low-latency relational pooling against PostgreSQL:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000      # 20 seconds
      max-lifetime: 1800000          # 30 minutes
      idle-timeout: 300000           # 5 minutes
      leak-detection-threshold: 30000 # 30 seconds
      pool-name: ZynpathProdHikariPool
```

### Sizing Rationale:
- **Max Pool Size (20):** Calculated for a single 2 vCPU / 4 GB RAM container instance interacting with a managed PostgreSQL instance (e.g., Cloud SQL or Supabase Pro). $PoolSize = (2 \times CPU) + SpindleCount = 4 + 16 \approx 20$.
- **Leak Detection (30s):** Alerts operations if any business transaction or background job holds a connection open without releasing it back to the pool.

---

## 5. Graceful Shutdown
To prevent dropped WebSocket matches and severed database transactions during deployments or autoscaling events, graceful shutdown is enforced:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

### Shutdown Lifecycle:
1. Load balancer / Kubernetes readiness probe flips to unready (traffic drained).
2. Embedded Tomcat stops accepting new TCP connections.
3. In-flight HTTP requests and active WebSocket matchmaking sessions are given up to 30 seconds to conclude naturally.
4. HikariCP pool flushes remaining statements and disconnects cleanly from PostgreSQL.
5. Scheduled background workers stop picking up new jobs.

---

## 6. Reverse Proxy & TLS Termination
- **TLS Termination:** Handled at the edge load balancer, Cloudflare, or ingress controller.
- **Port:** Backend listens internally on unprivileged port `8080` (HTTP).
- **Forwarded Headers:** Set `server.forward-headers-strategy: framework` or `native` to ensure `X-Forwarded-Proto`, `X-Forwarded-For`, and `X-Forwarded-Host` are respected for HTTPS URL generation.
- **Strict HTTPS:** The Android client connects exclusively via HTTPS/WSS (`https://api.zynpath.app` and `wss://api.zynpath.app`).

---

## 7. Android Client Production Release Alignment (Prompt 45)
- **Application ID**: `com.zynpath.game` (Release variant with `debuggable = false`).
- **Endpoint Alignment**: Android release builds point directly to `https://api.zynpath.app/api/v1` and `wss://api.zynpath.app/ws/multiplayer`, strictly aligning with backend reverse proxy routing.
- **Network Security Configuration**: Cleartext HTTP traffic is disabled globally via `network_security_config.xml`.
- **Signing & App Integrity**: Release artifacts (`.aab`) are signed with the developer upload key and re-signed by Google Play App Signing with production certificate fingerprints registered in Google Cloud Console.
- **Detailed Specifications**: See [ANDROID_RELEASE_CONFIGURATION.md](file:///d:/Zynpath/docs/ANDROID_RELEASE_CONFIGURATION.md) and [CI_CD_PIPELINE.md](file:///d:/Zynpath/docs/CI_CD_PIPELINE.md).

---

## 8. Operational Readiness & Configuration Verification (Prompt 47)

### 8.1 Test Execution Summary (`OperationalReadinessTest`)
- **Suite**: `com.zynpath.backend.readiness.OperationalReadinessTest`
- **Total Tests**: 4 / 4 PASSED (100% Pass Rate).
- **Verified Behaviors**:
  1. `actuatorLivenessProbe_shouldBeUp`: Verifies Actuator liveness endpoint responds with HTTP 200 and status `UP`.
  2. `actuatorReadinessProbe_shouldBeUp`: Verifies Actuator readiness endpoint responds with HTTP 200 and status `UP` when data pool is ready.
  3. `healthEndpoint_shouldReturnStatus`: Verifies `/api/v1/health` responds cleanly.
  4. `gracefulShutdown_configurationVerified`: Verifies graceful shutdown timeout and bean lifecycle configuration.

### 8.2 Production Startup Validation Verification
- Tested `ProductionStartupValidator` ensuring fail-fast behavior if Google OAuth client ID is placeholder, session TTL is invalid, or CORS has wildcard in production profile.

---

## 9. Prompt 50 Final Deployment Alignment
- **Production Deployment Procedures**: Exact step-by-step deployment instructions, pre-flight snapshot requirements, and smoke test commands are provided in [docs/PRODUCTION_DEPLOYMENT_RUNBOOK.md](file:///d:/Zynpath/docs/PRODUCTION_DEPLOYMENT_RUNBOOK.md).
- **Observability & Health Probes**: Production metrics, SLO thresholds, and Actuator monitoring configurations are documented in [docs/POST_LAUNCH_MONITORING.md](file:///d:/Zynpath/docs/POST_LAUNCH_MONITORING.md).
- **Incident Response Playbooks**: Standard operating procedures for handling production anomalies are detailed in [docs/INCIDENT_RESPONSE_RUNBOOK.md](file:///d:/Zynpath/docs/INCIDENT_RESPONSE_RUNBOOK.md).


