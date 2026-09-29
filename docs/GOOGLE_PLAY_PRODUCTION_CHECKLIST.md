# Google Play Production Launch Checklist — Zynpath

**Document ID:** `DOC-PLAY-PROD-001`  
**Application Title:** *Zynpath: Number Path Puzzle*  
**Application ID:** `com.zynpath.game`  
**Target SDK:** 36 (Android 16)  
**Status:** Authoritative Production Release Gate Checklist  

---

## 1. Overview of the Production Promotion Pathway

Promoting an application from testing tracks to public production on Google Play requires satisfying strict policy, testing, and operational prerequisites. 

For personal developer accounts created after November 2023, Google Play mandates a **Closed Testing phase with a minimum of 20 opt-in testers active for at least 14 continuous days** prior to unlocking production release capabilities. Organizational developer accounts may proceed directly through internal and closed testing.

---

## 2. Pre-Production Promotion Gates

| Gate # | Milestone / Requirement | Verification Requirement | Status |
|---|---|---|---|
| **GATE-01** | **Internal Testing Clearance** | Internal Testing Track active with $\ge 5$ verified devices, zero fatal crashes over 48 hours. | **READY TO EXECUTE** |
| **GATE-02** | **Closed Testing Requirement** | 20 testers enrolled for 14 continuous days (if personal developer account). | **PENDING CONSOLE ENROLLMENT** |
| **GATE-03** | **IARC Content Rating** | Complete official IARC questionnaire inside Play Console (Expected: Everyone / PEGI 3). | **DOCUMENTED** (`docs/CONTENT_RATING_PREPARATION.md`) |
| **GATE-04** | **Data Safety Declarations** | Complete Data Safety form matching `docs/DATA_SAFETY_MATRIX.md`. | **DOCUMENTED** (`docs/DATA_SAFETY_MATRIX.md`) |
| **GATE-05** | **Live Privacy Policy URL** | Live HTTPS URL hosted at `https://zynpath.app/privacy` (200 OK status). | **LOCAL ASSET READY** (`privacy_policy.html`) |
| **GATE-06** | **Live Account Deletion URL**| Live HTTPS URL hosted at `https://zynpath.app/delete-account` (200 OK status). | **LOCAL ASSET READY** (`account_deletion_request.html`) |
| **GATE-07** | **In-App Billing Products** | `zynpath_premium_monthly` and `zynpath_premium_6months` activated in Play Console. | **REQUIRES CONSOLE CREATION** |
| **GATE-08** | **Production Backend & DNS** | PostgreSQL 16+ live; `https://api.zynpath.app` responding to health checks. | **REQUIRES CLOUD DEPLOYMENT** |
| **GATE-09** | **Store Assets Uploaded** | 512x512 icon, 1024x500 feature graphic, and minimum 4 phone screenshots uploaded. | **ASSETS READY IN REPO** |
| **GATE-10** | **Target SDK Compliance** | Target SDK 36 verified against Google Play's platform requirement. | **VERIFIED (SDK 36)** |

---

## 3. Google Play Pre-Launch Report (PLR) Review

Before promoting to production, inspect Google's automated Pre-Launch Report generated on the testing track:
1. **Firebase Test Lab Stability:** Review automated crawl logs across 10+ virtual and physical Android devices (API 24 to 36).
2. **Crash & ANR Rate:** Must be **0.00%** on the crawled devices.
3. **Accessibility Violations:** Verify zero missing `contentDescription` flags or touch targets smaller than $48 \times 48\text{ dp}$.
4. **Security Vulnerabilities:** Confirm zero flags for plaintext HTTP, insecure SSL pinning, or exposed cryptographic keys.

---

## 4. Staged Rollout Strategy

Public production releases of Zynpath must use Google Play's **Staged Rollout** feature to mitigate unforeseen device fragmentation regressions:

```
Day 1: 5% Rollout  ──(Monitor 24h)──> Day 2: 10% Rollout  ──(Monitor 24h)──> 
Day 3: 25% Rollout ──(Monitor 24h)──> Day 4: 50% Rollout  ──(Monitor 24h)──> Day 5: 100% Rollout
```

### Staged Rollout Execution Schedule:
* **Stage 1 (Day 1 — 5% User Base):**
  * Target: Early adopters and highly active users.
  * Monitoring Period: 24 hours.
  * Validation: Monitor Android Vitals crash rate and Spring Boot server request volume.
* **Stage 2 (Day 2 — 10% User Base):**
  * Target: Broader device coverage.
  * Monitoring Period: 24 hours.
  * Validation: Verify WebSocket matchmaking latency and multiplayer match completion rates.
* **Stage 3 (Day 3 — 25% User Base):**
  * Target: Regional distribution.
  * Monitoring Period: 24 hours.
* **Stage 4 (Day 4 — 50% User Base):**
  * Target: High concurrency soak.
  * Monitoring Period: 24 hours.
* **Stage 5 (Day 5 — 100% Full Production):**
  * Target: Global release.

---

## 5. Rollout Halt Thresholds & Emergency Rollback

The release manager must immediately **HALT** the staged rollout if any of the following metric thresholds are exceeded:

| Metric | Google Play Bad Behavior Threshold | Zynpath Emergency Halt Threshold |
|---|---|---|
| **Crash Rate (User-Perceived)** | $\ge 1.09\%$ | **$\ge 0.40\%$** |
| **ANR Rate (User-Perceived)** | $\ge 0.47\%$ | **$\ge 0.20\%$** |
| **Backend 5xx Server Error Rate** | N/A | **$\ge 0.50\%$ of requests** |
| **Multiplayer Desync Rate** | N/A | **$\ge 1.00\%$ of completed matches** |
| **In-App Billing Failure Rate** | N/A | **$\ge 0.20\%$ of verified purchases** |

### Halt Procedure:
1. In Google Play Console, go to **Release** $\to$ **Production**.
2. Click **"Halt rollout"**. This immediately prevents new users from downloading the update while preserving the installed version for current users.
3. Diagnose root cause using de-obfuscated stack traces in Play Console and server logs.
4. Prepare a forward-fix release with an incremented `versionCode` (e.g., `versionCode = 2`, `versionName = "1.0.1"`). Android does not support version code downgrades.

---

## 6. Final Production Launch Sign-Off

Production promotion requires explicit dual-signoff:
* **Lead Software Architect / Engineering Lead:** `[PENDING OPERATOR SIGNOFF]`
* **Product Owner / Release Manager:** `[PENDING OPERATOR SIGNOFF]`
* **Date of Authorization:** `[PENDING]`
