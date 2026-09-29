# Zynpath: Mini League Room Management & Lobby Lifecycle

## Room Architecture

### Room Model
A Mini League room is identified by two tokens:
1. `roomId`: An authoritative internal UUID used for backend persistence, WebSocket session channels, and state reconciliation.
2. `roomCode`: A 6-character, human-readable, collision-resistant code drawn from an unambiguous alphanumeric alphabet (`[A-HJ-NP-Z2-9]`, excluding 0, O, 1, I).

### Room States
```
       +-----------------------+
       |        CREATED        |
       +-----------+-----------+
                   |
                   v
       +-----------------------+   Host Leaves (0 players)
       |  WAITING_FOR_PLAYERS  | -------------------------> CANCELLED
       +-----------+-----------+
                   |
     (2-5 players all ready)
                   |
                   v
       +-----------------------+
       |         READY         |
       +-----------+-----------+
                   |
         (Host triggers start)
                   |
                   v
       +-----------------------+
       |       COUNTDOWN       | (Authoritative 3s)
       +-----------+-----------+
                   |
                   v
       +-----------------------+
       |        ACTIVE         |
       +-----------+-----------+
                   | (First player finishes)
                   v
       +-----------------------+
       |      COMPLETING       | (45s Finishing Window)
       +-----------+-----------+
                   | (All finished or window expires)
                   v
       +-----------------------+
       |       COMPLETED       |
       +-----------------------+
```

### Concurrency & Capacity Invariants
- **Atomic Slot Reservation**: Joining a room via `joinRoomByCode` executes atomically on the server. If `currentParticipants >= maxParticipants`, the join request fails immediately with `ROOM_FULL`.
- **Duplicate Prevention**: A player cannot hold more than one slot in a room. Re-joining with an active session returns the existing room snapshot idempotently.
- **Roster Freezing**: When the host triggers match start, the participant roster is permanently frozen into the match session. Late joiners are rejected.
- **Host Transfer**: If the room host departs prior to match start:
  1. The participant list is inspected.
  2. The earliest joined participant (`min(joinedAt)`) is designated as the new host.
  3. `MINI_LEAGUE_HOST_CHANGED` is broadcast over WebSocket with `newHostPlayerId`.
  4. If zero participants remain, the room transitions to `CANCELLED`.
