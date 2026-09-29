package com.zynpath.game.core.puzzle.generator

/**
 * Authoritative outcome status for a puzzle generation execution.
 *
 * Adheres to Prompt 9 Section 27:
 * - [GENERATED]: Compliant puzzle successfully generated, structurally validated, and solver-verified.
 * - [INVALID_CONFIGURATION]: Request failed precondition checks before generation began.
 * - [NO_VALID_CANDIDATE]: Candidate attempt budget exhausted without finding an accepted puzzle.
 * - [SOLVER_INCONCLUSIVE]: Solver reached resource limits during verification.
 * - [CANCELLED]: Generation was cooperatively cancelled.
 * - [RESOURCE_LIMIT_REACHED]: Generation budget (time or attempts) exceeded.
 * - [INTERNAL_ERROR]: Unexpected generation engine exception.
 */
enum class GenerationStatus {
    /**
     * Successfully generated a structurally valid, solver-verified puzzle definition.
     */
    GENERATED,

    /**
     * The requested configuration parameters are contradictory, impossible, or out of legal bounds.
     */
    INVALID_CONFIGURATION,

    /**
     * Generation candidate attempts were exhausted without discovering a puzzle meeting all criteria.
     */
    NO_VALID_CANDIDATE,

    /**
     * Solver was unable to prove solvability or required uniqueness within the configured budget.
     */
    SOLVER_INCONCLUSIVE,

    /**
     * Generation was cooperatively cancelled via the cancellation signal.
     */
    CANCELLED,

    /**
     * Cumulative resource limits (time budget, attempts) were reached.
     */
    RESOURCE_LIMIT_REACHED,

    /**
     * An internal engine failure or invariant violation occurred.
     */
    INTERNAL_ERROR
}
