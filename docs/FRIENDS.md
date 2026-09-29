# Zynpath: Friends System, Social Relationships & Friend Alerts

## 1. Overview & Social Model
The Friends system in Zynpath allows authenticated players to connect with peers, send mutual friendship invitations, view online presence, and challenge friends to private multiplayer duels.

For detailed architecture, see:
- [docs/FRIENDS_SYSTEM.md](file:///d:/Zynpath/docs/FRIENDS_SYSTEM.md): Mutual friendship lifecycle, invitation URLs, and privacy constraints.
- [docs/FRIEND_REQUESTS.md](file:///d:/Zynpath/docs/FRIEND_REQUESTS.md): Server-authoritative request state machine and safety rules.

---

## 2. Friend Notification Alerts (Prompt 31)

### 2.1 Domain Event Integration
All friend alerts are generated strictly from server-authoritative domain events:
1. **`FRIEND_REQUEST`**:
   - Dispatched when Player A creates a valid friend request to Player B via `SocialService.sendFriendRequest(...)`.
   - Notification payload contains sender display name, invitation ID, and deep link into `Screen.Friends`.
   - **Privacy & Block Filtering**: Prior to dispatch, `NotificationService` verifies `socialService.isBlocked(sender, recipient)`. Blocked users can never generate notifications.
2. **`FRIEND_REQUEST_ACCEPTED`**:
   - Dispatched to Player A when Player B accepts the pending friend request.
   - Idempotency protection prevents duplicate alerts if the acceptance event is replayed.

### 2.2 Rate Limiting & Cooldown
- `NotificationService` enforces a 5-second per-sender cooldown and a 5-minute idempotency window per resource ID to prevent notification spam or abuse.

### 2.3 Deep-Link Navigation & Authorization
- Friend notifications route to `zynpath://friends?invitationId=...` or `https://zynpath.com/friends`.
- Android navigation opens `FriendsScreen`, revalidating the current session. If the user is unauthenticated, they are safely directed to `SignInScreen`.

### 2.4 Account Isolation & Offline Behavior
- Friend alerts are stored locally in Room (`recipientPlayerId` column) and observed reactively based on the active player profile.
- When logging out, cached friend notifications are purged from the device.

---

## 3. Block Management & Discovery Controls (Prompt 32)
- **Active Block List**: Players can view all blocked users via Settings → Privacy & Discovery (`GET /api/v1/social/blocks`).
- **Unblock Workflow**: Users can unblock players with confirmation (`DELETE /api/v1/social/blocks/{targetPlayerId}`).
- **Server Enforcement**: Blocked players are prohibited from sending friend requests, duel invites, or Mini League room invitations.
- **Friend Request Toggle**: Players can disable incoming friend requests globally via `allow_friend_requests = false`.

---

## 4. Abuse Prevention & Authorization Hardening (Prompt 36)

- **Social Abuse Guard (`SocialAbuseGuard`)**:
  - **Self-Request Prohibition**: Players cannot send friend requests to themselves.
  - **Pending Request Cap**: Players are strictly capped at 50 active pending outgoing friend requests to prevent botting, scraping, or mass solicitation.
  - **Duplicate Prevention**: Sending a request to a user with an already pending or accepted relationship returns an idempotent or descriptive error.
  - **Block Check**: Bidirectional block validation prevents request creation if either player has blocked the other.
- **Social Endpoint Rate Limiting**: All friend invitation and social interaction routes are governed by `@RateLimited(RateLimitPolicy.SOCIAL)` (max 20 requests/minute per player).
- **Bounded Pagination**: Friend queries (`GET /api/v1/social/friends`, `GET /api/v1/social/requests/incoming`, `GET /api/v1/social/blocks`) strictly cap page size to a maximum of 100 items per response.
- **Security Audit Logging**: All relationship changes (`FRIEND_REQUEST_SENT`, `FRIEND_REQUEST_ACCEPTED`, `PLAYER_BLOCKED`) are recorded via `SecurityAuditLogger` with caller identity and correlation tracking.
