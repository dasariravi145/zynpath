# Google Play App Signing Architecture & Setup Guide

## 1. Dual-Key Architecture
Google Play uses a dual-key security architecture to protect application distribution and signing:

| Key Type | Held By | Purpose | Consequences of Loss |
|---|---|---|---|
| **App Signing Key** | Google Infrastructure | Signs the optimized APKs delivered to end-user devices. Used by Android to verify app updates. | Protected by Google's secure key management; cannot be lost by developer. |
| **Upload Key** | Developer / CI Runner | Signs the Android App Bundle (`.aab`) before upload to Google Play Console. | Can be reset via Google Play Console without affecting existing users. |

---

## 2. Enrollment Workflow for New Applications
For Zynpath (`com.zynpath.game`), enrollment in Google Play App Signing occurs during the initial application creation in the Google Play Console:

1. **Let Google Generate the Key (Recommended):**
   - In Google Play Console: **Release** → **Setup** → **App Integrity**.
   - Select **Use Google-generated key** for the App Signing Key.
   - Google generates and stores the root signing key in its secure cloud hardware security modules (HSMs).
2. **Register Upload Key:**
   - Sign the first `.aab` using the generated `zynpath-upload-key`.
   - Upon first upload, Google Play automatically registers this key as the authorized Upload Key for all future releases.

---

## 3. Fingerprints & Third-Party Provider Alignment
Because Google re-signs the application with the **App Signing Key**, third-party identity providers (Google Sign-In, Firebase Authentication, Facebook Login) must be registered with the **App Signing Certificate Fingerprints**, NOT the Upload Key fingerprint!

### Where to Find Fingerprints:
In Google Play Console → **App Integrity** → **App Signing**:
- **SHA-1 Certificate Fingerprint**
- **SHA-256 Certificate Fingerprint**

### Required Provider Updates:
1. **Google Cloud Console (OAuth 2.0 Client IDs):**
   - Add the SHA-1 and SHA-256 fingerprints to the Android Client ID for package `com.zynpath.game`.
2. **Firebase Console (Project Settings → Your Apps):**
   - Add both SHA-1 and SHA-256 fingerprints to enable Google Sign-In and Phone Auth.
3. **Facebook Developer Dashboard:**
   - Convert the SHA-1 fingerprint to Base64 Key Hash and register it under Android Settings.

---

## 4. Upload Key Loss & Reset Procedure
If the upload keystore file is lost, corrupted, or compromised:
1. Generate a new upload key using `keytool`:
   ```bash
   keytool -genkeypair -v -keystore zynpath-new-upload-keystore.jks \
     -alias zynpath-upload-key -keyalg RSA -keysize 4096 -validity 10000
   ```
2. Export the public certificate in PEM format:
   ```bash
   keytool -export -rfc -keystore zynpath-new-upload-keystore.jks \
     -alias zynpath-upload-key -file upload_certificate.pem
   ```
3. In Google Play Console → **App Integrity** → **App Signing** → **Request upload key reset**.
4. Upload `upload_certificate.pem` and provide business justification.
5. Google Play support approves the reset (typically within 48 hours), after which builds signed with the new upload key will be accepted. Existing player installs are unaffected.
