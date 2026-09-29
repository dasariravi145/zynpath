# Zynpath Release Artifact & Packaging Verification Report

**Document Version:** 1.0  
**Phase:** 12 — Final Release Validation (Prompt 49/50)  
**Date:** September 2026  
**Target:** Android App Bundle (AAB), APK, R8 Mapping, and CI Workflow Validation  

---

## 1. Executive Summary

This report documents the packaging architecture, artifact identity specifications, code shrinking validation, and release gate controls for *Zynpath: Number Path Puzzle*.

In accordance with Prompt 49 Section 27 and Section 33:
- **Build Configuration Verification:** **PASSED**
- **R8 Optimization & Resource Shrinking Configuration:** **PASSED**
- **Unsigned Build Verification:** **PASSED** (Local development machine is intentionally unequipped with the production signing keystore to protect signing key integrity).
- **Signed Release Upload Verification:** **REQUIRES RELEASE AUTHORIZATION** (Reserved for CI workflow dispatch or official release manager with authorized upload key).

---

## 2. Release Artifact Identity

| Attribute | Specification | Verification Source | Status |
|---|---|---|---|
| **Application ID** | `com.zynpath.game` | `android/app/build.gradle.kts` | **VERIFIED** |
| **Version Code** | `1` (or `ZYNPATH_VERSION_CODE`) | `android/app/build.gradle.kts` | **VERIFIED** |
| **Version Name** | `1.0.0` (or `ZYNPATH_VERSION_NAME`) | `android/app/build.gradle.kts` | **VERIFIED** |
| **Target SDK** | `36` (Android 16) | `android/app/build.gradle.kts` | **VERIFIED** |
| **Compile SDK** | `36` (Android 16) | `android/app/build.gradle.kts` | **VERIFIED** |
| **Min SDK** | `24` (Android 7.0) | `android/app/build.gradle.kts` | **VERIFIED** |
| **Git Baseline Commit** | `911dbdbe426527237a9606cd3de8c0bc5165fc78` | Git `HEAD` | **VERIFIED** |
| **Git Branch** | `master` | Git `.git/HEAD` | **VERIFIED** |
| **Build Variant** | `release` | Gradle build model | **VERIFIED** |
| **Target Artifact Format** | Android App Bundle (`.aab`) | Play Console standard | **VERIFIED** |
| **Local Signing Status** | Unsigned (`signingConfig = null`) | Clean security separation | **VERIFIED** |
| **CI Signing Status** | Signed via GitHub Actions secret | `android-ci.yml` release job | **READY** |

---

## 3. R8 Optimization & Code Shrinking (Req 38, 39, 40)

### 3.1 Configuration Invariants
- `isMinifyEnabled = true`: Strips unused classes, methods, and attributes; shortens identifiers.
- `isShrinkResources = true`: Discards unreferenced drawables, layouts, and strings.
- `proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")`:
  - Keeps LineNumberTable and SourceFile attributes for clear crash stack traces in Google Play Console.
  - Preserves `@Keep`, Room Entities/DAOs, Hilt dependency injection classes, and Google Play Billing client wrappers.
- **Mapping File Output:** Located at `android/app/build/outputs/mapping/release/mapping.txt`. This file is archived alongside the AAB for symbol de-obfuscation.

---

## 4. Production Endpoints & Transport Security (Req 41, 42, 43)

| Target Service | Debug Build Value | Release Build Value | TLS Enforced? |
|---|---|---|---|
| **REST API Base URL** | `http://10.0.2.2:8080/api/v1` | `https://api.zynpath.app/api/v1` | **YES (HTTPS)** |
| **WebSocket Match URL** | `ws://10.0.2.2:8080/ws/multiplayer` | `wss://api.zynpath.app/ws/multiplayer` | **YES (WSS)** |
| **AdMob App ID** | Test App ID (`ca-app-pub-3940...`) | Test App ID (`ca-app-pub-3940...`) | N/A |
| **AdMob Rewarded Unit** | Test Ad Unit ID | `""` (Pending production ad unit) | N/A |

Cleartext traffic to production domains is strictly rejected by the Android OS via `network_security_config.xml`.

---

## 5. CI / CD Release Gate Security (Req 79, 80)

In `.github/workflows/android-ci.yml`:
1. **Pull Request Security:** Pull requests only execute `validate-and-build` (debug assembly and unit tests). PR workflows run with read-only tokens and have zero access to `ZYNPATH_UPLOAD_KEYSTORE_BASE64` or keystore passwords.
2. **Release Isolation:** The `release-bundle` job runs strictly when:
   - Manually dispatched by an authorized maintainer (`workflow_dispatch`), OR
   - Pushed via a verified semantic version tag (`refs/tags/v*`).
3. **Artifact Archiving:** Production AAB and R8 mapping files are securely archived as GitHub Actions build artifacts with 30-day retention.
