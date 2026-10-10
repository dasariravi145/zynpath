package com.zynpath.game.core.puzzle.solver

import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import kotlin.math.abs

/**
 * Result of evaluating a puzzle against simple casual heuristics.
 */
data class SimpleStrategyEvaluation(
    val solvedByAnySimpleStrategy: Boolean,
    val successfulStrategy: String?,
    val greedySolved: Boolean,
    val greedyBacktracks: Int,
    val leftWallFollowerSolved: Boolean,
    val leftWallFollowerBacktracks: Int,
    val rightWallFollowerSolved: Boolean,
    val rightWallFollowerBacktracks: Int,
    val serpentineSolved: Boolean,
    val serpentineBacktracks: Int,
    val perimeterFirstSolved: Boolean = false,
    val perimeterFirstBacktracks: Int = 0,
    val nearestUnvisitedSolved: Boolean = false,
    val nearestUnvisitedBacktracks: Int = 0,
    val rowSnakeSolved: Boolean = serpentineSolved,
    val rowSnakeBacktracks: Int = serpentineBacktracks,
    val columnSnakeSolved: Boolean = false,
    val columnSnakeBacktracks: Int = 0,
    val manhattanGreedySolved: Boolean = greedySolved,
    val manhattanGreedyBacktracks: Int = greedyBacktracks,
    val nearestCheckpointSolved: Boolean = false,
    val nearestCheckpointBacktracks: Int = 0,
    val details: String
)

/**
 * Baseline solver simulating simple human heuristics:
 * 1. Greedy Checkpoint Proximity: Minimizes Manhattan distance to the next numbered checkpoint.
 * 2. Left-Hand Wall Follower: Hugs the left wall / boundary.
 * 3. Right-Hand Wall Follower: Hugs the right wall / boundary.
 * 4. Serpentine Sweeper: Sweeps rows/columns greedily.
 * 5. Perimeter-First: Prioritizes outer boundary cells before internal cells.
 * 6. Nearest Unvisited: Minimizes Manhattan distance to closest unvisited cell.
 *
 * Implements Prompt Section 16 requirement:
 * Hard puzzles should be rejected when these strategies routinely solve them without meaningful planning.
 */
class SimpleStrategyBaselineSolver(
    private val maxBacktracksPerStrategy: Int = 5
) {

    fun evaluate(definition: PuzzleDefinition): SimpleStrategyEvaluation {
        val startCheckpoint = definition.checkpoints.find { it.number == 1 }
            ?: return SimpleStrategyEvaluation(
                solvedByAnySimpleStrategy = false,
                successfulStrategy = null,
                greedySolved = false, greedyBacktracks = 0,
                leftWallFollowerSolved = false, leftWallFollowerBacktracks = 0,
                rightWallFollowerSolved = false, rightWallFollowerBacktracks = 0,
                serpentineSolved = false, serpentineBacktracks = 0,
                perimeterFirstSolved = false, perimeterFirstBacktracks = 0,
                nearestUnvisitedSolved = false, nearestUnvisitedBacktracks = 0,
                details = "Missing start checkpoint"
            )

        val greedyRes = runGreedyProximity(definition, startCheckpoint)
        val nearestCpRes = runNearestCheckpoint(definition, startCheckpoint)
        val leftWallRes = runWallFollower(definition, startCheckpoint, isLeftHand = true)
        val rightWallRes = runWallFollower(definition, startCheckpoint, isLeftHand = false)
        val rowSnakeRes = runSerpentine(definition, startCheckpoint)
        val colSnakeRes = runColumnSnake(definition, startCheckpoint)
        val perimeterRes = runPerimeterFirst(definition, startCheckpoint)
        val nearestRes = runNearestUnvisited(definition, startCheckpoint)

        val solved = greedyRes.first || nearestCpRes.first || leftWallRes.first || rightWallRes.first ||
                rowSnakeRes.first || colSnakeRes.first || perimeterRes.first || nearestRes.first

        val winner = when {
            greedyRes.first -> "MANHATTAN_GREEDY"
            nearestCpRes.first -> "NEAREST_CHECKPOINT"
            leftWallRes.first -> "LEFT_WALL_FOLLOWER"
            rightWallRes.first -> "RIGHT_WALL_FOLLOWER"
            rowSnakeRes.first -> "ROW_SNAKE"
            colSnakeRes.first -> "COLUMN_SNAKE"
            perimeterRes.first -> "PERIMETER_FIRST"
            nearestRes.first -> "NEAREST_UNVISITED"
            else -> null
        }

        val details = buildString {
            append("Greedy: ${if (greedyRes.first) "SOLVED (${greedyRes.second} bts)" else "FAILED"}")
            append(", NearestCp: ${if (nearestCpRes.first) "SOLVED (${nearestCpRes.second} bts)" else "FAILED"}")
            append(", RowSnake: ${if (rowSnakeRes.first) "SOLVED (${rowSnakeRes.second} bts)" else "FAILED"}")
            append(", ColSnake: ${if (colSnakeRes.first) "SOLVED (${colSnakeRes.second} bts)" else "FAILED"}")
            append(", LeftWall: ${if (leftWallRes.first) "SOLVED (${leftWallRes.second} bts)" else "FAILED"}")
            append(", RightWall: ${if (rightWallRes.first) "SOLVED (${rightWallRes.second} bts)" else "FAILED"}")
            append(", Perimeter: ${if (perimeterRes.first) "SOLVED (${perimeterRes.second} bts)" else "FAILED"}")
            append(", NearestUnvisited: ${if (nearestRes.first) "SOLVED (${nearestRes.second} bts)" else "FAILED"}")
        }

        return SimpleStrategyEvaluation(
            solvedByAnySimpleStrategy = solved,
            successfulStrategy = winner,
            greedySolved = greedyRes.first,
            greedyBacktracks = greedyRes.second,
            leftWallFollowerSolved = leftWallRes.first,
            leftWallFollowerBacktracks = leftWallRes.second,
            rightWallFollowerSolved = rightWallRes.first,
            rightWallFollowerBacktracks = rightWallRes.second,
            serpentineSolved = rowSnakeRes.first,
            serpentineBacktracks = rowSnakeRes.second,
            perimeterFirstSolved = perimeterRes.first,
            perimeterFirstBacktracks = perimeterRes.second,
            nearestUnvisitedSolved = nearestRes.first,
            nearestUnvisitedBacktracks = nearestRes.second,
            rowSnakeSolved = rowSnakeRes.first,
            rowSnakeBacktracks = rowSnakeRes.second,
            columnSnakeSolved = colSnakeRes.first,
            columnSnakeBacktracks = colSnakeRes.second,
            manhattanGreedySolved = greedyRes.first,
            manhattanGreedyBacktracks = greedyRes.second,
            nearestCheckpointSolved = nearestCpRes.first,
            nearestCheckpointBacktracks = nearestCpRes.second,
            details = details
        )
    }

    // =========================================================================
    // 1. GREEDY PROXIMITY STRATEGY
    // =========================================================================

    private fun runGreedyProximity(
        def: PuzzleDefinition,
        startCp: NumberedCheckpoint
    ): Pair<Boolean, Int> {
        val totalCells = def.totalRequiredCells
        val checkpointsByPos = def.checkpointMap
        val finalCheckpoint = def.finalCheckpoint?.position
        val visited = HashSet<GridPosition>(totalCells)
        visited.add(startCp.position)

        val checkpointsByNumber = def.checkpoints.associateBy { it.number }
        var backtracks = 0

        fun manhattan(a: GridPosition, b: GridPosition): Int =
            abs(a.row - b.row) + abs(a.column - b.column)

        fun dfs(current: GridPosition, nextCpNum: Int): Boolean {
            if (visited.size == totalCells) {
                return current == finalCheckpoint
            }

            val nextTargetCp = checkpointsByNumber[nextCpNum]?.position

            val rawNeighbors = def.graph.getTraversableNeighbors(current)
            val legal = rawNeighbors.filter { nbr ->
                nbr !in visited &&
                (checkpointsByPos[nbr] == null || checkpointsByPos[nbr] == nextCpNum) &&
                (nbr != finalCheckpoint || visited.size == totalCells - 1)
            }

            if (legal.isEmpty()) return false

            // Sort neighbors greedily by distance to next target checkpoint
            val sorted = if (nextTargetCp != null) {
                legal.sortedBy { manhattan(it, nextTargetCp) }
            } else {
                legal
            }

            for (nbr in sorted) {
                visited.add(nbr)
                val newNextCp = if (checkpointsByPos[nbr] == nextCpNum) nextCpNum + 1 else nextCpNum
                if (dfs(nbr, newNextCp)) return true
                visited.remove(nbr)
                backtracks++
                if (backtracks > maxBacktracksPerStrategy) return false
            }
            return false
        }

        val success = dfs(startCp.position, nextCpNum = 2)
        return success to backtracks
    }

    // =========================================================================
    // 2. WALL FOLLOWER STRATEGY (LEFT / RIGHT)
    // =========================================================================

    private fun runWallFollower(
        def: PuzzleDefinition,
        startCp: NumberedCheckpoint,
        isLeftHand: Boolean
    ): Pair<Boolean, Int> {
        val totalCells = def.totalRequiredCells
        val checkpointsByPos = def.checkpointMap
        val finalCheckpoint = def.finalCheckpoint?.position
        val visited = HashSet<GridPosition>(totalCells)
        visited.add(startCp.position)

        var backtracks = 0

        fun relativeTurns(dir: Pair<Int, Int>): List<Pair<Int, Int>> {
            val (dr, dc) = dir
            // turn left: (-dc, dr), turn right: (dc, -dr), straight: (dr, dc), reverse: (-dr, -dc)
            return if (isLeftHand) {
                listOf(-dc to dr, dr to dc, dc to -dr, -dr to -dc)
            } else {
                listOf(dc to -dr, dr to dc, -dc to dr, -dr to -dc)
            }
        }

        fun dfs(current: GridPosition, lastDir: Pair<Int, Int>, nextCpNum: Int): Boolean {
            if (visited.size == totalCells) {
                return current == finalCheckpoint
            }

            val candidateDirs = relativeTurns(lastDir)
            for ((dr, dc) in candidateDirs) {
                val nbr = GridPosition(current.row + dr, current.column + dc)
                if (nbr in def.requiredCells &&
                    nbr !in visited &&
                    def.graph.canTraverse(current, nbr) &&
                    (checkpointsByPos[nbr] == null || checkpointsByPos[nbr] == nextCpNum) &&
                    (nbr != finalCheckpoint || visited.size == totalCells - 1)
                ) {
                    visited.add(nbr)
                    val newNextCp = if (checkpointsByPos[nbr] == nextCpNum) nextCpNum + 1 else nextCpNum
                    if (dfs(nbr, dr to dc, newNextCp)) return true
                    visited.remove(nbr)
                    backtracks++
                    if (backtracks > maxBacktracksPerStrategy) return false
                }
            }
            return false
        }

        val initialDirs = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)
        for (dir in initialDirs) {
            if (dfs(startCp.position, dir, nextCpNum = 2)) {
                return true to backtracks
            }
        }
        return false to backtracks
    }

    // =========================================================================
    // 3. SERPENTINE SWEEP STRATEGY
    // =========================================================================

    private fun runSerpentine(
        def: PuzzleDefinition,
        startCp: NumberedCheckpoint
    ): Pair<Boolean, Int> {
        val totalCells = def.totalRequiredCells
        val checkpointsByPos = def.checkpointMap
        val finalCheckpoint = def.finalCheckpoint?.position
        val visited = HashSet<GridPosition>(totalCells)
        visited.add(startCp.position)

        var backtracks = 0

        fun dfs(current: GridPosition, lastHorizontalDir: Int, nextCpNum: Int): Boolean {
            if (visited.size == totalCells) {
                return current == finalCheckpoint
            }

            // Prefer horizontal movement in lastHorizontalDir, then vertical down, then reverse horizontal
            val preferredDirs = listOf(
                0 to lastHorizontalDir,
                1 to 0,
                -1 to 0,
                0 to -lastHorizontalDir
            )

            for ((dr, dc) in preferredDirs) {
                val nbr = GridPosition(current.row + dr, current.column + dc)
                if (nbr in def.requiredCells &&
                    nbr !in visited &&
                    def.graph.canTraverse(current, nbr) &&
                    (checkpointsByPos[nbr] == null || checkpointsByPos[nbr] == nextCpNum) &&
                    (nbr != finalCheckpoint || visited.size == totalCells - 1)
                ) {
                    visited.add(nbr)
                    val newNextCp = if (checkpointsByPos[nbr] == nextCpNum) nextCpNum + 1 else nextCpNum
                    val newHDir = if (dc != 0) dc else lastHorizontalDir
                    if (dfs(nbr, newHDir, newNextCp)) return true
                    visited.remove(nbr)
                    backtracks++
                    if (backtracks > maxBacktracksPerStrategy) return false
                }
            }
            return false
        }

        val success = dfs(startCp.position, 1, nextCpNum = 2) || dfs(startCp.position, -1, nextCpNum = 2)
        return success to backtracks
    }

    private fun runColumnSnake(
        def: PuzzleDefinition,
        startCp: NumberedCheckpoint
    ): Pair<Boolean, Int> {
        val totalCells = def.totalRequiredCells
        val checkpointsByPos = def.checkpointMap
        val finalCheckpoint = def.finalCheckpoint?.position
        val visited = HashSet<GridPosition>(totalCells)
        visited.add(startCp.position)

        var backtracks = 0

        fun dfs(current: GridPosition, lastVerticalDir: Int, nextCpNum: Int): Boolean {
            if (visited.size == totalCells) {
                return current == finalCheckpoint
            }

            val preferredDirs = listOf(
                lastVerticalDir to 0,
                0 to 1,
                0 to -1,
                -lastVerticalDir to 0
            )

            for ((dr, dc) in preferredDirs) {
                val nbr = GridPosition(current.row + dr, current.column + dc)
                if (nbr in def.requiredCells &&
                    nbr !in visited &&
                    def.graph.canTraverse(current, nbr) &&
                    (checkpointsByPos[nbr] == null || checkpointsByPos[nbr] == nextCpNum) &&
                    (nbr != finalCheckpoint || visited.size == totalCells - 1)
                ) {
                    visited.add(nbr)
                    val newNextCp = if (checkpointsByPos[nbr] == nextCpNum) nextCpNum + 1 else nextCpNum
                    val newVDir = if (dr != 0) dr else lastVerticalDir
                    if (dfs(nbr, newVDir, newNextCp)) return true
                    visited.remove(nbr)
                    backtracks++
                    if (backtracks > maxBacktracksPerStrategy) return false
                }
            }
            return false
        }

        val success = dfs(startCp.position, 1, nextCpNum = 2) || dfs(startCp.position, -1, nextCpNum = 2)
        return success to backtracks
    }

    private fun runNearestCheckpoint(
        def: PuzzleDefinition,
        startCp: NumberedCheckpoint
    ): Pair<Boolean, Int> {
        val totalCells = def.totalRequiredCells
        val checkpointsByPos = def.checkpointMap
        val finalCheckpoint = def.finalCheckpoint?.position
        val visited = HashSet<GridPosition>(totalCells)
        visited.add(startCp.position)

        val checkpointsByNumber = def.checkpoints.associateBy { it.number }
        var backtracks = 0

        fun manhattan(a: GridPosition, b: GridPosition): Int =
            abs(a.row - b.row) + abs(a.column - b.column)

        fun dfs(current: GridPosition, nextCpNum: Int): Boolean {
            if (visited.size == totalCells) {
                return current == finalCheckpoint
            }

            val remainingCheckpoints = checkpointsByNumber.filterKeys { it >= nextCpNum }.values.map { it.position }

            val rawNeighbors = def.graph.getTraversableNeighbors(current)
            val legal = rawNeighbors.filter { nbr ->
                nbr !in visited &&
                (checkpointsByPos[nbr] == null || checkpointsByPos[nbr] == nextCpNum) &&
                (nbr != finalCheckpoint || visited.size == totalCells - 1)
            }

            if (legal.isEmpty()) return false

            val sorted = if (remainingCheckpoints.isNotEmpty()) {
                legal.sortedBy { nbr -> remainingCheckpoints.minOf { cp -> manhattan(nbr, cp) } }
            } else {
                legal
            }

            for (nbr in sorted) {
                visited.add(nbr)
                val newNextCp = if (checkpointsByPos[nbr] == nextCpNum) nextCpNum + 1 else nextCpNum
                if (dfs(nbr, newNextCp)) return true
                visited.remove(nbr)
                backtracks++
                if (backtracks > maxBacktracksPerStrategy) return false
            }
            return false
        }

        val success = dfs(startCp.position, nextCpNum = 2)
        return success to backtracks
    }

    // =========================================================================
    // 4. PERIMETER-FIRST STRATEGY
    // =========================================================================

    private fun runPerimeterFirst(
        def: PuzzleDefinition,
        startCp: NumberedCheckpoint
    ): Pair<Boolean, Int> {
        val totalCells = def.totalRequiredCells
        val checkpointsByPos = def.checkpointMap
        val finalCheckpoint = def.finalCheckpoint?.position
        val visited = HashSet<GridPosition>(totalCells)
        visited.add(startCp.position)

        val rows = def.gridDimensions.rows
        val cols = def.gridDimensions.columns
        var backtracks = 0

        fun isPerimeter(pos: GridPosition): Boolean =
            pos.row == 0 || pos.row == rows - 1 || pos.column == 0 || pos.column == cols - 1

        fun dfs(current: GridPosition, nextCpNum: Int): Boolean {
            if (visited.size == totalCells) {
                return current == finalCheckpoint
            }

            val rawNeighbors = def.graph.getTraversableNeighbors(current)
            val legal = rawNeighbors.filter { nbr ->
                nbr !in visited &&
                (checkpointsByPos[nbr] == null || checkpointsByPos[nbr] == nextCpNum) &&
                (nbr != finalCheckpoint || visited.size == totalCells - 1)
            }

            if (legal.isEmpty()) return false

            // Sort: perimeter cells first, then by degree
            val sorted = legal.sortedWith(
                compareByDescending<GridPosition> { isPerimeter(it) }
                    .thenBy { def.graph.getTraversableNeighbors(it).count { n -> n !in visited } }
            )

            for (nbr in sorted) {
                visited.add(nbr)
                val newNextCp = if (checkpointsByPos[nbr] == nextCpNum) nextCpNum + 1 else nextCpNum
                if (dfs(nbr, newNextCp)) return true
                visited.remove(nbr)
                backtracks++
                if (backtracks > maxBacktracksPerStrategy) return false
            }
            return false
        }

        val success = dfs(startCp.position, nextCpNum = 2)
        return success to backtracks
    }

    // =========================================================================
    // 5. NEAREST UNVISITED CELL STRATEGY
    // =========================================================================

    private fun runNearestUnvisited(
        def: PuzzleDefinition,
        startCp: NumberedCheckpoint
    ): Pair<Boolean, Int> {
        val totalCells = def.totalRequiredCells
        val checkpointsByPos = def.checkpointMap
        val finalCheckpoint = def.finalCheckpoint?.position
        val visited = HashSet<GridPosition>(totalCells)
        visited.add(startCp.position)

        var backtracks = 0

        fun manhattan(a: GridPosition, b: GridPosition): Int =
            abs(a.row - b.row) + abs(a.column - b.column)

        fun dfs(current: GridPosition, nextCpNum: Int): Boolean {
            if (visited.size == totalCells) {
                return current == finalCheckpoint
            }

            val rawNeighbors = def.graph.getTraversableNeighbors(current)
            val legal = rawNeighbors.filter { nbr ->
                nbr !in visited &&
                (checkpointsByPos[nbr] == null || checkpointsByPos[nbr] == nextCpNum) &&
                (nbr != finalCheckpoint || visited.size == totalCells - 1)
            }

            if (legal.isEmpty()) return false

            // Find all unvisited cells
            val unvisited = def.requiredCells.filter { it !in visited }

            // Score each neighbor by minimum distance to any other unvisited cell
            val sorted = legal.sortedBy { nbr ->
                val otherUnvisited = unvisited.filter { it != nbr }
                if (otherUnvisited.isEmpty()) 0
                else otherUnvisited.minOf { manhattan(nbr, it) }
            }

            for (nbr in sorted) {
                visited.add(nbr)
                val newNextCp = if (checkpointsByPos[nbr] == nextCpNum) nextCpNum + 1 else nextCpNum
                if (dfs(nbr, newNextCp)) return true
                visited.remove(nbr)
                backtracks++
                if (backtracks > maxBacktracksPerStrategy) return false
            }
            return false
        }

        val success = dfs(startCp.position, nextCpNum = 2)
        return success to backtracks
    }
}
