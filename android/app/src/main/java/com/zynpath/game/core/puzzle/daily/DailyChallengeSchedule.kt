package com.zynpath.game.core.puzzle.daily

import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authoritative deterministic scheduler for Daily Challenges.
 *
 * Implements Prompt 16 Sections 11 & 12:
 * Maps each UTC challenge calendar date to a stable, immutable puzzle assignment.
 * Guarantees:
 * - Deterministic: The exact same (dateKey, scheduleVersion) pair always resolves to the exact same puzzle.
 * - Language-independent: Uses SHA-256 byte hashing rather than JVM-dependent String.hashCode().
 * - Verified: Only selects puzzles from [DailyChallengePool].
 */
@Singleton
class DailyChallengeSchedule @Inject constructor() {

    companion object {
        const val CURRENT_SCHEDULE_VERSION = "1.0.0"
        const val CHALLENGE_VERSION = 1
    }

    /**
     * Resolves the authoritative [DailyChallengeDefinition] for the specified [dateKey].
     *
     * @param dateKey Canonical UTC date in YYYY-MM-DD format (e.g. "2026-09-26").
     * @param scheduleVersion Version tag for the schedule table. Defaults to [CURRENT_SCHEDULE_VERSION].
     */
    fun resolveChallenge(
        dateKey: String,
        scheduleVersion: String = CURRENT_SCHEDULE_VERSION
    ): DailyChallengeDefinition {
        require(dateKey.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            "Invalid dateKey format: $dateKey. Expected YYYY-MM-DD."
        }

        val poolIndex = computeDeterministicIndex(dateKey, scheduleVersion, DailyChallengePool.size)
        val entry = DailyChallengePool[poolIndex]
        val challengeId = "daily-$dateKey-v$CHALLENGE_VERSION"

        return DailyChallengeDefinition(
            challengeId = challengeId,
            dateKey = dateKey,
            challengeVersion = CHALLENGE_VERSION,
            puzzleId = entry.definition.puzzleId,
            puzzleVersion = entry.definition.puzzleVersion,
            puzzleFingerprint = entry.fingerprint,
            scheduleVersion = scheduleVersion,
            puzzleDefinition = entry.definition,
            difficultyTier = entry.difficultyTier,
            title = entry.title
        )
    }

    /**
     * Pure, architecture-neutral deterministic hash mapping.
     */
    private fun computeDeterministicIndex(
        dateKey: String,
        scheduleVersion: String,
        poolSize: Int
    ): Int {
        val digest = MessageDigest.getInstance("SHA-256")
        val input = "$scheduleVersion:$dateKey".toByteArray(Charsets.UTF_8)
        val hash = digest.digest(input)

        // Read the first 4 bytes as a positive integer
        val intValue = ByteBuffer.wrap(hash).getInt().toLong() and 0xFFFFFFFFL
        return (intValue % poolSize.toLong()).toInt()
    }
}
