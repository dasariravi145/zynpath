# Production Rollback Runbook

## 1. Rollback Decision Criteria
A production rollback must be initiated immediately if any of the following occur during or immediately after deployment:
1. **Startup Failure:** Container repeatedly crashes or fails the Actuator readiness probe (`/actuator/health/readiness`).
2. **Elevated Error Rate:** 5xx HTTP response rate exceeds 1% of total traffic over a 5-minute window.
3. **Database Contention:** HikariCP pool exhausted (`connection timeout`) continuously under standard traffic.
4. **WebSocket Breakage:** Handshake failure rate exceeds 5% or multiplayer match finalization fails.
5. **Data Inconsistency:** Reports of daily challenge scores or purchases failing authoritative validation.

---

## 2. Decoupling Application vs. Database Rollback

> [!WARNING]
> **Database Rollback Risk Assessment**
> While rolling back an application container image is instantaneous and non-destructive, database rollback requires extreme caution:
> - If the new schema only added nullable columns or new tables, **do NOT rollback the database**. Reverting the application container to the previous version is sufficient (Expand/Contract pattern).
> - Only execute a full database snapshot restore if data corruption or irreversible schema failure has occurred.

---

## 3. Application Container Rollback Procedure

When database schema changes are backward-compatible:

```bash
# 1. Identify previous healthy release commit hash
PREV_COMMIT=$(cat /var/zynpath/last_known_good_commit)

# 2. Re-point docker-compose to previous image
sed -i "s|zynpath-backend:.*|zynpath-backend:${PREV_COMMIT}|" /etc/zynpath/docker-compose.prod.yml

# 3. Re-deploy previous container image
docker compose -f /etc/zynpath/docker-compose.prod.yml up -d --no-deps backend

# 4. Verify readiness of previous version
curl -sf http://localhost:8080/actuator/health/readiness | grep '"status":"UP"'

# 5. Log incident notice
echo "Rollback to ${PREV_COMMIT} completed at $(date -u)" >> /var/log/zynpath/deployment_incidents.log
```

---

## 4. Emergency Database Restoration Procedure

Only execute when unrecoverable schema or data corruption has taken place:

```bash
# 1. STOP traffic ingress immediately (place edge load balancer in maintenance mode)
docker compose -f /etc/zynpath/docker-compose.prod.yml stop backend

# 2. Terminate active database connections
psql -U postgres -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'zynpath_prod' AND pid <> pg_backend_pid();"

# 3. Drop and recreate corrupted production database
psql -U postgres -d postgres -c "DROP DATABASE zynpath_prod;"
psql -U postgres -d postgres -c "CREATE DATABASE zynpath_prod OWNER zynpath_app;"

# 4. Restore pre-deployment snapshot
pg_restore -U zynpath_app -d zynpath_prod -v "/var/backups/zynpath/pre_deploy_${PREV_COMMIT}.dump"

# 5. Start previous application container
docker compose -f /etc/zynpath/docker-compose.prod.yml up -d backend

# 6. Re-enable traffic ingress
# Remove maintenance mode at edge load balancer
```

---

## 5. Post-Rollback Diagnostics & Incident Review
1. Export crash logs:
   ```bash
   docker logs --tail=1000 zynpath-backend > /var/log/zynpath/failed_deployment_$(date +%s).log
   ```
2. Re-create the failure hermetically in the staging environment.
3. Conduct Root Cause Analysis (RCA) before scheduling the next release window.
