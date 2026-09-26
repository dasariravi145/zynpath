# Zynpath Multiplayer & Game Modes Architecture

**Status:** Authoritative  
**Domain:** Real-Time Multiplayer, Matchmaking, and Server Authority  

---

## 1. Game Modes Specification

Zynpath unifies a single core logic mechanic across five distinct player experiences:

| Mode | Players | Connectivity | Account Requirement | Description |
|---|---|---|---|---|
| **Solo Play** | 1 | 100% Offline | None (Guest-First) | Base campaign across Worlds 1–6 (300 levels), hints, undo, local progress. |
| **Quick Duel** | 2 (1v1) | Online (WebSocket) | Authenticated | Automatic matchmaking; both players receive identical seed; first to solve wins. |
| **Friend Duel** | 2 (1v1) | Online (WebSocket) | Authenticated | Private room code or direct friend invite; identical seed; custom round rules. |
| **Mini League** | 2–5 | Online (WebSocket) | Authenticated | Private party room; multi-round tournament (3–5 rounds); cumulative point standings. |
| **Daily Challenge** | Asynchronous | Online Sync / Offline Play | Guest: Local Solve; Auth: Leaderboard | Globally uniform daily puzzle; 24h reset; speed rankings. |

---

## 2. Real-Time WebSocket Protocol

Multiplayer interactions are governed by a lightweight JSON message protocol transmitted over secure WebSockets (`wss://api.zynpath.com/ws`):

```
Client ──[ WSS / JSON ]──> Spring Boot Server (RoomSessionManager)
```

### 2.1 Core Protocol Frames
- `MATCH_JOIN`: Player enters queue (specifying mode and rating bracket).
- `ROOM_CREATED`: Server emits room metadata, opponent info, and synchronized countdown timer.
- `PUZZLE_INIT`: Dispatches the common puzzle payload (grid size, checkpoints, walls, seed).
- `PROGRESS_UPDATE`: Coarse progress broadcast (`coveragePercent: Int, nextCheckpoint: Int`). **Raw finger coordinates are strictly forbidden.**
- `REACTION_SEND`: Ephemeral preset reaction payload (`reactionId: String`).
- `CLAIM_WIN`: Solution verification payload containing the ordered list of coordinates `[Coordinate]`.
- `MATCH_OVER`: Final authoritative standings, completion times, and MMR/points delta.

---

## 3. Mini League Tournament Architecture (2–5 Players)

Mini Leagues enable competitive party play without complex server infrastructure:

```
[Create Room] ──> [2-5 Players Join via Code] ──> [Host Starts Tournament]
                                                         │
       ┌─────────────────────────────────────────────────┘
       ▼
[Round 1 / N] ──> [Simultaneous Independent Solving] ──> [Live Progress Indicators]
       │                                                         │
       ▼                                                         ▼
[Round Completed] <── [Server Validates Finishes & Awards Points (1st: 10pts, 2nd: 7pts...)]
       │
       ▼ (If Rounds Remaining)
[Next Round: New Seed]
       │
       ▼ (All Rounds Finished)
[Final League Podiums & Match Dissolution]
```

### 3.1 Point Distribution Matrix
- 1st Place: 10 Points
- 2nd Place: 7 Points
- 3rd Place: 5 Points
- 4th Place: 3 Points
- 5th Place: 1 Point
- Incomplete / DNF: 0 Points

---

## 4. Authoritative Server-Side Win Verification

To guarantee complete competitive fairness and eliminate client-side cheating (memory editing or falsified victory packets):

1. **Submission**: The client sends the complete path:
   ```json
   {
     "roomId": "room-8841-f92a",
     "playerId": "usr_9912",
     "solveDurationMs": 28450,
     "path": [
       {"row": 0, "col": 0},
       {"row": 0, "col": 1},
       {"row": 1, "col": 1}
     ]
   }
   ```
2. **Server Execution**:
   - The Spring Boot backend instantiates the headless `MovementValidator`, `CheckpointValidator`, and `CoverageValidator`.
   - It iterates through the coordinate list against the stored room `PuzzleGrid`:
     - Checks start cell == checkpoint 1.
     - Confirms every step is orthogonal and unobstructed by walls.
     - Confirms no cell coordinate repeats.
     - Confirms all checkpoints are visited in strictly ascending order.
     - Confirms `path.size == totalRequiredCells`.
3. **Verdict**:
   - If **100% valid**: The timestamp is recorded, the win is broadcast, and the opponent has a 10-second grace window to finish for runner-up placement.
   - If **invalid**: The claim is rejected, penalizing the claimant with a 5-second freeze penalty.

---

## 5. Network Resiliency & Disconnection Handling

- **Heartbeat Ping/Pong**: 15-second interval keeps connections alive and detects zombie drops.
- **Grace Period**: If a player disconnects during a Duel, a 15-second reconnection grace period is granted. If the player reconnects with the same session token, match state is restored.
- **Forfeit Logic**: If grace expires without reconnection, the remaining player is awarded an uncontested victory.
