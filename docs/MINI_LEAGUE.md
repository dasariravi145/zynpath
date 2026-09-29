# Zynpath: Mini League 2–5 Player Private Rooms

## Overview
Mini League provides online, private, room-based multiplayer puzzle racing for 2 to 5 total authenticated participants. One player creates and hosts the room, invites friends or distributes a collision-resistant 6-character room code, and starts the match once all participants confirm readiness.

## Core Principles
1. **2–5 Total Participants**:
   - The room host counts as 1 participant.
   - Requires at least 2 total participants to start (1 host + 1 guest).
   - Enforces a strict maximum of 5 participants (1 host + 4 guests). A 6th participant cannot join (`ROOM_FULL`).
2. **Server-Issued Verified Puzzle**:
   - Every participant receives the exact same solver-verified continuous puzzle from `MultiplayerPuzzlePool` (`GameMode.MINI_LEAGUE`).
   - Android clients strictly validate puzzle dimensions, checkpoints, and SHA-256 fingerprint before active gameplay.
3. **Real Authenticated Players Only**:
   - Zero bots, no simulated opponents, and no fake completion claims.
   - Guest players are prompted to sign in with an authenticated Zynpath account while Solo Play and Daily Challenge remain offline-accessible.
4. **Host Governance & Transfer**:
   - The host controls room configuration and triggers match start once all participants are ready.
   - If the host disconnects or leaves during the lobby phase, host ownership deterministically transfers to the earliest joined participant. If no players remain, the room cancels.
5. **Server-Authoritative Timing & Results**:
   - Finishing order (1st through 5th place) is established strictly by the authoritative backend upon server-side route validation.
   - The first valid completion triggers a 45-second finishing window for remaining participants, after which unfinished players receive explicit `UNFINISHED` / `FORFEIT` statuses.

## 6. Competitive History & Statistics Integration (Prompt 24)
- **Match Ingestion:** Every finalized Mini League room is ingested into `CompetitiveService`.
- **Match History:** Accessible via `GET /api/v1/multiplayer/history?mode=MINI_LEAGUE`. Displays participant count, player's finishing position (e.g. 1st, 2nd, etc.), outcome status (`FIRST_PLACE`, `TOP_THREE`, `COMPLETED`, `UNFINISHED`), and solve time.
- **Dedicated Metrics (No 1v1 Win/Loss Distortion):** Mini League outcomes are never compressed into simplistic 1v1 win/loss records. Tracked metrics include:
  - `miniLeagueParticipations`: Total room matches started and concluded.
  - `miniLeagueFirstPlaces`: 1st place finishes across rooms.
  - `miniLeagueTopThree`: Top 3 finishes in rooms.
  - `miniLeagueAverageFinish`: Mean finish position across completed matches.
  - Personal best solve times tagged with `MINI_LEAGUE`.
- **Leaderboards:** 1st place finishes power the `MINI_LEAGUE_WINS` leaderboard across `ALL_TIME`, `THIS_MONTH`, and `THIS_WEEK`.
- **Verified Achievements:** Unlocks `comp_mini_league_join` upon participating in the first room and `comp_mini_league_win` upon securing 1st place.

---

## 7. Mini League Notification Alerts (Prompt 31)
- **`MINI_LEAGUE_INVITATION` Alert**: When a friend invites a player into a private room, a notification alert is sent with inviter name, room code, and deep link into `mini_league`.
- **`MINI_LEAGUE_READY` & `MATCH_STARTING` Alerts**: Sent to eligible joined participants when the room reaches minimum participants or the host commences the match.
- **Room Authorization**: Opening an invitation link revalidates current room membership and active session state. If the room has concluded, canceled, or filled to capacity, a clear unavailable status is rendered.

---

## 8. Security Hardening & Room Integrity (Prompt 36)

- **Room Participant Authorization**: Enforced via `@RequireAccess(ROOM_PARTICIPANT_ONLY)` and `ResourceAuthorizationService.verifyRoomMembership`. Only verified members of the private room can view room state, confirm readiness, or receive match updates.
- **Strict Capacity & Concurrency Control**: Rooms enforce a strict cap of 2–5 players. Atomic room joining prevents concurrent race conditions from allowing a 6th player into the room.
- **Host Privilege Boundary**: Only the verified host can initiate the match start. Host actions submitted by non-host participants are rejected with `403 FORBIDDEN`.
- **WebSocket Room Authorization**: The WebSocket subscription interceptor verifies that the connecting player is an active member of the room before permitting subscriptions to `/topic/room/{roomId}`. Unauthorized subscription attempts are rejected immediately.
- **Authoritative Server Standings**: Standings and podium positions (1st through 5th place) are computed strictly by the server based on validated `ServerPuzzleValidator` submission times. Client-reported placement claims are strictly ignored.

---

## 9. Performance & Multi-Player State Optimization (Prompt 37)
- **Lifecycle-Aware State Collection**: `MiniLeagueScreen` collects UI state via `collectAsStateWithLifecycle()`, stopping unnecessary background state collection when the activity pauses.
- **WebSocket Reconnection Backoff**: Network interruptions during active league rooms trigger bounded exponential backoff reconnection (1s, 2s, 4s, max 8s, 3 attempts), auto-subscribing back to the room.
- **In-Memory Leaderboard Slicing**: Mini League 1st place victories are aggregated in `CompetitiveService.leaderboardCache`, eliminating full table scans on leaderboard requests.

---

## 10. Room & Match Recovery During Outages (Prompt 38)
- **Authoritative Room Restoration**: If a participant experiences a connection drop or process restart while in a room or active match, rejoining requests the current room and match snapshot via REST.
- **No Fabricated Participants**: Client caches never invent missing participants, alter ready states, or manipulate standings. Placements and finishing times are strictly derived from server events.
- **Finishing Window Parity**: The 45-second finishing window after 1st place finishes runs authoritatively on the backend. Client reconnects cannot prolong or reset this window.

---

## 11. Accessibility & Room Semantics (Prompt 39)
- **Room Membership & Ready State Semantics**: Every lobby participant row exposes clear textual status ("Ready", "Waiting", "Host") through TalkBack content descriptions.
- **Standings & Finish Positions**: Results podium and standings tables announce rank, player display name, and solve time explicitly without relying solely on gold/silver/bronze colors.
- **Accessible Racing Board**: In-match puzzle board supports TalkBack virtual cell grid and hardware keyboard/D-pad navigation.

---

## 12. Production Multi-Player Session Architecture & Persistence (Prompt 44)
- **Multi-Player Relational Persistence (`V3__multiplayer_sessions_and_results.sql`)**: Room matches transition to `match_sessions`, `match_participants`, and immutable `match_results` upon race conclusion. Each participant's rank, solve time, and forfeit status are recorded with database foreign key constraints.
- **In-Memory Lobby & Racing Coordination**: 2–5 player lobby state and active countdown timers run in JVM memory (`ConcurrentHashMap`), eliminating database IO overhead during rapid multi-player progress updates.
- **Single-Instance Production Baseline**: The initial production environment utilizes a single Spring Boot container. Multi-instance scaling will require Redis Pub/Sub for room event broadcast.
- **Server-Side 45s Window**: The post-1st-place 45-second timer runs server-side; clients cannot alter or extend finishing deadlines. In-flight matches conclude cleanly during the 30-second graceful shutdown window.

---

## 13. Backend Mini League Verification (Prompt 47)

### 13.1 Test Execution Summary (`MultiplayerIntegrationTest`)
- **Suite**: `com.zynpath.backend.multiplayer.MultiplayerIntegrationTest`
- **Verified Behaviors**:
  1. `miniLeague_createAndJoin_shouldEnforceCapacity`: Validates room creation by host and participant joining. Enforces 2–5 player capacity bounds and rejects a 6th player join request with `ROOM_FULL`.
  2. `miniLeague_roomLifecycle`: Verifies ready confirmations from each participant, host start authorization, synchronized 3s countdown, and shared solver-verified puzzle assignment.
  3. `miniLeague_standingsAndFinalization`: Verifies that server records 1st place finisher, triggers the 45-second window, and computes authoritative 1st–5th rankings based strictly on verified solve time.
  4. `miniLeague_hostTransfer`: Validates that if host disconnects before start, ownership transfers deterministically to the earliest joined participant.

### 13.2 Full Report Reference
- See [`docs/MULTIPLAYER_TEST_REPORT.md`](file:///d:/Zynpath/docs/MULTIPLAYER_TEST_REPORT.md) for full execution evidence.






