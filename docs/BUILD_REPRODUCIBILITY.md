# Build Reproducibility & Toolchain Specification

## 1. Objective & Philosophy
A build is considered reproducible when identical source code, compiled with the identical toolchain in an identical environment, produces an identical binary artifact. Zynpath minimizes build variances by pinning all toolchain components and eliminating dynamic dependency ranges.

---

## 2. Toolchain Inventory & Pinned Versions

| Component | Pinned Version | Definition Location | Purpose |
|---|---|---|---|
| **Java Development Kit** | JDK 17 LTS (Temurin / OpenJDK 17.0.12+) | `jvmToolchain(17)` / CI workflow | Core Java compilation & bytecode target |
| **Gradle Build Tool** | 9.4.1 | `gradle/wrapper/gradle-wrapper.properties` | Build lifecycle and dependency resolution |
| **Android Gradle Plugin** | 9.2.1 | `gradle/libs.versions.toml` (`agp`) | Android asset packaging and R8 execution |
| **Kotlin Compiler** | 2.2.10 | `gradle/libs.versions.toml` (`kotlin`) | Kotlin bytecode generation |
| **Android SDK Compile** | API Level 36 (Android 16) | `android.compileSdk = 36` | Compilation platform headers |
| **Android SDK Target** | API Level 36 (Android 16) | `android.defaultConfig.targetSdk = 36` | Android platform behavior target |
| **Android SDK Min** | API Level 24 (Android 7.0) | `android.defaultConfig.minSdk = 24` | Minimum supported operating system |

---

## 3. Dependency Pinning & Version Catalog
All external dependencies are declared centrally in [android/gradle/libs.versions.toml](file:///d:/Zynpath/android/gradle/libs.versions.toml):
- **Zero Dynamic Versions:** Floating versions (e.g., `1.0.+` or `latest.release`) are strictly prohibited across the entire project.
- **Dependency Locking:** Repositories are constrained in `settings.gradle.kts` using `RepositoriesMode.FAIL_ON_PROJECT_REPOS` and restricted to `google()` and `mavenCentral()`.

---

## 4. Reproducible Build Execution Procedure
To generate a bit-accurate or verified release build from any clean workstation:

```bash
# 1. Checkout exact release Git tag
git checkout tags/v1.0.0

# 2. Verify JDK 17 active in environment
java -version

# 3. Clean and assemble bundle using wrapper
cd android
./gradlew clean :app:bundleRelease \
  -PversionCode=1 \
  -PversionName="1.0.0" \
  --no-build-cache \
  --no-daemon

# 4. Compute artifact hash
sha256sum app/build/outputs/bundle/release/app-release.aab
```
