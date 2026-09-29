package com.zynpath.game.core.analytics.model

/**
 * 1v1 competitive mode performance summary (Quick Duel or Friend Duel).
 *
 * Implements Prompt 30 Sections 28, 29, 30, 33 & 34:
 * - Strictly isolates Friend Duel matches from Quick Duel.
 * - Handles zero-match denominators safely.
 * - Displays sample size text (e.g. "(N=15 matches)").
 */
data class CompetitiveModeSummary(
    val modeName: String,
    val matches: Int,
    val wins: Int,
    val losses: Int,
    val ties: Int,
    val winRate: Double
) {
    val sampleSizeText: String
        get() = if (matches > 0) "(N=$matches matches)" else "No matches played yet"

    val matchesPlayed: Int
        get() = matches

    val validatedCompletions: Int
        get() = wins

    val formattedWinRate: String
        get() = if (matches > 0) String.format("%.1f%%", winRate) else "N/A"

    val accessibilityDescription: String
        get() = "$modeName: $matches matches played. $wins wins, $losses losses, $ties ties. Win rate: $formattedWinRate."

    val accessibleDescription: String
        get() = accessibilityDescription
}

/**
 * Multi-participant Mini League performance summary.
 *
 * Implements Prompt 30 Section 31:
 * - Reflects party tournament results (participations, 1st place, top 3, average rank).
 * - Never forces tournament finishes into a binary 1v1 win/loss model.
 */
data class MiniLeagueSummary(
    val participations: Int,
    val firstPlaceFinishes: Int,
    val topThreeFinishes: Int,
    val averageFinishPosition: Double,
    val totalCompletions: Int
) {
    val validatedCompletions: Int
        get() = totalCompletions

    val podiumRate: Double
        get() = if (participations > 0) {
            (topThreeFinishes.toDouble() / participations.toDouble()) * 100.0
        } else {
            0.0
        }

    val sampleSizeText: String
        get() = if (participations > 0) "(N=$participations tournaments)" else "No tournaments played yet"

    val formattedAverageRank: String
        get() = if (participations > 0 && averageFinishPosition > 0.0) {
            String.format("%.1f", averageFinishPosition)
        } else {
            "N/A"
        }

    val formattedPodiumRate: String
        get() = if (participations > 0) String.format("%.1f%%", podiumRate) else "N/A"

    val accessibilityDescription: String
        get() = "Mini League: $participations tournaments. $firstPlaceFinishes first places, $topThreeFinishes podium finishes. Average rank: $formattedAverageRank."

    val accessibleDescription: String
        get() = accessibilityDescription
}

/**
 * Aggregated competitive multiplayer analytics across all supported game modes.
 */
data class CompetitiveAnalyticsSummary(
    val totalFinalizedMatches: Int,
    val quickDuel: CompetitiveModeSummary,
    val friendDuel: CompetitiveModeSummary,
    val miniLeague: MiniLeagueSummary,
    val totalValidatedCompletions: Int,
    val isCached: Boolean = false,
    val isAvailable: Boolean = true
) {
    val statusLabel: String
        get() = if (!isAvailable) {
            "Competitive history unavailable (offline/unauthenticated)"
        } else if (isCached) {
            "Cached from last online sync"
        } else {
            "Live server-authoritative data"
        }
}
