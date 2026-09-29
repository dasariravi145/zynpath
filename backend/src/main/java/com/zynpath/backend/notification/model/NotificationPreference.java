package com.zynpath.backend.notification.model;

/**
 * Player notification preferences configuration.
 *
 * Implements Prompt 31 Section 33 & 50:
 * - Friend alerts, multiplayer alerts, daily reminders.
 * - Daily reminder disabled by default to avoid unsolicited spam.
 */
public record NotificationPreference(
        String playerId,
        boolean friendAlertsEnabled,
        boolean multiplayerAlertsEnabled,
        boolean dailyReminderEnabled,
        int dailyReminderHour,
        int dailyReminderMinute
) {
    public static NotificationPreference createDefault(String playerId) {
        return new NotificationPreference(
                playerId,
                true,   // friend alerts enabled by default
                true,   // multiplayer alerts enabled by default
                false,  // daily challenge reminder OFF by default (user opt-in)
                9,      // 09:00 default
                0
        );
    }
}
