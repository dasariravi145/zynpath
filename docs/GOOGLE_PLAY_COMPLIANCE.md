# Zynpath Google Play Compliance Inventory & Release Preparation

**Status:** Authoritative  
**Domain:** Google Play Policy Compliance, Manifest Audit & Release Preparation  
**Target API:** Android 16 (API 36)  
**Min API:** Android 7.0 (API 24)  
**Version:** 1.0.0 (Build 1)  

---

## 1. Compliance Executive Summary

Zynpath is engineered as a **Guest-First, Offline-First** continuous-path puzzle game. The application is designed to ensure strict conformance with current Google Play Developer Program Policies:
1. **Zero Unnecessary Permissions:** The manifest is audited and minimized to 6 standard functional permissions.
2. **Target SDK Conformance:** Configured to target Android 16 (API 36) with modern `compileSdk = 36`, Kotlin 2.2.10, and AGP 9.2.1.
3. **Dual Account Deletion:** Accessible self-service deletion directly in-app and via an external web portal (`assets/compliance/account_deletion_request.html`) without requiring app reinstallation.
4. **Data Safety Evidence:** Transparent disclosures covering Guest UUIDs, optional Google/Facebook authentication tokens, Google Play Billing purchase tokens, and AdMob rewarded ads.
5. **No Unsupported Claims:** Zero fabrication of trademark registrations, content ratings, or unverified live prices.

---

## 2. Android Permission Audit

| Permission Name | Category | Protection Level | Actual Feature Using It | Runtime Prompt? | Sensitive / Restricted? | Verification Status |
|---|---|---|---|---|---|---|
| `android.permission.INTERNET` | Networking | Normal | Backend REST API, WebSocket multiplayer, Daily Challenge server verification, Google Play Billing, AdMob ads. | No | No | `VERIFIED` |
| `android.permission.ACCESS_NETWORK_STATE` | Networking | Normal | In-app offline detection, reachability banner, and seamless offline Solo routing. | No | No | `VERIFIED` |
| `android.permission.VIBRATE` | Hardware | Normal | Tactile haptic feedback for valid moves, checkpoint connections, and invalid path rejections. | No | No | `VERIFIED` |
| `com.android.vending.BILLING` | In-App Purchases | Normal | Google Play Billing library (v7.1.1) for optional Premium subscriptions. | No | No | `VERIFIED` |
| `android.permission.POST_NOTIFICATIONS` | Notifications | Dangerous (API 33+) | Daily Challenge reminders and friend match alerts. Requested on demand with graceful refusal. | Yes (API 33+) | No | `VERIFIED` |
| `android.permission.RECEIVE_BOOT_COMPLETED` | System Broadcast | Normal | Rescheduling local exact alarms for Daily Challenge reminders following device restart. | No | No | `VERIFIED` |

### Removed / Avoided Permissions:
- `READ_CONTACTS` / `WRITE_CONTACTS`: Avoided. Zynpath uses Public Zynpath IDs and shareable deep links; contact address books are never accessed.
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`: Avoided. Zynpath does not collect or use geographic coordinates.
- `CAMERA` / `RECORD_AUDIO`: Avoided. No photo taking or microphone streaming.
- `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE`: Avoided. All assets and local Room databases reside strictly in app-internal sandboxed storage.

---

## 3. Target SDK & Platform Readiness

- **`compileSdk = 36`**: Full access to Android 16 platform APIs and Jetpack Compose toolchain.
- **`targetSdk = 36`**: Conforms to Google's published requirement effective August 31, 2026 for new apps and updates.
- **`minSdk = 24`**: Supports 98%+ of active Android devices worldwide (Android 7.0 Nougat and above).
- **Edge-to-Edge Compatibility**: Jetpack Compose Scaffold and WindowInsets handling support edge-to-edge system navigation without visual overlap.
- **Backup & Restore Compliance**:
  - `res/xml/data_extraction_rules.xml` (API 31+): Excludes `zyn_secure_session.enc` while preserving `zynpath_database`.
  - `res/xml/backup_rules.xml` (pre-API 31): Excludes `zyn_secure_session.enc` while preserving `zynpath_database`.
  - Prevents fatal decryption crashes when users transfer to a new device without hardware KeyStore master keys.

---

## 4. Google Play Reviewer Access Instructions

When submitting Zynpath to Google Play Console, reviewers must be provided instructions to test all features without requiring billing purchases or private infrastructure:

### Reviewer Walkthrough Instructions:
1. **Core Gameplay (No Credentials Needed):**
   - Launch app. It automatically enters Guest Mode.
   - Tap **"Play"** or select **"Solo Worlds"** to test 300 continuous-path levels across 6 handcrafted worlds.
   - Tap **"Daily Challenge"** to test UTC daily puzzle solving. Works 100% offline.
2. **Reviewer Test Account (Online Multiplayer & Cloud Profile):**
   - Go to **Settings** → **Account**.
   - Tap **"Sign in with Google"** (or use Demo Reviewer credentials provided below).
   - Test Account Email: `[DEVELOPER_CONFIGURED_REVIEWER_EMAIL]` *(Configured in Play Console)*
   - Test Account Password: `[DEVELOPER_CONFIGURED_REVIEWER_PASSWORD]` *(Configured in Play Console)*
3. **Multiplayer Testing:**
   - With two devices running the test build or emulator, navigate to **Multiplayer Hub**.
   - Select **Friend Duel** or **Mini League** and enter a shared 6-character room code.
4. **Subscription / Premium Testing:**
   - In Google Play Console, add the reviewer account to the **License Testing** whitelist.
   - Navigate to **Settings** → **Premium**. Tap any subscription offer. Google Play will return a test purchase response without charging real funds.

---

## 5. Contact & Identity Information Placeholders

*The following fields require official developer details prior to Google Play Console publication:*

- **Developer / Organization Name:** `[DEVELOPER_OR_STUDIO_NAME]`
- **Public Support Email:** `support@zynpath.com` *(Placeholder)*
- **Privacy Officer Contact:** `privacy@zynpath.com` *(Placeholder)*
- **Official Website:** `https://zynpath.com` *(Placeholder)*
- **Public Privacy Policy URL:** `https://zynpath.com/privacy` *(Placeholder)*
- **Account Deletion Portal URL:** `https://zynpath.com/delete-account` *(Placeholder)*

---

## 6. Implementation vs. Declaration Status Table

| Item | Requirement Category | Status | Evidence / Location |
|---|---|---|---|
| Manifest Permissions | System Permissions | `IMPLEMENTED IN REPOSITORY` | `android/app/src/main/AndroidManifest.xml` |
| Target SDK 36 | Platform Target | `IMPLEMENTED IN REPOSITORY` | `android/app/build.gradle.kts` |
| Backup Rules | Data Security | `IMPLEMENTED IN REPOSITORY` | `res/xml/data_extraction_rules.xml` |
| In-App Privacy Access | User Data | `IMPLEMENTED IN REPOSITORY` | `SettingsScreen.kt` ("Read Policy" dialog) |
| In-App Account Deletion | User Data | `IMPLEMENTED IN REPOSITORY` | `SettingsScreen.kt`, `AccountController.java` |
| External Deletion Web Portal | User Data | `IMPLEMENTED IN REPOSITORY` | `assets/compliance/account_deletion_request.html` |
| Web Portal Hosting | Infrastructure | `REQUIRES EXTERNAL WEBSITE` | Must be deployed to `https://zynpath.com/delete-account` |
| Public Privacy Policy Hosting | Infrastructure | `REQUIRES EXTERNAL WEBSITE` | Must be deployed to `https://zynpath.com/privacy` |
| Data Safety Answers | Console Form | `DOCUMENTED FOR PLAY CONSOLE` | `docs/DATA_SAFETY_MATRIX.md` |
| Target Audience & Rating | Content Rating | `DOCUMENTED FOR PLAY CONSOLE` | `docs/TARGET_AUDIENCE_REVIEW.md`, `docs/CONTENT_RATING_PREPARATION.md` |
| Play Billing Product Setup | Monetization | `REQUIRES MANUAL CONSOLE ACTION` | Create `zynpath_premium_monthly` and `zynpath_premium_6months` in Console |
| AdMob Production App ID | Advertising | `REQUIRES PRODUCTION CONFIGURATION` | Replace test IDs in `app/build.gradle.kts` before release |
| Google Play App Signing | Security | `IMPLEMENTED IN REPOSITORY` | Documented in `docs/GOOGLE_PLAY_APP_SIGNING.md` |
| Release AAB Generation | Distribution | `IMPLEMENTED IN REPOSITORY` | GitHub Actions workflow `.github/workflows/android-ci.yml` |
| ProGuard Mapping Archival | Maintenance | `IMPLEMENTED IN REPOSITORY` | `mapping.txt` preserved for every release version |

---

## 7. Release Engineering & App Signing Compliance (Prompt 45)
- **Target SDK 36 Conformance**: `android/app/build.gradle.kts` compiles and targets API Level 36 (Android 16), satisfying Google Play's 2026 platform requirement.
- **Google Play App Signing Integration**: Dual-key architecture established; AABs are signed with the developer Upload Key, and Google's cloud infrastructure re-signs with the authoritative App Signing Key.
- **Upload Key Security**: Zero production keystores or credentials are committed to version control; CI injection via GitHub Secrets is enforced.
- **De-obfuscation Guarantee**: `mapping.txt` is systematically generated by R8 and archived in CI builds with 30-day retention to guarantee production crash trace de-obfuscation in Play Console.
- **Reference Documentation**: See [GOOGLE_PLAY_APP_SIGNING.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_APP_SIGNING.md), [CI_CD_PIPELINE.md](file:///d:/Zynpath/docs/CI_CD_PIPELINE.md), and [ANDROID_RELEASE_CHECKLIST.md](file:///d:/Zynpath/docs/ANDROID_RELEASE_CHECKLIST.md).

---

## 8. Final Pre-Launch Audit Verification (Prompt 49)
- **Policy Audit Status**: Audited across all current Google Play Developer Program policies as of September 2026.
- **Title & Description Limits**: Title (28 chars, limit 30) and Short Description (71 chars, limit 80) strictly adhere to character constraints.
- **Target Audience**: Confirmed Ages 13+ General Audience (non-Designed for Families).
- **Compliance Status**: Code repository is **READY FOR INTERNAL TESTING**. Production submission remains **BLOCKED FOR PRODUCTION REVIEW** strictly pending operator publishing of the external compliance URLs (`https://zynpath.app/privacy` and `https://zynpath.app/delete-account`) and completion of the in-console IARC rating questionnaire.
- **Reference**: See [docs/GOOGLE_PLAY_PRELAUNCH_REPORT.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_PRELAUNCH_REPORT.md) and [docs/RELEASE_CANDIDATE_REPORT.md](file:///d:/Zynpath/docs/RELEASE_CANDIDATE_REPORT.md).

---

## 9. Prompt 50 Handover and Testing Track Alignment
- **Internal Testing Runbook**: Operational procedures for enrolling QA testers, configuring Play App Signing, and uploading `.aab` bundles are documented in [docs/GOOGLE_PLAY_INTERNAL_TESTING_GUIDE.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_INTERNAL_TESTING_GUIDE.md).
- **Production Staged Rollout**: Staged rollout schedules (5% to 100%) and automatic halt thresholds are codified in [docs/GOOGLE_PLAY_PRODUCTION_CHECKLIST.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_PRODUCTION_CHECKLIST.md).
- **Master Action Checklist**: Complete listing of manual operator requirements is maintained in [docs/MANUAL_ACTION_CHECKLIST.md](file:///d:/Zynpath/docs/MANUAL_ACTION_CHECKLIST.md).


