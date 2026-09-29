# Zynpath Gameplay Timer & Monotonic Timing Architecture

**Document Version:** 1.0.0  
**Phase:** Phase 3 — Interactive Gameplay  
**Milestone:** Prompt 13/50  
**Status:** Authoritative  

---

## 1. Overview & Objective

Zynpath puzzles require an accurate, monotonic active-play timer. Timing directly impacts personal best records, speed statistics, and player progression.

Authoritative constraints:
1. **Never use wall-clock subtraction as active play duration:** Wall clocks can jump backwards or forwards due to NTP synchronization, manual clock changes, or daylight saving transitions.
2. **Exclude non-gameplay duration:** Loading assets, viewing level selection, pausing, backgrounding, and screen locks must never count towards active elapsed play duration.
3. **Idempotent operations:** Repeated calls to pause or resume must never distort the accumulated active duration or introduce overlapping intervals.
4. **Decouple calculation from display:** The authoritative elapsed duration is computed mathematically using monotonic time deltas, independent of Compose recomposition or coroutine delay ticks.

---

## 2. Core Architecture (`TimeProvider` & `GameplayTimer`)

### 2.1 Clock Abstraction (`TimeProvider`)
```kotlin
interface TimeProvider {
    fun monotonicTimeMs(): Long
    fun wallClockTimeMs(): Long
}
```
- **`SystemTimeProvider`:** Production implementation. Uses `System.nanoTime() / 1_000_000L` for monotonic elapsed time (guaranteed monotonic within an Android JVM process), and `System.currentTimeMillis()` for calendar metadata.
- **`TestTimeProvider`:** Deterministic unit test clock allowing precise manual advancement of monotonic and wall-clock times.

### 2.2 Timer State (`GameplayTimerState`)
```kotlin
data class GameplayTimerState(
    val accumulatedDurationMs: Long = 0L,
    val isRunning: Boolean = false,
    val activeIntervalStartMonotonicMs: Long? = null
) {
    fun currentElapsedDurationMs(currentMonotonicMs: Long): Long {
        return if (isRunning && activeIntervalStartMonotonicMs != null) {
            val interval = (currentMonotonicMs - activeIntervalStartMonotonicMs).coerceAtLeast(0L)
            accumulatedDurationMs + interval
        } else {
            accumulatedDurationMs
        }
    }
}
```

---

## 3. Lifecycle & Timing Events

### 3.1 Level Opening
When `GameplayShellScreen` appears and `loadLevel()` loads the puzzle asset:
- Timer remains **stopped** at `0 ms` (or at the saved session's accumulated duration if restoring a paused session).
- Level catalog loading time, asset parsing, and initial render do **not** consume active gameplay time.

### 3.2 First Valid Touch (`StartPath`)
- When the player places their finger on Checkpoint #1 and the engine accepts `StartPath`:
- `gameplayTimer.start()` transitions `isRunning = true` and captures `activeIntervalStartMonotonicMs = now`.
- Coroutine ticker starts emitting display updates every 100ms.

### 3.3 Pause (`onPauseGame` / Pause Dialog)
- `gameplayTimer.pause()`:
  1. Computes active interval: `interval = (now - activeIntervalStartMonotonicMs)`.
  2. Accumulates duration: `accumulatedDurationMs += interval`.
  3. Clears `activeIntervalStartMonotonicMs = null`.
  4. Sets `isRunning = false`.
- Ticker coroutine is cancelled.
- Repeated calls while paused return the same frozen accumulated duration without modification.

### 3.4 Resume (`onResumeGame`)
- `gameplayTimer.resume()`:
  1. Starts a fresh active interval: `activeIntervalStartMonotonicMs = now`.
  2. Preserves existing `accumulatedDurationMs`.
  3. Sets `isRunning = true`.
- Ticker coroutine resumes updating Compose state from the preserved duration.
- Repeated calls while already running are strictly no-op.

### 3.5 App Backgrounding & Device Sleep
- Jetpack Compose lifecycle observer captures `Lifecycle.Event.ON_PAUSE` / `ON_STOP`.
- Calls `viewModel.onAppBackgrounded()`.
- Active interval is immediately captured and frozen in Room.
- Zero background time is added, even if the device remains asleep for hours.

### 3.6 Reset (`onResetClicked`)
- `gameplayTimer.reset()`:
  1. Sets `accumulatedDurationMs = 0L`.
  2. Sets `isRunning = false`, `activeIntervalStartMonotonicMs = null`.
- Display updates to `00:00`.
- Session snapshot in Room is updated to `elapsedActiveTimeMs = 0L`.

### 3.7 Completion (`handleValidatedCompletion`)
- When the final checkpoint is reached with 100% cell coverage:
- `finalTimeMs = gameplayTimer.stop()`.
- Monotonic elapsed duration is frozen permanently.
- Passed directly to `ValidatedCompletionResult.elapsedTimeMs`.
- Persisted to `level_progress.bestTimeMs` if faster than previous personal best.

---

## 4. UI Formatting
Time is formatted as `MM:SS`:
```kotlin
fun formatTime(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
```
Displays cleanly across stats header, pause overlay, and victory dialogs.
