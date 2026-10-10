package com.zynpath.game.core.puzzle.curation

/**
 * Authoritative coarse difficulty classification bands for Zynpath puzzles.
 *
 * Implements Prompt 10 Section 17:
 * - Internal algorithmic difficulty classifications based on objective composite score [0.0, 1.0].
 * - [BEGINNER]: 0.00 .. <0.20 (Introductory 4x4, no walls, straightforward routing)
 * - [EASY]:     0.20 .. <0.40 (4x4 advanced / 5x5 early, no walls)
 * - [MEDIUM]:   0.40 .. <0.60 (5x5 with walls / 6x6 early)
 * - [HARD]:     0.60 .. <0.80 (6x6 with walls / 7x7)
 * - [EXPERT]:   0.80 .. 1.00  (7x7 late / 8x8 grandmaster)
 */
enum class DifficultyBand(
    val minScore: Double,
    val maxScore: Double,
    val displayName: String
) {
    TUTORIAL(0.0, 0.20, "Tutorial"),
    BEGINNER(0.0, 0.20, "Beginner"),
    EASY(0.20, 0.40, "Easy"),
    MEDIUM(0.40, 0.60, "Medium"),
    HARD(0.60, 0.75, "Hard"),
    VERY_HARD(0.75, 0.88, "Very Hard"),
    EXPERT(0.88, 0.94, "Expert"),
    EXTREME(0.94, 1.00, "Extreme");

    companion object {
        fun fromScore(score: Double): DifficultyBand {
            val clamped = score.coerceIn(0.0, 1.0)
            if (clamped >= 0.94) return EXTREME
            if (clamped >= 0.88) return EXPERT
            if (clamped >= 0.75) return VERY_HARD
            if (clamped >= 0.60) return HARD
            if (clamped >= 0.40) return MEDIUM
            if (clamped >= 0.20) return EASY
            return TUTORIAL
        }
    }
}
