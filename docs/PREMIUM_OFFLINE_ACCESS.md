# Premium Offline Access & Cache Policy

## Overview

Zynpath is designed from the ground up as an offline-first puzzle game. While online connectivity is required for purchase verification and initial pack download, **previously authorized Premium content remains fully playable offline**.

---

## Bounded Offline Entitlement Cache

Offline access to Premium Solo content is governed by the authoritative entitlement cache established in Prompt 26:

1. **Local Storage**: The client caches the active entitlement in an internal, private file (`zyn_entitlement_cache.json`).
2. **Bounded Lifetime**: The cache enforces a maximum offline validity window (default: 30 days from last successful server verification).
3. **Integrity Validation**: The cached record contains an HMAC/hash digest verifying account binding and preventing manual tampering.
4. **Known Expiry**: If `currentPeriodEndMs` has elapsed, the cache marks status as `EXPIRED` even if offline, preventing indefinite free access.
5. **Periodic Refresh**: Whenever the device is online and network connectivity is restored, the client silently queries `/api/v1/subscription/entitlement` to refresh the entitlement and extend the offline cache window.

---

## Offline Gameplay Experience

- **Zero Network Interruptions During Gameplay**: No network calls or heartbeat checks occur during path drawing, movement, undo, or timer ticks.
- **Local Progress Persistence**: Level completions and best times are recorded directly to Room (`premium_pack_progress`), ensuring zero progress is lost even if the player never connects to the internet again.
- **Solver Verification & Hints**: Both puzzle validation and the `PuzzleHintEngine` execute 100% locally on the device using Kotlin coroutines on background dispatchers, requiring no internet connection for solver hints.

---

## Storage & Content Management

- **Storage Isolation**: Premium packs reside in app-private storage (`context.filesDir/premium_packs/<packId>`).
- **Safe Cleanup**: Players may choose to clear downloaded pack assets to free storage space.
- **Progress Preservation**: Deleting local pack files **never** deletes Room completion records or personal best times.
- **Active Session Protection**: Pack deletion is blocked while an active gameplay session is in progress.
