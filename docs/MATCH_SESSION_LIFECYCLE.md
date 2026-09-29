# Zynpath Match Session Lifecycle & State Machine

## 1. Overview
Every Zynpath multiplayer match is governed by an authoritative Finite State Machine (FSM) implemented in `com.zynpath.backend.multiplayer.model.MatchState` and orchestrated by `MatchSessionService`.

---

## 2. Match States

| State | Description | Category |
| :--- | :--- | :--- |
| `CREATED` | Session initialized with backend-issued match ID and assigned puzzle | Transient |
| `WAITING_FOR_PLAYERS` | Waiting for all invited or matched players to join and subscribe | Lobby |
| `READY` | All required participants have confirmed ready | Transition |
| `COUNTDOWN` | Synchronized 3-second countdown coordinated by server scheduler | Transition |
| `ACTIVE` | Gameplay is active; path tracing permitted; provisional progress streamed | Playable |
| `COMPLETING` | At least one valid solution claim submitted; concluding remaining finishes | Playable |
| `COMPLETED` | Authoritative results finalized, finish order locked, session archived | Terminal |
| `CANCELLED` | Session cancelled by host or abort before start | Terminal |
| `EXPIRED` | Session exceeded TTL (10 minutes) without completion | Terminal |

---

## 3. Legal State Transitions

```
[CREATED]
    |
    v
[WAITING_FOR_PLAYERS] ---> [CANCELLED] (if host leaves or cancels)
    |
    v (all players ready)
[READY]
    |
    v (scheduler: startCountdown)
[COUNTDOWN] (3 seconds)
    |
    v (scheduler: startedAt stamped)
[ACTIVE]
    |
    +---> [COMPLETING] (first valid solution submitted)
    |          |
    |          v (duel concludes or mini-league finishes)
    +------> [COMPLETED]
    |
    v
[EXPIRED] (if inactive > 10 min TTL)
```

Arbitrary client-directed state changes are strictly rejected by the backend.

---

## 4. Reconnection & Disconnect Policy

### 4.1 Temporary Connection Interruption
- If a player's WebSocket disconnects, their participant status is set to `connected = false`.
- The match session is preserved in memory for the duration of the disconnect grace period.
- Rejoining within the grace period restores the session using `ReconnectionSnapshot`.

### 4.2 Authoritative Snapshot Contents
Upon reconnecting (`/matches/{matchId}/reconnect` or WS `RECONNECT_STATE`), the player receives:
- Stable `matchId` and `gameMode`.
- Current authoritative `matchState`.
- Complete `puzzle` assignment (identical grid, checkpoints, blocked edges, and SHA fingerprint).
- Participant statuses (ready, completed, solve time, finish order).
- Authoritative `startedAt` and `countdownDurationMs`.
- Authoritative `results` list if match completed.
- Monotonic sequence number for event deduplication.

### 4.3 Abandonment vs Disconnect
- A temporary disconnect does not instantly forfeit the match.
- If a player explicitly leaves the match via the UI, they forfeit their participation.
- If an active match expires (10-minute TTL), it transitions to `EXPIRED`.

---

## 5. Quick Duel Live Match Lifecycle & Forfeit Policy (Prompt 21)

### 5.1 Authoritative Start & Countdown
- Once both players confirm ready, the server transitions the match to `COUNTDOWN` and broadcasts `MATCH_COUNTDOWN(3)`.
- When the countdown finishes, the server transitions to `ACTIVE`, records `startedAt`, and broadcasts `MATCH_STARTED`.
- Moves submitted before `ACTIVE` are rejected immediately.

### 5.2 Explicit Forfeit vs Interruption
- An explicit exit triggers `POST /matches/{id}/forfeit` and WebSocket `FORFEIT`.
- The server finalizes the match immediately: the forfeiting player is marked `FORFEITED` (loss) and the remaining opponent is awarded `WINNER`.
- Temporary network drops do not count as an instant forfeit, giving the player a 30-second window to reconnect via `/matches/{id}/reconnect`.

---

## 6. Friend Duel Match Session & Rematch Lifecycle (Prompt 22)

### 6.1 Private Session Creation from Invitation
- When a recipient accepts a Friend Duel invitation, `FriendDuelService` invokes `MatchSessionService.createMatchSession(GameMode.FRIEND_DUEL, hostPlayerId, null)`.
- The recipient is added as participant 2. The match state initializes at `WAITING_FOR_PLAYERS`.
- A 20-second ready timeout is enforced. If either participant fails to mark ready, the private session is automatically cancelled.

### 6.2 Rematch Transition Flow
```
[COMPLETED MATCH #1]
       |
       v (Player A requests rematch)
[REMATCH_REQUESTED (PENDING)] ---> [REMATCH_DECLINED / EXPIRED / CANCELLED]
       |
       v (Player B accepts rematch)
[NEW MATCH SESSION #2]
       - New Match ID
       - Novel Verified Puzzle (excluding Match #1 puzzle ID)
       - WAITING_FOR_PLAYERS -> READY -> COUNTDOWN -> ACTIVE
```
- A rematch is never an in-place restart of Match #1; it creates a distinct, brand-new match session (`MATCH #2`) while archiving Match #1.

---

## 7. Mini League Room & Match Lifecycle (Prompt 23)

### 7.1 Room Creation & Lobby Phase
- Host creates room specifying capacity ($2 \le N \le 5$). The server generates an authoritative internal `roomId` and public collision-resistant 6-char `roomCode`.
- `MiniLeagueRoom` encapsulates the lobby state before match start:
  - Participants enter via room code or direct friend invitation.
  - Room state remains `WAITING_FOR_PLAYERS` until 2–5 players are present and all have set readiness to `true`.
  - Host departs: Host role transfers to earliest joined participant (`min(joinedAt)`). If 0 players remain, room cancels.

### 7.2 Match Start & 45s Finishing Window
- When start criteria are met, the host triggers start.
- Roster is frozen into `MatchSession` (`GameMode.MINI_LEAGUE`).
- 3s synchronized countdown begins, leading into `ACTIVE`.
- First player to complete with server-validated path triggers transition to `COMPLETING`.
- A 45-second finishing timer begins. All remaining players who validate paths within this window receive ranks (2nd, 3rd, 4th, 5th).
- Upon window expiration or all players finishing, the match transitions to `COMPLETED` and unfinished participants are marked `UNFINISHED`.



