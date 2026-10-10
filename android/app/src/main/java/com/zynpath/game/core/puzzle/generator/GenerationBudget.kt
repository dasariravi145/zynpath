package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.solver.SearchBudget

/**
 * Deterministic generation budget controlling candidate attempts and search iterations.
 *
 * Implements Prompt 2A Section 6:
 * Candidate generation must use finite maxCandidateAttempts and deterministic seeds.
 * Absolutely no while(true) or infinite loops.
 *
 * @property maxCandidateAttempts Maximum independent candidate puzzles to generate before reporting failure.
 * @property maxSearchIterations Maximum iteration steps for adjusting checkpoints/walls on a single candidate.
 * @property searchBudget Search budget assigned to candidate solver verification.
 * @property stableLevelId Optional stable level identifier (e.g. "w3_lvl55").
 * @property contentVersion Content schema version integer.
 */
data class GenerationBudget(
    val maxCandidateAttempts: Int = 25,
    val maxSearchIterations: Int = 30,
    val searchBudget: SearchBudget = SearchBudget.CANDIDATE_SCREENING,
    val stableLevelId: String? = null,
    val contentVersion: Int = 1
) {
    init {
        require(maxCandidateAttempts in 1..100) {
            "maxCandidateAttempts must be in 1..100, but was $maxCandidateAttempts"
        }
        require(maxSearchIterations in 1..100) {
            "maxSearchIterations must be in 1..100, but was $maxSearchIterations"
        }
    }

    companion object {
        /** Default generation budget adhering to Prompt 2A limits. */
        val DEFAULT = GenerationBudget()

        /** Fast generation budget for small board tests. */
        val FAST = GenerationBudget(
            maxCandidateAttempts = 10,
            maxSearchIterations = 15,
            searchBudget = SearchBudget.STRICT_BOUNDED
        )

        /** Strict bounded candidate screening budget for generator loops. */
        val STRICT = GenerationBudget(
            maxCandidateAttempts = 25,
            maxSearchIterations = 30,
            searchBudget = SearchBudget.CANDIDATE_SCREENING
        )
    }
}
