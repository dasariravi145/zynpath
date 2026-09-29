# Database Backup & Disaster Recovery Specification

## 1. Overview & Scope
This document specifies the disaster recovery, backup policy, retention schedule, and verified restore procedures for Zynpath's PostgreSQL production database.

---

## 2. Backup Strategy & Scope

### Data in Scope:
- Core Player Accounts & Profiles (`player_accounts`, `player_profiles`, `external_identities`)
- Entitlements & Purchases (`subscription_entitlements`, `player_cosmetics`, `player_hint_balances`)
- Historical Records & Results (`match_results`, `daily_challenge_attempts`, `competitive_stats`)
- Social Relationships (`friend_relationships`, `friend_requests`, `player_blocks`)
- Compliance Logs (`account_deletion_audits`, `account_export_requests`)

### Backup Cadence & Retention:
| Backup Type | Frequency | Retention Period | Storage Target | Encryption |
|---|---|---|---|---|
| **Continuous WAL** | Real-time stream (Point-in-Time Recovery) | 7 days | Encrypted Object Storage (S3/GCS) | AES-256 (KMS) |
| **Full Logical Snapshot** | Daily at 02:00 UTC | 30 days | Secondary Cloud Region | AES-256 (KMS) |
| **Pre-Release Snapshot** | Immediately before every release | 14 days | Local + Encrypted Object Storage | AES-256 (KMS) |
| **Monthly Archive** | 1st of every month | 365 days | Cold Archive (Glacier/Coldline) | AES-256 (KMS) |

---

## 3. Automated Backup Script (Cron / Systemd)
Reference script for automated daily logical snapshots:

```bash
#!/bin/bash
set -euo pipefail

BACKUP_DIR="/var/backups/zynpath"
TIMESTAMP=$(date -u +"%Y%m%d_%H%M%SZ")
BACKUP_FILE="${BACKUP_DIR}/zynpath_backup_${TIMESTAMP}.dump"

mkdir -p "${BACKUP_DIR}"

# 1. Execute compressed PostgreSQL dump
pg_dump -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" \
  -Fc --no-owner --no-privileges -f "${BACKUP_FILE}"

# 2. Encrypt and upload to remote object storage
aws s3 cp "${BACKUP_FILE}" "s3://zynpath-backups-prod/${TIMESTAMP}/backup.dump" \
  --sse aws:kms --sse-kms-key-id "${KMS_KEY_ARN}"

# 3. Clean up local snapshots older than 7 days
find "${BACKUP_DIR}" -type f -name "*.dump" -mtime +7 -delete

echo "Zynpath backup completed successfully: ${BACKUP_FILE}"
```

---

## 4. Verification & Authorization Controls
- **Destructive Restore Authorization:** Restoring a database snapshot over an existing production database permanently replaces current records. Any production restoration requires **written dual-authorization** from the Lead Backend Engineer and Product Owner.
- **Never Auto-Overwrite:** Restore scripts must require explicit confirmation (`--force-destructive-restore`) and fail if executed without an operator present.

---

## 5. Step-by-Step Restoration Procedure

### Step 1: Quarantine the Instance
Isolate the database from application traffic to ensure no concurrent writes:
```bash
docker compose -f backend/docker-compose.prod.yml stop backend
```

### Step 2: Download & Validate Snapshot
Download the target snapshot from secure storage and verify checksum:
```bash
aws s3 cp "s3://zynpath-backups-prod/20260927_020000Z/backup.dump" /tmp/restore_target.dump
pg_restore -l /tmp/restore_target.dump | head -n 20
```

### Step 3: Execute Controlled Restoration
Restore the data into a clean staging or recovery database first to verify integrity:
```bash
createdb -U postgres zynpath_verification
pg_restore -U zynpath_app -d zynpath_verification -v /tmp/restore_target.dump

# Run row count sanity checks
psql -U zynpath_app -d zynpath_verification -c "SELECT count(*) FROM player_accounts;"
psql -U zynpath_app -d zynpath_verification -c "SELECT count(*) FROM match_results;"
```

### Step 4: Promote and Re-Connect
Once verified in the isolated database, redirect backend configuration (`SPRING_DATASOURCE_URL`) to the restored database and start the backend service.
