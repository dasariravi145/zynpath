# Zynpath API Contracts

## 1. API Conventions
- **Base URI Prefix**: `/api/v1`
- **Format**: JSON (`application/json;charset=UTF-8`)
- **Error Standard**: RFC 7807 problem details with error codes, descriptions, timestamps, and tracking request IDs.
- **Safety**: Stack traces and raw internal error strings are never returned to clients.

---

## 2. Health Endpoint

### `GET /api/v1/health`
Used by local diagnostics, load balancers, and container orchestrators to probe service liveness.

#### Request
- **Headers**: `Accept: application/json`
- **Auth**: None (Public)

#### Response (200 OK)
```json
{
  "status": "UP",
  "service": "Zynpath Backend",
  "version": "1.0.0-SNAPSHOT",
  "timestamp": 1727334200000,
  "environment": "dev"
}
```

---

## 3. Standard Error Envelope

When any endpoint encounters an error (e.g. 400 Bad Request, 404 Not Found, 422 Unprocessable Entity, 500 Internal Server Error):

```json
{
  "code": "VALIDATION_FAILED",
  "message": "Required path fields are missing or invalid.",
  "timestamp": 1727334200000,
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "details": [
    "path: must not be empty",
    "matchId: must be a valid UUID"
  ]
}
```

---

## 4. Competitive Validation Contract (Draft Specification)

### `POST /api/v1/puzzle/validate`
Submitted at the conclusion of a competitive Quick Duel, Friend Duel, or Mini League round.

#### Request Payload (`CompetitiveValidationClaim`)
```json
{
  "matchId": "c4b13a7e-4001-447a-9721-6b22f00a5201",
  "playerId": "player_8f39b1a0",
  "puzzleId": "world_1_level_5",
  "puzzleVersion": 1,
  "path": [
    {"row": 0, "column": 0},
    {"row": 0, "column": 1},
    {"row": 1, "column": 1},
    {"row": 1, "column": 0}
  ],
  "completionTimeMs": 14250,
  "submissionId": "sub_99a81e3c"
}
```

#### Validation Rules Enforced Server-Side:
1. **Match Membership**: `playerId` must belong to `matchId` and match must be in `ACTIVE` state.
2. **Puzzle Identity**: `puzzleId` and `puzzleVersion` must match the seed distributed to both players.
3. **Origin & Ascending Checkpoints**: The path must visit checkpoint 1 first, then checkpoint 2, through checkpoint $N$ in strict numerical sequence.
4. **Orthogonal Adjacency**: Consecutive path cells $(r_i, c_i) \rightarrow (r_{i+1}, c_{i+1})$ must satisfy $|r_{i+1} - r_i| + |c_{i+1} - c_i| = 1$. No diagonals.
5. **Wall & Edge Obstacles**: Path must not cross blocked edges (walls) or grid boundaries.
6. **No Revisited Cells**: Every coordinate in `path` must be unique (no self-intersections).
7. **Complete Required Coverage**: Number of unique visited cells must equal total required playable cells ($R$).
8. **Final Checkpoint Termination**: The final coordinate in `path` must be the final checkpoint $N$.
9. **Dual Win Enforcement**: Reaching final checkpoint with $< 100\%$ cell coverage is rejected. Covering all cells in wrong checkpoint order is rejected. Both conditions must strictly pass.

#### Response (200 OK - Valid Victory)
```json
{
  "isValid": true,
  "verifiedTimeMs": 14250,
  "checkpointsVisited": 3,
  "cellsCovered": 16,
  "totalRequiredCells": 16,
  "message": "Puzzle path valid. Victory confirmed."
}
```

#### Response (422 Unprocessable Entity - Invalid Submission)
```json
{
  "code": "INVALID_PUZZLE_SOLUTION",
  "message": "Submitted path failed authoritative rules: missing 2 required cells.",
  "timestamp": 1727334200000,
  "requestId": "b201a0ef-11c4-42b8-912a-0012e841aa91",
  "details": [
    "Coverage 14/16: Reached final checkpoint before covering all required cells."
  ]
}
```

---

## 5. Ephemeral Reaction Event Contract (Draft Specification)

### WebSocket Frame: `/topic/match/{matchId}/reactions`
Predefined reactions only. Arbitrary text strings are rejected.

#### Reaction Types:
- `WOW` (😮 "Wow!")
- `NICE` (👍 "Nice!")
- `GG` (🤝 "GG!")
- `WELL_PLAYED` (👏 "Well played!")
- `GOOD_LUCK` (🍀 "Good luck!")
- `AMAZING` (🔥 "Amazing!")
- `REMATCH` (🔄 "Rematch!")

#### Event Payload:
```json
{
  "matchId": "c4b13a7e-4001-447a-9721-6b22f00a5201",
  "senderId": "player_8f39b1a0",
  "reaction": "GG",
  "timestamp": 1727334215000
}
```
Reactions are held in server memory for active session participants, rate-limited to 1 reaction per 3 seconds per player, and discarded upon session termination.
