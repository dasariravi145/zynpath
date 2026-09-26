# Zynpath Local Mobile Testing Guide

**Status:** Authoritative  
**Target:** Physical Android Devices & Android Virtual Devices (AVD)  

---

## 1. Prerequisites & Environment Setup

Testing Zynpath directly on a real Android phone is an essential requirement throughout development. You do **not** need to publish to the Google Play Store to run and test the complete application.

### Detected Local Paths (This Machine)
- **Java Home:** `C:\Program Files\Java\jdk-17`
- **Android SDK:** `C:\Users\ADMIN\AppData\Local\Android\Sdk`
- **ADB Executable:** `C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools\adb.exe`
- **Installed Android Platforms:** `android-35`, `android-36`, `android-36.1`
- **Installed Build Tools:** `35.0.0`, `36.0.0`, `36.1.0`, `37.0.0`

> **Convenience Tip (PowerShell):**  
> Add ADB to your session PATH:  
> `$env:Path += ";C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools"`

---

## 2. Preparing Your Physical Android Phone

1. **Enable Developer Options**:
   - Open **Settings** $\to$ **About Phone**.
   - Tap **Build Number** 7 times until you see `"You are now a developer!"`.
2. **Enable USB Debugging**:
   - Go to **Settings** $\to$ **System / Additional Settings** $\to$ **Developer Options**.
   - Enable **USB Debugging**.
   - (Optional) Enable **Stay Awake** (keeps screen on while charging during testing).
3. **Connect Device via USB**:
   - Plug the phone into your PC via USB cable.
   - On the phone, accept the prompt: `"Allow USB debugging from this computer?"` (Check "Always allow").
4. **Verify ADB Detection**:
   ```powershell
   & "C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools\adb.exe" devices
   ```
   *Expected output: Lists your device ID with status `device` (not `unauthorized` or `offline`).*

---

## 3. Wireless Debugging Setup (No USB Cable Needed)

For Android 11 (API 30) and newer:
1. Ensure your PC and Android phone are on the **same Wi-Fi network**.
2. On your phone: **Developer Options** $\to$ Enable **Wireless Debugging**.
3. Tap **Pair device with pairing code**. Note the IP, Port, and 6-digit Code.
4. On your PC terminal:
   ```powershell
   adb pair <IP>:<PORT>
   # Enter pairing code when prompted
   adb connect <IP>:<CONNECTION_PORT>
   ```

---

## 4. Building & Installing the Debug APK

### 4.1 Assemble Debug APK
From the `android/` directory:
```powershell
cd d:\Zynpath\android
.\gradlew.bat assembleDebug
```
*Generated APK location:* `android/app/build/outputs/apk/debug/app-debug.apk`

### 4.2 One-Command Build & Install
```powershell
cd d:\Zynpath\android
.\gradlew.bat installDebug
```
Or directly using ADB:
```powershell
& "C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools\adb.exe" install -r d:\Zynpath\android\app\build\outputs\apk\debug\app-debug.apk
```

---

## 5. Live Logcat Inspection

Filter high-priority Zynpath application logs in real-time:

```powershell
adb logcat -s Zynpath:V ZynpathEngine:V AndroidRuntime:E
```

Clear logcat buffer:
```powershell
adb logcat -c
```

Capture crash dump to file:
```powershell
adb logcat -d > crash_dump.txt
```

---

## 6. Connecting Physical Device to Local Backend

When testing multiplayer modes locally against your Spring Boot backend running on `localhost:8080`:

### Method A: ADB Reverse Port Forwarding (Recommended for USB)
This makes `http://localhost:8080` and `ws://localhost:8080` on your physical phone route directly to your development PC:

```powershell
adb reverse tcp:8080 tcp:8080
```
*Now the mobile app can reach the local Spring Boot backend using `http://localhost:8080/api`!*

### Method B: Local Network IP (For Wireless Testing)
1. Find your PC's local IP address:
   ```powershell
   Get-NetIPAddress -AddressFamily IPv4 | Where-Object InterfaceAlias -NotLike "*Loopback*"
   ```
   *(e.g., `192.168.1.150`)*
2. Point your app's debug configuration to `http://192.168.1.150:8080`.

### Method C: Android Emulator Loopback
If using an Android Virtual Device (AVD), the special host loopback alias is:
- **`http://10.0.2.2:8080`**

---

## 7. Network Security & Build Variant Segregation

### 7.1 Cleartext Traffic Control
In `android/app/src/main/res/xml/network_security_config.xml`:
- **Debug Builds**: Cleartext HTTP allowed exclusively for `localhost`, `10.0.2.2`, and local subnet `192.168.*.*`.
- **Release Builds**: Cleartext traffic strictly disabled; all network communication mandates TLS 1.3 (`https://` and `wss://`).

### 7.2 Solo Mode Autonomy Verification
To verify that Solo Play is 100% offline:
1. Install debug APK on your phone.
2. Turn on **Airplane Mode** (disable Wi-Fi and Cellular data).
3. Launch Zynpath.
4. Play and solve levels in World 1.
5. Exit app, kill from Recents, and relaunch.
6. Verify all stars, completion times, and settings remain perfectly intact.
