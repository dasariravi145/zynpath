package com.zynpath.game.core.notification.model

/**
 * Standard notification event categories.
 *
 * Implements Prompt 31 Section 6:
 * - Stable identifiers matching backend event categories.
 */
enum class NotificationEventType {
    FRIEND_REQUEST,
    FRIEND_REQUEST_ACCEPTED,
    FRIEND_DUEL_INVITATION,
    FRIEND_DUEL_INVITATION_ACCEPTED,
    FRIEND_DUEL_INVITATION_DECLINED,
    MINI_LEAGUE_INVITATION,
    MINI_LEAGUE_READY,
    MATCH_STARTING,
    MATCH_RESULT,
    DAILY_CHALLENGE_REMINDER
}
