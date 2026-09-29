package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.solver.UniquenessStatus

/**
 * Result of comprehensive puzzle difficulty analysis.
 *
 * Implements Prompt 10 Sections 6, 16, 17, and 18:
 * - Deterministic composite difficulty estimate [0.0, 1.0].
 * - Coarse [DifficultyBand] classification.
 * - Granular [DifficultyMetrics] breakdown.
 * - Explicitly labeled as provisional algorithmic estimate (not empirically player-calibrated).
 */
data class DifficultyAnalysisResult(
    val puzzleId: String,
    val estimatedScore: Double,
    val difficultyBand: DifficultyBand,
    val metrics: DifficultyMetrics,
    val componentScores: Map<String, Double>,
    val isProvisional: Boolean = true,
    val configurationVersion: String = DifficultyAnalysisConfiguration.CONFIG_VERSION,
    val analyzerVersion: String = DifficultyAnalysisConfiguration.ANALYZER_VERSION
) {
    val uniquenessStatus: UniquenessStatus
        get() = metrics.uniquenessStatus

    /**
     * User-facing presentation summary without exposing internal solver search internals.
     */
    fun toPresentationSummary(): String {
        return "${difficultyBand.displayName} (Rating: ${String.format(java.util.Locale.US, "%.2f", estimatedScore)})"
    }
}
