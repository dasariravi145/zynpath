package com.zynpath.game.core.puzzle.curation

/**
 * Configuration options controlling the level curation pipeline.
 *
 * Implements Prompt 10 Sections 24, 26, and 29:
 * - Deterministic generation candidate budget per level.
 * - Strict uniqueness enforcement flag.
 * - Symmetry-aware duplicate rejection and similarity threshold.
 * - Configurable progression curve with periodic recovery levels.
 */
data class CurationConfiguration(
    val maxCandidatesPerLevel: Int = 25,
    val requireUnique: Boolean = true,
    val rejectSymmetricDuplicates: Boolean = true,
    val maxAllowedSimilarity: Double = 0.85,
    val allowRecoveryLevels: Boolean = true,
    val recoveryLevelFrequency: Int = 5,
    val recoveryDifficultyReduction: Double = 0.06,
    val curationVersion: String = CURATION_VERSION,
    val difficultyConfig: DifficultyAnalysisConfiguration = DifficultyAnalysisConfiguration.DEFAULT
) {
    companion object {
        const val CURATION_VERSION = "1.0.0"
        val DEFAULT = CurationConfiguration()
    }
}
