# Continuous Integration & Deployment (CI/CD) Pipeline Specification

## 1. Pipeline Architecture
Zynpath implements an automated, secure CI/CD pipeline using **GitHub Actions** defined in [.github/workflows/android-ci.yml](file:///d:/Zynpath/.github/workflows/android-ci.yml).

The pipeline enforces least privilege permissions (`contents: read`) and strictly decouples continuous validation from production release artifact generation.

---

## 2. Pipeline Execution Stages

```mermaid
graph TD
    Trigger[PR or Push to main] --> Validate[Job 1: validate-and-build]
    Validate --> BuildDebug[Assemble Debug APK]
    BuildDebug --> CacheArtifacts[Archive Debug APK (Push only)]

    ManualTrigger[Manual workflow_dispatch OR Release Tag v*] --> ValidateRel[Job 1: validate-and-build]
    ValidateRel --> ReleaseJob[Job 2: release-bundle]
    ReleaseJob --> DecodeKey[Decode Keystore from Secrets]
    DecodeKey --> BuildAAB[./gradlew :app:bundleRelease]
    BuildAAB --> Checksums[Generate SHA-256 & Provenance]
    Checksums --> ArchiveRelease[Archive AAB + Mapping File]
    ArchiveRelease --> ShredKey[Secure Shred Keystore]
```

### Stage 1: Continuous Validation (`validate-and-build`)
- **Triggers:** Every pull request targeting `main` and direct pushes to `main`.
- **Tasks:**
  - Checkouts repository with depth 1.
  - Provisions JDK 17 (Temurin) and Android SDK 36.
  - Utilizes read-only Gradle dependency cache on pull requests.
  - Assembles debug APK (`./gradlew :app:assembleDebug`).
- **Security Guarantee:** Pull request builds run in an isolated context with **zero access** to production signing secrets.

### Stage 2: Production Release Bundle (`release-bundle`)
- **Triggers:**
  - Manually via `workflow_dispatch` (allowing custom `versionCode` and `versionName` inputs).
  - Automatically upon pushing an immutable Git version tag (`v1.0.0`, `v1.0.1`, etc.).
- **Tasks:**
  - Extracts base64-encoded keystore from repository secrets into a temporary runner directory.
  - Injects upload signing credentials via environment variables.
  - Executes `./gradlew :app:bundleRelease`.
  - Calculates SHA-256 checksum of `app-release.aab`.
  - Captures Git commit provenance (SHA, branch, actor, timestamp).
  - Uploads bundle and ProGuard mapping file (`mapping.txt`) to GitHub Actions artifacts with 30-day retention.
  - Securely overwrites and removes the temporary keystore using `shred -u` in an `always()` post-step.

---

## 3. GitHub Secrets Inventory

| Secret Name | Description | Format |
|---|---|---|
| `ZYNPATH_UPLOAD_KEYSTORE_BASE64` | Base64-encoded content of `zynpath-upload-keystore.jks` | Base64 string (`base64 -w 0 keystore.jks`) |
| `ZYNPATH_UPLOAD_KEYSTORE_PASSWORD` | Password protecting the keystore file | Plaintext string |
| `ZYNPATH_UPLOAD_KEY_ALIAS` | Key alias name | String (e.g. `zynpath-upload-key`) |
| `ZYNPATH_UPLOAD_KEY_PASSWORD` | Password protecting the key alias | Plaintext string |

---

## 4. Manual Release Approval Gate
Automated publication to Google Play tracks is deliberately NOT automated in this workflow:
- The CI pipeline stops after generating and verifying the signed `.aab` artifact and mapping file.
- Deployment to Google Play Console (Internal, Closed, or Production track) requires human review and manual upload per [PLAY_CONSOLE_UPLOAD.md](file:///d:/Zynpath/docs/PLAY_CONSOLE_UPLOAD.md).

---

## 5. Prompt 50 Release Manifest & Verification Alignment
- **Artifact Manifest**: Canonical artifact identities, file paths, toolchain pinning, and SHA-256 verification procedures are documented in [docs/RELEASE_ARTIFACT_MANIFEST.md](file:///d:/Zynpath/docs/RELEASE_ARTIFACT_MANIFEST.md).
- **Manual Actions Checklist**: Operator actions required to configure GitHub Secrets and promote builds are catalogued in [docs/MANUAL_ACTION_CHECKLIST.md](file:///d:/Zynpath/docs/MANUAL_ACTION_CHECKLIST.md).

