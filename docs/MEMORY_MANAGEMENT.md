# Memory Management

## 1. Objectives & Guidelines
Zynpath operates within strict memory bounds to prevent `OutOfMemoryError` (OOM) on entry-level devices with 2GB–3GB RAM:
- Bounded in-memory collections and caches with clear eviction and lifecycle invalidation.
- Structured coroutine scopes tied to Android component lifecycles (`viewModelScope`, lifecycle-aware composables).
- Avoiding memory leaks by decoupling background sync and persistent storage operations from transient Activity/Composable contexts.
- Immediate reclamation of heavy graphical resources, audio players, and network connections upon exit.

## 2. Allocation Safeguards
- **Compose Canvas Allocations**:
  - `PuzzleBoard` reuses preallocated `Path` instances (`sharedPath`, `sharedDiamondPath`) to prevent creating thousands of short-lived heap allocations during active path drags.
  - Text layout measurements for checkpoints are memoized in `CheckpointTextCache`, eliminating `TextMeasurer.measure` calls every frame.
  - Stroke and CornerRadius geometry objects are created with `remember(density)`.
- **Level Catalog Caching**:
  - Packaged puzzle definitions are loaded once into `loadedPuzzleCache` and validation status into `assetValidationCache`.
  - Raw JSON strings are not retained in memory after parsing.
- **WebSocket & Multiplayer Lifecycle**:
  - `MultiplayerWebSocketClient` releases OkHttpClient WebSocket instances upon match exit or screen navigation.
  - Reconnect jobs and timeout counters are cancelled and nulled upon `disconnect()`.
- **Audio Lifecycle**:
  - Sound effects share a centralized `SoundPool` instance (Prompt 34) with bounded max streams (6 streams), preventing per-sound MediaPlayer creation and resource exhaustion.
- **Backend Memory Bounds**:
  - In-memory historical sessions and leaderboards use bounded caches (`MAX_SAMPLES = 50`, max page size = 50).
  - Stale attempt sessions in `DailyChallengeService` expire after a 2-hour TTL.
