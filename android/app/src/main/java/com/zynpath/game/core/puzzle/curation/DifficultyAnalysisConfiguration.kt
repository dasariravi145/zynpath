package com.zynpath.game.core.puzzle.curation

/**
 * Versioned configuration parameters and scoring weights for puzzle difficulty evaluation.
 *
 * Implements Prompt 10 Sections 14, 17, and 18:
 * - Deterministic normalized weight distribution across objective complexity signals.
 * - Versioned configuration to guarantee reproducible scoring over time.
 * - Resource limits for solver search during difficulty evaluation.
 */
data class DifficultyAnalysisConfiguration(
    val weightSize: Double = 0.20,
    val weightWalls: Double = 0.20,
    val weightCheckpoints: Double = 0.15,
    val weightTurns: Double = 0.15,
    val weightGaps: Double = 0.15,
    val weightSolverNodes: Double = 0.15,
    val solverNodeBudget: Long = 50_000L,
    val solverTimeBudgetMs: Long = 2_000L,
    val configVersion: String = CONFIG_VERSION,
    val analyzerVersion: String = ANALYZER_VERSION
) {
    init {
        val totalWeight = weightSize + weightWalls + weightCheckpoints + weightTurns + weightGaps + weightSolverNodes
        require(totalWeight in 0.99..1.01) {
            "Difficulty scoring weights must sum to approximately 1.0 (got $totalWeight)"
        }
    }

    companion object {
        const val CONFIG_VERSION = "1.0.0"
        const val ANALYZER_VERSION = "1.0.0"

        val DEFAULT = DifficultyAnalysisConfiguration()
    }
}
