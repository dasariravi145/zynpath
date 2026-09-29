# Zynpath Local Development and Testing Guide

**Status:** Authoritative  
**Platform:** Windows 11 / Windows PowerShell  
**Target Environments:** Android Studio Emulator (AVD), Physical Android Devices via USB/Wi-Fi, Local Spring Boot Backend  

---

## 1. Project Directory Structure

Zynpath consists of two primary software projects housed within this repository:

| Component | Repository Path | Framework / Stack |
| :--- | :--- | :--- |
| **Android Application** | `d:\Zynpath\android` | Kotlin 2.3.0, Jetpack Compose, Room, Hilt, AGP 8.7.3 |
| **Backend Service** | `d:\Zynpath\backend` | Spring Boot 3.4.3, Java 17 LTS, WebSockets, Actuator, Flyway |
| **Documentation** | `d:\Zynpath\docs` | Architectural, operational, and testing specifications |
| **Assets & Specs** | `d:\Zynpath\assets` | Audio, icons, levels, and brand assets |

---

## 2. Verified Toolchain and Version Matrix

All tools below have been verified on this Windows workstation:

| Tool / Dependency | Required Version | Verified Local Version | Path / Executable |
| :--- | :--- | :--- | :--- |
| **Java JDK** | 17 LTS | `17.0.12+8-LTS-286` (Oracle) | `C:\Program Files\Java\jdk-17` |
| **Apache Maven** | 3.9+ | `3.9.16` | `D:\software\apache-maven-3.9.16-bin\apache-maven-3.9.16` |
| **Gradle** | 8.11+ / 9.x | `9.4.1` (Wrapper) | `d:\Zynpath\android\gradlew.bat` |
| **Android SDK** | compileSdk 36, minSdk 24 | Platforms 35, 36, 36.1 | `C:\Users\ADMIN\AppData\Local\Android\Sdk` |
| **Android ADB** | 1.0.41+ | `37.0.0` | `C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools\adb.exe` |
| **Android Emulator** | Latest AVD Manager | Detected AVD: `Pixel_7` | `C:\Users\ADMIN\AppData\Local\Android\Sdk\emulator\emulator.exe` |
| **Container Engine** | Optional for Docker | Docker 29.0.1 | `docker` CLI available |
| **Database** | Dev: In-memory; Prod: PG 16+ | In-Memory `ConcurrentHashMap` | Built into Spring Boot `dev` profile |

---

## 3. Local Backend Configuration

### 3.1 Persistence Model in Local Development
For rapid local iteration and testing, the Spring Boot backend (`application-dev.yml`) uses thread-safe in-memory stores (`ConcurrentHashMap`) for account sessions, player progress sync, matchmaking tickets, WebSocket sessions, and room states.
- **No external database is required** to run the backend locally or test multiplayer duels.
- For production staging where relational database persistence is tested, Flyway migrations (`V1` through `V6` in `src/main/resources/db/migration/`) apply automatically against a PostgreSQL 16+ database instance.

### 3.2 Ports & Endpoints
- **Server Port:** `8080` (bound to `0.0.0.0` to permit emulator and LAN connections).
- **Health Endpoint:** `GET http://localhost:8080/api/v1/health`
- **Actuator Probes:** `GET http://localhost:8080/actuator/health`
- **REST Base URL:** `http://localhost:8080/api/v1`
- **Multiplayer WebSocket:** `ws://localhost:8080/ws/multiplayer`
- **Presence WebSocket:** `ws://localhost:8080/ws/presence`

---

## 4. Android Network Configuration

### 4.1 Emulator Loopback Access (`10.0.2.2`)
The standard Android Virtual Device loopback alias `10.0.2.2` directs traffic to the host workstation's `127.0.0.1`.
In `android/app/build.gradle.kts`, the `debug` buildType is pre-configured with:
- `BACKEND_BASE_URL` = `"http://10.0.2.2:8080/api/v1"`
- `BACKEND_WS_URL` = `"ws://10.0.2.2:8080/ws/multiplayer"`

### 4.2 Network Security Config (`network_security_config.xml`)
To safeguard production integrity while allowing seamless local debugging:
- **Base Policy:** Cleartext traffic is strictly forbidden (`cleartextTrafficPermitted="false"`).
- **Debug Domain Whitelist:** Cleartext HTTP and WS is explicitly enabled only for:
  - `10.0.2.2` (Android emulator host loopback)
  - `localhost` (ADB reverse port forwarding)
  - `127.0.0.1` (Local loopback)
- **Production Safety:** Release builds target `https://api.zynpath.app` and `wss://api.zynpath.app` with enforced TLS 1.3 certificate validation.

---

## 5. Quick-Start Commands (PowerShell)

### Step 1: Start the Local Backend
Open a PowerShell terminal:
```powershell
Set-Location -Path "d:\Zynpath\backend"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
*Alternatively, run the pre-built JAR:*
```powershell
java -jar "d:\Zynpath\backend\target\zynpath-backend-1.0.0-SNAPSHOT.jar" --spring.profiles.active=dev
```

Verify backend health in a second terminal:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/health" -Method Get
```
*Expected Response:*
```json
{
  "status": "UP",
  "service": "Zynpath Backend",
  "version": "1.0.0-SNAPSHOT",
  "environment": "development"
}
```

---

### Step 2: Launch the Android Emulator
Launch the verified `Pixel_7` virtual device from your terminal or Android Studio:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd Pixel_7
```
Check that the emulator is recognized by ADB:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices
```
*Output should display `emulator-5554 device`.*

---

### Step 3: Build & Install the Android Debug APK
Build the debug APK:
```powershell
Set-Location -Path "d:\Zynpath\android"
.\gradlew.bat :app:assembleDebug
```
*Output APK location:*  
`d:\Zynpath\android\app\build\outputs\apk\debug\app-debug.apk`

Install directly onto the running emulator:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r "d:\Zynpath\android\app\build\outputs\apk\debug\app-debug.apk"
```

Launch Zynpath on the emulator:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell monkey -p com.zynpath.game.debug -c android.intent.category.LAUNCHER 1
```

---

## 6. Testing Scenarios

### 6.1 Solo Gameplay Offline Autonomy
Zynpath is architected with a local-first philosophy:
1. Turn on **Airplane Mode** on the emulator or stop the backend process.
2. Launch Zynpath. The app opens directly to the Home screen without network lockouts.
3. Tap **Play Solo** $\to$ Select World 1 $\to$ Play Level 1.
4. Drag/tap to construct number paths connecting the arithmetic targets.
5. Solve the level. Complete audio, haptic, and star celebrations trigger locally.
6. Progress is persisted locally to Room SQLite (`ZynpathDatabase`).
7. Kill the app (`adb shell am force-stop com.zynpath.game.debug`) and relaunch.
8. Verify that unlocked levels, high scores, and earned stars are completely preserved.

### 6.2 In-App Backend Diagnostics
1. From the Zynpath Home screen, tap the **Settings** (gear) icon.
2. Scroll to **Backend Connectivity (Debug)**.
3. Observe:
   - Configured Base URL: `http://10.0.2.2:8080/api/v1`
   - Real-time connection badge: `CONNECTED` (Green)
   - Measured round-trip latency to `/health`.
4. Tap **Test Connection** to execute an immediate live probe.

### 6.3 Two-Player Local Multiplayer Testing
You can test real-time 1v1 Quick Duel or Friend Duel locally using two endpoints:
- **Option A (Two Emulators):** Start `Pixel_7` (port 5554) and a second AVD (port 5556). Both connect to `http://10.0.2.2:8080/api/v1`.
- **Option B (Emulator + Physical Device):**
  1. Connect physical Android phone via USB.
  2. Run `adb -s <DEVICE_SERIAL> reverse tcp:8080 tcp:8080`.
  3. The physical phone reaches the backend via `http://localhost:8080/api/v1` while the emulator uses `http://10.0.2.2:8080/api/v1`.
- **Execution:**
  1. On Device 1: Tap **Multiplayer** $\to$ **Create Friend Duel**. Note the 6-character room code.
  2. On Device 2: Tap **Multiplayer** $\to$ **Join Duel** $\to$ Enter room code (or use deep link `zynpath://duel/join?code=XYZ`).
  3. Both players enter the match room. As Player 1 makes moves, Player 2 sees live progress updates over the WebSocket channel.

---

## 7. External Integrations & Sandbox Credentials

When testing in a local debug environment without production keys, the following behaviors apply:

| Feature / Service | Debug / Sandbox Behavior | Production Requirement |
| :--- | :--- | :--- |
| **Google Mobile Ads (AdMob)** | Uses official Google sample AdMob App ID (`ca-app-pub-3940256099942544~3347511713`) and sample rewarded ad unit (`ca-app-pub-3940256099942544/5224354917`). Test video ads play reliably. | Real AdMob App ID & Ad Unit ID in release build or CI environment. |
| **Google Play Billing** | Uses `FakeBillingRepository` in unit tests, or Google Play License Testing accounts for sandbox purchase simulations on Play-connected devices. | Google Play Console In-App Product & Subscription setup with active service account. |
| **Google Sign-In / OAuth** | Guest Mode functions 100% offline with full level progression. To test Google Auth locally, register your debug keystore SHA-1 fingerprint in Google Cloud Console. | Production OAuth 2.0 Web Client ID and Play App Signing SHA-1 fingerprint. |
| **Push Notifications** | Local reminders use Android `AlarmManager` and `NotificationManager` directly without external cloud dependencies. | Firebase Cloud Messaging (FCM) credentials for remote push. |

---

## 8. Log Inspection & Troubleshooting

### Android Logcat
Filter Zynpath tags in real-time:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" logcat -s Zynpath:V ZynpathEngine:V AndroidRuntime:E
```

Clear logcat buffer:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" logcat -c
```

### Backend Logs
When running via `mvn spring-boot:run -Dspring-boot.run.profiles=dev`, log output streams directly to standard out with `DEBUG` verbosity for `com.zynpath.backend`.

### Common Issues & Resolutions
1. **Port 8080 in use:**  
   Find the blocking process:  
   `Get-NetTCPConnection -LocalPort 8080`  
   Or pass a different port:  
   `java -jar target/zynpath-backend-1.0.0-SNAPSHOT.jar --server.port=8081`  
   (Remember to adjust `BACKEND_BASE_URL` if changing port).
2. **Emulator shows `OFFLINE` in Settings:**  
   Confirm backend is running. From emulator browser, navigate to `http://10.0.2.2:8080/api/v1/health` to verify connectivity.
3. **ADB unauthorized:**  
   Unlock the device/emulator screen and accept the "Always allow USB debugging from this computer" prompt.
