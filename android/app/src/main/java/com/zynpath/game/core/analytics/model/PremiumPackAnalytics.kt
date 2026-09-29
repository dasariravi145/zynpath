package com.zynpath.game.core.analytics.model

/**
 * Progression summary for a specific Premium Solo puzzle pack.
 *
 * Implements Prompt 30 Section 14:
 * - Tracks pack-specific puzzle completions and personal bests.
 * - Strictly isolated from canonical 300 free levels.
 */
data class PackProgressSummary(
    val packId: String,
    val title: String,
    val completedCount: Int,
    val totalCount: Int,
    val bestSolveTimeMs: Long?,
    val isFullyCompleted: Boolean
) {
    val packTitle: String
        get() = title

    val completedPuzzles: Int
        get() = completedCount

    val totalPuzzles: Int
        get() = totalCount

    val isCompleted: Boolean
        get() = isFullyCompleted

    val completionPercentage: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    val formattedBestSolveTime: String
        get() = bestSolveTimeMs?.let { WorldProgressItem.formatDuration(it) } ?: "--:--"
}

/**
 * Aggregated analytics for all downloaded and installed Premium Solo puzzle packs.
 */
data class PremiumPackAnalytics(
    val completedPuzzlesCount: Int,
    val totalInstalledPuzzlesCount: Int,
    val completedPacksCount: Int,
    val totalInstalledPacksCount: Int,
    val packSummaries: List<PackProgressSummary>
) {
    val completedPuzzles: Int
        get() = completedPuzzlesCount

    val totalInstalledPuzzles: Int
        get() = totalInstalledPuzzlesCount

    val totalInstalledPacks: Int
        get() = totalInstalledPacksCount

    val packs: List<PackProgressSummary>
        get() = packSummaries

    val overallCompletionPercentage: Float
        get() = if (totalInstalledPuzzlesCount > 0) {
            completedPuzzlesCount.toFloat() / totalInstalledPuzzlesCount.toFloat()
        } else {
            0f
        }
}
