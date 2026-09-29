package com.zynpath.game.core.puzzle.curation

/**
 * Authoritative reasons for hard rejection of a candidate puzzle during curation.
 *
 * Implements Prompt 10 Section 20:
 * Hard rejection rules ensure that no mathematically invalid, unsolvable, non-unique,
 * duplicate, or world-violating puzzle ever enters gameplay catalog.
 */
enum class QualityRejectionReason(val description: String) {
    STRUCTURAL_INVALIDITY("Puzzle definition fails structural integrity invariants"),
    UNSOLVABLE("Puzzle has no valid full-coverage Hamiltonian solution"),
    INCONCLUSIVE_VERIFICATION("Solver verification timed out or was inconclusive"),
    FAILED_COMPLETION_VALIDATION("Discovered solution failed authoritative CompletionValidator"),
    GRID_DIMENSION_MISMATCH("Grid dimensions do not match the target world configuration"),
    CHECKPOINT_COUNT_OUT_OF_RANGE("Checkpoint count violates target world bounds"),
    WALL_COUNT_OUT_OF_RANGE("Wall count violates target world bounds"),
    UNIQUENESS_NOT_PROVEN("Puzzle is non-unique or uniqueness could not be proven"),
    EXACT_DUPLICATE("Puzzle is an exact topological duplicate of an already accepted level"),
    EXCESSIVE_SIMILARITY("Puzzle is excessively similar to recently accepted levels"),
    DIFFICULTY_OUT_OF_RANGE("Puzzle estimated difficulty score is outside target acceptable band")
}
