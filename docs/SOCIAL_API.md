# Zynpath Social REST & WebSocket API Specification

## Base URLs
- Local Development: `http://10.0.2.2:8080/api/v1` (Android Emulator) / `http://localhost:8080/api/v1`
- Production: `https://api.zynpath.com/api/v1`
- WebSocket Endpoint: `ws://10.0.2.2:8080/ws/presence` / `wss://api.zynpath.com/ws/presence`

---

## Authentication
All social endpoints require a valid session bearer token in the `Authorization` header:
```http
Authorization: Bearer <sessionToken>
```

---

## REST Endpoints

### 1. Public Profile & Player Discovery
- **`GET /api/v1/social/me`**
  - Returns: `PublicPlayerProfile` of the authenticated caller.
- **`GET /api/v1/social/players/search?publicId={publicZynpathId}`**
  - Returns: `PublicPlayerProfile` with relationship status and presence masked according to visibility rules.
  - Errors: `404 Not Found` if the player does not exist.

### 2. Friends List & Mutual Friendship
- **`GET /api/v1/social/friends`**
  - Returns: Array of `FriendSummary` for all established friends.
- **`DELETE /api/v1/social/friends/{friendPlayerId}`**
  - Removes the mutual friendship between the caller and the target friend.
  - Returns: `{"status": "REMOVED"}`

### 3. Friend Requests
- **`GET /api/v1/social/friends/requests/incoming`**
  - Returns: Array of `FriendRequestSummary` for pending incoming requests.
- **`GET /api/v1/social/friends/requests/outgoing`**
  - Returns: Array of `FriendRequestSummary` for pending outgoing requests.
- **`POST /api/v1/social/friends/requests`**
  - Body: `{"targetPublicZynpathId": "ZYN-XXXX-YYYY"}`
  - Returns: `FriendRequest`
  - Errors: `400 Bad Request` (Self request), `404 Not Found` (Unknown player), `409 Conflict` (Already friends or request pending), `403 Forbidden` (Blocked).
- **`POST /api/v1/social/friends/requests/{requestId}/accept`**
  - Accepts pending incoming request and creates mutual friendship.
- **`POST /api/v1/social/friends/requests/{requestId}/reject`**
  - Rejects pending incoming request.
- **`POST /api/v1/social/friends/requests/{requestId}/cancel`**
  - Cancels pending outgoing request.

### 4. Player Blocking
- **`POST /api/v1/social/blocks/{targetPlayerId}`**
  - Blocks target player from sending requests or multiplayer invites.
  - Returns: `{"status": "BLOCKED"}`
- **`DELETE /api/v1/social/blocks/{targetPlayerId}`**
  - Unblocks target player.
  - Returns: `{"status": "UNBLOCKED"}`

### 5. Presence & Heartbeats
- **`POST /api/v1/social/presence/heartbeat`**
  - Body: `{"state": "ONLINE" | "AWAY"}`
  - Updates ephemeral presence lease.
- **`GET /api/v1/social/presence`**
  - Returns: Array of `{"playerId":"...", "publicZynpathId":"...", "presence":"ONLINE" | "AWAY" | "OFFLINE"}` for all accepted friends.

### 6. Multiplayer Room Invitations (Prompt 19 Section 36)
- **`POST /api/v1/social/invitations/multiplayer`**
  - Body: `{"recipientPlayerId":"...", "gameMode":"FRIEND_DUEL", "roomId":"..."}`
  - Validates friendship and returns `MultiplayerInvitation`.
