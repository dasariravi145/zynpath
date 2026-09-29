# Production Environment Variables Inventory & Specification

## 1. Secret Management Boundaries
All production secrets must be provided via environment variables injected at runtime (via container orchestrator, AWS Parameter Store, GCP Secret Manager, or Docker Compose `.env`).

**Strict Constraints:**
- Never commit actual secrets or production values to version control.
- Never bake credentials into container images or client assets.
- Production startup fails immediately if mandatory secrets are omitted or left with default placeholder values.

---

## 2. Environment Variables Specification

| Variable Name | Purpose | Required In | Format / Constraints | Safe Example | Missing Value Behavior |
|---|---|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Sets active Spring profile | All | String (`prod`, `staging`, `dev`, `test`) | `prod` | Defaults to `dev` (Insecure for prod!) |
| `SERVER_PORT` | HTTP/WS listen port | All | Integer (1–65535) | `8080` | Defaults to `8080` |
| `SPRING_DATASOURCE_URL` | JDBC connection string | Prod, Staging | `jdbc:postgresql://<host>:<port>/<dbname>?sslmode=require` | `jdbc:postgresql://postgres.internal:5432/zynpath_prod?sslmode=require` | Startup fails with `DataSourceException` |
| `SPRING_DATASOURCE_USERNAME` | Database username | Prod, Staging | Alphanumeric string | `zynpath_app_user` | Startup fails with auth error |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Prod, Staging | Secure random string (min 24 chars) | `[SECURE_RANDOM_SECRET]` | Startup fails with auth error |
| `ZYNPATH_AUTH_GOOGLE_CLIENT_ID` | Google Play / Android OAuth Client ID | Prod, Staging | `<id>.apps.googleusercontent.com` | `1029384756-abc123def456.apps.googleusercontent.com` | Startup fails via `ProductionStartupValidator` |
| `ZYNPATH_AUTH_SESSION_TTL_HOURS` | Session token validity duration | Prod, Staging | Positive integer (hours) | `720` (30 days) | Defaults to `720` |
| `ZYNPATH_CORS_ALLOWED_ORIGINS` | Permitted HTTP CORS origins | Prod, Staging | Comma-separated HTTPS URLs (No wildcard `*` allowed in prod) | `https://zynpath.app,https://admin.zynpath.app` | Startup fails if wildcard `*` in prod |
| `ZYNPATH_WEBSOCKET_ALLOWED_ORIGINS` | Permitted WebSocket origins | Prod, Staging | Comma-separated HTTPS URLs | `https://zynpath.app,https://android.zynpath.app` | Defaults to `https://zynpath.app` |
| `GOOGLE_APPLICATION_CREDENTIALS` | Service Account JSON path for Play Billing API | Prod, Staging | Valid filesystem path to JSON key | `/etc/zynpath/secrets/play-service-account.json` | Purchase validation operates in degraded/mock mode |
| `ADMOB_SSV_KEY_URL` | AdMob SSV Public Key server endpoint | Prod, Staging | Valid HTTPS URL | `https://www.gstatic.com/admob/reward/verifier-keys.json` | Defaults to standard Google AdMob keys endpoint |
| `FCM_SERVICE_ACCOUNT_KEY` | Firebase Cloud Messaging credentials | Prod, Staging | Valid filesystem path or JSON string | `/etc/zynpath/secrets/fcm-credentials.json` | Push notification service disabled gracefully |
| `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | Actuator exposed endpoints | Prod | Comma-separated names | `health,info,metrics,prometheus` | Defaults to `health,info` |

---

## 3. Safe `.env.example` Template
A safe `.env.example` template for containerized or staging/production deployments:

```bash
# ==============================================================================
# ZYNPATH BACKEND ENVIRONMENT CONFIGURATION (TEMPLATE)
# DO NOT COMMIT SENSITIVE VALUES TO VERSION CONTROL.
# ==============================================================================

# Core Application Profile
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Relational Database (PostgreSQL)
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/zynpath_prod?sslmode=prefer
SPRING_DATASOURCE_USERNAME=zynpath_app
SPRING_DATASOURCE_PASSWORD=CHANGE_ME_SECURE_PASSWORD_32_CHARS_MIN

# Authentication & Identity
ZYNPATH_AUTH_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
ZYNPATH_AUTH_SESSION_TTL_HOURS=720

# Network Security Boundaries
ZYNPATH_CORS_ALLOWED_ORIGINS=https://zynpath.app
ZYNPATH_WEBSOCKET_ALLOWED_ORIGINS=https://zynpath.app

# Google Play Developer API (Purchase Verification)
GOOGLE_APPLICATION_CREDENTIALS=/etc/zynpath/secrets/play-service-account.json

# Actuator & Observability
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus
```
