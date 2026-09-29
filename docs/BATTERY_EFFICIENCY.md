# Battery Efficiency & Background Work

## 1. Battery Principles
Zynpath minimizes power consumption during both active gameplay and background states:
- **No Background Animations or Tickers**: When the app transitions to the background (`onStop`/`onPause`), UI coroutines and tickers pause immediately.
- **Lifecycle-Aware Collection**: StateFlow streams (active cosmetics, palette, multiplayer states) pause collection when the UI is inactive via `collectAsStateWithLifecycle()`.
- **WorkManager Constraints**: Background synchronization jobs run under strict WorkManager constraints:
  - NetworkType: `CONNECTED`.
  - BatteryNotLow: `true`.
  - Exponential backoff policy for transient network failures.
- **No Unnecessary Sensors or Services**:
  - No continuous GPS or location tracking (Zynpath does not require location services).
  - No persistent partial wake locks during idle gameplay.
  - Haptic feedback is fired only on logical discrete cell steps, never continuously on raw pointer move samples.
- **Connection Idling**:
  - WebSocket connections are established only when actively in multiplayer lobbies or duels; connections terminate immediately when the player leaves the multiplayer experience.
