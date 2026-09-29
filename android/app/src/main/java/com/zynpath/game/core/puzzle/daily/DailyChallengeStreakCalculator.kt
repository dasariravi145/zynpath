package com.zynpath.game.core.puzzle.daily

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Result of local daily streak calculation.
 */
data class DailyStreakResult(
    val currentStreak: Int,
    val maxStreak: Int,
    val isTodayCompleted: Boolean
)

/**
 * Authoritative, pure Kotlin streak calculator.
 *
 * Implements Prompt 16 Section 30:
 * A streak requires validated completion on strictly consecutive UTC calendar dates.
 * - Does not count merely opening a challenge or unfinished attempts.
 * - If today's challenge is not yet completed, a streak is preserved if yesterday was completed.
 * - If neither today nor yesterday was completed, the active streak resets to 0.
 */
object DailyChallengeStreakCalculator {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Calculates the active and historical maximum streak.
     *
     * @param completedDateKeys Collection of date keys in "YYYY-MM-DD" format where completion was validated.
     * @param referenceUtcDate Current UTC date to evaluate the streak against.
     */
    fun calculateStreak(
        completedDateKeys: Set<String>,
        referenceUtcDate: LocalDate
    ): DailyStreakResult {
        if (completedDateKeys.isEmpty()) {
            return DailyStreakResult(currentStreak = 0, maxStreak = 0, isTodayCompleted = false)
        }

        val dates = completedDateKeys.mapNotNull { key ->
            try {
                LocalDate.parse(key, formatter)
            } catch (_: Exception) {
                null
            }
        }.toSet()

        if (dates.isEmpty()) {
            return DailyStreakResult(currentStreak = 0, maxStreak = 0, isTodayCompleted = false)
        }

        val isTodayCompleted = dates.contains(referenceUtcDate)
        val isYesterdayCompleted = dates.contains(referenceUtcDate.minusDays(1))

        // Calculate current active streak
        var currentStreak = 0
        if (isTodayCompleted) {
            currentStreak = 1
            var cursor = referenceUtcDate.minusDays(1)
            while (dates.contains(cursor)) {
                currentStreak++
                cursor = cursor.minusDays(1)
            }
        } else if (isYesterdayCompleted) {
            currentStreak = 1
            var cursor = referenceUtcDate.minusDays(2)
            while (dates.contains(cursor)) {
                currentStreak++
                cursor = cursor.minusDays(1)
            }
        }

        // Calculate all-time max streak across historical completions
        val sortedDates = dates.sorted()
        var maxStreak = 0
        var tempStreak = 0
        var prevDate: LocalDate? = null

        for (date in sortedDates) {
            if (prevDate == null) {
                tempStreak = 1
            } else if (date == prevDate.plusDays(1)) {
                tempStreak++
            } else if (date != prevDate) {
                tempStreak = 1
            }
            if (tempStreak > maxStreak) {
                maxStreak = tempStreak
            }
            prevDate = date
        }

        return DailyStreakResult(
            currentStreak = currentStreak,
            maxStreak = maxOf(maxStreak, currentStreak),
            isTodayCompleted = isTodayCompleted
        )
    }
}
