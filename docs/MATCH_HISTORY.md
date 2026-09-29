# Zynpath Match History Architecture & Verification

## 1. Overview & Source of Truth

The Zynpath backend server is the authoritative source of truth for all competitive multiplayer match records. Android displays server-verified historical records and caches previously fetched results locally for offline review. Client devices are never permitted to independently invent, fabricate, or record wins, losses, ties, or completion times.

Only matches that reach an authoritative terminal state (`COMPLETED`, `FORFEITED`) via `MatchSessionService` enter the competitive match history. Cancelled pre-start rooms and abandoned invites are excluded from completed match history.

---

## 2. Supported Match History Modes

Competitive match history preserves distinct mode records and does not conflate different gameplay formats:

- **Quick Duel (`QUICK_DUEL`)**: Real-time 1v1 automated matchmaking against global opponents.
- **Friend Duel (`FRIEND_DUEL`)**: Direct 1v1 invited private match between mutual friends.
- **Mini League (`MINI_LEAGUE`)**: 2–5 player private lobby rooms where participants compete synchronously for finishing ranks.

---

## 3. Match History Data Model & Privacy

Each finalized match history record contains:
- `matchId`: Server-issued unique match identifier.
- `gameMode`: `QUICK_DUEL`, `FRIEND_DUEL`, or `MINI_LEAGUE`.
- `puzzleId`: Identifier of the assigned solver-verified puzzle.
- `startedAt` / `endedAt`: Authoritative server epoch timestamps.
- `participants`: Summaries of participants with public fields only (`publicZynpathId`, `displayName`, `avatarId`, `solveTimeMs`, `finishOrder`, `isWinner`).
- Private billing details, email addresses, and OAuth tokens are strictly excluded.

---

## 4. Bounded Pagination & Access Control

- Match history requests are paginated with deterministic ordering (`endedAt DESC`, bounded page size 1..50).
- Detailed match history retrieval requires authenticated session token.
- Object-level authorization verifies that the requesting player was a participant in the match; unauthorized requests receive HTTP 403 Forbidden.

---

## 5. Verification & Test Evidence (Prompt 47)

### 5.1 Test Suites Executed
- **MultiplayerIntegrationTest**: Verifies match finalization, forfeit recording, and participant outcome history.
- **SecurityRegressionTest**: Verifies that non-participants cannot access private match history details (`testMatchAuthorization_nonParticipant_shouldBeRejected`).
- **All tests PASSED (100%)**.

### 5.2 Related Artifacts
- Full execution evidence documented in [`docs/MULTIPLAYER_TEST_REPORT.md`](file:///d:/Zynpath/docs/MULTIPLAYER_TEST_REPORT.md) and [`docs/SECURITY_REGRESSION_REPORT.md`](file:///d:/Zynpath/docs/SECURITY_REGRESSION_REPORT.md).
