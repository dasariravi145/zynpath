# Friend Duel Rematch System

## 1. Concept & Requirements

After a Friend Duel match concludes and the authoritative result is displayed, either participant can request a **Rematch**.

Key requirements:
1. **Consent Required:** A rematch is never started unilaterally. The opponent must explicitly accept the request.
2. **New Match Session:** Acceptance spawns a brand new `matchId`. The completed match and its historical results remain immutable.
3. **Novelty Puzzle Policy:** The backend puzzle pool (`MultiplayerPuzzlePool.selectPuzzleForModeExcluding`) selects an alternative verified puzzle from the verified pool, ensuring players do not immediately repeat the exact same puzzle layout. If the pool contains only one puzzle for that mode, it falls back gracefully to that puzzle without generating an unverified puzzle.
4. **Clean Decline:** Declining a rematch returns both players to the result screen without recording any competitive penalty or loss.

---

## 2. Rematch States

- **`NOT_REQUESTED`**: Initial state after match completion.
- **`PENDING`**: Player A requested rematch; awaiting Player B response (60s timer).
- **`ACCEPTED`**: Player B accepted; new match session created and dispatched.
- **`DECLINED`**: Player B declined; rematch request dismissed.
- **`CANCELLED`**: Player A withdrew the request before Player B responded.
- **`EXPIRED`**: 60s window elapsed without response.

---

## 3. Protocol & API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/multiplayer/matches/{matchId}/rematch` | Request a rematch for a completed match |
| `POST` | `/api/v1/multiplayer/matches/{matchId}/rematch/respond` | Respond to rematch (`{"accept": true/false}`) |
| `GET` | `/api/v1/multiplayer/matches/{matchId}/rematch` | Query rematch request status |

### WebSocket Events:
- `REMATCH_REQUESTED`: Payload contains `requesterPlayerId`, `expiresAt`.
- `REMATCH_ACCEPTED`: Payload contains `newMatchId`. Triggers immediate transition into the new match lobby.
- `REMATCH_DECLINED`: Notifies requester that the rematch was declined.
- `REMATCH_EXPIRED`: Informs both participants that the rematch offer timed out.
