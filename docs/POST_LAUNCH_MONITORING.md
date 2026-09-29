# Post-Launch Monitoring & Operational Observability — Zynpath

**Document ID:** `DOC-OPS-MON-001`  
**Application Title:** *Zynpath: Number Path Puzzle*  
**Application ID:** `com.zynpath.game`  
**Status:** Authoritative Post-Launch Observability Framework  

---

## 1. Overview & Observability Pillars

To guarantee a premium player experience following release, Zynpath implements structured monitoring across three primary pillars:
1. **Client Health & Android Vitals:** Google Play Console crash, ANR, and rendering metrics.
2. **Backend Services & Infrastructure:** Spring Boot Actuator metrics, Prometheus telemetry, HikariCP database pool saturation, and JVM health.
3. **Gameplay & Business Operations:** Matchmaking queue health, authoritative puzzle validation throughput, subscription verification, and account deletion tracking.

---

## 2. Core Service Level Objectives (SLOs) & Thresholds

| Metric Domain | Metric Name | Target Objective | Warning Threshold | Emergency Action Threshold |
|---|---|---|---|---|
| **Android Vitals** | **User-Perceived Crash Rate** | $\le 0.15\%$ | $> 0.30\%$ | **$\ge 0.47\%$** (Halt Rollout) |
| **Android Vitals** | **User-Perceived ANR Rate** | $\le 0.08\%$ | $> 0.15\%$ | **$\ge 0.24\%$** (Halt Rollout) |
| **Android Vitals** | **Slow Rendering Frames ($>16\text{ms}$)**| $\le 2.0\%$ | $> 4.0\%$ | $\ge 8.0\%$ |
| **App Startup** | **Cold Start Time ($t_{cold}$)** | $< 1200\text{ ms}$ | $> 1500\text{ ms}$ | $\ge 2500\text{ ms}$ |
| **Backend REST** | **HTTP 5xx Server Error Rate** | $\le 0.05\%$ | $> 0.20\%$ | **$\ge 0.50\%$** |
| **Backend REST** | **p95 REST Latency** | $< 80\text{ ms}$ | $> 150\text{ ms}$ | $\ge 300\text{ ms}$ |
| **Multiplayer** | **WebSocket Match Disconnects** | $\le 0.50\%$ | $> 1.00\%$ | $\ge 2.50\%$ |
| **Matchmaking** | **Quick Duel Queue Wait (p95)** | $< 10\text{ s}$ | $> 20\text{ s}$ | $\ge 35\text{ s}$ (45s ceiling) |
| **Database** | **HikariCP Active Connection Pool** | $\le 10 / 20$ | $> 15 / 20$ | $\ge 19 / 20$ |
| **Monetization**| **Billing Token Validation Failures** | $\le 0.05\%$ | $> 0.10\%$ | $\ge 0.50\%$ |

---

## 3. Daily & Weekly Monitoring Cadence

### Daily Checklist (First 14 Days Post-Launch):
* [ ] **Android Vitals Inspection:** Review Play Console dashboard for new crash clusters or ANRs grouped by Android OS version and device model.
* [ ] **Server Error Log Review:** Inspect Spring Boot production log output for unhandled exceptions or elevated error codes:
  ```bash
  docker logs --since 24h zynpath-backend | grep -E "ERROR|WARN" | head -n 50
  ```
* [ ] **Database Connection Health:** Check HikariCP connection leak detection logs and PostgreSQL disk capacity.
* [ ] **Multiplayer Race Metrics:** Verify match completion rates (confirm completed matches vs abandoned/forfeited games).
* [ ] **Account Deletion Verification:** Confirm all received account deletion requests were successfully redacted without remaining orphaned sessions.

### Weekly Review Cadence:
* [ ] **Personal Analytics Performance:** Review query times on `GET /api/v1/analytics/personal` to ensure indexing remains effective as player match histories grow.
* [ ] **AdMob Rewarded Hint Performance:** Verify rewarded ad fill rate, impression count, and confirmed SSV callback conversions.
* [ ] **Subscription Renewal Health:** Check Google Play Developer API webhook processing for renewals, grace periods, and cancellations.

---

## 4. Operational Alerting Architecture

```
┌─────────────────────────────────┐       ┌─────────────────────────────────┐
│  Spring Boot Actuator Endpoints │       │   Google Play Android Vitals    │
│   /actuator/prometheus          │       │   Crash / ANR Threshold Alerts  │
└────────────────┬────────────────┘       └────────────────┬────────────────┘
                 │                                         │
                 ▼                                         ▼
┌─────────────────────────────────┐       ┌─────────────────────────────────┐
│     Prometheus / Grafana        │       │     Email / Ops Notification    │
│  (JVM Heap, HikariCP, Latency)  │       │    (On-Call Engineering Team)   │
└─────────────────────────────────┘       └─────────────────────────────────┘
```

### Critical Alerts Configured:
1. **`ZynpathBackendDown`**: Triggers if `/api/v1/health` fails 3 consecutive checks over 1 minute.
2. **`HighServerErrorRate`**: Triggers if HTTP 5xx responses exceed 1% over a 5-minute rolling window.
3. **`DatabasePoolSaturated`**: Triggers if HikariCP active connections exceed 18 (out of 20) for more than 2 minutes.
4. **`PlayVitalsSpike`**: Configured inside Google Play Console to send immediate email notifications if the crash rate exceeds $0.40\%$.

---

## 5. Privacy-Safe Telemetry Standards

Zynpath enforces strict privacy controls in all operational logging and telemetry:
* **Zero PII in Logs:** User passwords, billing tokens, JWT access tokens, and email addresses are automatically redacted via regex patterns before outputting to stdout or log aggregators (`docs/OPERATIONAL_LOGGING.md`).
* **Zero Third-Party Trackers:** No invasive surveillance SDKs (such as Facebook Pixel, Adjust, or AppsFlyer) exist in the codebase.
* **Aggregated Metrics Only:** All operational metrics report system throughput, error rates, and latencies without user-identifying telemetry.
