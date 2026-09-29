# Release Rollback & Forward-Fix Strategy

## 1. Fundamental Android Rollback Limitation

> [!WARNING]
> **Android OS Version Code Enforces Strictly Forward Progress**
> The Android Package Manager (`PackageManager`) will **NEVER** allow an in-place update if the incoming `versionCode` is lower than the currently installed `versionCode`.
> - If `versionCode = 2` is released with a fatal bug, publishing an older build with `versionCode = 1` will be rejected by Google Play Console and ignored by user devices.
> - Downgrading via sideload requires `adb install -d` or complete app uninstallation, which **permanently destroys local player data** (un-synced Solo level progress, settings, and encryption keys).

---

## 2. Emergency Release Mitigation Options

Depending on the rollout status, release engineers must follow one of two procedures:

### Option A: Halting a Staged Rollout (Immediate Action)
If a release is currently in a staged rollout (e.g., 10% or 20%):
1. Navigate to Google Play Console → **Release** → **Production**.
2. Click **Halt rollout**.
3. **Outcome:**
   - Immediately stops distributing the offending version to any new users.
   - Users who already installed the build remain on that version until a forward-fix is delivered.

### Option B: The "Forward-Fix" Hotfix Procedure (Authoritative Resolution)
To replace a broken release across all users:
1. **Identify and Revert:**
   - In git, revert the offending commit or apply the critical bug fix.
   - Verify that local Room database schema migrations are preserved without breaking existing data.
2. **Increment Version Code:**
   - Set `versionCode` to strictly greater than the broken release ($versionCode_{hotfix} = versionCode_{broken} + 1$).
   - Set `versionName` to indicate the patch release (e.g. `1.0.1`).
3. **Build & Verify:**
   - Trigger the `release-bundle` CI workflow or execute:
     ```bash
     ./gradlew :app:bundleRelease -PversionCode=3 -PversionName=1.0.1
     ```
4. **Expedited Play Console Release:**
   - Upload the new bundle to the Production track.
   - Set rollout to 100% and notify the Google Play review team if emergency review is available.

---

## 3. Backend & API Version Compatibility (Expand / Contract)
Because mobile updates take days or weeks to propagate across all active devices:
- The Spring Boot backend must **never** make breaking changes to REST endpoints or WebSocket message formats that break older client versions.
- Endpoints must support both version $N$ and version $N-1$ concurrently.
- Deprecated fields must be maintained until telemetry confirms client adoption of the updated version exceeds 99%.
