# Zynpath Release Artifact Manifest

**Manifest ID:** `MANIFEST-REL-1.0.0-B1`  
**Generated Date:** September 2026  
**Phase:** Phase 12 — Final Release and Project Handoff (Prompt 50/50)  
**Status:** Authoritative Release Candidate Specifications  

---

## 1. Release Identification

| Attribute | Canonical Value | Notes |
|---|---|---|
| **Product Name** | `Zynpath` | Official application brand |
| **Full Title** | `Zynpath: Number Path Puzzle` | Play Store listing title (28 characters) |
| **Application ID** | `com.zynpath.game` | Canonical Android package name |
| **Version Code** | `1` | Parameterized via `ZYNPATH_VERSION_CODE` |
| **Version Name** | `1.0.0` | Parameterized via `ZYNPATH_VERSION_NAME` |
| **Git Baseline Commit** | `911dbdbe426527237a9606cd3de8c0bc5165fc78` | Verified clean tree on branch `master` |
| **Git Branch** | `master` | Primary release branch |
| **Build Variant** | `release` | Minified, non-debuggable, optimized |
| **Build Toolchain** | JDK 17, Gradle 8.11.1, AGP 8.7.3, Kotlin 2.0.21 | Deterministic pinned toolchain |
| **Target SDK** | `36` (Android 16) | Meets/exceeds Google Play policy |
| **Min SDK** | `24` (Android 7.0 Nougat) | Broad global device compatibility |

---

## 2. Artifact Paths and Integrity Details

### 2.1 Android App Bundle (AAB)
* **Intended Artifact Path:** `android/app/build/outputs/bundle/release/app-release.aab`
* **Format:** Android App Bundle (ZIP format containing split base and feature modules)
* **Signing Status:**
  * In local/CI unconfigured environment: `UNSIGNED` (or test-signed via debug key for local inspection).
  * In production release environment: Signed with registered developer `Upload Key` via GitHub Secrets injection.
* **ProGuard / R8 Mapping File:**
  * Path: `android/app/build/outputs/mapping/release/mapping.txt`
  * Retention: 30-day archival in CI pipeline; mandatory upload to Google Play Console for de-obfuscating crash traces.
* **Integrity Checksum (Production Target):**
  * Algorithm: SHA-256
  * Verification Command: `sha256sum android/app/build/outputs/bundle/release/app-release.aab`

### 2.2 Backend Service Container Image
* **Image Name:** `zynpath-backend`
* **Image Tag:** `1.0.0`
* **Base Image:** `eclipse-temurin:17-jre-jammy`
* **User Context:** Non-root `zynpath:zynpath` (`UID 10001`, `GID 10001`)
* **Exposed Port:** `8080` (HTTP / WebSocket)

---

## 3. Build Configuration Verification Matrix

```groovy
// Verified configuration in android/app/build.gradle.kts
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        isDebuggable = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        // If upload signing key is absent, gracefully falls back to unsigned bundle
        signingConfig = signingConfigs.getByName("release")
    }
}
```

---

## 4. Verification & Distribution Readiness

| Distribution Track | Eligibility | Blocker / Requirement |
|---|---|---|
| **Local Debug APK** | **VERIFIED & READY** | Generated at `android/app/build/outputs/apk/debug/app-debug.apk` |
| **Internal Testing Track (AAB)** | **READY FOR INTERNAL TESTING** | Requires generating `.aab` with upload key or internal test key |
| **Closed Testing Track (Alpha)** | **PENDING INTERNAL TESTING** | Requires 20 testers for 14 days (personal accounts) |
| **Production Rollout** | **BLOCKED FOR PRODUCTION REVIEW** | Requires resolving all 5 blockers in `docs/FINAL_RELEASE_BLOCKERS.md` |
