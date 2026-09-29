# Zynpath Environment Configuration Guide

## 1. Overview
Zynpath employs strict environment separation between local development and release production. This ensures that developer IP addresses, debugging proxy endpoints, and mock servers can never leak into production releases.

---

## 2. Android Build Variants

| Variant | Base API URL | Base WebSocket URL | Network Security Config | Diagnostics Section in Settings |
|---|---|---|---|---|
| **Debug** | `http://10.0.2.2:8080/api/v1` (Emulator default) | `ws://10.0.2.2:8080/ws` | Permits `cleartextTrafficPermitted="true"` for `10.0.2.2`, `localhost`, and `127.0.0.1` | **Visible** (`BuildConfig.DEBUG == true`) |
| **Release** | `https://api.zynpath.com/api/v1` | `wss://api.zynpath.com/ws` | Strict HTTPS only. Cleartext traffic disabled. | **Hidden / Excluded** |

Configured in `android/app/build.gradle.kts`:
```kotlin
buildTypes {
    debug {
        buildConfigField("String", "BACKEND_BASE_URL", "\"http://10.0.2.2:8080/api/v1\"")
        buildConfigField("String", "BACKEND_WS_URL", "\"ws://10.0.2.2:8080/ws\"")
    }
    release {
        isMinifyEnabled = false
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        buildConfigField("String", "BACKEND_BASE_URL", "\"https://api.zynpath.com/api/v1\"")
        buildConfigField("String", "BACKEND_WS_URL", "\"wss://api.zynpath.com/ws\"")
    }
}
```

---

## 3. Network Topologies for Local Testing

### A. Android Emulator (AVD)
- The Android emulator runs in a sandboxed virtual network.
- `10.0.2.2` inside the emulator routes directly to `127.0.0.1` on the development host computer.
- The default debug `BACKEND_BASE_URL` (`http://10.0.2.2:8080/api/v1`) connects directly without any port forwarding.

### B. Physical Android Device via USB (ADB Reverse)
When debugging on a physical phone connected over USB:
1. Ensure the phone has USB debugging authorized (`adb devices`).
2. Run reverse port forwarding:
   ```bash
   adb reverse tcp:8080 tcp:8080
   ```
3. Inside Android Settings -> Backend Connectivity Diagnostic, the app tests reachability on `http://10.0.2.2:8080` (or `http://localhost:8080` if configured).
4. When finished testing, clean up port forwarding:
   ```bash
   adb reverse --remove tcp:8080
   # or remove all:
   adb reverse --remove-all
   ```

### C. Physical Android Device via Local Wi-Fi (LAN)
If USB cable is not convenient:
1. Ensure development computer and phone are connected to the identical local Wi-Fi subnet.
2. Determine developer PC LAN IP (e.g. `192.168.1.100` via `ipconfig`).
3. Ensure backend is bound to `0.0.0.0` (configured in `backend/src/main/resources/application.yml`).
4. Ensure Windows Defender Firewall allows inbound traffic on port 8080 for private networks.
5. In debug configuration or local testing script, point the app to `http://192.168.1.100:8080/api/v1`.
6. Public port forwarding or cloud deployment must never be used.

---

## 4. Backend Configuration Variables

Environment variables supported by `backend/src/main/resources/application.yml`:

| Environment Variable | Default Value | Description |
|---|---|---|
| `SERVER_PORT` | `8080` | Port for the embedded web server |
| `SERVER_ADDRESS` | `0.0.0.0` | Bind address (0.0.0.0 allows LAN/ADB reachability) |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile (`dev`, `test`, `prod`) |
| `LOGGING_LEVEL_COM_ZYNPATH` | `DEBUG` | Logging level for Zynpath application packages |

A template is maintained at `backend/src/main/resources/application-example.yml`.

---

## 5. Security Guardrails
1. Never commit `.env`, service account keys, keystores, or production database URLs.
2. Never hardcode developer machine LAN IPs into Git version control.
3. Release builds enforce TLS 1.3/1.2; cleartext HTTP will be rejected by Android OS.
