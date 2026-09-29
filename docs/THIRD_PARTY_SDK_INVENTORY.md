# Zynpath Third-Party SDK Inventory & Data Handling Review

**Status:** Authoritative  
**Domain:** Google Play Third-Party SDK Audit & Privacy Compliance  
**Last Verified:** September 27, 2026  

---

## 1. SDK Audit Summary

Zynpath maintains a strictly minimized third-party dependency footprint. The codebase does not include third-party analytics trackers, social graph scrapers, device fingerprinting libraries, or cross-app advertising networks. Every integrated library is audited against Google Play Developer Program Policies.

---

## 2. Integrated Third-Party SDK Registry

### 1. Google Play Billing Library
- **Artifact:** `com.android.billingclient:billing-ktx:7.1.1`
- **Provider:** Google LLC
- **Purpose:** Securely processes optional Premium monthly (`₹99`) and 6-month (`₹499`) subscriptions.
- **Data Collected / Shared:**
  - Google Play Account ID (handled directly by Google Play Services, never exposed to app).
  - Purchase token and Order ID (cryptographically verified server-side).
- **Network Endpoints:** Official Google Play Store services (`com.android.vending`).
- **Policy Compliance:** Full compliance with Google Play Payments Policy. No third-party or external alternative payment rails are included in the mobile client.
- **Verification Status:** `VERIFIED IN CODEBASE` (`android/app/build.gradle.kts:109`).

### 2. Google Mobile Ads SDK (AdMob)
- **Artifact:** `com.google.android.gms:play-services-ads:23.6.0`
- **Provider:** Google LLC
- **Purpose:** Delivers optional rewarded video advertisements for free players seeking bonus Solo puzzle hints.
- **Data Collected / Shared:**
  - Google Advertising ID (GAID), device IP address, diagnostic telemetry, and ad interaction events (handled per Google's Advertising Policies).
- **User Control & Frequency:**
  - **100% Optional:** Ads are never displayed automatically, never interrupt gameplay, and are never shown during multiplayer.
  - **Capped:** Maximum of 5 rewarded ads per 24-hour UTC window.
  - **Ad-Free for Subscribers:** The advertising SDK is completely deactivated for players with active Premium subscriptions.
- **Policy Compliance:** Complies with Google Play Ads Policy and Families Self-Certified Ads SDK standards.
- **Verification Status:** `VERIFIED IN CODEBASE` (`android/app/build.gradle.kts:112`).

### 3. OkHttp & Logging Interceptor
- **Artifact:** `com.squareup.okhttp3:okhttp:4.12.0`, `logging-interceptor:4.12.0`
- **Provider:** Square, Inc. / Open Source (Apache 2.0)
- **Purpose:** HTTP/2, TLS 1.3 client, and real-time WebSocket transport for multiplayer duels.
- **Data Collected / Shared:** None. Operates purely as a local network transport utility.
- **Security Audit:** Logging interceptor is restricted to `Level.BASIC` or disabled in production to guarantee that bearer authorization tokens and session keys are never logged in Android logcat.
- **Verification Status:** `VERIFIED IN CODEBASE` (`android/app/build.gradle.kts:105-106`).

### 4. AndroidX Jetpack & Google Open Source Libraries
- **Artifacts:**
  - `androidx.compose.bom:2026.02.01` (Compose UI, Material 3, Foundation)
  - `androidx.room:room-runtime:2.8.4` (SQLite object mapping for offline progress)
  - `androidx.datastore:datastore-preferences:1.1.2` (Coroutines-based key-value storage)
  - `androidx.work:work-runtime-ktx:2.10.0` (Offline sync and periodic maintenance)
  - `androidx.navigation:navigation-compose:2.8.8` (Declarative in-app routing)
  - `com.google.dagger:hilt-android:2.59.2` (Compile-time dependency injection)
- **Provider:** Google LLC / Android Open Source Project
- **Data Collected / Shared:** None. Standard system and application framework components.
- **Verification Status:** `VERIFIED IN CODEBASE`.

---

## 3. Explicit List of Omitted / Unused SDKs

To eliminate compliance ambiguity during Google Play app review, the following categories of SDKs are confirmed **ABSENT** from the Zynpath codebase:
- **No Third-Party Analytics SDKs:** No Google Analytics for Firebase, AppsFlyer, Adjust, Kochava, Flurry, or Mixpanel.
- **No Third-Party Crash Trackers:** No Crashlytics, Sentry, or Bugsnag (all crash handling is performed via native Android system crash reporting and our in-app `ErrorClassifier`).
- **No Facebook Audience Network or Ad Mediation SDKs:** No AppLovin, Unity Ads, IronSource, or Mintegral.
- **No Device Fingerprinting SDKs:** Zero hardware MAC address, IMEI, or telephony inspection libraries.
