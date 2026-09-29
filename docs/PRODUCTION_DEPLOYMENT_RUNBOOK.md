# Production Deployment Runbook — Zynpath Backend Services

**Document ID:** `RUNBOOK-DEPLOY-PROD-001`  
**Application Title:** *Zynpath: Number Path Puzzle — Backend Service*  
**Runtime:** Java 17 LTS / Spring Boot 3.4.3 / Docker  
**Database:** PostgreSQL 16+ (Managed Cluster with WAL archiving)  
**Target Hostname:** `api.zynpath.app`  
**Status:** Authoritative Operational Deployment Runbook  

---

## 1. Overview & Architectural Boundaries

The Zynpath backend is engineered as a hardened, high-efficiency Spring Boot modular monolith designed for predictable low-cost deployment. It manages real-time multiplayer matchmaking, authoritative solution verification, UTC daily challenges, and server-to-server in-app purchase validation.

All client solo puzzle play is 100% on-device. The backend is never in the critical path for solo gameplay.

---

## 2. Pre-Deployment Prerequisites

Prior to commencing deployment, ensure the following cloud infrastructure assets are provisioned:
1. **Host Environment:** A virtual server (minimum 2 vCPU, 4 GB RAM) running Linux (Ubuntu 22.04 LTS or Amazon Linux 2023) with Docker Engine 24+ and Docker Compose v2 installed.
2. **Managed Database:** A PostgreSQL 16+ instance provisioned within the private VPC subnet with automated daily backups and WAL archiving enabled.
3. **Public DNS & TLS:** An `A` or `CNAME` record pointing `api.zynpath.app` to the reverse proxy (Nginx or Cloudflare Edge SSL) with strict TLS 1.3 / 1.2 termination.
4. **Environment Secret Template:** All variables in `infrastructure/production.env` populated with high-entropy production values.

---

## 3. Required Production Environment Variables

Ensure the following environment variables are securely injected into the container environment (never committed to version control):

```bash
# Production Profile Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Database Connectivity (PostgreSQL 16+)
DATABASE_URL=jdbc:postgresql://postgres-prod.internal:5432/zynpath_production
DATABASE_USERNAME=zynpath_prod_user
DATABASE_PASSWORD=[SECURE_256BIT_POSTGRES_PASSWORD]

# Authentication & JWT (Minimum 256-bit entropy HMAC-SHA256 secret)
JWT_SECRET=[SECURE_BASE64_256BIT_SECRET_STRING]
JWT_EXPIRATION_MS=3600000
JWT_REFRESH_EXPIRATION_MS=2592000000

# OAuth Identity Provider Client IDs
GOOGLE_CLIENT_ID=[GOOGLE_OAUTH_PRODUCTION_WEB_CLIENT_ID]
FACEBOOK_APP_ID=[FACEBOOK_PRODUCTION_APP_ID]

# Google Play Developer API (Base64-encoded Service Account JSON key)
GOOGLE_APPLICATION_CREDENTIALS=/app/secrets/google-play-service-account.json

# Firebase Cloud Messaging (Base64-encoded FCM Admin Service Account JSON key)
FCM_SERVICE_ACCOUNT_KEY=/app/secrets/firebase-service-account.json

# AdMob Server-Side Verification (SSV) Key
ADMOB_SSV_KEY_ID=[ADMOB_PRODUCTION_SSV_KEY_ID]
```

---

## 4. Step-by-Step Production Deployment Sequence

### Step 1: Pre-Flight Database Snapshot
Before initiating any deployment or migration, trigger a point-in-time snapshot of the production PostgreSQL database:
```bash
# On database host or via cloud console
pg_dump -h postgres-prod.internal -U zynpath_prod_user -F c -b -v -f /backups/zynpath_pre_deploy_$(date +%Y%m%d_%H%M%S).dump zynpath_production
```

### Step 2: Build Multi-Stage Production Container
On the build machine or CI server:
```bash
cd backend
docker build -t zynpath-backend:1.0.0 -f Dockerfile .
```
*Docker Build Invariants:* Uses Eclipse Temurin 17 JRE base, non-root user `zynpath:zynpath` (`UID 10001`), with multi-stage layer caching.

### Step 3: Execute Database Migrations (Flyway)
Spring Boot executes Flyway migrations automatically upon startup before binding the HTTP port. Flyway verifies checksums for migrations `V1` through `V6`:
* `V1__Initial_Schema.sql`: Users, profiles, sessions, and guest linking.
* `V2__Multiplayer_Tables.sql`: Duels, leagues, and match history.
* `V3__Daily_Challenge.sql`: Deterministic UTC challenge records and scores.
* `V4__Purchases_And_Billing.sql`: Google Play Billing purchase token records.
* `V5__Rewarded_Ads.sql`: AdMob SSV transaction logs and anti-replay tables.
* `V6__Auditing_And_Indexes.sql`: Security audit logging and compound indexes.

*Safety Rule:* `hibernate.ddl-auto: validate` is strictly enforced. The application will immediately abort startup if the JPA entities do not match the migrated database schema.

### Step 4: Deploy Container via Docker Compose
On the target production host:
```bash
cd /opt/zynpath
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d --remove-orphans
```

### Step 5: Verify Actuator Health Probes
Execute local and remote HTTP health checks:
```bash
# Local container check
curl -f http://localhost:8080/actuator/health/liveness
curl -f http://localhost:8080/actuator/health/readiness

# Public endpoint check
curl -f https://api.zynpath.app/api/v1/health
```
*Expected Response:*
```json
{"status":"UP","timestamp":"2026-09-27T20:00:00Z","service":"zynpath-backend"}
```

### Step 6: Post-Deployment Smoke Test
Verify core backend subsystems using synthetic verification requests:
1. **Guest Session Issuance:**
   ```bash
   curl -X POST https://api.zynpath.app/api/v1/auth/guest-login \
     -H "Content-Type: application/json" \
     -d '{"clientNonce":"smoke-test-nonce-001"}'
   ```
   *Verify:* HTTP 200 with valid JWT `token` and `refreshToken`.
2. **Daily Challenge Retrieval:**
   ```bash
   curl https://api.zynpath.app/api/v1/daily/today
   ```
   *Verify:* HTTP 200 with valid canonical UTC date and puzzle ID.
3. **WebSocket Connection Handshake:**
   Connect a test client to `wss://api.zynpath.app/ws/multiplayer` and confirm HTTP 101 Switching Protocols.

---

## 5. Rollback Procedures

### 5.1 Application Rollback (Zero DB Schema Change)
If an application defect is discovered post-deployment that does not involve database schema modification:
```bash
# Revert to previous Docker image tag
docker compose -f docker-compose.prod.yml down
sed -i 's/:1.0.0/:previous_stable/g' docker-compose.prod.yml
docker compose -f docker-compose.prod.yml up -d
```

### 5.2 Database Rollback (Destructive Migration Failure)
If a database migration corrupts data or fails in an unrecoverable state:
1. Immediately stop the backend container to cease write traffic:
   ```bash
   docker compose -f docker-compose.prod.yml down
   ```
2. Restore the pre-deployment database dump created in Step 1:
   ```bash
   pg_restore -h postgres-prod.internal -U zynpath_prod_user -d zynpath_production --clean --if-exists /backups/zynpath_pre_deploy_*.dump
   ```
3. Re-deploy the previous known-stable backend container image.
