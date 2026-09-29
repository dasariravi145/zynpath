# Zynpath Google Play Policy Source Register

**Status:** Authoritative  
**Review Date:** September 27, 2026  
**Primary Reference:** Google Play Developer Program Policies & Android Developer Documentation  
**Next Required Review:** Immediately prior to Google Play Release Track promotion (Prompt 50)  

---

## 1. Overview & Policy Monitoring Framework

Google Play policies are actively updated to enhance user privacy, account security, and billing transparency. Zynpath enforces a policy audit register where every technical implementation in the codebase is explicitly mapped to the official Google Play Policy requirement.

---

## 2. Policy Source Registry Table

| # | Policy Subject | Official Source URL | Effective Date / Version | Applicable Requirement | Actual Code Implementation Evidence | Remaining Console / External Action | Current Status |
|---|---|---|---|---|---|---|---|
| 1 | **Target API Level** | `https://developer.android.com/google/play/requirements/target-sdk` | August 31, 2026 (Target API 36) | New apps and updates must target API 36 (Android 16) or higher. | `android/app/build.gradle.kts`: `compileSdk = 36`, `targetSdk = 36`. Tested with Kotlin 2.2.10, AGP 9.2.1. | Verify Google Play Console bundle upload validation. | `IMPLEMENTED IN REPOSITORY` |
| 2 | **User Data & Privacy Policy** | `https://support.google.com/googleplay/android-developer/answer/10787469` | Continuous | Must provide a comprehensive Privacy Policy accessible in-app and via a public URL disclosing all collected, shared, and local data. | In-app policy accessible in `SettingsScreen.kt` ("Read Policy" dialog). Draft in `docs/PRIVACY_POLICY_DRAFT.md`. | Deploy public privacy policy webpage to `https://zynpath.com/privacy`. | `REQUIRES EXTERNAL WEBSITE` |
| 3 | **Account Deletion (App & Web)** | `https://support.google.com/googleplay/android-developer/answer/13327111` | May 31, 2024 (Updated 2026) | Apps supporting account creation must offer an in-app deletion option and an external web URL allowing deletion without app reinstallation. | In-app deletion: `SettingsScreen.kt`, `AccountRepositoryImpl.kt`, backend `AccountController.java` (`DELETE /api/v1/account/delete`). Web portal template: `assets/compliance/account_deletion_request.html`. | Host web deletion portal on `https://zynpath.com/delete-account` and submit URL in Play Console. | `REQUIRES EXTERNAL WEBSITE` |
| 4 | **Data Safety Section** | `https://support.google.com/googleplay/android-developer/answer/10787469` | Continuous | Form disclosures in Play Console must accurately represent actual SDK and backend data handling. | Complete evidence matrix in `docs/DATA_SAFETY_MATRIX.md`. Data flow documented in `docs/DATA_FLOW_INVENTORY.md`. | Complete Data Safety questionnaire in Google Play Console. | `DOCUMENTED FOR PLAY CONSOLE` |
| 5 | **Google Play Billing** | `https://support.google.com/googleplay/android-developer/answer/9858738` | Billing Library v7+ | Digital game content and subscriptions must use Google Play Billing. No external payment links. Disclose renewal, price, and cancellation terms. | `play-billing:7.1.1` in `build.gradle.kts`. Server verification via `SubscriptionService.java`. Explicit Play Store cancellation guidance in `SettingsScreen.kt` and `PremiumScreen.kt`. | Configure monthly (`₹99`) and 6-month (`₹499`) subscription products in Play Console. | `REQUIRES MANUAL CONSOLE ACTION` |
| 6 | **Advertising Policy** | `https://support.google.com/googleplay/android-developer/answer/9857753` | Continuous | Must declare advertising if SDK is present. Rewarded ads must be optional, clearly disclosed, and only reward verified completion. | `play-services-ads:23.6.0` in `build.gradle.kts`. Rewarded ads capped at 5/day for Solo hints only. Ad-free for Premium subscribers. No ads in multiplayer. | Declare "Yes, my app contains ads" in Play Console App Content. | `DOCUMENTED FOR PLAY CONSOLE` |
| 7 | **Permissions Minimization** | `https://support.google.com/googleplay/android-developer/answer/9799150` | Continuous | Only request permissions strictly necessary for core functionality. Sensitive permissions require justifiable use cases. | `AndroidManifest.xml` audited: strictly `INTERNET`, `ACCESS_NETWORK_STATE`, `VIBRATE`, `BILLING`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`. Zero location, contacts, camera, or SMS permissions. | None. Verified minimal. | `IMPLEMENTED IN REPOSITORY` |
| 8 | **App Access & Reviewer Instructions** | `https://support.google.com/googleplay/android-developer/answer/9859455` | Continuous | Provide Google Play reviewers credentials or instructions to access all authenticated features without paywalls. | Reviewer credentials and testing guide documented in `docs/GOOGLE_PLAY_COMPLIANCE.md` Section 9. Solo gameplay requires zero login (Guest-First). | Enter test account credentials in Play Console "App Access" section. | `REQUIRES MANUAL CONSOLE ACTION` |
| 9 | **Target Audience & Content Rating** | `https://support.google.com/googleplay/android-developer/answer/9893335` | Continuous | Apps must specify target age group. Apps including children must comply with Families Policy. IARC rating questionnaire must be completed. | Target Audience worksheet in `docs/TARGET_AUDIENCE_REVIEW.md`. Recommended target: Ages 13+. Content rating preparation in `docs/CONTENT_RATING_PREPARATION.md`. | Complete official IARC questionnaire in Play Console. | `REQUIRES MANUAL CONSOLE ACTION` |
| 10 | **Data Extraction & Backup Rules** | `https://developer.android.com/guide/topics/data/autobackup` | API 31+ | Android Auto Backup must exclude cryptographic keys and hardware Keystore session files to prevent cross-device decryption crashes. | `res/xml/data_extraction_rules.xml` (API 31+) and `res/xml/backup_rules.xml` exclude `zyn_secure_session.enc` while preserving `zynpath_database`. | None. Built into APK. | `IMPLEMENTED IN REPOSITORY` |
| 11 | **Network Security & Cleartext** | `https://developer.android.com/training/articles/security-config` | Continuous | Disallow cleartext HTTP traffic in production release builds. | `res/xml/network_security_config.xml` enforces `cleartextTrafficPermitted="false"`. Debug overrides strictly isolate `10.0.2.2` and `localhost`. | Ensure production SSL certificates are active on `api.zynpath.com`. | `REQUIRES PRODUCTION CONFIGURATION` |
| 12 | **Social Interaction & Abuse Safeguards** | `https://support.google.com/googleplay/android-developer/answer/9876937` | Continuous | Apps with user interaction must provide mechanisms to block/report users and prevent harassment. | Preset-only communication (zero free-text chat). Blocking mechanism (`PrivacyScreen.kt`). Rate-limited WebSocket and friend requests (`RateLimiterService.java`). | None. Built into app. | `IMPLEMENTED IN REPOSITORY` |

---

## 3. Pre-Release Policy Re-Check Protocol
Prior to submission on Google Play Console (Prompt 50):
1. **Re-verify target API requirements** on Android Developers portal.
2. **Confirm URL availability** for `https://zynpath.com/privacy` and `https://zynpath.com/delete-account`.
3. **Verify Google Play Billing product identifiers** (`zynpath_premium_monthly`, `zynpath_premium_6months`) match backend catalog constants.
4. **Re-run Android Lint and manifest permission checks**.
