# Zynpath Android Lifecycle & Interruption Test Report

**Document Version:** 1.0  
**Phase:** 11 — Comprehensive Testing and Quality Assurance (Prompt 48/50)  
**Date:** September 2026  
**Target:** Activity/Fragment Lifecycle, ViewModel State Preservation, Process Death, Configuration Changes  

---

## 1. Executive Summary

Mobile games must be exceptionally resilient to external interruptions (incoming phone calls, app switching, system-initiated process termination, screen rotation, and low-memory conditions).

Zynpath implements a two-tier state preservation architecture:
1. **Tier 1 (In-Memory `SavedStateHandle`):** Instant state recovery during configuration changes and transient app backgrounding.
2. **Tier 2 (Room Database `gameplay_sessions` table):** Durable, ACID-compliant persistence for long-term session resumption after process death or deliberate user exit.

### Overall Verification Status: **PASSED (All automated lifecycle suites verified)**

---

## 2. Timer & Session Continuity (Req 36, 37)

### 2.1 Timer State Machine
The gameplay timer transitions cleanly through four states:
- `RUNNING`: Timer increments every 1000ms while user actively interacts.
- `PAUSED`: Triggered when user opens the Pause dialog or app loses window focus (`ON_PAUSE`). Elapsed time is locked; no drift occurs.
- `RESUMED`: Resumes from the exact accumulated millisecond value when `ON_RESUME` fires and dialog is dismissed.
- `COMPLETED`: Frozen at final solve duration upon victory.

### 2.2 Test Results
- Verified in `GameplayLifecycleAndInterruptionTest.testPauseAndResumePreservesTimerAndPath()`:
  - User draws path of 4 cells, timer reaches 14,000ms.
  - Pause event triggers: `isPaused` becomes `true`.
  - Additional time passage while paused does not increase `elapsedTimeMs`.
  - Resume event triggers: `isPaused` becomes `false`.
  - Drawing subsequent cells continues from 14,000ms. **PASSED**.

---

## 3. App Backgrounding & Foreground Restoration (Req 38)

### 3.1 Lifecycle Callback Handling
When Zynpath transitions through Android lifecycle events:
- **`onPause` / `onStop`:**
  1. The gameplay timer immediately halts.
  2. The active path coordinates, move count, and elapsed time are written asynchronously to Room via `GameplaySessionRepository.saveActiveSession()`.
  3. Any active multiplayer ping/pong heartbeat interval adjusts to low-power background mode (or gracefully signals disconnect if backgrounding exceeds 30 seconds).
- **`onStart` / `onResume`:**
  1. The UI restores the exact path segments, visited checkpoint badges, and timer from ViewModel state.
  2. The puzzle board state is refreshed without graphical stutter or redrawing artifacts.

### 3.2 Test Results
- Verified in `GameplayLifecycleAndInterruptionTest.testBackgroundingDoesNotCorruptGameplayState()`:
  - State before backgrounding: Path $[(0,0), (0,1), (0,2), (1,2)]$, Moves: 3.
  - Background lifecycle dispatched (`onStop`).
  - Foreground lifecycle dispatched (`onStart`).
  - Path, moves, and board state match pre-backgrounding values with zero data loss. **PASSED**.

---

## 4. Process Death & Session Restoration (Req 39, 91)

### 4.1 System-Initiated Process Termination
When Android terminates the app process in low-memory situations (`KILL_BACKGROUND_PROCESS`):
1. In-memory ViewModel state is destroyed.
2. Upon user re-launch, `HomeViewModel` queries `GameplaySessionRepository.observeActiveSession()`.
3. If an incomplete session exists for the user, Home screen presents a prominent **"Resume Puzzle"** banner showing:
   - Level name & World (e.g., "World 1 - Level 4").
   - Elapsed time and current progress (e.g., "16 / 25 cells covered").
4. Tapping "Resume" navigates to the gameplay screen and pre-populates the board with the exact saved path coordinates.

### 4.2 Test Results
- Verified in `GameplayLifecycleAndInterruptionTest.testProcessRestorationRestoresSessionFromDatabase()`:
  - Session saved to Room DB entity `GameSessionEntity(puzzleId = "w1_lvl4", pathCoordinates = "[...]", elapsedSeconds = 45)`.
  - Simulated process recreation: New `GameplaySessionRepository` and `HomeViewModel` instantiated.
  - Active session cleanly detected and loaded. **PASSED**.

---

## 5. Device Configuration Change & Rotation (Req 40)

### 5.1 Configuration Changes Handled
- **Screen Orientation:** Portrait $\leftrightarrow$ Landscape.
- **Window Resizing:** Multi-window split screen mode.
- **Dark/Light Theme Switch:** Dynamic system theme changes.
- **Font Scaling:** User changes system accessibility font size.

### 5.2 Preservation Mechanism
Because Jetpack Compose UI state is hoisted to Android ViewModels (backed by `SavedStateHandle`), orientation changes do not recreate ViewModel instances. The Compose tree recomposes into the new `ZynpathWindowInfo` dimensions while retaining the identical path stack and timer.

### 5.3 Test Results
- Verified in `GameplayLifecycleAndInterruptionTest.testRotationPreservesPathAndTimer()`:
  - 10-cell path drawn in Portrait.
  - Simulated orientation change to Landscape (`WindowWidthSize.Expanded`).
  - Path length, checkpoint progress, and move count remain identical. **PASSED**.

---

## 6. Back Navigation Safety (Req 41)

When a player presses the system back gesture or the in-game back button:
1. If the puzzle has zero moves, the screen immediately exits to Level Selection.
2. If moves have been made, an unobtrusive confirmation dialog appears:
   - *"Leave puzzle? Your progress will be saved so you can resume anytime."*
   - Options: *"Keep Playing"* or *"Save & Exit"*.
3. Choosing *"Save & Exit"* ensures atomic commit to Room DB before navigation pop. Durable progress is never discarded accidentally.
- Verified in `GameplayLifecycleAndInterruptionTest.testBackNavigationSavesProgressBeforeExit()`. **PASSED**.
