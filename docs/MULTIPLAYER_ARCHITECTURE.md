# Zynpath Multiplayer Architecture

## 1. Overview
The Zynpath online multiplayer architecture establishes the shared, authoritative real-time foundation across:
- **Quick Duel (1v1)**: Fast, automated matchmaking pairing two players with identical continuous-route puzzles.
- **Friend Duel (1v1)**: Direct private challenge using authenticated Public Zynpath IDs.
- **Mini League (2–5 Players)**: Dynamic multi-participant tournament rooms.

This architecture enforces **server authority**, **identical puzzle delivery**, **synchronized countdowns**, **dual-win verification**, and **guest-first offline isolation**.

---

## 2. Core Architectural Principles

### 2.1 Server Authoritative Game State
- The backend Spring Boot server manages the authoritative match lifecycle, finite state machine (FSM), participant connectivity, and victory resolution.
- The Android client receives authoritative puzzle definitions and broadcasts provisional progress (`PLAYER_PROGRESS`) for live opponent display.
- Completed solutions are independently re-verified on the server using `ServerPuzzleValidator`.

### 2.2 Shared Verified Puzzle Seed
- All participants in a match receive the **exact same** solver-verified puzzle assignment (`PuzzleAssignment`).
- Puzzles are drawn from `MultiplayerPuzzlePool`, which maintains verified continuous-path boards (4x4, 5x5, 6x6) with checkpoints (1..N) and blocked edges.
- Each assignment carries a SHA-256 fingerprint; puzzle assignments are immutable and persist across disconnects or reconnects.

### 2.3 Synchronized Countdown & Authoritative Timing
- When all participants signal `READY`, the server coordinates a synchronized 3-second countdown (`MATCH_COUNTDOWN`).
- Match start time (`startedAt`) is recorded authoritatively on the server when entering `ACTIVE`.
- Solve times are calculated as `currentTime - startedAt`, preventing client clock manipulation.

### 2.4 Competitive Hint Policy
- Hints that reveal solution steps are strictly **disabled** in competitive online multiplayer.
- Undo and Reset remain free and unlimited.

### 2.5 Offline Isolation
- Online multiplayer requires an authenticated Zynpath account session.
- Unauthenticated guest players can access the multiplayer lobby to view requirements and sign in.
- Solo Play and offline Daily Challenge operate with 100% independence from backend availability.

---

## 3. High-Level System Diagram

```
+-------------------------------------------------------------+
|                     Android Application                     |
|                                                             |
|  [MultiplayerHubScreen] <---> [MultiplayerHubViewModel]     |
|                                     |                       |
|                          [MultiplayerRepository]            |
|                           /                    \            |
|             [MultiplayerApiService]   [MultiplayerWebSocket] |
+------------------------/--------------------------\---------+
                        / (HTTPS REST)               \ (WSS)
                       v                              v
+-------------------------------------------------------------+
|                  Spring Boot Modular Monolith               |
|                                                             |
|  [MultiplayerController]            [MultiplayerWebSocket]  |
|          |                                     |            |
|  [MatchmakingService] <------------------------+            |
|          |                                                  |
|  [MatchSessionService] <---> [MultiplayerPuzzlePool]        |
|          |                                                  |
|  [ServerPuzzleValidator] (Ascending checkpoints & 100% cell)|
|          |                                                  |
|  [Durable Match & Result Persistence (schema-multiplayer)]  |
+-------------------------------------------------------------+
```

---

## 4. Component Summary

| Component | Layer | Purpose |
| :--- | :--- | :--- |
| `MatchSession` | Backend Model | Thread-safe in-memory session holding state, participants, puzzle, sequence counter, and results |
| `MatchState` | Backend Model | FSM: `CREATED`, `WAITING_FOR_PLAYERS`, `READY`, `COUNTDOWN`, `ACTIVE`, `COMPLETING`, `COMPLETED`, `CANCELLED`, `EXPIRED` |
| `MiniLeagueRoom` | Backend Model | Private 2–5 participant room with public 6-char code, host tracking, and capacity enforcement |
| `MiniLeagueInvitation` | Backend Model | Direct friend room invitation with 60s TTL |
| `MiniLeagueService` | Backend Service | Room creation, code generation, joining, host transfer, readiness, and match starting |
| `MultiplayerPuzzlePool` | Backend Service | Curated catalog of solver-verified continuous puzzles |
| `ServerPuzzleValidator` | Backend Service | Dual-win rule validator executing on server |
| `MatchmakingService` | Backend Service | Dedicated Quick Duel FIFO queue, duplicate tap suppression, block filtering |
| `FriendDuelService` | Backend Service | Authenticated 1v1 friend challenges, cross-invite auto-resolution, and rematches |
| `MatchSessionService` | Backend Service | Coordinates match lifecycle, countdowns, reconnects, claims |
| `MultiplayerWebSocketHandler`| Backend WS | Authenticated real-time gateway on `/ws/multiplayer` |
| `MultiplayerRepository` | Android Core | Coordinates network calls, socket events, room states, and client presentation states |
| `MiniLeagueScreen` | Android Feature | Material 3 UI displaying room lobby, code sharing, 2–5 player racing, and standings |
| `MultiplayerHubScreen` | Android Feature | Material 3 UI displaying mode hubs, matchmaking queues, and lobbies |

---

## 5. Security & Privacy Safeguards
1. **Bearer Session Token Verification**: Every REST endpoint and WebSocket connection validates session tokens via `SessionSecurityService`.
2. **Authorized Subscriptions**: Players can only subscribe to matches where they are registered participants.
3. **No Credential Logging**: Auth tokens and sensitive headers are excluded from standard application logs.
4. **Idempotent Results**: Multiple completion claims from the same participant cannot create duplicate results or overwrite existing winner order.
5. **Capacity Invariant Enforcement**: Mini League rooms strictly bound participants to 2–5 players. Joining the 6th participant is rejected transactionally on the server.

---

## 6. Competitive Progression & Authoritative Analytics (Prompt 24)
When matches conclude (`MatchSessionService.concludeMatch(...)`), the system feeds finalized records directly into `CompetitiveService`:
- **Finalized Match Ingestion:** Persists finalized match summaries and participant results into the competitive history log with idempotent protection (`finalizedMatchIds` set).
- **Match History API:** Supports authenticated, paginated retrieval (`/api/v1/multiplayer/history`) with game mode filtering (`QUICK_DUEL`, `FRIEND_DUEL`, `MINI_LEAGUE`).
- **Match Details API:** Restricts viewing detailed participant standings (`/api/v1/multiplayer/history/{matchId}`) strictly to registered match participants.
- **Player Statistics API:** Authoritatively aggregates personal win rates, finishes, completions, and personal best times (`/api/v1/multiplayer/stats`).
- **Server Leaderboards:** Computes deterministic rank standings (`/api/v1/multiplayer/leaderboard`) across supported categories (`QUICK_DUEL_WINS`, `MINI_LEAGUE_WINS`, `TOTAL_COMPLETIONS`) and time periods (`ALL_TIME`, `THIS_MONTH`, `THIS_WEEK`) without exposing private account details.


