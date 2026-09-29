package com.zynpath.game.core.analytics.model

/**
 * Single day entry in the Daily Challenge calendar history.
 *
 * Implements Prompt 30 Sections 24, 26 & 27:
 * - Distinguishes local vs server-verified completions.
 * - Does not mark missing dates as completed.
 */
data class DailyCalendarEntry(
    val dateKey: String, // Format: YYYY-MM-DD
    val isCompleted: Boolean,
    val attemptCount: Int,
    val solveTimeMs: Long?,
    val bestTimeMs: Long?,
    val verificationStatus: String, // LOCAL_COMPLETION, SERVER_VERIFIED, PENDING_VERIFICATION
    val isLeaderboardEligible: Boolean
) {
    val isServerVerified: Boolean
        get() = "SERVER_VERIFIED".equals(verificationStatus, ignoreCase = true)

    val isParticipated: Boolean
        get() = attemptCount > 0 || isCompleted

    val accessibleDescription: String
        get() = accessibilityDescription

    val formattedBestTime: String
        get() = bestTimeMs?.takeIf { it > 0 }?.let { WorldProgressItem.formatDuration(it) } ?: "--:--"

    val accessibilityDescription: String
        get() = "$dateKey: " + if (isCompleted) {
            "Completed in $formattedBestTime (${if (isServerVerified) "Server Verified" else "Local Completion"})."
        } else if (attemptCount > 0) {
            "Attempted ($attemptCount attempts), not completed."
        } else {
            "Not played."
        }
}

/**
 * Aggregated analytics for Daily Challenge engagement and performance.
 */
data class DailyChallengeAnalytics(
    val totalParticipatedDays: Int,
    val totalCompletedDays: Int,
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val serverVerifiedCompletionsCount: Int,
    val localCompletionsCount: Int,
    val leaderboardEligibleCount: Int,
    val fastestSolveTimeMs: Long?,
    val averageSolveTimeMs: Long?,
    val recentHistory: List<DailyCalendarEntry>
) {
    val calendarEntries: List<DailyCalendarEntry>
        get() = recentHistory

    val currentStreak: Int
        get() = currentStreakDays

    val maxStreak: Int
        get() = bestStreakDays

    val totalParticipations: Int
        get() = totalParticipatedDays

    val serverVerifiedCompletions: Int
        get() = serverVerifiedCompletionsCount

    val completionRate: Double
        get() = if (totalParticipatedDays > 0) {
            (totalCompletedDays.toDouble() / totalParticipatedDays.toDouble()) * 100.0
        } else {
            0.0
        }

    val formattedFastestTime: String
        get() = fastestSolveTimeMs?.let { WorldProgressItem.formatDuration(it) } ?: "--:--"

    val formattedAverageTime: String
        get() = averageSolveTimeMs?.let { WorldProgressItem.formatDuration(it) } ?: "--:--"
}
