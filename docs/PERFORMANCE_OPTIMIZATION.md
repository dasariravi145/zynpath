# Performance Optimization Overview

## 1. Principles
Zynpath prioritizes smooth, responsive gameplay on all Android hardware tiers (from budget 60Hz devices to 120Hz flagship screens) without altering puzzle logic, competitive validation, or user-visible behaviors.
- **Defer Nonessential Work**: Non-critical subsystems (analytics, cosmetics preview, social checks) are initialized lazily or after the first frame renders.
- **Draw-Phase Animation Deferral**: Animation state reads (`pulseAlpha`, `headPulseScale`) are confined strictly to the Jetpack Compose draw phase (`Canvas { ... }`), eliminating recomposition of the surrounding composables.
- **Lifecycle-Aware State Collection**: All screens use `collectAsStateWithLifecycle()` to pause Flow collection when screens are paused or stopped in the background.
- **Whole-Second UI Tickers**: HUD timers poll at low frequencies and only emit UI state updates on whole-second transitions, reducing state emissions and recompositions by 90% while preserving millisecond-accurate solve durations.
- **Safe Geometry Caching**: Intermediate coordinates and path geometry reuse preallocated objects without allocating new `Path` instances inside high-frequency pointer drag loops.
- **Non-Destructive Room Indexing**: Indexes added to `level_progress(worldId, isCompleted)`, `daily_challenge(isCompleted, dateKey)`, and `game_sessions(levelId, status)` speed up lookup queries without changing existing tables.
- **Server In-Memory Caching**: Bounded server-side caches for leaderboards and daily challenge rankings eliminate repeated O(N) database scans and N-account queries on every HTTP request.

## 2. Optimization Inventory
| Area | Optimization Technique | Expected Benefit | Verified Status |
|---|---|---|---|
| Startup | Lazy singleton initialization, non-blocking guest entry | Instant first interactive frame | NOT VERIFIED |
| Recomposition | `collectAsStateWithLifecycle()`, whole-second timer emission | 90% fewer recompositions on active gameplay | NOT VERIFIED |
| Board Canvas | Reusable `Path`, `CheckpointTextCache`, draw-phase animated alpha | 60/120 FPS fluid drawing with 0 canvas allocations | NOT VERIFIED |
| Touch Handling | Bounded Manhattan intermediate resolution | Zero dropped moves during rapid diagonal swipes | NOT VERIFIED |
| Asset Loading | In-memory `assetValidationCache` & `loadedPuzzleCache` | Elimination of redundant disk reads & JSON deserialization | NOT VERIFIED |
| Room DB | Migration 9 -> 10 compound indexes | Fast filtered queries on solo progress & sessions | NOT VERIFIED |
| DataStore | `.distinctUntilChanged()` on `userPreferencesFlow` | Prevents redundant downstream UI updates | NOT VERIFIED |
| WebSocket | Bounded exponential backoff (1s, 2s, 4s, max 8s, 3 attempts) | Eliminates reconnect storms on network drops | NOT VERIFIED |
| Backend | In-memory leaderboard & daily challenge ranking cache | O(1) sliced pagination instead of full table scans | NOT VERIFIED |
| Diagnostics | `PerformanceTracker` privacy-conscious metrics recorder | Zero-overhead telemetry without player gesture PII | NOT VERIFIED |

---

## 3. Resilience Performance & Resource Management (Prompt 38)
- **Bounded Error Ring Buffers**: `ZynpathDiagnostics` caps event and error storage at 100 in-memory items, preventing unbounded heap growth during prolonged sessions or repeated errors.
- **Error Presentation Deduplication**: `ErrorDeduplicator` enforces a 3000ms cooldown window on identical errors, preventing UI stutter from rapid snackbar/dialog triggers.
- **Resource Teardown on Failure**: Failed network requests, closed WebSocket connections, and interrupted sessions immediately clean up coroutines, listeners, and media streams, avoiding memory leaks.

---

## 4. Accessibility Performance Guarantees (Prompt 39)
- **Zero Solver Invocations on Focus**: Screen-reader traversal (TalkBack) across virtual cells executes in O(1) time using static `CellState` properties. The solver is never invoked during cell focus changes.
- **Allocation-Free Virtual Semantics**: Virtual cell overlays use lightweight transparent `Box` elements with predefined semantic string templates (`buildCellAccessibilityDescription`), avoiding allocations in Compose render loops.
- **Throttled Live Regions**: `LiveRegionMode.Polite` announcements are triggered only on distinct move rejection events, preventing TalkBack speech queues from overwhelming the main thread during fast dragging.


