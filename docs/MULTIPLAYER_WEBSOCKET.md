# Zynpath Multiplayer Real-Time WebSocket Transport

## 1. Overview
Real-time match signaling, countdown synchronization, opponent progress indicators, and completion notifications are transported over the Spring Boot WebSocket gateway at `/ws/multiplayer`.

---

## 2. Authentication & Authorization
- **Authentication**: Clients pass a valid bearer session token via URL query parameter `?token=<token>` or by sending an `AUTH` message immediately after connection establishment:
  ```json
  { "type": "AUTH", "token": "zyn_sess_..." }
  ```
- **Subscription Authorization**: Clients subscribe to a match using:
  ```json
  { "type": "SUBSCRIBE_MATCH", "matchId": "match_..." }
  ```
  The server verifies that the authenticated `playerId` is a registered participant of the target match. Unauthorized subscriptions return an error envelope and are dropped.

---

## 3. Versioned Event Envelope
All server-dispatched events are wrapped in a standard versioned envelope:

```json
{
  "eventId": "evt_4982a17f",
  "eventType": "PLAYER_PROGRESS",
  "schemaVersion": "1.0",
  "matchId": "match_83910248",
  "serverTimestamp": 1727352000000,
  "sequenceNumber": 42,
  "payload": {
    "playerId": "usr_91823",
    "publicZynpathId": "ZYN-8492",
    "coveredCells": 18,
    "lastCheckpoint": 4
  }
}
```

### 3.1 Event Ordering & Idempotency
- Every event within a match session carries a monotonically increasing `sequenceNumber`.
- Clients discard stale events whose sequence number is lower than the current local sequence.

---

## 4. Event Types Catalog

| Event Type | Direction | Payload Description |
| :--- | :--- | :--- |
| `MATCH_FOUND` | Server -> Client | Authoritative match snapshot when paired |
| `PLAYER_JOINED` | Server -> Client | New participant joined the lobby |
| `PLAYER_READY` | Bidirectional | Participant confirmed readiness |
| `MATCH_COUNTDOWN` | Server -> Client | 3-second synchronized countdown starting |
| `MATCH_STARTED` | Server -> Client | Gameplay is active; start timestamp stamped |
| `PLAYER_PROGRESS` | Bidirectional | Provisional covered cells & checkpoint (throttled) |
| `PLAYER_COMPLETED` | Server -> Client | Validated victory claim; finish order & time |
| `MATCH_COMPLETED` | Server -> Client | Match finalized with complete result list |
| `MATCH_CANCELLED` | Server -> Client | Match aborted before completion |
| `RECONNECT_STATE` | Server -> Client | Complete state snapshot for reconnection |
| `PRESET_REACTION` / `REACTION` | Bidirectional | Quick emoji/phrase reaction (rate limited) |
| `CLAIM_COMPLETION` | Client -> Server | Full solution path coordinates submission for dual-win validation |
| `FORFEIT` | Client -> Server | Immediate voluntary match forfeit event |
| `FRIEND_DUEL_INVITED` | Server -> Client | Real-time delivery of private duel invitation |
| `FRIEND_DUEL_ACCEPTED` | Server -> Client | Notification that invitation was accepted |
| `FRIEND_DUEL_DECLINED` | Server -> Client | Notification that invitation was declined |
| `FRIEND_DUEL_CANCELLED` | Server -> Client | Notification that invitation was cancelled |
| `FRIEND_DUEL_EXPIRED` | Server -> Client | Notification that invitation expired |
| `FRIEND_DUEL_MATCH_CREATED`| Server -> Client | Direct signal with `matchId` when duel lobby is ready |
| `REMATCH_REQUESTED` | Server -> Client | Opponent initiated post-match rematch challenge |
| `REMATCH_ACCEPTED` | Server -> Client | Mutual consent achieved; new match ID assigned |
| `REMATCH_DECLINED` | Server -> Client | Rematch was declined; return to result/hub |
| `REMATCH_EXPIRED` | Server -> Client | Rematch response window timed out |
| `MINI_LEAGUE_ROOM_CREATED` | Server -> Client | Authoritative Mini League room created |
| `MINI_LEAGUE_INVITED` | Server -> Client | Direct delivery of Mini League room invitation |
| `MINI_LEAGUE_PLAYER_JOINED` | Server -> Client | Participant entered room lobby |
| `MINI_LEAGUE_PLAYER_LEFT` | Server -> Client | Participant departed room lobby |
| `MINI_LEAGUE_HOST_CHANGED` | Server -> Client | Host transferred to earliest participant |
| `MINI_LEAGUE_READY_CHANGED`| Server -> Client | Participant ready status updated |
| `MINI_LEAGUE_ROOM_CANCELLED`| Server -> Client | Room cancelled by host or zero remaining |
| `MINI_LEAGUE_ROOM_EXPIRED` | Server -> Client | Room TTL expired |
| `MINI_LEAGUE_MATCH_CREATED`| Server -> Client | Mini League match created and ready for countdown |

---

## 5. Network Efficiency Guidelines
- **No Raw Pointer Streaming**: Individual touch coordinates and finger movement trajectories are **never** streamed over WebSocket.
- **Provisional Progress**: Only high-level milestones (`coveredCells` count, `lastCheckpoint` order) are transmitted, throttled at checkpoints and $\ge 2$ cell deltas.
- **Rate-Limited Reactions**: Preset reactions (👍, ⚡, 🔥, 🤯) are throttled to 1 per 2 seconds. Free-text chat is strictly prohibited.
- **Single Connection Reuse**: The authenticated `/ws/multiplayer` connection is reused for both presence/invitations and active match gameplay without opening duplicate sockets.


