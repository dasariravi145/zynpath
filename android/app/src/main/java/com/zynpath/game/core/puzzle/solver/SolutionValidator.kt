package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult

/**
 * Result of validating a candidate solution discovered by the solver.
 */
sealed interface SolutionValidationResult {
    /**
     * The candidate path strictly satisfies all 12 game rules and 8 completion invariants.
     */
    data class Valid(val result: ValidatedCompletionResult) : SolutionValidationResult

    /**
     * The candidate path violated one or more game rules, indicating an internal solver engine defect.
     */
    data class Defect(
        val reason: String,
        val message: String,
        val candidatePath: PuzzlePath
    ) : SolutionValidationResult
}

/**
 * Authoritative solution validation gate for [PuzzleSolver].
 *
 * Adheres strictly to Prompt 8 Section 23:
 * - Every discovered candidate must pass the existing authoritative [CompletionValidator].
 * - Validates: correct start, correct endpoint, orthogonal adjacency, wall compliance,
 *   no repeated cells, full required-cell coverage, and correct checkpoint order.
 * - If an internally generated candidate fails validation, treats it as an engine defect.
 */
object SolutionValidator {

    /**
     * Validates [path] against [definition] using [CompletionValidator].
     */
    fun validate(definition: PuzzleDefinition, path: PuzzlePath): SolutionValidationResult {
        return when (val completion = CompletionValidator.validate(definition, path)) {
            is CompletionCheckResult.Success -> {
                SolutionValidationResult.Valid(completion.completionResult)
            }
            is CompletionCheckResult.Failure -> {
                SolutionValidationResult.Defect(
                    reason = completion.reason.name,
                    message = completion.message,
                    candidatePath = path
                )
            }
        }
    }

    /**
     * Asserts that [path] is valid.
     *
     * @throws IllegalStateException if the candidate path fails validation, treating it as an engine defect.
     */
    fun assertValid(definition: PuzzleDefinition, path: PuzzlePath): ValidatedCompletionResult {
        return when (val result = validate(definition, path)) {
            is SolutionValidationResult.Valid -> result.result
            is SolutionValidationResult.Defect -> {
                throw IllegalStateException(
                    "ENGINE DEFECT: Internally generated candidate solution failed validation: " +
                        "[${result.reason}] ${result.message}"
                )
            }
        }
    }
}
