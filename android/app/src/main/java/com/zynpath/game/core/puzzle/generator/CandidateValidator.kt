package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.validator.DefinitionValidationResult
import com.zynpath.game.core.puzzle.validator.PuzzleDefinitionValidator

/**
 * Structural candidate verification ensuring a generated [PuzzleDefinition] meets
 * all structural and configuration integrity rules before running the solver.
 *
 * Implements Prompt 9 Section 18:
 * - Runs the authoritative [PuzzleDefinitionValidator].
 * - Enforces exact match against configured checkpoint counts and wall boundaries.
 * - Does NOT treat structural validation as proof of solvability (solver verification is required).
 */
class CandidateValidator {

    /**
     * Validates that [candidate] adheres to all structural rules and matches [config].
     */
    fun validateCandidate(
        candidate: PuzzleDefinition,
        config: GenerationConfiguration
    ): CandidateValidationResult {
        // 1. Run authoritative structural validator
        when (val structural = PuzzleDefinitionValidator.validate(candidate)) {
            is DefinitionValidationResult.Invalid -> {
                return CandidateValidationResult.Invalid(
                    "Structural error: ${structural.errorSummary}"
                )
            }
            is DefinitionValidationResult.Valid -> { /* Passed */ }
        }

        // 2. Checkpoint count compliance
        if (candidate.checkpoints.size != config.checkpointCount) {
            return CandidateValidationResult.Invalid(
                "Checkpoint count mismatch: expected ${config.checkpointCount}, found ${candidate.checkpoints.size}"
            )
        }

        // 3. Wall count compliance
        val wallCount = candidate.blockedEdges.size
        if (wallCount < config.minWalls || wallCount > config.maxWalls) {
            return CandidateValidationResult.Invalid(
                "Wall count ($wallCount) outside configured bounds [${config.minWalls}..${config.maxWalls}]"
            )
        }

        return CandidateValidationResult.Valid
    }
}

sealed interface CandidateValidationResult {
    val isValid: Boolean

    data object Valid : CandidateValidationResult {
        override val isValid: Boolean get() = true
    }

    data class Invalid(val reason: String) : CandidateValidationResult {
        override val isValid: Boolean get() = false
    }
}
