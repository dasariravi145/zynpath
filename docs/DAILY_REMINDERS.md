# Zynpath: Daily Challenge Reminders & Local Scheduling

## Overview
Daily Challenge reminders provide players with an optional, local reminder when a new daily puzzle is available.

Zynpath adheres to a strict policy:
- Reminders are strictly opt-in (`dailyReminderEnabled = false` by default).
- Zero server polling is used; scheduling is 100% local to the device.
- Reminders are automatically suppressed if today's challenge has already been completed.
- Daily Challenge identity remains globally canonical using UTC (`DailyChallengeClock`).

---

## 1. Scheduling Policy: `AlarmManagerDailyReminderScheduler.kt`

- **Mechanism**:
  - Uses `AlarmManager.setInexactRepeating()` with `AlarmManager.INTERVAL_DAY`.
  - Avoids requiring invasive exact-alarm privileges (`SCHEDULE_EXACT_ALARM`) for non-critical game reminders.
  - Aligns delivery with platform battery optimization standards (Doze mode friendly).
- **Timezone Handling**:
  - Scheduled using the user's **current local device timezone** (e.g. 9:00 AM local time).
  - Daily challenge puzzle identity uses the shared UTC date string (e.g. `2026-09-27`), ensuring all players globally solve the same canonical puzzle regardless of local time.

---

## 2. Redundancy Suppression & Smart Deduplication

In `DailyChallengeReminderReceiver.kt`:
1. When the alarm triggers, it checks `DailyChallengeDao.getDailyChallenge(todayUtcKey)`.
2. If `dailyChallenge.isCompleted == true`:
   - The receiver logs `"Today's Daily Challenge is already completed locally; suppressing reminder."`
   - No system notification is posted.
   - No in-app notification record is inserted.
3. If not completed:
   - Posts a clean notification via `CHANNEL_DAILY_REMINDERS`.
   - Records an in-app notification record.
   - Provides an action that deep-links directly into `daily`.

---

## 3. Disabling & Rescheduling

- **Disabling (`cancelDailyReminder`)**:
  - Toggling off Daily Reminders cancels the pending `PendingIntent` in `AlarmManager`.
  - Existing Daily Challenge puzzle progress and streaks are fully preserved.
- **Rescheduling**:
  - When the user changes their preferred reminder time, the scheduler cancels the existing alarm and registers the new timestamp.

---

## 4. Device Reboot Restoration: `BootCompletedReceiver.kt`

- Registered in `AndroidManifest.xml` with `RECEIVE_BOOT_COMPLETED` and `ACTION_MY_PACKAGE_REPLACED`.
- Upon system startup, `BootCompletedReceiver` queries `PreferencesRepository`.
- If `isDailyReminderEnabled == true`, it immediately reschedules the repeating alarm.
- Does not run a persistent background service, keeping battery consumption to zero.
