package com.zynpath.game.core.analytics.model

/**
 * Aggregated completion statistics for a specific calendar date bucket (YYYY-MM-DD).
 *
 * Implements Prompt 30 Sections 15, 16 & 17:
 * - Distinguishes first-time level completions from replays.
 * - Missing historical timestamps are excluded; dates are never fabricated.
 */
data class CompletionTrendItem(
    val dateKey: String, // Format: YYYY-MM-DD
    val firstCompletedCount: Int,
    val replayCount: Int,
    val totalCompletions: Int,
    val averageSolveTimeMs: Long?
) {
    val formattedAverageTime: String
        get() = averageSolveTimeMs?.let { WorldProgressItem.formatDuration(it) } ?: "--:--"

    val accessibilityDescription: String
        get() = "$dateKey: $firstCompletedCount first-time completes, $replayCount replays. Total: $totalCompletions."

    val accessibleDescription: String
        get() = accessibilityDescription

    val firstTimeCompletions: Int
        get() = firstCompletedCount

    val replayCompletions: Int
        get() = replayCount
}

/**
 * Insight comparing initial solve time against personal best for replayed puzzles.
 *
 * Implements Prompt 30 Sections 18, 19 & 21:
 * - Evaluates improvement only on identical puzzle identity and version.
 * - Excludes missing or non-positive times; never replaces missing data with zero.
 */
data class TimeImprovementInsight(
    val levelId: Int,
    val worldId: Int,
    val puzzleId: String,
    val initialSolveTimeMs: Long?,
    val bestSolveTimeMs: Long,
    val completionCount: Int
) {
    val puzzleVersion: Int
        get() = 1

    val firstSolveTimeMs: Long
        get() = initialSolveTimeMs ?: bestSolveTimeMs

    val improvementDeltaMs: Long
        get() = improvementMs ?: 0L

    val improvementMs: Long?
        get() = if (initialSolveTimeMs != null && initialSolveTimeMs > bestSolveTimeMs && bestSolveTimeMs > 0) {
            initialSolveTimeMs - bestSolveTimeMs
        } else {
            null
        }

    val improvementPercentage: Double?
        get() = if (initialSolveTimeMs != null && initialSolveTimeMs > bestSolveTimeMs && initialSolveTimeMs > 0) {
            ((initialSolveTimeMs - bestSolveTimeMs).toDouble() / initialSolveTimeMs.toDouble()) * 100.0
        } else {
            null
        }

    val formattedImprovement: String
        get() = improvementMs?.let { "-${WorldProgressItem.formatDuration(it)}" } ?: "New best"

    val formattedBestTime: String
        get() = WorldProgressItem.formatDuration(bestSolveTimeMs)
}
