# PROMPT 24 VALIDATION HANDOFF & RELEASE READINESS SPECIFICATION

## Overview
This document provides the authoritative handoff from Prompt 23 to Prompt 24 ("COMPREHENSIVE AUDITING, COMPILATION, TESTING, APK GENERATION, AND DEVICE VERIFICATION").
No commands were executed during Prompt 23. No claims are made that any build, test, or verification has already passed. All verification and execution are explicitly deferred to Prompt 24.

---

## 1. Implementation Scope Covered (Prompts 01–23)
- **Prompt 01–06**: Core Design System (navy/royal-blue/cyan/gold theme), responsive layout tokens, 300-level Solo campaign progression, gesture & accessible puzzle input, solver-validated level curation, audio and haptics coordination.
- **Prompt 07–10**: Guest session handling, Google Sign-In, Firebase Auth, Room/Datastore persistent storage, and secure token lifecycle.
- **Prompt 11–13**: Daily Challenge online synchronizer, daily reminders, and streak tracking.
- **Prompt 14–17**: Competitive 1v1 Quick Duel matchmaking, WebSocket subscriptions, STOMP event dispatching, and match result recording.
- **Prompt 18–20**: Friends Arena 1–5 player custom rooms, room code generation/sanitization, WebSocket room event broadcasts, synchronized puzzle gameplay, authoritative finish order, and host rematch triggers.
- **Prompt 21–22**: Facebook Friend Discovery integration, permissions handling, invite link deep-linking (`zynpath://arena?code=...`), and UI polish.
- **Prompt 23**: Navigation graph integrity, `popUpTo` route template alignment, removal of obsolete coming-soon UI dialogs, DTO serialization parity (activeMatchId and PuzzleAssignment parsing), and release-readiness handoff preparation.

---

## 2. Relevant Android and Backend Modules
### Android (`D:\Zynpath\android\app`)
- **Navigation**: `feature/navigation/Screen.kt`, `feature/navigation/NavGraph.kt`
- **Home Game Hub**: `feature/home/HomeScreen.kt`, `feature/home/components/FriendsArenaCard.kt`
- **Solo Gameplay**: `feature/gameplay/GameplayShellScreen.kt`, `feature/gameplay/GameplayViewModel.kt`, `core/puzzle/engine/PuzzleEngine.kt`
- **Quick Duel**: `feature/multiplayer/QuickDuelScreen.kt`, `feature/multiplayer/QuickDuelViewModel.kt`
- **Friends Arena**:
  - Entry: `feature/multiplayer/FriendsArenaEntryScreen.kt`
  - Lobby: `feature/multiplayer/FriendsArenaRoomScreen.kt`, `feature/multiplayer/FriendsArenaRoomViewModel.kt`
  - Join: `feature/multiplayer/FriendsArenaJoinScreen.kt`, `feature/multiplayer/FriendsArenaJoinViewModel.kt`
  - Gameplay: `feature/multiplayer/FriendsArenaGameplayScreen.kt`, `feature/multiplayer/FriendsArenaGameplayViewModel.kt`
  - Results: `feature/multiplayer/FriendsArenaResultsScreen.kt`, `feature/multiplayer/FriendsArenaResultsViewModel.kt`
  - Facebook Discovery: `feature/multiplayer/FriendsArenaFacebookScreen.kt`, `feature/multiplayer/FriendsArenaFacebookViewModel.kt`
- **Multiplayer Core**:
  - API Service: `core/multiplayer/api/OkHttpMultiplayerApiService.kt`
  - Repository: `core/multiplayer/repository/MultiplayerRepositoryImpl.kt`
  - Models: `core/multiplayer/model/MultiplayerModels.kt`

### Backend (`D:\Zynpath\backend`)
- **Controller**: `src/main/java/com/zynpath/backend/multiplayer/controller/MultiplayerController.java`
- **Services**:
  - `FriendsArenaService.java`
  - `MultiplayerMatchService.java`
  - `MatchmakingService.java`
- **Models & DTOs**:
  - `model/FriendsArenaRoom.java`
  - `model/FriendsArenaMember.java`
  - `model/FriendsArenaMatch.java`
  - `model/MultiplayerDto.java`

---

## 3. Known Unfinished Integrations
1. **Facebook Friend Graph Live Network Sync**: Live friend discovery is constrained by real Facebook Graph API developer test user requirements (`user_friends` permission). In local test mode without live FB credentials, UI correctly surfaces honest disabled/unauthorized state.
2. **Push Notification Transport**: FCM service definitions exist in manifest, but live token dispatch requires a provisioned `google-services.json` connected to an active Firebase Cloud project.

---

## 4. Missing Google / Facebook Credentials & Provider Setup
- **Google Sign-In**: Requires an active OAuth 2.0 Web Client ID in `local.properties` or environment (`GOOGLE_WEB_CLIENT_ID`). When absent, guest authentication and local profile persistence remain fully operational.
- **Facebook Login & Discovery**: Requires `facebook_app_id` and `facebook_client_token` in `android/app/src/main/res/values/strings.xml` or gradle properties. Direct 6-character room codes bypass Facebook auth completely.

---

## 5. Backend Environment Requirements
- **Runtime**: Java 17+ JDK.
- **Build Tool**: Maven (uses `pom.xml`).
- **Dependencies**: Spring Boot 3.x, Spring WebSocket / STOMP message broker.
- **Database**: In-memory H2 or configured PostgreSQL / Redis instance (configured via `application.yml` or `application-prod.yml`).
- **Default Ports**:
  - HTTP / REST: `8080` (or `PORT` env var).
  - WebSocket endpoint: `ws://<host>:8080/ws/multiplayer`.

---

## 6. Build and Test Commands to Execute in Prompt 24
*(To be executed strictly in Prompt 24 only — NOT in Prompt 23)*

### Backend Verification:
```bash
cd D:\Zynpath\backend
mvn clean compile
mvn test
```

### Android Unit Tests & Verification:
```bash
cd D:\Zynpath\android
gradlew.bat testDebugUnitTest
```

### Android APK Build:
```bash
cd D:\Zynpath\android
gradlew.bat assembleDebug
```

---

## 7. Android Installation Precautions & Device Safety
> [!CAUTION]
> **CRITICAL DATA PRESERVATION RULE:**
> An existing physical test device contains active guest Solo progress and unlocked levels.
> - **DO NOT uninstall the application** (`adb uninstall com.zynpath.game`).
> - **DO NOT clear application data** (`adb shell pm clear com.zynpath.game`).
> - Install APK updates strictly using install-with-replace:
>   ```bash
>   adb install -r -d app/build/outputs/apk/debug/app-debug.apk
>   ```
> - Verify that `applicationId` remains `com.zynpath.game` and debug signing key compatibility is maintained so data is preserved across installs.

---

## 8. Physical-Device Verification Checklist
- [ ] **Launch & Session**: App opens without crash; splash routes to existing guest or authenticated session.
- [ ] **Solo Progress Integrity**: World Map displays existing unlocked levels (no progress reset).
- [ ] **Solo Gameplay & Victory**: Completing a level records validated completion and transitions smoothly to next level.
- [ ] **Backstack Integrity**: Hardware back button from Gameplay, Level Selection, or Settings returns to previous screen without exiting abruptly or blank-screen hangs.
- [ ] **Guest vs Auth Boundaries**: Entering Quick Duel or creating a Friends Arena room prompts for authentication or clearly informs guests without crashing.

---

## 9. Friends Arena 2–5 Player Test Scenarios
1. **Room Creation (Host)**:
   - Host clicks "Create Room" in Friends Arena.
   - Authoritative backend room is created and assigned a 6-character alphanumeric code.
   - Host lobby displays "1/5 Players", with Host in Slot 1 and Slots 2–5 open.
   - "Start Match" button is disabled (requires minimum 2 players).
2. **Room Joining (Non-Host / Participant 2)**:
   - Player 2 enters the 6-character code in "Join Room".
   - Backend confirms join; Player 2 occupies Slot 2.
   - Both host and participant receive WebSocket room update showing 2/5 occupancy.
   - Host's "Start Match" button becomes enabled.
3. **Capacity Enforcement (Up to 5 Players)**:
   - Additional players (3, 4, 5) join using the code.
   - Lobby updates to reflect each occupant.
   - A 6th player attempting to join receives authoritative `ROOM_FULL` rejection.
4. **Authoritative Match Start & Gameplay**:
   - Host taps "Start Match".
   - Backend generates single shared puzzle assignment (`PuzzleAssignmentDto`).
   - Countdown triggers simultaneously across all clients.
   - During gameplay, each participant's cell progress is broadcast without solution leakage.
5. **Authoritative Results & Rematch**:
   - First player solving the puzzle submits claim; backend validates and records finish order.
   - All players receive match completion and navigate to Results screen showing verified standings.
   - Host initiates rematch; new match ID is broadcast, smoothly transitioning all active occupants.

---

## 10. Known Blockers and Uncertainties
1. **Device Network Routing**: For physical-device multiplayer testing, ensure the Android client points to the host computer's local IP address (e.g., `http://192.168.x.x:8080`) rather than `10.0.2.2` (which is emulator-only).
2. **Local Firewall**: Port 8080 must be open to incoming traffic on the local subnet for physical device connection.
3. **No Validation Run**: No Gradle or Maven commands have been executed in Prompt 23; all compilation and runtime checks will be executed in Prompt 24.
