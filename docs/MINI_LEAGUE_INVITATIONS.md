# Zynpath: Mini League Invitations & Link Sharing

## Invitation Lifecycle

### Friend Invitations
- Hosts can invite mutual accepted friends to an open Mini League room via authenticated REST API (`POST /api/multiplayer/rooms/{roomId}/invite`).
- **Social Integrity & Block Policy**: Invitations require active accepted friendship. If either player has blocked the other, the request is rejected with `403 Forbidden`.
- **States**:
  - `PENDING`: Dispatched to recipient; recipient notified via WebSocket event `MINI_LEAGUE_INVITED`.
  - `ACCEPTED`: Recipient joined room; room slot consumed.
  - `DECLINED`: Recipient rejected invite.
  - `CANCELLED`: Host revoked invite or left room.
  - `EXPIRED`: Time-to-live (60s) elapsed without response.
  - `INVALIDATED`: Room filled to maximum capacity (5 players) or started before acceptance.

## Shareable Room Links & Android Sharesheet
- Hosts can share their room invitation via Android Sharesheet (`Intent.createChooser` with `ACTION_SEND`).
- **Deep Link URIs**:
  - Custom Scheme: `zynpath://minileague?code=7K2X9P`
  - Web Deep Link: `https://zynpath.com/minileague?code=7K2X9P`
- **Security & Privacy**:
  - Links contain ONLY the collision-resistant public room code.
  - Internal database IDs, access tokens, refresh tokens, and provider credentials are never embedded in shared URLs.
