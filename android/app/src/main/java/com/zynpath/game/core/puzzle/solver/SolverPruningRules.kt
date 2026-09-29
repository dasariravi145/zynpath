package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition

/**
 * Sound, conservative mathematical pruning rules for the continuous-path Hamiltonian solver.
 *
 * Each pruning rule is guaranteed to be a necessary condition for any valid full-coverage Hamiltonian path:
 * - Pruning a branch is executed only when completion is mathematically impossible.
 * - No valid solution path can ever be pruned.
 *
 * Adheres strictly to Prompt 8 Sections 14, 15, 16, and 17.
 */
object SolverPruningRules {

    /**
     * Rule 1: Premature Final Checkpoint Entry (Section 10 & 14).
     *
     * The final numbered checkpoint must be the exact final position of the completed path.
     * Entering it when more than 1 required cell remains unvisited is strictly illegal.
     */
    fun isPrematureFinalCheckpoint(
        candidate: GridPosition,
        finalCheckpointPos: GridPosition,
        remainingRequiredCells: Int
    ): Boolean {
        return candidate == finalCheckpointPos && remainingRequiredCells > 1
    }

    /**
     * Rule 2: Checkpoint Sequence Compliance (Section 11 & 14).
     *
     * Checkpoints must be entered in strictly ascending sequence (1 -> 2 -> ... -> N).
     * Entering any checkpoint out of order is strictly illegal.
     */
    fun isCheckpointSequenceValid(
        checkpointNumber: Int?,
        nextRequiredCheckpoint: Int
    ): Boolean {
        return checkpointNumber == null || checkpointNumber == nextRequiredCheckpoint
    }

    /**
     * Rule 3: Unvisited Cell Connectivity & Reachability Flood-Fill (Section 14 & 15).
     *
     * In an unbroken continuous path, all remaining unvisited required cells must belong to a
     * single connected component accessible from [candidate] through unvisited cells.
     *
     * Crucially, because the final checkpoint cannot be traversed until the very last step,
     * the final checkpoint acts as an obstacle: other unvisited cells cannot route *through* it.
     * If any unvisited required cell is disconnected from [candidate], completion is impossible.
     */
    fun isConnectivityPreserved(
        definition: PuzzleDefinition,
        candidate: GridPosition,
        finalCheckpointPos: GridPosition,
        remainingRequiredCells: Int,
        state: SolverSearchState
    ): Boolean {
        if (remainingRequiredCells <= 1) return true

        val gen = state.nextBfsGeneration()
        val queue = state.bfsQueue
        val token = state.bfsVisitedToken
        val visited = state.visitedBitmap
        val colCount = state.colCount

        var head = 0
        var tail = 0

        val candidateIdx = state.indexOf(candidate)
        queue[tail++] = candidateIdx
        token[candidateIdx] = gen

        var reachedCount = 1 // candidate itself is reached

        while (head < tail) {
            val currIdx = queue[head++]
            val currRow = currIdx / colCount
            val currCol = currIdx % colCount
            val currPos = GridPosition(currRow, currCol)

            val neighbors = definition.graph.getTraversableNeighbors(currPos)
            for (i in neighbors.indices) {
                val nbr = neighbors[i]
                val nbrIdx = nbr.row * colCount + nbr.column

                // Must not be visited in main search, and not visited in this BFS
                if (!visited[nbrIdx] && token[nbrIdx] != gen) {
                    token[nbrIdx] = gen
                    reachedCount++

                    // If nbr is the final checkpoint and we still have other cells to cover,
                    // it is reached as a target, but we CANNOT expand through it!
                    val isFinal = (nbr == finalCheckpointPos)
                    if (!isFinal || remainingRequiredCells == 1) {
                        queue[tail++] = nbrIdx
                    }
                }
            }
        }

        // reachedCount includes candidate + unvisited cells reached.
        // Total reached must be >= remainingRequiredCells (including candidate).
        return reachedCount >= remainingRequiredCells
    }

    /**
     * Rule 4: Intermediate Cell Degree Check (Section 14 & 16).
     *
     * Every intermediate cell in a Hamiltonian path must have degree >= 2 (1 entry, 1 exit).
     * If any unvisited cell (other than the candidate and other than the final checkpoint) has
     * fewer than 2 available edges in the remaining graph, it can never be both entered and exited.
     * The designated final checkpoint has special endpoint semantics requiring degree >= 1.
     */
    fun hasSufficientDegrees(
        definition: PuzzleDefinition,
        candidate: GridPosition,
        finalCheckpointPos: GridPosition,
        remainingRequiredCells: Int,
        state: SolverSearchState
    ): Boolean {
        if (remainingRequiredCells <= 2) return true

        val visited = state.visitedBitmap
        val colCount = state.colCount

        for (pos in definition.requiredCells) {
            val idx = pos.row * colCount + pos.column
            if (visited[idx] || pos == candidate) continue

            val isFinal = (pos == finalCheckpointPos)
            val minRequiredDegree = if (isFinal) 1 else 2

            // Count available neighbors: unvisited required neighbors + candidate if adjacent and unblocked
            var availableDegree = 0
            val neighbors = definition.graph.getTraversableNeighbors(pos)
            for (i in neighbors.indices) {
                val nbr = neighbors[i]
                val nbrIdx = nbr.row * colCount + nbr.column
                if (nbr == candidate || !visited[nbrIdx]) {
                    availableDegree++
                    if (availableDegree >= minRequiredDegree) break
                }
            }

            if (availableDegree < minRequiredDegree) {
                return false
            }
        }

        return true
    }

    /**
     * Rule 5: Checkpoint Reachability Pruning (Section 14 & 17).
     *
     * Checkpoint [nextRequiredCheckpoint] must eventually be visited.
     * Any path to [nextRequiredCheckpoint] can ONLY traverse unvisited ordinary cells
     * and CANNOT step on any checkpoint with number > [nextRequiredCheckpoint], because
     * entering a later checkpoint out of sequence is illegal (WRONG_CHECKPOINT_ORDER).
     *
     * If [targetCheckpointPos] is not reachable from [candidate] through legal unvisited cells,
     * the branch is provably unsolvable and must be pruned.
     */
    fun isNextCheckpointReachable(
        definition: PuzzleDefinition,
        candidate: GridPosition,
        nextRequiredCheckpoint: Int,
        targetCheckpointPos: GridPosition?,
        state: SolverSearchState
    ): Boolean {
        // If candidate IS the target checkpoint, or no target checkpoint remains, reachability is satisfied
        if (targetCheckpointPos == null || candidate == targetCheckpointPos) return true

        val gen = state.nextBfsGeneration()
        val queue = state.bfsQueue
        val token = state.bfsVisitedToken
        val visited = state.visitedBitmap
        val colCount = state.colCount

        var head = 0
        var tail = 0

        val candidateIdx = state.indexOf(candidate)
        queue[tail++] = candidateIdx
        token[candidateIdx] = gen

        val targetIdx = state.indexOf(targetCheckpointPos)

        while (head < tail) {
            val currIdx = queue[head++]
            if (currIdx == targetIdx) {
                return true
            }

            val currRow = currIdx / colCount
            val currCol = currIdx % colCount
            val currPos = GridPosition(currRow, currCol)

            val neighbors = definition.graph.getTraversableNeighbors(currPos)
            for (i in neighbors.indices) {
                val nbr = neighbors[i]
                val nbrIdx = nbr.row * colCount + nbr.column

                if (!visited[nbrIdx] && token[nbrIdx] != gen) {
                    val cpNum = definition.getCheckpointAt(nbr)
                    // Checkpoints > nextRequiredCheckpoint cannot be traversed prior to nextRequiredCheckpoint!
                    if (cpNum == null || cpNum <= nextRequiredCheckpoint) {
                        token[nbrIdx] = gen
                        queue[tail++] = nbrIdx
                    }
                }
            }
        }

        return false
    }

    // =========================================================================
    // Backward-compatible overloads for standalone testing with raw arrays
    // =========================================================================

    fun isConnectivityPreserved(
        definition: PuzzleDefinition,
        candidate: GridPosition,
        finalCheckpointPos: GridPosition,
        remainingRequiredCells: Int,
        visitedBitmap: BooleanArray,
        columnCount: Int,
        bfsQueue: IntArray,
        bfsVisited: BooleanArray
    ): Boolean {
        if (remainingRequiredCells <= 1) return true
        bfsVisited.fill(false)

        var head = 0
        var tail = 0
        val candidateIdx = candidate.row * columnCount + candidate.column
        bfsQueue[tail++] = candidateIdx
        bfsVisited[candidateIdx] = true

        var reachedCount = 1

        while (head < tail) {
            val currIdx = bfsQueue[head++]
            val currRow = currIdx / columnCount
            val currCol = currIdx % columnCount
            val currPos = GridPosition(currRow, currCol)

            val neighbors = definition.graph.getNeighbors(currPos)
            for (i in neighbors.indices) {
                val nbr = neighbors[i]
                val nbrIdx = nbr.row * columnCount + nbr.column
                if (nbr in definition.requiredCells && !visitedBitmap[nbrIdx] && !bfsVisited[nbrIdx]) {
                    bfsVisited[nbrIdx] = true
                    reachedCount++
                    val isFinal = (nbr == finalCheckpointPos)
                    if (!isFinal || remainingRequiredCells == 1) {
                        bfsQueue[tail++] = nbrIdx
                    }
                }
            }
        }
        return reachedCount >= remainingRequiredCells
    }

    fun hasSufficientDegrees(
        definition: PuzzleDefinition,
        candidate: GridPosition,
        finalCheckpointPos: GridPosition,
        remainingRequiredCells: Int,
        visitedBitmap: BooleanArray,
        columnCount: Int
    ): Boolean {
        if (remainingRequiredCells <= 2) return true
        for (pos in definition.requiredCells) {
            val idx = pos.row * columnCount + pos.column
            if (visitedBitmap[idx] || pos == candidate) continue
            val isFinal = (pos == finalCheckpointPos)
            val minRequiredDegree = if (isFinal) 1 else 2
            var availableDegree = 0
            val neighbors = definition.graph.getNeighbors(pos)
            for (i in neighbors.indices) {
                val nbr = neighbors[i]
                val nbrIdx = nbr.row * columnCount + nbr.column
                if (nbr == candidate || (!visitedBitmap[nbrIdx] && nbr in definition.requiredCells)) {
                    availableDegree++
                    if (availableDegree >= minRequiredDegree) break
                }
            }
            if (availableDegree < minRequiredDegree) return false
        }
        return true
    }
}
