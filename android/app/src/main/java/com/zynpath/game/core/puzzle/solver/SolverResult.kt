package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Authoritative, immutable result of a puzzle solving operation.
 *
 * Implements Prompt 8 Sections 8, 19, and 20:
 * - Structured status outcomes ([SolverStatus]).
 * - Discovered valid full-coverage [PuzzlePath] instances.
 * - Rigorous uniqueness proof distinction:
 *   - Finding 1 solution proves solvability ([isSolved] == true).
 *   - Finding 2 distinct solutions proves non-uniqueness ([uniqueness] == [UniquenessStatus.NON_UNIQUE]).
 *   - Finding 1 solution without exhaustive search does NOT prove uniqueness ([isUnique] == false).
 *   - Reaching a search limit does NOT prove unsolvability ([isUnsolvable] == false).
 *
 * @property status Final solver status outcome.
 * @property solutions Ordered list of distinct valid full-coverage [PuzzlePath] instances found.
 * @property solutionCount Total number of valid solutions discovered (equals `solutions.size`).
 * @property isExhaustive Whether the search tree was fully exhausted without hitting search limits or early exits.
 * @property statistics Execution performance and search metrics.
 * @property diagnosticMessage Optional diagnostic or error description.
 */
data class SolverResult(
    val status: SolverStatus,
    val solutions: List<PuzzlePath>,
    val solutionCount: Int = solutions.size,
    val isExhaustive: Boolean,
    val statistics: SolverStatistics,
    val diagnosticMessage: String? = null
) {
    /**
     * True if at least one valid solution was discovered.
     * Finding one solution proves solvability (Section 8).
     */
    val isSolved: Boolean
        get() = solutions.isNotEmpty()

    /**
     * True if and only if exhaustive search proved that exactly one valid solution exists.
     * Finding one solution without exhaustive search does NOT prove uniqueness.
     */
    val isUnique: Boolean
        get() = isExhaustive && solutions.size == 1

    /**
     * True if and only if exhaustive search proved zero solutions exist.
     * Reaching a search limit does NOT prove unsolvability.
     */
    val isUnsolvable: Boolean
        get() = isExhaustive && solutions.isEmpty()

    /**
     * Formal uniqueness classification adhering to Section 19.
     */
    val uniqueness: UniquenessStatus
        get() = when {
            status == SolverStatus.MULTIPLE_SOLUTIONS -> UniquenessStatus.NON_UNIQUE
            solutions.size >= 2 -> UniquenessStatus.NON_UNIQUE
            status == SolverStatus.UNSOLVABLE -> UniquenessStatus.UNSOLVABLE
            isExhaustive && solutions.isEmpty() -> UniquenessStatus.UNSOLVABLE
            isExhaustive && solutions.size == 1 -> UniquenessStatus.UNIQUE
            else -> UniquenessStatus.UNKNOWN
        }

    /** The primary or first discovered solution path, if any. */
    val firstSolution: PuzzlePath?
        get() = solutions.firstOrNull()

    val nodeCount: Long
        get() = statistics.nodesExplored

    companion object {
        fun invalid(message: String): SolverResult = SolverResult(
            status = SolverStatus.INVALID_PUZZLE,
            solutions = emptyList(),
            isExhaustive = true,
            statistics = SolverStatistics.EMPTY,
            diagnosticMessage = message
        )
    }
}
