package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.catalog.CuratedWorld4Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld4PartB
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Test

class Benchmark131Test {

    @Test
    fun findFastest20Checkpoints() {
        val origDef = CuratedWorld4Levels.getLevel(131)!!
        val sol = CuratedWorld4PartB.SOLUTION_131.positions
        val solver = PuzzleSolver()
        val fastConfig = SolverConfiguration(maxSolutions = 2, nodeLimit = 50_000L, timeBudgetMs = 3_000L)

        // Baseline step indices of checkpoints:
        // 0, 7, 8, 11, 15, 22, 30, 34, 38, 45, 51, 53, 60, 65, 71, 76, 80, 83, 91, 99
        val baseSteps = listOf(0, 7, 8, 11, 15, 22, 30, 34, 38, 45, 51, 53, 60, 65, 71, 76, 80, 83, 91, 99)

        // We want to add step 26: (7,4)
        // Try dropping each droppable candidate from baseSteps (excluding 0 and 99):
        val candidatesToDrop = listOf(8, 11, 34, 38, 51, 53, 60, 65, 80, 83)

        for (dropStep in candidatesToDrop) {
            val newSteps = (baseSteps - dropStep + 26).sorted()
            if (newSteps.size != 20) continue
            val cps = newSteps.mapIndexed { idx, step -> NumberedCheckpoint(idx + 1, sol[step]) }
            val testDef = origDef.copy(checkpoints = cps)
            val res = solver.solve(testDef, fastConfig)
            println("Dropped step $dropStep, added 26: status=${res.status}, uniq=${res.uniqueness}, nodes=${res.statistics.nodesExplored}, time=${res.statistics.elapsedTimeMs}ms")
        }
    }
}
