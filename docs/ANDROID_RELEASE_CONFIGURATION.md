# Android Release Configuration Specification

## 1. Application Identity & Package Namespace
- **Production Application ID:** `com.zynpath.game`
- **Debug Application ID:** `com.zynpath.game.debug` (configured via `applicationIdSuffix = ".debug"`)
- **Package Namespace:** `com.zynpath.game`
- **Target Platform:** Android 16 (API Level 36)
- **Minimum Platform:** Android 7.0 (API Level 24)

The application ID `com.zynpath.game` is permanent and authoritative for Google Play Console registration.

---

## 2. Version Management Strategy

### Invariants:
1. **`versionCode` (Authoritative Release Order):**
   - Strictly positive integer.
   - Monotonically increasing with every production or closed testing upload to Google Play Console.
   - Initial production version code: `1`.
   - Never decrement or reuse a published `versionCode`.
2. **`versionName` (Human-Readable Semantic Version):**
   - Semantic Versioning format: `MAJOR.MINOR.PATCH` (e.g., `1.0.0`).
   - Initial production version name: `1.0.0`.
3. **Dynamic Build Override:**
   In [android/app/build.gradle.kts](file:///d:/Zynpath/android/app/build.gradle.kts), version properties can be injected at build time via Gradle properties or environment variables:
   ```bash
   ./gradlew :app:bundleRelease -PversionCode=2 -PversionName=1.0.1
   # OR via environment variables:
   ZYNPATH_VERSION_CODE=2 ZYNPATH_VERSION_NAME=1.0.1 ./gradlew :app:bundleRelease
   ```

---

## 3. Build Variant Separation Matrix

| Configuration Property | `debug` Variant | `release` Variant |
|---|---|---|
| **Application ID** | `com.zynpath.game.debug` | `com.zynpath.game` |
| **Debuggable (`android:debuggable`)** | `true` | `false` |
| **Minification / R8 Code Shrinking** | `false` | `true` |
| **Resource Shrinking** | `false` | `true` |
| **ProGuard Rules** | None | `proguard-android-optimize.txt` + `proguard-rules.pro` |
| **Signing Config** | `signingConfigs.debug` (standard Android SDK debug key) | `signingConfigs.release` (Upload Key from env/properties) or `null` |
| **Backend Base URL** | `http://10.0.2.2:8080/api/v1` | `https://api.zynpath.app/api/v1` |
| **Backend WebSocket URL** | `ws://10.0.2.2:8080/ws/multiplayer` | `wss://api.zynpath.app/ws/multiplayer` |
| **Google Mobile Ads App ID** | `ca-app-pub-3940256099942544~3347511713` (Google Test) | Production App ID (Configured in Play Console) |
| **AdMob Rewarded Ad Unit ID** | `ca-app-pub-3940256099942544/5224354917` (Google Test) | Production Rewarded Unit ID |
| **Network Security Config** | Cleartext permitted for `10.0.2.2`, `localhost` | **Cleartext traffic strictly prohibited** |

---

## 4. Cleartext Traffic & TLS Enforcement
In [android/app/src/main/res/xml/network_security_config.xml](file:///d:/Zynpath/android/app/src/main/res/xml/network_security_config.xml):
- `base-config cleartextTrafficPermitted="false"` applies to all release network calls.
- Cleartext domain overrides (`10.0.2.2`, `localhost`, `127.0.0.1`) apply exclusively during development.
- Any attempt in release mode to contact non-HTTPS/WSS endpoints results in immediate runtime connection rejection by the Android operating system.
