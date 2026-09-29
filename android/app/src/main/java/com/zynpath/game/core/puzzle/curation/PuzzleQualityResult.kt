package com.zynpath.game.core.puzzle.curation

/**
 * Result of comprehensive quality evaluation for a candidate puzzle.
 *
 * Implements Prompt 10 Sections 19, 20, and 21:
 * - Clear separation of hard rejection rules from soft quality signals.
 * - [isAccepted] indicates whether the puzzle satisfies all mandatory quality and correctness gates.
 * - [softQualityScore] evaluates candidate elegance, route variety, choice richness, and progression fit.
 */
data class PuzzleQualityResult(
    val puzzleId: String,
    val isAccepted: Boolean,
    val rejectionReasons: List<QualityRejectionReason>,
    val rejectionDetails: List<String>,
    val softQualityScore: Double,
    val softSignals: Map<String, Double>
) {
    companion object {
        fun accepted(
            puzzleId: String,
            softQualityScore: Double,
            softSignals: Map<String, Double>
        ): PuzzleQualityResult = PuzzleQualityResult(
            puzzleId = puzzleId,
            isAccepted = true,
            rejectionReasons = emptyList(),
            rejectionDetails = emptyList(),
            softQualityScore = softQualityScore,
            softSignals = softSignals
        )

        fun rejected(
            puzzleId: String,
            reasons: List<QualityRejectionReason>,
            details: List<String>,
            softSignals: Map<String, Double> = emptyMap()
        ): PuzzleQualityResult = PuzzleQualityResult(
            puzzleId = puzzleId,
            isAccepted = false,
            rejectionReasons = reasons,
            rejectionDetails = details,
            softQualityScore = 0.0,
            softSignals = softSignals
        )
    }
}
