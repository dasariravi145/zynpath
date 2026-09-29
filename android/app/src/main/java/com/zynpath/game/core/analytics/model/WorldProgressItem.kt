package com.zynpath.game.core.analytics.model

/**
 * Progression metrics for a specific canonical world (Worlds 1–6).
 *
 * Implements Prompt 30 Section 12 & 13:
 * - Tracks completed levels, total levels, completion percentage, and valid solve times.
 * - Missing historical times are excluded from averages and never fabricated as zero.
 */
data class WorldProgressItem(
    val worldId: Int,
    val worldName: String,
    val gridSize: Int,
    val hasWalls: Boolean,
    val completedLevels: Int,
    val totalLevels: Int,
    val totalStars: Int,
    val averageSolveTimeMs: Long?,
    val fastestSolveTimeMs: Long?,
    val isUnlocked: Boolean,
    val isFullyCompleted: Boolean
) {
    val completionPercentage: Float
        get() = if (totalLevels > 0) completedLevels.toFloat() / totalLevels.toFloat() else 0f

    val formattedAverageTime: String
        get() = averageSolveTimeMs?.let { formatDuration(it) } ?: "No timed solves"

    val formattedFastestTime: String
        get() = fastestSolveTimeMs?.let { formatDuration(it) } ?: "--:--"

    val accessibilityDescription: String
        get() = "World $worldId, $worldName: $completedLevels of $totalLevels levels completed, ${totalStars} stars earned. " +
                if (isFullyCompleted) "World complete." else if (isUnlocked) "In progress." else "Locked."

    val accessibleDescription: String
        get() = accessibilityDescription

    val isCompleted: Boolean
        get() = isFullyCompleted

    val levelRangeStart: Int
        get() = when (worldId) {
            1 -> 1
            2 -> 21
            3 -> 51
            4 -> 101
            5 -> 151
            6 -> 201
            else -> 1
        }

    val levelRangeEnd: Int
        get() = when (worldId) {
            1 -> 20
            2 -> 50
            3 -> 100
            4 -> 150
            5 -> 200
            6 -> 300
            else -> 20
        }

    companion object {
        fun formatDuration(ms: Long): String {
            if (ms <= 0) return "--:--"
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val millis = (ms % 1000) / 100
            return String.format("%02d:%02d.%d", minutes, seconds, millis)
        }
    }
}
