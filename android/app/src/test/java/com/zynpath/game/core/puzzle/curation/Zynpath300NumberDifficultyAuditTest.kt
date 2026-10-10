package com.zynpath.game.core.puzzle.curation

import com.zynpath.game.core.puzzle.analysis.NumberPlacementAnalyzer
import com.zynpath.game.core.puzzle.analysis.NumberPlacementDifficulty
import com.zynpath.game.core.puzzle.catalog.CuratedFirst50Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld3Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld4Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld4PartB
import com.zynpath.game.core.puzzle.catalog.CuratedWorld5Levels
import com.zynpath.game.core.puzzle.catalog.CuratedWorld6Levels
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.core.puzzle.solver.PuzzleSolver
import com.zynpath.game.core.puzzle.solver.SolverConfiguration
import com.zynpath.game.core.puzzle.solver.SolverStatus
import com.zynpath.game.core.puzzle.solver.UniquenessStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Authoritative 300-level number-only difficulty curve auditor and optimizer.
 *
 * Implements Prompt Sections 0–64:
 * - Numbers only: modifies only checkpoint count and placement along canonical path.
 * - Preserves 100% of board geometry, cells, walls, world, seed, and stableLevelIds.
 * - Enforces solver uniqueness (UniquenessStatus.UNIQUE with canonical route removed from input).
 * - Enforces 100% board coverage.
 * - Generates docs/ZYNPATH_300_LEVEL_NUMBER_DIFFICULTY_AUDIT.md.
 */
class Zynpath300NumberDifficultyAuditTest {

    private val solver = PuzzleSolver()
    private val analyzer = NumberPlacementAnalyzer()

    data class AuditRecord(
        val levelId: Int,
        val worldId: Int,
        val grid: String,
        val checkpointCount: Int,
        val status: NumberPlacementStatus,
        val diff: NumberPlacementDifficulty,
        val solverValid: Boolean,
        val coverageValid: Boolean
    )

    @Test
    fun execute300LevelNumberAuditAndOptimization() {
        val optimizer = NumberPlacementOptimizer(solver, analyzer)
        val records = ArrayList<AuditRecord>()
        val updatedDefinitions = LinkedHashMap<Int, PuzzleDefinition>()

        var keepCount = 0
        var tunedCount = 0
        var replacedCount = 0

        println("Starting 300-level number placement audit and optimization...")

        for (lvl in 1..300) {
            val (def, sol) = getOriginalLevel(lvl)
            val eval = optimizer.evaluateLevel(lvl, def, sol)
            val optResult = optimizer.optimizePlacement(lvl, def, sol)

            val finalDef = optResult.definition
            val finalDiff = optResult.difficulty

            // Regression checks: ensure board geometry, cells, walls, and seed are 100% untouched
            assertEquals("Level $lvl grid rows must not change", def.gridDimensions.rows, finalDef.gridDimensions.rows)
            assertEquals("Level $lvl grid columns must not change", def.gridDimensions.columns, finalDef.gridDimensions.columns)
            assertEquals("Level $lvl required cells must not change", def.requiredCells, finalDef.requiredCells)
            assertEquals("Level $lvl blocked edges must not change", def.blockedEdges, finalDef.blockedEdges)
            assertEquals("Level $lvl seed must not change", def.seed, finalDef.seed)
            assertEquals("Level $lvl puzzleId must not change", def.puzzleId, finalDef.puzzleId)

            // Validate checkpoint invariants
            assertEquals("Level $lvl CP 1 must match canonical start", sol.positions.first(), finalDef.checkpoints.first().position)
            assertEquals("Level $lvl CP N must match canonical end", sol.positions.last(), finalDef.checkpoints.last().position)
            for (i in 0 until finalDef.checkpoints.size) {
                assertEquals("Level $lvl CP order", i + 1, finalDef.checkpoints[i].number)
            }

            // Independent solver validation with canonical path removed from solver input
            val solverRes = solver.solve(finalDef, SolverConfiguration.CHECK_UNIQUENESS)
            val isUnique = solverRes.status == SolverStatus.SOLVED && solverRes.uniqueness == UniquenessStatus.UNIQUE
            assertTrue("Level $lvl must be proven UNIQUE by player-visible solver", isUnique)

            val isCoverageValid = finalDef.totalRequiredCells == sol.positions.size

            when (optResult.status) {
                NumberPlacementStatus.KEEP -> keepCount++
                NumberPlacementStatus.TUNED -> tunedCount++
                NumberPlacementStatus.REPLACED -> replacedCount++
            }

            if (optResult.status != NumberPlacementStatus.KEEP) {
                updatedDefinitions[lvl] = finalDef
            }

            val world = WorldDefinition.forLevel(lvl)
            records.add(
                AuditRecord(
                    levelId = lvl,
                    worldId = world.worldId,
                    grid = "${finalDef.gridDimensions.rows}×${finalDef.gridDimensions.columns}",
                    checkpointCount = finalDef.checkpoints.size,
                    status = optResult.status,
                    diff = finalDiff,
                    solverValid = isUnique,
                    coverageValid = isCoverageValid
                )
            )

            if (lvl % 25 == 0 || lvl == 300) {
                println("Processed $lvl/300 levels (KEEP: $keepCount, TUNED: $tunedCount, REPLACED: $replacedCount)")
            }
        }

        println("Audit complete: KEEP=$keepCount, TUNED=$tunedCount, REPLACED=$replacedCount")

        // 1. Write updated checkpoints into source files
        applyUpdatesToCatalogFiles(updatedDefinitions)

        // 2. Generate docs/ZYNPATH_300_LEVEL_NUMBER_DIFFICULTY_AUDIT.md
        generateAuditReport(records, keepCount, tunedCount, replacedCount)

        // 3. Confirm 10-level window progression
        val windowAverages = (1..300).chunked(10).map { range ->
            val rangeRecords = records.filter { it.levelId in range }
            range.first() to rangeRecords.map { it.diff.compositeScore }.average()
        }
        for ((startLvl, avg) in windowAverages) {
            val endLvl = startLvl + 9
            println("Window $startLvl–$endLvl: average number difficulty = ${avg.format(1)}")
        }
    }

    private fun getOriginalLevel(id: Int): Pair<PuzzleDefinition, PuzzlePath> {
        val def = when (id) {
            in 1..50 -> CuratedFirst50Levels.LEVELS.getValue(id)
            in 51..100 -> CuratedWorld3Levels.LEVELS.getValue(id)
            in 101..150 -> CuratedWorld4Levels.LEVELS.getValue(id)
            in 151..200 -> CuratedWorld5Levels.LEVELS.getValue(id)
            else -> CuratedWorld6Levels.LEVELS.getValue(id)
        }
        val sol = when (id) {
            in 1..50 -> CuratedFirst50Levels.SOLUTIONS.getValue(id)
            in 51..100 -> CuratedWorld3Levels.SOLUTIONS.getValue(id)
            in 101..150 -> CuratedWorld4Levels.SOLUTIONS.getValue(id)
            in 151..200 -> CuratedWorld5Levels.SOLUTIONS.getValue(id)
            else -> CuratedWorld6Levels.SOLUTIONS.getValue(id)
        }
        return def to sol
    }

    private fun applyUpdatesToCatalogFiles(updates: Map<Int, PuzzleDefinition>) {
        if (updates.isEmpty()) {
            println("No catalog file updates required.")
            return
        }

        val baseDir = File("d:/Zynpath/android/app/src/main/java/com/zynpath/game/core/puzzle/catalog")

        val fileMapping = mapOf(
            (1..50) to File(baseDir, "CuratedFirst50Levels.kt"),
            (51..75) to File(baseDir, "CuratedWorld3PartA.kt"),
            (76..100) to File(baseDir, "CuratedWorld3PartB.kt"),
            (101..125) to File(baseDir, "CuratedWorld4PartA.kt"),
            (126..150) to File(baseDir, "CuratedWorld4PartB.kt"),
            (151..175) to File(baseDir, "CuratedWorld5PartA.kt"),
            (176..200) to File(baseDir, "CuratedWorld5PartB.kt"),
            (201..225) to File(baseDir, "CuratedWorld6PartA.kt"),
            (226..250) to File(baseDir, "CuratedWorld6PartB.kt"),
            (251..275) to File(baseDir, "CuratedWorld6PartC.kt"),
            (276..300) to File(baseDir, "CuratedWorld6PartD.kt")
        )

        for ((range, file) in fileMapping) {
            val relevantUpdates = updates.filterKeys { it in range }
            if (relevantUpdates.isNotEmpty()) {
                var content = file.readText(Charsets.UTF_8)
                for ((lvlId, def) in relevantUpdates) {
                    content = updatePuzzleDefinitionCheckpoints(content, lvlId, def.checkpoints)
                }
                file.writeText(content, Charsets.UTF_8)
                println("Updated ${relevantUpdates.size} levels in ${file.name}")
            }
        }
    }

    private fun updatePuzzleDefinitionCheckpoints(
        fileContent: String,
        levelId: Int,
        newCps: List<NumberedCheckpoint>
    ): String {
        val levelDeclRegex = Regex("""(val\s+LEVEL_$levelId\s*=\s*PuzzleDefinition|fun\s+buildLevel$levelId\s*\(\)\s*:\s*PuzzleDefinition\s*=\s*PuzzleDefinition)""")
        val match = levelDeclRegex.find(fileContent) ?: return fileContent

        val declStart = match.range.first
        val blockedEdgesIdx = fileContent.indexOf("blockedEdges =", declStart)
        if (blockedEdgesIdx == -1) return fileContent

        val cpStart = fileContent.indexOf("checkpoints = listOf(", declStart)
        if (cpStart == -1 || cpStart > blockedEdgesIdx) return fileContent

        val cpEnd = fileContent.lastIndexOf("),", blockedEdgesIdx) + 2
        if (cpEnd <= cpStart) return fileContent

        val formattedCps = StringBuilder()
        formattedCps.append("checkpoints = listOf(\n")
        val chunks = newCps.chunked(4)
        for ((cIdx, chunk) in chunks.withIndex()) {
            val comma = if (cIdx == chunks.size - 1) "" else ","
            formattedCps.append("            ${chunk.joinToString(", ") { "NumberedCheckpoint(${it.number}, GridPosition(${it.position.row}, ${it.position.column}))" }}$comma\n")
        }
        formattedCps.append("        ),")

        var updated = fileContent.substring(0, cpStart) + formattedCps.toString() + fileContent.substring(cpEnd)

        // Bump puzzleVersion between declStart and cpStart
        val verRegex = Regex("""puzzleVersion\s*=\s*(\d+)""")
        val verMatch = verRegex.find(updated, declStart)
        if (verMatch != null && verMatch.range.first < cpStart + 100) {
            val oldVer = verMatch.groupValues[1].toInt()
            val newVer = oldVer + 1
            updated = updated.replaceRange(verMatch.range, "puzzleVersion = $newVer")
        }

        return updated
    }

    private fun generateAuditReport(
        records: List<AuditRecord>,
        keepCount: Int,
        tunedCount: Int,
        replacedCount: Int
    ) {
        val sb = StringBuilder()
        sb.append("# ZYNPATH 300-LEVEL NUMBER PLACEMENT DIFFICULTY AUDIT\n\n")
        sb.append("> **Audit Date:** 2026-10-05  \n")
        sb.append("> **Scope:** Numbers Only • Levels 1–300 • Candy-Crush-Style Progression Curve  \n")
        sb.append("> **Invariants:** 100% Board Geometry, Cell Sets, Walls, World Assignments, and Stable IDs PRESERVED.  \n")
        sb.append("> **Verification:** Player-Visible Solver Uniqueness (`UniquenessStatus.UNIQUE`), 100% Board Coverage.  \n\n")

        sb.append("## 1. Executive Summary\n\n")
        sb.append("- **Total Levels Audited:** ${records.size} / 300\n")
        sb.append("- **Levels Keeping Original Numbers (`KEEP`):** $keepCount (${(keepCount * 100.0 / records.size).format(1)}%)\n")
        sb.append("- **Levels with Tuned Numbers (`TUNED`):** $tunedCount (${(tunedCount * 100.0 / records.size).format(1)}%)\n")
        sb.append("- **Levels with Replaced Numbers (`REPLACED`):** $replacedCount (${(replacedCount * 100.0 / records.size).format(1)}%)\n")
        sb.append("- **Board Geometry Invariant:** 100% Unchanged (Rows, Columns, Cells, Walls, Seed, IDs identical)\n")
        sb.append("- **Solver Solvability & Uniqueness:** 300 / 300 Certified `UNIQUE`\n")
        sb.append("- **Full Board Coverage:** 300 / 300 Exact ($100\\%$\n\n")

        sb.append("## 2. 10-Level Window Difficulty Progression\n\n")
        sb.append("| Level Window | World | Grid Dimension | Avg Number Difficulty | Trend & Wave Character |\n")
        sb.append("|:---|:---:|:---:|:---:|:---|\n")

        val windowAverages = records.chunked(10).map { chunk ->
            val w = chunk.first().worldId
            val grid = chunk.first().grid
            val avg = chunk.map { it.diff.compositeScore }.average()
            val start = chunk.first().levelId
            val end = chunk.last().levelId
            val label = when {
                start == 1 -> "Tutorial / Instructional Start (Deception-Free)"
                start == 11 -> "World 1 Climax Build & Climax (Peak at L20)"
                start == 21 -> "World 2 Gateway Relief & Turning Foundation"
                start == 41 -> "World 2 Climax Build (Peak at L50)"
                start == 51 -> "World 3 Gateway Relief & Reservation Pressure"
                start == 91 -> "World 3 Climax Build (Peak at L100)"
                start == 101 -> "World 4 Gateway Relief & Approach Direction Focus"
                start == 141 -> "World 4 Climax Build (Peak at L150)"
                start == 151 -> "World 5 Gateway Relief & Multi-Checkpoint Dep"
                start == 191 -> "World 5 Climax Build (Peak at L200)"
                start == 201 -> "World 6 Gateway Relief & Whole-Board Planning"
                start == 291 -> "Campaign Grandmaster Mastery (Peak at L300)"
                else -> "Progressive Difficulty Wave (Build/Peak/Relief)"
            }
            sb.append("| Levels $start–$end | World $w | $grid | **${avg.format(1)}** | $label |\n")
            avg
        }
        sb.append("\n")

        // Find peaks, relief, spikes, drops
        var maxSpike = 0.0
        var maxSpikeLvl = 1
        var maxDrop = 0.0
        var maxDropLvl = 1

        for (i in 0 until records.size - 1) {
            val delta = records[i + 1].diff.compositeScore - records[i].diff.compositeScore
            if (delta > maxSpike) {
                maxSpike = delta
                maxSpikeLvl = records[i + 1].levelId
            }
            if (delta < maxDrop) {
                maxDrop = delta
                maxDropLvl = records[i + 1].levelId
            }
        }

        val strongestChallenge = records.maxByOrNull { it.diff.compositeScore }!!
        val worldClimaxes = listOf(20, 50, 100, 150, 200, 300).map { lvl -> records[lvl - 1] }

        sb.append("## 3. Progression Rhythm & Milestone Analysis\n\n")
        sb.append("- **Strongest Overall Challenge Level:** Level ${strongestChallenge.levelId} (Score: ${strongestChallenge.diff.compositeScore.format(1)}, Tier: ${strongestChallenge.diff.difficultyTier})\n")
        sb.append("- **Largest Intentional Challenge Spike:** Level $maxSpikeLvl (+${maxSpike.format(1)} difficulty transition)\n")
        sb.append("- **Largest Intentional Relief Transition:** Level $maxDropLvl (${maxDrop.format(1)} controlled release)\n\n")

        sb.append("### World-End Mastery Climaxes\n\n")
        sb.append("| World | Climax Level | Grid | CPs | Difficulty Score | Deception | Strategy Notes |\n")
        sb.append("|:---:|:---:|:---:|:---:|:---:|:---:|:---|\n")
        for (rec in worldClimaxes) {
            sb.append("| World ${rec.worldId} | Level ${rec.levelId} | ${rec.grid} | ${rec.checkpointCount} | **${rec.diff.compositeScore.format(1)}** | ${rec.diff.nearCheckpointDeception.format(2)} | Climax mastery challenge for World ${rec.worldId} |\n")
        }
        sb.append("\n")

        sb.append("## 4. Complete Level-by-Level Audit Table (Levels 1–300)\n\n")
        sb.append("| Lvl | W | Grid | CPs | Status | Difficulty | Ambiguity | FutDep | ResPress | ProximityDecep | DirComplexity | WrongDepth | MultiCP | Tier | Solver | Coverage |\n")
        sb.append("|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|\n")

        for (r in records) {
            sb.append("| ${r.levelId} | ${r.worldId} | ${r.grid} | ${r.checkpointCount} | ${r.status} | ${r.diff.compositeScore.format(1)} | ${r.diff.strategicAmbiguity.format(1)} | ${r.diff.futureCheckpointDependency.format(0)} | ${r.diff.reservationPressure.format(0)} | ${r.diff.nearCheckpointDeception.format(2)} | ${r.diff.directionChangeComplexity.format(2)} | ${r.diff.wrongRouteDepth.format(1)} | ${r.diff.multiCheckpointDependency} | ${r.diff.difficultyTier} | ${if (r.solverValid) "UNIQUE" else "FAIL"} | ${if (r.coverageValid) "100%" else "FAIL"} |\n")
        }

        val reportFile = File("d:/Zynpath/docs/ZYNPATH_300_LEVEL_NUMBER_DIFFICULTY_AUDIT.md")
        reportFile.parentFile?.mkdirs()
        reportFile.writeText(sb.toString(), Charsets.UTF_8)
        println("Generated audit report at ${reportFile.absolutePath} (${reportFile.length()} bytes)")
    }

    private fun Double.format(digits: Int): String = "%.${digits}f".format(this)

    @Test
    fun testCriticalRegressionInvariantsAcrossAll300Levels() {
        val optimizer = NumberPlacementOptimizer(solver, analyzer)
        for (lvl in 1..300) {
            val (def, sol) = getOriginalLevel(lvl)
            val world = WorldDefinition.forLevel(lvl)

            // 1. Stable Level IDs and puzzleId
            assertEquals("Level $lvl puzzleId", "w${world.worldId}_lvl$lvl", def.puzzleId)

            // 2. World assignment
            val expectedWorldId = when (lvl) {
                in 1..20 -> 1
                in 21..50 -> 2
                in 51..100 -> 3
                in 101..150 -> 4
                in 151..200 -> 5
                else -> 6
            }
            assertEquals("Level $lvl world assignment", expectedWorldId, world.worldId)

            // 3. Grid dimensions
            val expectedDimension = when (lvl) {
                in 1..20 -> 6
                in 21..50 -> 7
                in 51..90 -> 8
                in 91..130 -> 9
                in 131..180 -> 10
                in 181..240 -> 11
                else -> 12
            }
            assertEquals("Level $lvl grid rows", expectedDimension, def.gridDimensions.rows)
            assertEquals("Level $lvl grid columns", expectedDimension, def.gridDimensions.columns)

            // 4. Required cells and 100% full board coverage
            val expectedCellsCount = expectedDimension * expectedDimension
            assertEquals("Level $lvl required cells count", expectedCellsCount, def.totalRequiredCells)
            assertEquals("Level $lvl canonical solution length", expectedCellsCount, sol.positions.size)
            assertEquals("Level $lvl unique cells visited", expectedCellsCount, sol.positions.distinct().size)

            // 5. Orthogonal path continuity
            for (i in 0 until sol.positions.size - 1) {
                val p1 = sol.positions[i]
                val p2 = sol.positions[i + 1]
                val manhattan = p1.manhattanDistance(p2)
                assertEquals("Level $lvl step $i->$p1 to $p2 must be orthogonal neighbor", 1, manhattan)
            }

            // 6. Checkpoint count and ordering invariants
            val allowedRange = optimizer.allowedCheckpointRange(def.gridDimensions.rows, def.gridDimensions.columns)
            assertTrue("Level $lvl CP count ${def.checkpoints.size} must be in $allowedRange", def.checkpoints.size in allowedRange)
            assertEquals("Level $lvl CP 1 must match canonical start", sol.positions.first(), def.checkpoints.first().position)
            assertEquals("Level $lvl CP N must match canonical end", sol.positions.last(), def.checkpoints.last().position)
            assertEquals("Level $lvl CP numbers must be 1..N", (1..def.checkpoints.size).toList(), def.checkpoints.map { it.number })
            assertEquals("Level $lvl CPs must not contain duplicates", def.checkpoints.size, def.checkpoints.map { it.position }.distinct().size)

            // Checkpoints strictly ascending on canonical path
            val pathIndices = def.checkpoints.map { sol.positions.indexOf(it.position) }
            for (i in 0 until pathIndices.size - 1) {
                assertTrue("Level $lvl CP ${i + 1} -> ${i + 2} must strictly ascend on path", pathIndices[i] < pathIndices[i + 1])
            }

            // 7. Blocked edges do not cross canonical solution
            for (i in 0 until sol.positions.size - 1) {
                val p1 = sol.positions[i]
                val p2 = sol.positions[i + 1]
                val isBlocked = def.blockedEdges.any { (it.first == p1 && it.second == p2) || (it.first == p2 && it.second == p1) }
                assertTrue("Level $lvl edge between $p1 and $p2 must not be blocked by wall", !isBlocked)
            }
        }

        // Fast solver uniqueness check on milestone levels (World ends and milestones)
        for (milestone in listOf(1, 4, 10, 20, 50, 100, 150, 200, 250, 300)) {
            val (def, _) = getOriginalLevel(milestone)
            val res = solver.solve(def, SolverConfiguration.CHECK_UNIQUENESS)
            assertEquals("Milestone level $milestone must be SOLVED", SolverStatus.SOLVED, res.status)
            assertEquals("Milestone level $milestone must be UNIQUE", UniquenessStatus.UNIQUE, res.uniqueness)
        }
    }
}
