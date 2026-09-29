package com.zynpath.game.core.puzzle.daily

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authoritative injectable clock abstraction for Daily Challenge date calculations.
 *
 * Implements Prompt 16 Sections 9 & 10:
 * - Canonical UTC date boundary: 00:00:00 UTC through 23:59:59.999 UTC.
 * - Computes challenge start, challenge end, and milliseconds until the next UTC reset.
 * - Decouples time calculations from Compose UI and device locale formatting.
 */
interface DailyChallengeClock {
    /** Current UTC instant. */
    fun currentUtcInstant(): Instant

    /** Current UTC calendar date. */
    fun currentUtcDate(): LocalDate

    /** Current canonical UTC date key in "YYYY-MM-DD" format. */
    fun currentUtcDateKey(): String

    /** Milliseconds remaining until the next 00:00:00 UTC challenge reset. */
    fun millisUntilNextReset(): Long

    /** Epoch millisecond timestamp of 00:00:00 UTC for [date]. */
    fun challengeWindowStartMs(date: LocalDate): Long

    /** Epoch millisecond timestamp of 23:59:59.999 UTC for [date]. */
    fun challengeWindowEndMs(date: LocalDate): Long

    /** Checks whether the given [dateKey] represents the current active UTC challenge day. */
    fun isToday(dateKey: String): Boolean
}

@Singleton
class SystemDailyChallengeClock @Inject constructor() : DailyChallengeClock {

    override fun currentUtcInstant(): Instant = Instant.now()

    override fun currentUtcDate(): LocalDate = LocalDate.now(ZoneOffset.UTC)

    override fun currentUtcDateKey(): String =
        currentUtcDate().format(DateTimeFormatter.ISO_LOCAL_DATE)

    override fun millisUntilNextReset(): Long {
        val now = currentUtcInstant()
        val tomorrowStart = currentUtcDate().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
        val millis = Duration.between(now, tomorrowStart).toMillis()
        return if (millis > 0) millis else 0L
    }

    override fun challengeWindowStartMs(date: LocalDate): Long =
        date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

    override fun challengeWindowEndMs(date: LocalDate): Long =
        date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() - 1L

    override fun isToday(dateKey: String): Boolean =
        currentUtcDateKey() == dateKey
}
