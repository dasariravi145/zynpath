# Zynpath: Notification Security, Deep-Link Authorization & Privacy

## Overview
Notifications in Zynpath adhere to strict zero-trust and privacy-first architectural guidelines. Possession of a notification, deep-link URI, or push message never acts as an authorization token.

---

## 1. Deep-Link Authorization Revalidation

Every deep-link destination routed from a notification (`friends`, `friend_duel`, `mini_league`, `match_details/{id}`, `daily`) revalidates the active player session:
- **Authentication Check**: If the player is not authenticated when opening a multiplayer or friend link, navigation routes to the Sign-In flow (`Screen.SignIn`).
- **Resource Authorization**:
  - Mini League rooms verify participant membership on the backend. An unauthorized player attempting to access room details receives `ACCESS_DENIED` or `ROOM_NOT_FOUND`.
  - Expired invitations cannot be accepted. The client and backend display the expired state rather than reopening the session.
  - Match details verify participant access before rendering scoreboards.

---

## 2. Blocked User Filtering & Rate Limiting

- **Server-Authoritative Block Rules**:
  - Block relationships are enforced at the service level (`SocialService.isBlocked`).
  - Blocked players cannot send friend requests or duel invitations.
  - Because notifications are purely event-driven from verified domain actions, blocked users cannot trigger notification creation or push alerts.
  - Does not rely on client-side filtering alone.
- **Rate Limiting & Deduplication**:
  - Duplicate invitations or repeated actions within a 5-minute window for the same resource are deduplicated by `NotificationService`.
  - Prevents notification spam or denial-of-service through malicious client scripts.

---

## 3. Account Isolation & Token Security

- **Multi-User Devices**:
  - When Player A signs out, all local notifications are purged from Room (`notificationDao.clearAccountNotifications(playerId)`).
  - Player A's push token association is deleted from the backend server.
  - When Player B logs in on the same device, they only see notifications addressed to Player B.
- **Push Token Privacy**:
  - Push tokens are never stored in public profile tables or exposed via search APIs.
  - Server logs mask tokens (`fcm_tok_...3f8a`) to prevent token leakage in diagnostic records.
- **Lock-Screen Privacy**:
  - Lock-screen notifications use concise, non-revealing text (e.g., "Friend Duel Challenge: Player invited you to a duel").
  - Private access tokens or room keys are never included in notification titles or bodies.

---

## 4. Security Hardening & Authorization (Prompt 36)

- **Ownership & Object-Level Authorization**: All notification endpoints enforce `@RequireAccess(AUTHENTICATED)` and verify resource ownership via `ResourceAuthorizationService.verifyOwnership`. Players can only list, read, or mark as read notifications that are addressed directly to their own account identity.
- **Push Token Registration Security**: The push token endpoint (`POST /api/v1/notifications/push-token`) binds the device token strictly to the caller's verified `SecurityContext.getCurrentPlayerId()`. Untrusted client-supplied account IDs are rejected.
- **Audit Logging Token Redaction**: `SecurityAuditLogger` records push token registrations (`PUSH_TOKEN_REGISTERED`) while completely masking the raw FCM/APNS token value (`tok_...` prefix only).
- **Bounded Pagination Limits**: Notification queries enforce a hard upper bound of 100 items per page (`limit = Math.min(limit, 100)`), preventing database resource exhaustion.
