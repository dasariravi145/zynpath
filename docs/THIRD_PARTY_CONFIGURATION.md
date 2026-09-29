# Third-Party Service Configuration & Integration Inventory — Zynpath

**Document ID:** `DOC-CONFIG-3RD-001`  
**Application Title:** *Zynpath: Number Path Puzzle*  
**Application ID:** `com.zynpath.game`  
**Status:** Authoritative External Integrations Inventory  

---

## 1. Executive Summary

Zynpath integrates with an intentional, minimal footprint of external third-party services to deliver cloud authentication, real-time multiplayer, Google Play billing, optional rewarded hints, and automated CI/CD builds.

This document inventories every external provider, documenting credential ownership, environment variable mapping, production requirements, and fallback behavior when unconfigured.

---

## 2. Third-Party Integration Matrix

| External Service | Subsystem Purpose | Environment Variable / Config Key | Configuration Ownership | Production Requirement | Fallback Behavior When Unconfigured |
|---|---|---|---|---|---|
| **Google Play Console** | App distribution, Play App Signing, in-app billing subscriptions | Service Account JSON via `GOOGLE_APPLICATION_CREDENTIALS` | Product Owner / Release Manager | **MANDATORY** for Play Store publication and live billing | Client runs in guest mode; billing falls back to local unconfigured test state. |
| **Google Cloud Console** | Google Sign-In OAuth token verification | `GOOGLE_CLIENT_ID` | DevSecOps Lead | **MANDATORY** for Google Sign-In | Guest play works 100%; Google Sign-In button displays configuration notice. |
| **Firebase Console** | Optional Firebase Auth / FCM Push Notifications | `FCM_SERVICE_ACCOUNT_KEY` | DevSecOps Lead | Optional (enhanced push alerts) | In-app notification center functions locally via Room database; push alerts suppressed. |
| **Google Mobile Ads (AdMob)** | Optional rewarded ads for extra solo hints | `ADMOB_APP_ID`, `ADMOB_REWARDED_UNIT_ID`, `ADMOB_SSV_KEY_ID` | Monetization Lead | **MANDATORY** for live ad monetization | Test ad IDs used during development; ad requests fail gracefully without blocking gameplay. |
| **PostgreSQL Hosting** | Managed relational database cluster | `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` | Infrastructure / Backend Lead | **MANDATORY** for production backend deployment | Backend uses embedded in-memory/H2 profile during local tests. |
| **GitHub Actions** | Automated CI builds and release bundle packaging | `ZYNPATH_UPLOAD_KEYSTORE_BASE64`, `ZYNPATH_UPLOAD_KEY_PASSWORD` | Release Engineer | **MANDATORY** for automated release `.aab` signing | CI builds debug APK; release bundle generation defaults to unsigned bundle. |
| **Domain & Edge TLS** | Reverse proxy, TLS termination, API routing | DNS records for `api.zynpath.app` and `zynpath.app` | Infrastructure Lead | **MANDATORY** for public multiplayer and compliance URLs | Local testing connects via `localhost:8080` or `10.0.2.2:8080`. |

---

## 3. Detailed Service Configuration Procedures

### 3.1 Google Play Developer API (Billing Verification)
1. In [Google Cloud Console](https://console.cloud.google.com), create a project linked to your Google Play Console account.
2. Enable the **Google Play Android Developer API**.
3. Create a **Service Account** with the role `Service Account User`.
4. In Google Play Console $\to$ **Users & Permissions**, invite the service account email and grant **"View financial data"** and **"Manage orders and subscriptions"** permissions.
5. Generate a JSON private key and deploy to `/app/secrets/google-play-service-account.json` on the production host.

### 3.2 Google Sign-In (OAuth 2.0 Credentials)
1. In Google Cloud Console $\to$ **APIs & Services** $\to$ **Credentials**:
   * Create an **OAuth 2.0 Client ID** of type **Android**:
     * Package name: `com.zynpath.game`
     * SHA-1 Certificate Fingerprint: Registered from Google Play App Signing certificate and local debug keystore.
   * Create an **OAuth 2.0 Client ID** of type **Web application**:
     * Authorized redirect URIs: `https://api.zynpath.app/api/v1/auth/google/callback`
2. Set the Web Client ID string in `backend/src/main/resources/application-prod.yml` or inject via `GOOGLE_CLIENT_ID`.

### 3.3 Google Mobile Ads (AdMob)
1. In the [AdMob Console](https://apps.admob.com), register `Zynpath: Number Path Puzzle` (Android).
2. Create an ad unit:
   * Format: **Rewarded**
   * Ad Unit Name: `Solo Extra Hint Rewarded`
   * Server-Side Verification (SSV): Enable SSV and specify callback URL `https://api.zynpath.app/api/v1/ads/admob-ssv`.
3. Obtain the AdMob App ID (format: `ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY`) and replace the test placeholder in `android/app/build.gradle.kts`.

---

## 4. Credential Rotation & Security Policy
* **Zero Credential Retention in Git:** All third-party secrets must remain strictly externalized.
* **Annual Rotation:** Service account keys and JWT signing secrets must be reviewed and rotated annually.
* **Revocation Procedure:** If any service account key is suspected of compromise, immediately delete the key in Google Cloud Console / Firebase and generate a fresh key pair per `docs/INCIDENT_RESPONSE_RUNBOOK.md`.
