# Production Deployment Runbook

## 1. Overview & Scope
This runbook defines the authoritative, repeatable sequence for deploying Zynpath's Spring Boot backend to staging and production environments.

> [!IMPORTANT]
> **Execution Status: PENDING / REQUIRES DEPLOYMENT AUTHORIZATION**
> No cloud accounts, databases, or live hosting services are provisioned automatically. All steps below represent the verified deployment procedure ready for execution by an authorized operator.

---

## 2. Pre-Deployment Prerequisites
- Docker Engine 24+ and Docker Compose v2+ installed on target deployment host.
- Managed PostgreSQL 15+ database provisioned with network connectivity to application host.
- Domain `api.zynpath.app` pointing to reverse proxy / edge load balancer with valid TLS certificates.
- Environment variables configured per [docs/ENVIRONMENT_VARIABLES.md](file:///d:/Zynpath/docs/ENVIRONMENT_VARIABLES.md).

---

## 3. Step-by-Step Deployment Procedure

### Step 1: Verify Configuration
Verify that the target host's environment file (`/etc/zynpath/.env` or orchestrator secret store) is populated with valid production credentials.
```bash
# Verify no placeholder strings exist
grep -E "CHANGE_ME|YOUR_GOOGLE|dev-google" /etc/zynpath/.env && echo "FATAL: Unresolved placeholders" && exit 1
```

### Step 2: Prepare Deployment Artifact
Build the reproducible container image tagged with the Git commit hash:
```bash
COMMIT_HASH=$(git rev-parse --short HEAD)
docker build -t zynpath-backend:${COMMIT_HASH} -f backend/Dockerfile backend/
docker tag zynpath-backend:${COMMIT_HASH} zynpath-backend:latest
```

### Step 3: Confirm Backup Readiness
Ensure a full database snapshot has been taken within the last 60 minutes:
```bash
# Execute pre-deployment snapshot per BACKUP_AND_RESTORE.md
pg_dump -U zynpath_app -d zynpath_prod -Fc -f "/var/backups/zynpath/pre_deploy_${COMMIT_HASH}.dump"
```

### Step 4: Review Pending Migrations
Inspect unapplied migrations in `backend/src/main/resources/db/migration/`:
- Confirm all new migrations are backward-compatible.
- Verify DDL operations do not lock critical tables indefinitely.

### Step 5: Deploy New Container
Start the new container instance gracefully:
```bash
# Launch container with graceful replacement
docker compose -f backend/docker-compose.prod.yml up -d --no-deps backend
```

### Step 6: Check Readiness Probe
Poll the Actuator readiness endpoint until the application reports `UP`:
```bash
# Wait for readiness probe (fails if Flyway migrations did not succeed)
curl -sf http://localhost:8080/actuator/health/readiness | grep '"status":"UP"'
```

### Step 7: Verify Critical Endpoints
Perform lightweight operational smoke testing against core endpoints:
```bash
# 1. Health Liveness
curl -sf http://localhost:8080/actuator/health/liveness

# 2. Daily Challenge Endpoint (public solver-verified puzzle check)
curl -sf -H "Accept: application/json" http://localhost:8080/api/daily-challenge/current

# 3. WebSocket Upgrade Handshake
wscat -c "wss://api.zynpath.app/ws/multiplayer" --wait 5
```

### Step 8: Observe Logs & Error Rates
Monitor container output for unexpected exceptions or database pool saturation:
```bash
docker compose -f backend/docker-compose.prod.yml logs -f --tail=100 backend
```
Verify zero occurrences of:
- `ProductionStartupValidator: Startup validation failed`
- `FlywaySqlScriptException`
- `HikariPool-1 - Connection is not available`

### Step 9: Confirm Release Status
- Notify the engineering team of successful rollout.
- Tag the repository release:
  ```bash
  git tag -a "backend-v1.0.0-${COMMIT_HASH}" -m "Production release ${COMMIT_HASH}"
  ```
