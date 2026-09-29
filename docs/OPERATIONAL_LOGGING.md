# Operational Logging & Observability Specification

## 1. Overview
Zynpath implements structured operational logging with zero sensitive data leakage and native Micrometer-based metrics exposure. The logging system is designed to work with standard stdout/stderr container collectors (e.g. systemd, Docker json-file, FluentBit, CloudWatch) without requiring proprietary or paid observability platforms.

---

## 2. Log Privacy & Token Scrubbing Rules
To comply with GDPR, Google Play Developer Policies, and industry security baselines, sensitive artifacts are scrubbed prior to writing to log streams.

### Explicitly Forbidden Log Contents:
- **Authentication Credentials:** Bearer tokens, Google ID tokens, Facebook access tokens, refresh tokens, password hashes.
- **Billing Identifiers:** Google Play purchase tokens, order IDs, account identifiers.
- **Invitation Secrets:** Private duel tokens, cryptographic match signatures.
- **High-Frequency Gameplay Telemetry:** Raw touch/gesture coordinates, in-flight coordinate arrays.

### Automated Sanitization:
The backend employs [SecurityAuditLogger.java](file:///d:/Zynpath/backend/src/main/java/com/zynpath/backend/security/audit/SecurityAuditLogger.java) to scrub credentials using precompiled regular expressions:
```java
// Pattern matches and masks tokens such as "Bearer eyJhbGciOi..." -> "Bearer [REDACTED]"
private static final Pattern TOKEN_PATTERN = 
    Pattern.compile("(Bearer\\s+)[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*", Pattern.CASE_INSENSITIVE);
private static final Pattern PURCHASE_TOKEN_PATTERN = 
    Pattern.compile("(purchaseToken[\"']?\\s*[:=]\\s*[\"']?)[A-Za-z0-9-_]{10,}", Pattern.CASE_INSENSITIVE);
```

---

## 3. Production Log Levels & Formatting
Configured in [application-prod.yml](file:///d:/Zynpath/backend/src/main/resources/application-prod.yml):

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [traceId=%X{traceId:-}] - %msg%n"
  level:
    root: INFO
    com.zynpath.backend: INFO
    org.springframework.web: WARN
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
    org.hibernate.type.descriptor.sql.BasicBinder: WARN
    com.zaxxer.hikari: WARN
```

### Log Configuration Rationales:
- **`org.hibernate.SQL: WARN`**: Suppresses query printing in production, preventing sensitive entity field leakage and IO bottlenecks.
- **`BasicBinder: WARN`**: Strictly prohibits binding parameter value output to stdout.
- **Trace Correlation:** Every request populates MDC with `traceId` for correlation across HTTP and WebSocket log entries.

---

## 4. Metrics Readiness (Prometheus / Micrometer)
The backend exposes native Prometheus metrics at `/actuator/prometheus` via Micrometer:

### Core Operational Metrics:
1. **HTTP Traffic & Latency:**
   - `http.server.requests` (tagged by `uri`, `status`, `method`).
2. **Database Performance:**
   - `hikaricp.connections.active`: Currently executing database queries.
   - `hikaricp.connections.idle`: Available connection pool capacity.
   - `hikaricp.connections.pending`: Threads waiting for a database connection (critical bottleneck alert).
3. **Multiplayer & WebSocket:**
   - `websocket.sessions.active`: Current count of live WebSocket sessions.
   - `multiplayer.matches.active`: Ongoing 1v1 and Mini League sessions.
   - `multiplayer.finalization.failure`: Count of server-authoritative match validation errors.
4. **Offline Synchronization:**
   - `sync.batch.size`: Distribution of synchronization operations per sync request.
   - `sync.conflict.count`: Frequency of client-vs-server timestamps resolved by server authority.

> [!NOTE]
> High-cardinality private tags (such as raw UUIDs or player email addresses) are strictly avoided in metric tags to prevent metric store memory explosion.
