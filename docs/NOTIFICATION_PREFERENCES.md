# Zynpath: Notification Preferences & Privacy Controls

## Overview
Zynpath gives users granular control over what notifications they receive, through both client-side preferences (persisted in Jetpack DataStore) and server-side preferences synchronized across devices for authenticated accounts.

Notifications are strictly optional. No notification type is enabled without respecting player consent and privacy.

---

## 1. Configurable Preference Categories

Players can configure preferences in **Settings → Notifications & Reminders** or within the Notification Center:

1. **Friend Alerts (`friendAlertsEnabled`)**:
   - Default: `true` (for authenticated accounts).
   - Controls: Friend requests and friend request acceptance alerts.
   - Suppresses: Both in-app alerts and push notifications for social actions when disabled.

2. **Multiplayer Alerts (`multiplayerAlertsEnabled`)**:
   - Default: `true` (for authenticated accounts).
   - Controls: Friend Duel challenges, Mini League room invites, room readiness, and authoritative match conclusion results.
   - Suppresses: All multiplayer push and in-app alerts when disabled.

3. **Daily Challenge Reminder (`dailyReminderEnabled`)**:
   - Default: `false` (opt-in only; no unsolicited alerts).
   - Controls: Local daily alarm reminding player of today's Daily Challenge.
   - Suppresses: Cancels future AlarmManager scheduled work when toggled off.

4. **Preferred Reminder Time (`dailyReminderHour`, `dailyReminderMinute`)**:
   - Default: `09:00` (9:00 AM local device time).
   - Configurable hour (0-23) and minute (0-59).
   - When modified, automatically reschedules the local daily reminder to the new time.

---

## 2. Platform Permission Status

On Android 13+ (API 33+), system notifications require the runtime permission `android.permission.POST_NOTIFICATIONS`.

- **Contextual Request**:
  - The permission is never requested during first app launch or during gameplay.
  - A friendly rationale dialog explains that notifications allow duel invites and reminders before presenting the system prompt.
  - If denied, the app gracefully dismisses the prompt, persists `isSystemPermissionGranted = false`, and allows the player to continue playing without nagging or penalty.
- **In-App Independence**:
  - In-app notification center records are always maintained and viewable regardless of whether system notification permissions are granted or denied.

---

## 3. Synchronization & Persistence

- **Local Storage**: `UserPreferences.kt` via Jetpack DataStore ensures instantaneous UI updates and offline resilience.
- **Remote Synchronization**: `NotificationRepository` updates `PUT /api/v1/notifications/preferences` when online, aligning server-side push filtering with the player's choices.

---

## 4. Settings Screen Integration & Account Isolation (Prompt 32)
- **Unified Section**: Exposed inside the unified `SettingsScreen` under "NOTIFICATIONS & REMINDERS" with responsive switches, live time readout, and direct link to the Notification Center.
- **Account Sign-Out & Switching**: When signing out, push tokens are deregistered (`DELETE /api/v1/notifications/push-token`) and cached notification records are cleared to prevent notification bleed between accounts.
