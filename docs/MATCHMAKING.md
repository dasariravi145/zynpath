# Zynpath Matchmaking Foundation

## 1. Overview
The Zynpath matchmaking architecture provides fair, secure, and authenticated opponent pairing across all competitive modes:
- **Quick Duel (1v1)**: Automated FIFO queue matching eligible players.
- **Friend Duel (1v1)**: Direct challenge between authenticated accepted friends.
- **Mini League (2–5 Players)**: Dynamic multi-player tournament room creation.

---

## 2. Quick Duel Queue Lifecycle

### 2.1 Enqueue Flow
1. **Authentication Check**: The player's bearer token is validated via `SessionSecurityService`. Unauthenticated guest accounts are rejected.
2. **Duplicate Tap Suppression**: `playerActiveTicketMap` maps `playerId -> ticketId`. If a player already has an active ticket, the existing ticket is returned, preventing multiple simultaneous entries from rapid button taps.
3. **Queue Candidate Evaluation**: The FIFO queue `quickDuelQueue` is scanned for eligible waiting tickets:
   - **Self-Match Exclusion**: A player is never paired against themselves (`candidate.playerId != playerId`).
   - **Ticket Expiration**: Tickets older than the queue timeout (45 seconds) are purged.
   - **Block Policy Exclusion**: If either player has blocked the other via `SocialService.isBlocked(...)`, the pairing is skipped.
4. **Match Creation**: When an eligible opponent is found, a `MatchSession` is created with mode `QUICK_DUEL`. Both tickets are removed from the queue, and both participants are notified via WebSocket event `MATCH_FOUND`.
5. **No Match Found**: If no waiting opponent exists, a new `QueueTicket` is created and enqueued with status `SEARCHING`.

---

## 3. Queue Cancellation & Timeout

### 3.1 Cancellation
- Players can cancel their ticket at any time while waiting in `SEARCHING`.
- `MatchmakingService.cancelTicket(playerId)` removes the ticket from `ticketsById`, `playerActiveTicketMap`, and the `quickDuelQueue`.
- The Android client transitions from `SEARCHING` back to `IDLE` and disconnects the temporary matchmaking WebSocket.

### 3.2 Bounded Timeout
- Maximum queue wait duration is bounded to **45 seconds** (`QUEUE_TIMEOUT_MS = 45_000L`).
- If no match is found within 45 seconds, the ticket expires with status `TIMEOUT`.
- **Bot Prohibition**: In accordance with Zynpath fairness guidelines, players are **never** silently placed into a fake match with a bot. The player is presented with a clear timeout message and may retry or cancel.

---

## 4. Friend Duel & Mini League Room Setup

### 4.1 Friend Duel Creation
- Host issues a challenge to a target Public Zynpath ID (e.g., `ZYN-8492`).
- Requires established mutual friendship (`SocialService.areFriends`).
- Enforces block rules in both directions.
- Transitions immediately to `WAITING_FOR_PLAYERS`.

### 4.2 Mini League Room Creation
- Total participant capacity is strictly bounded: **Minimum 2, Maximum 5**.
- The creator counts as one participant (e.g., capacity 3 means host + 2 invitees).
- The room cannot start with fewer than 2 accepted participants.

---

## 5. Quick Duel Client Matchmaking Integration (Prompt 21)

### 5.1 Client Queue State Machine
- **State Transition**: `IDLE` $\to$ `SEARCHING` $\to$ `MATCH_FOUND` $\to$ `READY` $\to$ `COUNTDOWN` $\to$ `ACTIVE`.
- **Duplicate Request Defense**: Client-side UI disables the "Find Opponent" button immediately upon initiation.
- **WebSocket Push & Polling Fallback**: The client receives instantaneous `MATCH_FOUND` events via `/ws/multiplayer` while running a low-frequency 2-second fallback poll against `GET /matchmaking/quick-duel/status`.
- **Candidate Ticket Retention**: `MatchmakingService` preserves matched ticket records with `matchedSessionId` so fallback polling reliably returns `MATCH_FOUND` with the session ID.
- **Ready Window**: Once matched, players enter a 20-second ready confirmation window before countdown initiates.

