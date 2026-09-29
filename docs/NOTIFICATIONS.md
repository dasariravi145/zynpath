# Zynpath: In-App Notification Center & Notification Architecture

## Overview
Zynpath implements an offline-first, privacy-conscious notification system designed to keep players informed of social invitations, multiplayer match events, and optional Daily Challenge reminders without annoying spam or intrusive alerts.

Notifications are never a requirement to play Zynpath. Guest players retain full offline Solo puzzle access without needing notifications.

---

## 1. Notification Event Categories

All notifications use stable enum identifiers across Android and the Java Spring Boot backend:

| Event Type | Category | Description | Primary Action Destination |
|---|---|---|---|
| `FRIEND_REQUEST` | Social | Another player sent a friend request | `friends` |
| `FRIEND_REQUEST_ACCEPTED` | Social | Sent friend request was accepted | `friends` |
| `FRIEND_DUEL_INVITATION` | Multiplayer | Friend challenged user to a 1v1 duel | `friend_duel` |
| `FRIEND_DUEL_INVITATION_ACCEPTED` | Multiplayer | Friend accepted duel invitation | `friend_duel` |
| `FRIEND_DUEL_INVITATION_DECLINED` | Multiplayer | Friend declined duel invitation | `friend_duel` |
| `MINI_LEAGUE_INVITATION` | Multiplayer | Friend invited user to 3-6 player room | `mini_league` |
| `MINI_LEAGUE_READY` | Multiplayer | Mini League room reached minimum players | `mini_league` |
| `MATCH_STARTING` | Multiplayer | Authoritative match has commenced | `multiplayer_active` |
| `MATCH_RESULT` | Multiplayer | Match finalized with authoritative outcomes | `match_details/{id}` |
| `DAILY_CHALLENGE_REMINDER` | Engagement | Local reminder for today's daily puzzle | `daily` |

---

## 2. In-App Notification Center (`NotificationsScreen.kt`)

The Notification Center is accessible via the bell icon on the Home screen and through the Settings screen.

### Features:
- **Two Tab Views**:
  - **All**: Complete reverse-chronological list of active notifications.
  - **Unread**: Filtered view displaying pending unread notifications with badge count.
- **Visual Design**:
  - Distinct category icons (Group for friends, Sports for duels, Trophy for leagues & results, Calendar for daily reminders).
  - Unread visual indicator (mint accent badge) alongside non-color-only text cues (`UNREAD`).
  - Humanized relative timestamps (e.g., `Just now`, `5m ago`, `2h ago`).
  - Action button directing the user directly to the relevant gameplay screen.
  - Dismiss button (X) allowing users to clear individual items from the list.
  - Mark All as Read button in top app bar.
- **Empty States**:
  - Explanatory empty illustration and message when all alerts are read.
- **Contextual Permission Banner**:
  - Clear rationale dialog explaining benefits before requesting Android `POST_NOTIFICATIONS`.
  - Non-intrusive "Not Now" dismiss option.

---

## 3. Data Architecture & Persistence

### Android Client (Room v8)
- **Table**: `notifications`
  - `notificationId` (TEXT, PK): Unique stable ID (`notif_...`).
  - `recipientPlayerId` (TEXT): Account isolation key.
  - `eventType` (TEXT): Stable event enum string.
  - `title` (TEXT): Localized headline.
  - `message` (TEXT): Concise notification body.
  - `relatedResourceId` (TEXT, nullable): Room ID, match ID, or duel ID.
  - `actionDestination` (TEXT, nullable): Deep-link route.
  - `createdAt` (INTEGER): Timestamp in epoch milliseconds.
  - `expiresAt` (INTEGER, nullable): Expiration epoch ms for transient invites.
  - `isRead` (INTEGER): 0 = unread, 1 = read.
  - `isDismissed` (INTEGER): 0 = visible, 1 = dismissed.
- **Indexes**:
  - `index_notifications_recipientPlayerId`
  - `index_notifications_recipientPlayerId_isRead`
  - `index_notifications_createdAt`
- **Migration**: `MIGRATION_7_8` safely creates table and indexes without touching existing user data.

### Backend (`NotificationService.java`)
- In-memory concurrent storage with thread-safe collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`).
- Automatic 5-minute idempotency deduplication window for duplicate events on the same resource.
- Validates recipient account existence and checks user preference toggles prior to notification persistence or push dispatch.

---

## 4. Account Isolation & Multi-User Safety

- **Sign-Out (`onSignOut()`)**:
  - Local cached notifications for the active player are wiped from Room database (`notificationDao.clearAccountNotifications(playerId)`).
  - Push token association is deactivated on backend (`DELETE /api/v1/notifications/push-token`).
  - Prevents leaking one player's notifications or duel invitations to another player sharing the device.
- **Account Switch**:
  - On new session login, the fresh account's notifications are fetched from the server and local state is populated for that player ID only.

---

## 5. Offline Behavior

- In-app notification center functions seamlessly when offline, displaying locally cached notifications.
- Marking as read offline updates Room immediately; remote synchronization occurs when network returns.
- Free Solo gameplay remains 100% offline-first and never depends on network or notifications.

---

## 6. Notification Failure Isolation (Prompt 38)
- **Zero Gameplay Interruption**: Notification delivery, push token registration, or channel creation failures are strictly isolated within `NotificationWorker`. They never block or interrupt Solo gameplay, Daily Challenge, or multiplayer modes.
- **Preference & Permission Safeguards**: Revoked notification permissions or disabled system toggles fail silently without throwing unhandled exceptions.

---

## 7. Engagement & Achievement Notification Integration (Prompt 41)
- **In-App Celebrations Over Push Spam**: Achievement unlocks display immediate in-app banner and modal celebrations with sound and haptics. Push notifications are not sent for achievement unlocks while the user is actively playing.
- **Opt-In Daily Reminders**: Daily Challenge notifications strictly require explicit user opt-in in Player Settings and honor system notification permissions.
- **Strict Quiet Hours**: Reminders and alerts respect configured quiet hours (default: 22:00 to 08:00 local time) and are never delivered during silent windows.
- **Zero Streak Shaming Pushes**: The system never dispatches urgent or guilt-tripping push notifications warning of streak expiration.

---

## 8. Android 13+ (API 33+) Runtime Permission Compliance (Prompt 43)

- **On-Demand Runtime Request**: `android.permission.POST_NOTIFICATIONS` is requested only when a player explicitly enables notifications or Daily Challenge reminders in Settings. It is never forced at startup.
- **Graceful Refusal**: Denial of notification permission is handled smoothly. The app operates normally without warning spam, and offline Solo gameplay is completely unaffected.
- **Boot Alarm Restoration**: Local exact alarms for Daily Challenge reminders are rescheduled via `BootCompletedReceiver` using `RECEIVE_BOOT_COMPLETED` without tracking background user activity.

