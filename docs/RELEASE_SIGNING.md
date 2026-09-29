# Release Signing Architecture & Key Management

## 1. Overview & Security Boundaries
Zynpath enforces strict separation between development and release cryptographic signing. 

**Core Rules:**
- Production keystores (`*.jks`, `*.keystore`), private keys, and signing passwords must **NEVER** be committed to version control.
- Release builds must **NEVER** silently fall back to debug signing. If upload signing credentials are absent, the build produces an unsigned artifact or halts with a clear configuration error.
- All signing credentials are provided via environment variables or an untracked `keystore.properties` file ignored by Git.

---

## 2. Upload Keystore Generation Procedure
The upload key is used by the developer or CI runner to sign the Android App Bundle before uploading to Google Play Console. Google Play verifies the upload signature, extracts the APKs, and re-signs them with the master Google Play App Signing key.

### Generating the Upload Key:
Run the following command using Java 17+ `keytool`:

```bash
keytool -genkeypair -v \
  -keystore zynpath-upload-keystore.jks \
  -alias zynpath-upload-key \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000 \
  -dname "CN=Zynpath Release Engineering, OU=Engineering, O=Zynpath, L=San Francisco, ST=California, C=US"
```

> [!CAUTION]
> Store the generated `zynpath-upload-keystore.jks` in a secure enterprise password manager (e.g. 1Password, Bitwarden, or AWS KMS / GCP Secret Manager). If the upload key is lost, a key reset request must be submitted to Google Play Developer Support.

---

## 3. Configuration Mechanisms

### Option A: Local Development / Offline Release Build (`keystore.properties`)
Create an untracked file `android/keystore.properties` (patterned after [android/keystore.properties.example](file:///d:/Zynpath/android/keystore.properties.example)):

```properties
storeFile=/path/to/zynpath-upload-keystore.jks
storePassword=YourSecretKeystorePassword
keyAlias=zynpath-upload-key
keyPassword=YourSecretKeyPassword
```

### Option B: CI/CD Environment Variables (GitHub Actions / Cloud Build)
Inject credentials directly via environment variables:

| Environment Variable | Description | Example / Format |
|---|---|---|
| `ZYNPATH_UPLOAD_KEYSTORE_PATH` | Filesystem path to the decoded keystore file | `/tmp/upload-keystore.jks` |
| `ZYNPATH_UPLOAD_KEYSTORE_PASSWORD` | Master password for the keystore file | `[SECRET]` |
| `ZYNPATH_UPLOAD_KEY_ALIAS` | Alias name for the upload keypair | `zynpath-upload-key` |
| `ZYNPATH_UPLOAD_KEY_PASSWORD` | Password for the key alias | `[SECRET]` |

---

## 4. Zero-Debug-Fallback Enforcement
In [android/app/build.gradle.kts](file:///d:/Zynpath/android/app/build.gradle.kts):
```kotlin
buildTypes {
    release {
        if (hasReleaseSigning) {
            signingConfig = signingConfigs.getByName("release")
        } else {
            signingConfig = null // Produces unsigned artifact, NEVER debug-signed
        }
    }
}
```
If `hasReleaseSigning` evaluates to false, `signingConfig` remains `null`. Any attempt to distribute an unsigned artifact to Google Play Console will be immediately rejected by Google Play's upload validation.
