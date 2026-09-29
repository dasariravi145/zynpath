# Zynpath Player Presence Architecture

## Overview
Zynpath implements an authenticated, lightweight, ephemeral presence architecture to indicate player activity without persistent disk thrashing or unnecessary battery drain.

---

## 1. Presence States
- `ONLINE`: The player is actively in the application, authenticated, and has reported server activity within the last 60 seconds.
- `AWAY`: The player's application has moved to the background, but remains within the 120-second lease window.
- `OFFLINE`: The player has explicitly disconnected, signed out, or their presence lease has elapsed.
- `UNKNOWN`: The presence state cannot be determined reliably, or the requesting player is not permitted to view it.

---

## 2. Server Authority & Ephemeral Leases
- **No Client Spoofing:** Presence state is determined server-side from active WebSocket sessions or authenticated HTTP heartbeats.
- **Lease Durations:**
  - `ONLINE_LEASE_MS`: 60,000 ms (1 minute).
  - `AWAY_LEASE_MS`: 120,000 ms (2 minutes).
- **In-Memory Storage:** To prevent high write volume on primary storage, presence records are stored ephemerally in `ConcurrentHashMap` within `PresenceService`.
- **Disconnect Cleanup:** When a WebSocket session closes or encounters a transport error, `PresenceWebSocketHandler` immediately flags the player as `OFFLINE`.

---

## 3. Privacy Policy
- **Friend-Only Visibility:** A player's presence state is exposed **only** to accepted mutual friends.
- **Unrelated Users:** When an arbitrary user searches for a public profile via Public Zynpath ID, the returned presence state is always masked to `UNKNOWN`.
- **No Location or History:** Presence only discloses the high-level state (`ONLINE`, `AWAY`, `OFFLINE`). Precise location, device type, and gameplay telemetry are never broadcast.

---

## 4. WebSocket & HTTP Fallback
- **WebSocket Endpoint:** `/ws/presence` with session token authentication via query parameter `?token=...` or initial `AUTH` payload.
- **HTTP Heartbeat Endpoint:** `POST /api/v1/social/presence/heartbeat` with `{"state":"ONLINE" | "AWAY"}`.
- **Client Lifecycle:** The Android app utilizes `LifecycleEventObserver` on `LocalLifecycleOwner`:
  - `ON_RESUME`: Reports `ONLINE`.
  - `ON_PAUSE`: Reports `AWAY`.
  - Offline/Disconnected: Fallback to cached or offline status.

---

## 5. Multiplayer Match Presence Integration (Prompt 20)
- **Multiplayer vs Social Presence**: Social presence (`/ws/presence`) reflects general app engagement. Active match presence is tracked independently within `MatchSession` (`isConnected` boolean and `lastActiveAt` timestamp).
- **Match Reconnection**: Disconnecting from `/ws/multiplayer` triggers a match-level grace period rather than instantly forfeiting the player, enabling seamless rejoining via `ReconnectionSnapshot`.

