# Developer Onboarding & Local Setup Guide — Zynpath

**Document ID:** `GUIDE-DEV-ONBOARD-001`  
**Application Title:** *Zynpath: Number Path Puzzle*  
**Platform:** Native Android (Kotlin) & Backend (Java 17 / Spring Boot)  
**Status:** Authoritative Developer Quick-Start Manual  

---

## 1. Welcome to Zynpath

Welcome to the **Zynpath: Number Path Puzzle** engineering team. Zynpath is a deterministic, continuous-path logic puzzle application built with modern native Android technologies (Kotlin, Jetpack Compose, Room, DataStore) backed by a lightweight, authoritative Spring Boot 3.4.3 service.

This guide provides everything needed to configure your local workstation, build the project, run test suites, and make contributions.

---

## 2. Workstation Prerequisites

Ensure your development environment meets the following baseline toolchain requirements:

| Tool / SDK | Required Version | Verification Command | Notes |
|---|---|---|---|
| **Git** | 2.40+ | `git --version` | Version control |
| **Java JDK** | OpenJDK 17 LTS | `java -version` | Required for both Android Gradle builds and Spring Boot backend |
| **Android Studio** | Ladybug (2024.2.1+) or Meerkat | Check Studio About dialog | Recommended IDE for Android development |
| **Android SDK** | API Level 36 (Android 16) | Android Studio SDK Manager | Install `android-36` platform and build-tools |
| **Apache Maven** | 3.9+ | `mvn -version` | Required for backend build and testing |
| **Docker Engine** | 24+ & Compose v2 | `docker --version` | Optional, for local containerized backend & PostgreSQL |

---

## 3. Repository Clone & Directory Layout

Clone the repository to your local workspace:
```bash
git clone https://github.com/your-org/zynpath.git
cd zynpath
```

### Directory Structure Overview:
```
zynpath/
├── android/                  # Native Android Kotlin application
│   ├── app/                  # Application module, UI screens, viewmodels, Hilt DI
│   ├── core/
│   │   ├── domain/           # Pure Kotlin puzzle engine (zero Android dependencies)
│   │   ├── data/             # Room DB (v11), DataStore, network repositories
│   │   └── ui/               # Design system, Compose theme tokens, canvas renderer
│   ├── gradle/libs.versions.toml # Centralized dependency catalog
│   └── build.gradle.kts      # Android root build script
├── backend/                  # Spring Boot 3.4.3 modular monolith
│   ├── src/main/java/        # Game rooms, puzzle validation, matchmaking, auth
│   ├── src/main/resources/   # Application profiles (prod, staging, dev) & Flyway V1-V6
│   ├── Dockerfile            # Multi-stage Eclipse Temurin 17 container
│   └── pom.xml               # Backend Maven configuration
├── assets/                   # Store listing metadata, branding vectors, compliance HTML
├── docs/                     # Authoritative engineering specifications & runbooks
└── README.md                 # Project summary and quick start
```

---

## 4. Mobile Client (Android) Setup & Build

### 4.1 Import into Android Studio
1. Open Android Studio.
2. Select **File $\to$ Open...** and navigate to the `android/` subfolder.
3. Allow Gradle to synchronize dependencies defined in `libs.versions.toml`.

### 4.2 Build and Run Debug APK
Open a terminal in the `android/` directory:
```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Assemble the debug APK
./gradlew assembleDebug
```
*Generated Output:* `android/app/build/outputs/apk/debug/app-debug.apk`

### 4.3 Connect to Local Backend
* **Android Emulator:** Automatically connects to local backend via `http://10.0.2.2:8080/api/v1`.
* **Physical USB Device:** Route local port 8080 to the device over ADB:
  ```bash
  adb reverse tcp:8080 tcp:8080
  ```
* **Verify Connectivity:** Launch Zynpath $\to$ **Settings** $\to$ Scroll to developer section $\to$ Tap **"BACKEND CONNECTIVITY (DEBUG)"** to verify REST and WebSocket ping responses.

---

## 5. Backend Service Setup & Execution

### 5.1 Run Tests Locally
Open a terminal in the `backend/` directory:
```bash
cd backend
mvn clean test
```
*Expected Result:* 16 test suites, 73 tests pass (100% pass rate) using the embedded H2/PostgreSQL test configuration.

### 5.2 Start Backend in Development Mode
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
The server will start on port `8080`. Verify the health endpoint:
```bash
curl http://localhost:8080/api/v1/health
```

---

## 6. Testing Strategy & Execution Commands

| Test Category | Target Subsystem | Command |
|---|---|---|
| **Core Puzzle Tests** | Mathematical invariants, solver, generator | `cd android && ./gradlew :core:domain:test` |
| **Android Unit Tests** | ViewModels, Room DB migrations, DataStore | `cd android && ./gradlew :app:testDebugUnitTest` |
| **Compose UI Tests** | UI flows, accessibility, gestures, navigation | `cd android && ./gradlew :app:testDebugUnitTest --tests "*ComprehensiveTest*"` |
| **Backend Integration**| REST APIs, WebSocket matchmaking, Flyway | `cd backend && mvn test` |

---

## 7. Core Architectural Invariants to Uphold

When submitting pull requests or making modifications, you must adhere to these inviolable architectural principles:
1. **Preserve the 7 Puzzle Rules:** Never alter orthogonal movement, ascending checkpoint ordering, or full grid coverage victory requirements.
2. **Never Put Backend in the Solo Critical Path:** Solo gameplay, tutorials, and offline practice must remain 100% playable without network access.
3. **Server-Authoritative Multiplayer:** The backend must always re-validate the complete path submission using `ServerPuzzleValidator`. Never trust client win claims.
4. **Competitive Fairness:** Never allow hints, skips, or puzzle assistance in multiplayer duels or competitive daily leaderboards.
5. **Zero Committed Secrets:** Never commit passwords, keystores, or API tokens. Use environment variables.
