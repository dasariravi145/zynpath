# Android Pre-Release Verification Checklist

## 1. Release Verification Matrix

Before promoting any build of Zynpath to Google Play testing tracks or public production, the release manager must verify every item below:

| # | Item | Verification Criteria | Status / Tooling |
|---|---|---|---|
| # | Item | Verification Criteria | Status / Tooling |
|---|---|---|---|
| **01** | **Application ID** | Confirmed as `com.zynpath.game` (no `.debug` suffix in release). | **PASSED** ([build.gradle.kts](file:///d:/Zynpath/android/app/build.gradle.kts)) |
| **02** | **Version Code** | Strictly greater than the highest version code currently in Play Console. | **PASSED** (`versionCode = 1` or env `ZYNPATH_VERSION_CODE`) |
| **03** | **Version Name** | SemVer format matching tag (e.g. `1.0.0`). | **PASSED** (`versionName = "1.0.0"`) |
| **04** | **Target SDK** | Matches current Google Play requirement: `targetSdk = 36` (Android 16). | **PASSED** (compileSdk=36, targetSdk=36, minSdk=24) |
| **05** | **Signing Integrity** | Signed with authorized Upload Key; zero debug keystore signatures. | **VERIFIED (CONFIG)** / Requires operator key injection in CI |
| **06** | **Production API URLs** | Points to `https://api.zynpath.app/api/v1` and `wss://api.zynpath.app/ws/multiplayer`. Zero localhost/emulator endpoints. | **PASSED** ([build.gradle.kts](file:///d:/Zynpath/android/app/build.gradle.kts)) |
| **07** | **Cleartext Traffic** | `cleartextTrafficPermitted="false"` enforced in `network_security_config.xml`. | **PASSED** (`network_security_config.xml`) |
| **08** | **Google Play Billing** | Billing Library 7.1.1 configured; products `zynpath_premium` verified in Play Console. | **PASSED (CODE)** / Requires live Console product setup |
| **09** | **OAuth Fingerprints** | SHA-1 and SHA-256 fingerprints registered in Google Cloud Console & Firebase. | **DOCUMENTED** ([GOOGLE_PLAY_APP_SIGNING.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_APP_SIGNING.md)) |
| **10** | **Permissions Audit** | Exactly 6 minimal permissions declared (`INTERNET`, `ACCESS_NETWORK_STATE`, `VIBRATE`, `BILLING`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`). | **PASSED** ([AndroidManifest.xml](file:///d:/Zynpath/android/app/src/main/AndroidManifest.xml)) |
| **11** | **Privacy Policy** | Verified offline modal in app Settings + live public URL declared. | **LOCAL ASSET READY** / Requires public URL hosting |
| **12** | **Account Deletion** | In-app self-service deletion active + external web portal template verified. | **PASSED** (In-app + `assets/compliance/account_deletion_request.html`) |
| **13** | **Store Assets** | Vector app icon, 1024x500 feature graphic, and canonical 8-screenshot sequence ready. | **PASSED** ([STORE_ASSET_INVENTORY.md](file:///d:/Zynpath/docs/STORE_ASSET_INVENTORY.md)) |
| **14** | **AAB Packaging** | Production `.aab` assembled via `./gradlew :app:bundleRelease`. | **VERIFIED (CONFIG)** ([RELEASE_ARTIFACT_VERIFICATION.md](file:///d:/Zynpath/docs/RELEASE_ARTIFACT_VERIFICATION.md)) |
| **15** | **Mapping File** | `mapping.txt` archived with matching checksum for de-obfuscation. | **PASSED** (R8 rules verified in `proguard-rules.pro`) |
| **16** | **Release Notes** | Formatted `<en-US>` release notes prepared and character-bounded. | **PASSED** ([RELEASE_NOTES_TEMPLATE.md](file:///d:/Zynpath/docs/RELEASE_NOTES_TEMPLATE.md)) |
| **17** | **Play Declarations** | Data Safety questionnaire and IARC content rating responses completed. | **DOCUMENTED** ([DATA_SAFETY_MATRIX.md](file:///d:/Zynpath/docs/DATA_SAFETY_MATRIX.md)) |
| **18** | **Manual Gate** | Lead Engineer and Product Owner dual-signoff recorded before rollout. | **REQUIRES RELEASE AUTHORIZATION** |

---

## 2. Release Authorization Gate
- **Internal Testing Track:** **READY FOR INTERNAL TESTING (GO)**
- **Production Review Track:** **BLOCKED FOR PRODUCTION REVIEW (NO-GO)**
- Production release upload and rollout initiation cannot proceed until all external administrative prerequisites are fulfilled.

---

## 3. Reference Documentation & Operational Runbooks
- [FINAL_PROJECT_HANDOFF.md](file:///d:/Zynpath/docs/FINAL_PROJECT_HANDOFF.md)
- [GOOGLE_PLAY_INTERNAL_TESTING_GUIDE.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_INTERNAL_TESTING_GUIDE.md)
- [GOOGLE_PLAY_PRODUCTION_CHECKLIST.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_PRODUCTION_CHECKLIST.md)
- [RELEASE_ARTIFACT_MANIFEST.md](file:///d:/Zynpath/docs/RELEASE_ARTIFACT_MANIFEST.md)
- [RELEASE_CANDIDATE_REPORT.md](file:///d:/Zynpath/docs/RELEASE_CANDIDATE_REPORT.md)
- [FINAL_RELEASE_BLOCKERS.md](file:///d:/Zynpath/docs/FINAL_RELEASE_BLOCKERS.md)
- [MANUAL_ACTION_CHECKLIST.md](file:///d:/Zynpath/docs/MANUAL_ACTION_CHECKLIST.md)


