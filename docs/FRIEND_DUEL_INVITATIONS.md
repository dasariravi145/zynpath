# Friend Duel Invitations — Protocol & Race Handling

## 1. Invitation Lifecycle & Invariants

Each Friend Duel invitation is an authoritative domain entity governed by `FriendDuelService`:

```java
public record FriendDuelInvitation(
    String invitationId,
    String inviterPlayerId,
    String recipientPlayerId,
    GameMode gameMode,
    InvitationStatus status,
    long createdAt,
    long expiresAt,
    String matchId
)
```

### States:
- **`PENDING`**: Dispatched by the inviter; awaiting recipient response. Valid for 60 seconds.
- **`ACCEPTED`**: Explicitly accepted by recipient. Atomically triggers private match session creation.
- **`DECLINED`**: Recipient declined the challenge. Inviter notified; no match created.
- **`CANCELLED`**: Inviter withdrew the challenge before acceptance.
- **`EXPIRED`**: 60-second validity window elapsed without explicit acceptance.
- **`INVALIDATED`**: Terminated due to relationship changes (unfriend, block) or server shutdown.

---

## 2. Race-Condition Resolution

1. **Simultaneous Cross-Invitations:**
   If Player A invites Player B while Player B has already sent an unexpired pending invitation to Player A, `FriendDuelService.sendInvitation` auto-accepts the existing invitation, transitioning it to `ACCEPTED` and creating a single private match. Both players are routed into the same room.

2. **Acceptance vs Cancellation Race:**
   State transitions are synchronized within `FriendDuelService` using `ReentrantLock` and concurrent mappings. If an inviter calls `cancelInvitation` concurrently with a recipient's `acceptInvitation`, only the first transaction to acquire the state lock succeeds; the other returns `INVALID_STATE`.

3. **Acceptance vs Expiration:**
   When `acceptInvitation` is invoked, `invitation.isExpired()` is checked authoritatively on the backend before any match creation. Expired invitations cannot be accepted.

---

## 3. Real-Time Delivery & Offline Handling

- **WebSocket Direct Dispatch:** Invitations trigger `FRIEND_DUEL_INVITED` event envelopes dispatched directly to the recipient's authenticated socket via `MultiplayerWebSocketHandler.dispatchToPlayer`.
- **Offline Recipient:** If the recipient is currently offline, the pending invitation is stored in memory and returned when the recipient opens the app and calls `GET /api/v1/multiplayer/invitations/incoming`.

---

## 4. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/multiplayer/invitations` | Dispatch invitation (`targetPublicZynpathId`) |
| `GET` | `/api/v1/multiplayer/invitations/incoming` | List active pending incoming challenges |
| `GET` | `/api/v1/multiplayer/invitations/outgoing` | List active pending outgoing challenges |
| `GET` | `/api/v1/multiplayer/invitations/{id}` | Inspect specific invitation status |
| `POST` | `/api/v1/multiplayer/invitations/{id}/accept` | Accept challenge & obtain match snapshot |
| `POST` | `/api/v1/multiplayer/invitations/{id}/decline` | Decline challenge |
| `POST` | `/api/v1/multiplayer/invitations/{id}/cancel` | Cancel outgoing challenge |
