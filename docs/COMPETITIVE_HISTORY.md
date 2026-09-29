# Zynpath Competitive Match History

## 1. Overview & Source of Truth

The Zynpath backend server is the authoritative source of truth for all competitive multiplayer match records. Android displays server-verified historical records and caches previously fetched results locally for offline review. Client devices are never permitted to independently invent, fabricate, or record wins, losses, ties, or completion times.

Only matches that reach an authoritative terminal state (`COMPLETED`) via `MatchSessionService.concludeMatch(...)` enter the competitive match history. Cancelled pre-start rooms and abandoned invites are excluded from completed match history.

---

## 2. Supported Match History Modes

Competitive match history preserves distinct mode records and does not conflate different gameplay formats:

- **Quick Duel (`QUICK_DUEL`)**: Real-time 1v1 automated matchmaking against global opponents.
- **Friend Duel (`FRIEND_DUEL`)**: Direct 1v1 invited private match between mutual friends.
- **Mini League (`MINI_LEAGUE`)**: 2–5 player private lobby rooms where participants compete synchronously for finishing ranks.

---

## 3. Match History Data Model

Each finalized match history record contains:

| Field | Type | Description |
|---|---|---|
| `matchId` | String | Server-issued unique match identifier. |
| `gameMode` | GameMode | `QUICK_DUEL`, `FRIEND_DUEL`, or `MINI_LEAGUE`. |
| `puzzleId` | String | Identifier of the assigned solver-verified puzzle. |
| `puzzleVersion` | int | Schema version of the puzzle specification. |
| `puzzleFingerprint` | String | SHA-256 fingerprint guaranteeing identical grid geometry. |
| `gridRows` / `gridCols` | int | Dimensions of the puzzle grid (e.g., 5x5). |
| `startedAt` | long | Authoritative server epoch timestamp of match start. |
| `endedAt` | long | Authoritative server epoch timestamp of match conclusion. |
| `matchStatus` | String | Terminal state (`COMPLETED`). |
| `participantCount` | int | Total number of participating players. |
| `participants` | List | Summary of all participants, finish orders, and outcomes. |
| `myResult` | Object | The requesting player's specific validated outcome. |

---

## 4. Participant Result Model

For each participant, privacy-preserving public fields are stored:

- `playerId`: Internal UUID (used strictly for authorization checks).
- `publicZynpathId`: Formatted public player identity (e.g., `ZYN-XXXX-YYYY`).
- `displayName`: Player's public display name.
- `avatarId`: Selected public avatar icon identifier.
- `completed`: Boolean indicating whether the player submitted an authoritatively verified solution.
- `solveTimeMs`: Server-measured solve time in milliseconds (null if uncompleted).
- `finishOrder`: Verified finishing position (1 for winner in 1v1; 1..5 in Mini League).
- `isWinner`: Boolean indicating if the player took first place.
- `outcomeStatus`: Display string (`VICTORY`, `DEFEAT`, `1st Place`, `2nd Place`, `FORFEIT`, `ABANDONED`, `UNFINISHED`).

---

## 5. Bounded Pagination

Match history requests are paginated with deterministic ordering:

- **Order**: Newest finalized match first (`endedAt DESC`).
- **Page Size**: Bounded between 1 and 50 records (default: 20).
- **Pagination Strategy**: Indexed offset/page with `hasMore` indicator, preventing unbounded network payloads.
- **Deduplication**: Client repositories deduplicate records across pages using stable `matchId` keys.

---

## 6. Access Control & Authorization

- Private match history requests require session bearer authentication (`Authorization: Bearer <token>`).
- The acting player ID is authoritatively derived from the validated server session; client-provided player IDs are never trusted.
- Detailed match records (`/api/v1/multiplayer/history/{matchId}`) verify that the requesting player was an actual participant in the match session. Unauthorized requests return `403 FORBIDDEN` (`MATCH_ACCESS_DENIED`).
