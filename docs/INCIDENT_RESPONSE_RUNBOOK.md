# Incident Response Runbook — Zynpath Production Services

**Document ID:** `SOP-INCIDENT-001`  
**Application Title:** *Zynpath: Number Path Puzzle*  
**Scope:** Backend Services, Real-Time Multiplayer, Billing, and Security  
**Status:** Authoritative Emergency Operational Standard Operating Procedure (SOP)  

---

## 1. Incident Severity Classification

| Severity Level | Definition | Response SLA | Escalation Target |
|---|---|---|---|
| **SEV-1 (Critical)** | Complete service outage, data corruption, active security breach, or total billing verification failure. | **15 minutes** | Lead Architect, Backend Lead, DevSecOps Lead |
| **SEV-2 (High)** | Real-time multiplayer outage, failure of Daily Challenge leaderboards, or elevated authentication failure rates ($>5\%$). | **1 hour** | Backend On-Call Engineer |
| **SEV-3 (Medium)** | Offline sync degradation, intermittent WebSocket reconnect latency, or non-critical UI asset failures. | **4 hours** | Engineering Team |
| **SEV-4 (Low)** | Minor cosmetic or documentation issues, non-impacting telemetry gaps. | **Next Business Day** | Product Team |

---

## 2. Standard Incident Response Playbooks

### Playbook 1: Service Outage / Host Unreachability (SEV-1)
* **Symptoms:** Synthetic health probe `/api/v1/health` returns HTTP 5xx or connection timeout; client shows offline banner.
* **Immediate Actions:**
  1. Inspect container status on production host:
     ```bash
     docker ps -a
     docker logs --tail 200 zynpath-backend
     ```
  2. If the container exited unexpectedly due to Out-Of-Memory (OOM):
     * Review JVM heap allocation (`-XX:MaxRAMPercentage=75.0`).
     * Restart the container: `docker compose -f docker-compose.prod.yml restart backend`.
  3. If host is unresponsive:
     * Check cloud provider infrastructure status.
     * Verify Nginx reverse proxy logs (`/var/log/nginx/error.log`).
  4. Client Impact Mitigation: Remind users via status page that **Solo mode remains 100% playable offline**.

### Playbook 2: Authentication & Guest Linking Failure (SEV-2)
* **Symptoms:** Elevated HTTP 401/403 errors on `/api/v1/auth/*`; users unable to link Google or Facebook accounts.
* **Immediate Actions:**
  1. Verify JWT secret validity in environment variables.
  2. Inspect Google Cloud Console OAuth token expiration and OAuth Consent Screen status.
  3. Verify token validation logs in Spring Boot:
     ```bash
     docker logs zynpath-backend | grep -i "AuthenticationException"
     ```
  4. Ensure client clock skew is within acceptable tolerance ($\pm 5\text{ minutes}$).

### Playbook 3: Google Play Billing Verification Failure (SEV-1)
* **Symptoms:** Users purchase subscriptions in-app but backend returns HTTP 422 / 500 on purchase token verification.
* **Immediate Actions:**
  1. Check connectivity to Google Play Developer API:
     * Verify service account credentials file at `/app/secrets/google-play-service-account.json`.
     * Confirm Google Play Console API access permissions (ensure Service Account has "View financial data" permission).
  2. Inspect billing error logs:
     ```bash
     docker logs zynpath-backend | grep -i "SubscriptionException"
     ```
  3. Mitigation: The Android client implements local grace period caching; valid Play Billing purchase objects will maintain local access for up to 24 hours while backend server-to-server connectivity recovers.

### Playbook 4: Real-Time Multiplayer Outage / Matchmaking Lockup (SEV-2)
* **Symptoms:** Quick Duel queue times out after 45 seconds; WebSocket connections disconnect prematurely.
* **Immediate Actions:**
  1. Inspect active WebSocket connection count and thread pool saturation:
     ```bash
     curl http://localhost:8080/actuator/metrics/jvm.threads.live
     ```
  2. Verify reverse proxy WebSocket upgrade headers in Nginx configuration:
     ```nginx
     proxy_set_header Upgrade $http_upgrade;
     proxy_set_header Connection "upgrade";
     proxy_read_timeout 3600s;
     ```
  3. If room state is deadlocked in memory:
     * Restart Spring Boot container to clear ephemeral in-memory matchmaking queues. Active solo play is unaffected.

### Playbook 5: Data Synchronization Failure (SEV-3)
* **Symptoms:** Client reports sync errors; offline progress does not reflect on alternate devices.
* **Immediate Actions:**
  1. Inspect PostgreSQL connection pool utilization via Actuator:
     ```bash
     curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
     ```
  2. If HikariCP pool is exhausted (20/20 connections active), check for long-running unindexed queries or locked rows.
  3. Verify client operation queue payload size bounds (must be $<1\text{ MB}$).

### Playbook 6: Unsolvable Puzzle Release / Corrupt Level Emergency (SEV-1)
* **Symptoms:** Community reports that Daily Challenge or a campaign level is mathematically impossible to solve.
* **Immediate Actions:**
  1. Extract the reported puzzle JSON from the catalog or daily table.
  2. Run the offline mathematical backtracker solver:
     ```bash
     java -cp zynpath-backend.jar com.zynpath.backend.puzzle.tools.SolvePuzzleCli --puzzle puzzle.json
     ```
  3. If confirmed unsolvable (0 solutions found):
     * **For Daily Challenge:** Update the database record for `daily_challenge_schedule` for the target UTC date to assign an emergency backup puzzle ID from the verified 14-puzzle pool (`docs/DAILY_CHALLENGE_SCHEDULE.md`).
     * **For Solo Level:** Prepare an emergency catalog hotfix release.

### Playbook 7: Security Incident & Credential Compromise (SEV-1)
* **Symptoms:** Discovered API key leakage, unauthorized access spike, or database breach indicator.
* **Immediate Actions:**
  1. **Isolate Affected Systems:** Block compromised IP addresses at the edge reverse proxy or cloud firewall.
  2. **Rotate Secrets:**
     * Generate a new 256-bit `JWT_SECRET`. (Note: This invalidates all active sessions, requiring users to refresh or re-login).
     * Rotate database passwords in PostgreSQL and update container environment.
     * Revoke and reissue the Google Play Service Account JSON key in Google Cloud Console.
  3. **Audit Access Logs:** Review `security_audit_log` database table for unauthorized data export or account modification requests.
  4. **Post-Mortem:** Conduct root-cause analysis within 48 hours and document prevention remediations.

---

## 3. Communication & Escalation Templates

### User-Facing Outage Notification Template:
> **Zynpath Status Update:** We are currently investigating an issue affecting online multiplayer and cloud profile synchronization. Solo World exploration, offline practice, and interactive tutorials remain 100% playable on your device. We are actively working to restore full online services.

---

## 4. Post-Incident Review Protocol
Every SEV-1 or SEV-2 incident requires a mandatory post-mortem document completed within 72 hours, covering:
1. Executive Timeline (detection, response, resolution).
2. Root Cause Analysis (5 Whys methodology).
3. Preventative Architectural & Operational Action Items.
