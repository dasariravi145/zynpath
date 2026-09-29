# Release Artifacts & De-obfuscation Management

## 1. Artifact Formats & Responsibilities

| Artifact Format | Extension | Primary Audience | Target Destination |
|---|---|---|---|
| **Android App Bundle** | `.aab` | Google Play Store | Google Play Console (Internal / Closed / Production) |
| **ProGuard Mapping File** | `mapping.txt` | Engineering / Play Console | Google Play Console App Integrity / Sentry / Crashlytics |
| **Integrity Checksum** | `.aab.sha256` | Release Engineering | CI Artifact Storage / Audit Logs |
| **Universal APK** | `.apk` | QA / Device Farm | Internal Test Distribution / Manual Side-Loading |

---

## 2. Standard Output Paths

Following standard Gradle build execution:

```
android/app/build/outputs/
├── bundle/
│   └── release/
│       ├── app-release.aab             # Signed production App Bundle
│       ├── app-release.aab.sha256      # SHA-256 cryptographic checksum
│       └── provenance.txt              # Git commit SHA, actor, and build date
├── mapping/
│   └── release/
│       ├── mapping.txt                 # R8 de-obfuscation translation table
│       └── configuration.txt           # Consolidated R8 keep rules applied
└── apk/
    ├── debug/
    │   └── app-debug.apk               # Local development debug build
    └── release/
        └── app-release-unsigned.apk    # Local release APK (or signed if key present)
```

---

## 3. Mapping File Preservation Invariant

> [!IMPORTANT]
> **De-Obfuscation Guarantee**
> For every `versionCode` uploaded to Google Play Console, the corresponding `mapping.txt` must be retained permanently. Without this exact file, crash stack traces from production users cannot be decoded into human-readable Kotlin source line numbers.

### Play Console Upload Step:
1. In Google Play Console → **Release** → **App bundle explorer**.
2. Select the relevant `versionCode`.
3. Under **Downloads** → **Assets**, verify that the de-obfuscation file (`mapping.txt`) was uploaded alongside the `.aab` (automatically included in modern App Bundles or uploaded manually if required).

---

## 4. Artifact Integrity & SHA-256 Verification
Prior to releasing any `.aab` to testing tracks or production, verify its checksum:

```bash
# Calculate SHA-256 checksum locally
sha256sum android/app/build/outputs/bundle/release/app-release.aab

# Compare against the CI build artifact checksum
cat app-release.aab.sha256
```
If the checksum does not match the CI provenance record, the artifact must be discarded immediately.
