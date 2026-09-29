# Zynpath Security Configuration & Production Hardening

## 1. Environment Separation & Profiles

Zynpath enforces strict separation across four operational tiers to prevent permissive development configurations from leaking into production:

| Setting / Property | Local (`dev`) | Staging (`staging`) | Production (`prod`) |
| :--- | :--- | :--- | :--- |
| **Transport** | HTTP / WS allowed for local testing | HTTPS / WSS mandatory | HTTPS / WSS mandatory |
| **HSTS** | Disabled | Enabled (31536000s) | Enabled (31536000s, preload) |
| **Allowed Origins** | `http://localhost:*`, `http://127.0.0.1:*` | `https://staging.zynpath.app` | `https://zynpath.app`, mobile app schemes |
| **Database** | In-memory H2 / local Postgres | Managed RDS / Cloud SQL | Managed RDS / Cloud SQL with TLS |
| **Session TTL** | 30 days (default) | 30 days | 30 days |
| **Provider Auth** | Mock verifier / sandbox credentials | Real Google/FB Staging apps | Real Google/FB Production apps |
| **Google Play Billing** | Test purchase tokens allowed | License testers | Real Google Play Developer API |
| **Rate Limiting** | Active (sliding window) | Active (sliding window) | Active (sliding window + reverse proxy WAF) |
| **Error Details** | Correlation ID only | Correlation ID only | Correlation ID only |

---

## 2. Secrets Management Policy

### Rule: Zero Production Secrets in Version Control
All sensitive credentials, private keys, database passwords, and API secrets must be provided at runtime via environment variables or secret vaults (e.g. AWS Secrets Manager, Google Secret Manager, HashiCorp Vault).

```yaml
# Safe template convention in application.yml
zynpath:
  auth:
    google:
      client-id: ${GOOGLE_CLIENT_ID:mock-google-client-id}
      client-secret: ${GOOGLE_CLIENT_SECRET:}
    facebook:
      app-id: ${FACEBOOK_APP_ID:mock-facebook-app-id}
      app-secret: ${FACEBOOK_APP_SECRET:}
  subscription:
    google-play:
      service-account-json: ${GOOGLE_PLAY_CREDENTIALS_JSON:}
  datasource:
    url: ${DATABASE_URL:jdbc:h2:mem:zynpath;DB_CLOSE_DELAY=-1}
    username: ${DATABASE_USER:sa}
    password: ${DATABASE_PASSWORD:}
```

### Prohibited Actions:
- Never commit signing keys, service-account private keys, or passwords.
- Never commit real `.p8`, `.p12`, or service account JSON keyfiles.
- `application-example.yml` contains only documentation placeholders.

---

## 3. Transport Security & Headers (`SecurityHeadersFilter`)

In production environments, all traffic is encrypted with TLS 1.3:

1. **Strict-Transport-Security (HSTS)**:
   `max-age=31536000; includeSubDomains; preload`
2. **X-Content-Type-Options**:
   `nosniff` (prevents MIME type sniffing)
3. **X-Frame-Options**:
   `DENY` (prevents clickjacking attacks)
4. **Content-Security-Policy (CSP)**:
   `default-src 'none'; frame-ancestors 'none';`
5. **Referrer-Policy**:
   `strict-origin-when-cross-origin`
6. **Cache-Control**:
   `no-store, no-cache, must-revalidate, max-age=0` (enforced on all private authenticated `/api/v1/` routes)
7. **CORS Configuration**:
   - Wildcard origins `*` with credentials are prohibited.
   - Whitelist restricted to verified origins (`https://zynpath.app`, `capacitor://localhost`, `http://localhost:[port]` during development).

---

## 4. Safe Error Handling & Information Disclosure Prevention

### Problem
Raw exception traces disclose database schemas, framework versions, internal IP addresses, and file paths, assisting attackers in crafting targeted exploits.

### Mitigation (`GlobalExceptionHandler`)
- Every uncaught exception is intercepted and converted to a safe `ErrorResponse` payload:
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected internal error occurred. Please reference request ID: a8f12c9b",
  "path": "/api/v1/multiplayer/matches/claim",
  "requestId": "a8f12c9b",
  "timestamp": 1727421800000
}
```
- Full diagnostic stack traces are written to internal server logs keyed by `requestId`, but never returned to clients.
- Authentication failures return uniform `401 Unauthorized` without leaking whether a username or email exists.

---

## 5. Security Event Observability (`SecurityAuditLogger`)

Security logs are separated from standard application debug logs and emit structured events:
- Format: `[SECURITY_AUDIT] event=<TYPE> actor=<ID> ip=<IP> resource=<RES> outcome=<STATUS> details={...}`
- **Automatic Sanitization**:
  - Session tokens (`zyn_...`) and bearer strings are replaced with `[REDACTED_TOKEN]`.
  - Google Play purchase tokens are replaced with `[REDACTED_PURCHASE_TOKEN]`.
  - PII (emails, names) and passwords are never logged in detail maps.

---

## 6. Verification Status
- Component implementation: IMPLEMENTED (`SecurityHeadersFilter`, `GlobalExceptionHandler`, `SecurityAuditLogger`, `WebSecurityConfig`).
- Unit/Integration tests: DEFERRED TO FINAL TESTING.
