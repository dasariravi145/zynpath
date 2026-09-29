package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzlePath

/**
 * Encapsulated reversible search state for depth-first Hamiltonian path exploration.
 *
 * Implements Prompt 8 Section 6 and Section 9:
 * - Efficient visited-cell lookup via primitive boolean bitmap.
 * - Reversible sequential path stack avoiding graph/state cloning.
 * - Reusable pre-allocated BFS scratch buffers with $O(1)$ generation token clearing.
 * - Node exploration, backtrack, and pruning telemetry.
 */
class SolverSearchState(
    val rowCount: Int,
    val colCount: Int,
    val totalRequiredCells: Int
) {
    val gridSize: Int = rowCount * colCount

    /** Fast $O(1)$ primitive bitmask for cell visit tracking. */
    val visitedBitmap: BooleanArray = BooleanArray(gridSize)

    /** Ordered path stack maintaining exact chronological progression. */
    val path: ArrayList<GridPosition> = ArrayList(totalRequiredCells)

    /** Scratch queue for BFS flood-fill and reachability evaluations. */
    val bfsQueue: IntArray = IntArray(gridSize)

    /** Generation-stamped BFS visited tracking for $O(1)$ zero-allocation reset. */
    val bfsVisitedToken: IntArray = IntArray(gridSize)
    var bfsGeneration: Int = 1

    // Execution metrics & counters
    var nodesExplored: Long = 0L
    var backtracks: Long = 0L
    var prunedBranches: Long = 0L

    // Search termination flags
    var cancelled: Boolean = false
    var searchLimitReached: Boolean = false
    var defectMessage: String? = null

    /** Returns the 1D index for a given position. */
    fun indexOf(pos: GridPosition): Int = pos.row * colCount + pos.column

    /** Returns true if the cell at [pos] is currently marked visited. */
    fun isVisited(pos: GridPosition): Boolean = visitedBitmap[indexOf(pos)]

    /** Marks [pos] as visited and pushes it onto the path stack. */
    fun push(pos: GridPosition) {
        visitedBitmap[indexOf(pos)] = true
        path.add(pos)
    }

    /** Pops the last position from the path stack and marks it unvisited. */
    fun pop(): GridPosition {
        val last = path.removeAt(path.size - 1)
        visitedBitmap[indexOf(last)] = false
        return last
    }

    /** Returns the current head position of the path, or null if empty. */
    fun currentHead(): GridPosition? = path.lastOrNull()

    /** Current path length. */
    val currentLength: Int
        get() = path.size

    /**
     * Advances the BFS generation counter for $O(1)$ flood-fill resetting.
     */
    fun nextBfsGeneration(): Int {
        if (bfsGeneration == Int.MAX_VALUE) {
            bfsVisitedToken.fill(0)
            bfsGeneration = 1
        } else {
            bfsGeneration++
        }
        return bfsGeneration
    }

    /**
     * Constructs an immutable [PuzzlePath] snapshot of the current search stack.
     */
    fun toPuzzlePath(): PuzzlePath = PuzzlePath(path.toList())

    /**
     * Compiles current metrics into an immutable [SolverStatistics] object.
     */
    fun toStatistics(elapsedMs: Long): SolverStatistics = SolverStatistics(
        nodesExplored = nodesExplored,
        backtracks = backtracks,
        prunedBranches = prunedBranches,
        elapsedMs = elapsedMs
    )
}
