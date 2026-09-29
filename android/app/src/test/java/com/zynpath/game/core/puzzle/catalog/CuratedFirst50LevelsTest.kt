package com.zynpath.game.core.puzzle.catalog

import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.experience.MilestoneType
import com.zynpath.game.core.puzzle.experience.ProgressionPlan
import com.zynpath.game.core.puzzle.generator.PuzzleFingerprint
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintConfiguration
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Focused unit tests for Curated Solo Levels 1–50 and Early Progression.
 *
 * Implements Prompt 28 Task 16:
 * 1. All 50 stable level IDs.
 * 2. Valid clue sequence.
 * 3. Valid continuous solutions.
 * 4. Full playable-cell coverage.
 * 5. Solvability via authoritative CompletionValidator.
 * 6. No exact duplicate level fingerprints.
 * 7. Appropriate difficulty progression.
 * 8. Chapter mapping (1–25: Chapter 1, 26–50: Chapter 2).
 * 9. Existing guest progress compatibility (Level 1 preserved).
 * 10. Hint validity against canonical solutions.
 * 11. Level 25 Chapter 1 milestone.
 * 12. Level 50 Chapter 2 milestone.
 */
class CuratedFirst50LevelsTest {

    // 1. All 50 stable level IDs
    @Test
    fun `test all 50 level IDs exist and have valid definitions and solutions`() {
        assertEquals("Must contain exactly 50 curated level definitions", 50, CuratedFirst50Levels.LEVELS.size)
        assertEquals("Must contain exactly 50 verified canonical solutions", 50, CuratedFirst50Levels.SOLUTIONS.size)

        for (id in 1..50) {
            val def = CuratedFirst50Levels.LEVELS[id]
            val sol = CuratedFirst50Levels.SOLUTIONS[id]
            assertNotNull("Level $id definition must exist", def)
            assertNotNull("Level $id solution must exist", sol)
            assertEquals("Level $id definition must have matching puzzleId", if (id <= 20) "w1_lvl$id" else "w2_lvl$id", def?.puzzleId)
        }
    }

    // 2. Valid clue sequence
    @Test
    fun `test all 50 levels have checkpoints numbered 1 to N without gaps`() {
        for (id in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(id)
            val checkpoints = def.checkpoints.sortedBy { it.number }
            assertTrue("Level $id must have at least 4 checkpoints", checkpoints.size >= 4)
            for (idx in checkpoints.indices) {
                assertEquals("Checkpoint index must match 1-based number in level $id", idx + 1, checkpoints[idx].number)
            }
        }
    }

    // 3. Valid continuous solutions
    @Test
    fun `test all 50 solutions are strictly continuous and orthogonally connected`() {
        for (id in 1..50) {
            val sol = CuratedFirst50Levels.SOLUTIONS.getValue(id)
            val positions = sol.positions
            val expectedSize = if (id <= 20) 16 else 25
            assertEquals("Level $id solution must have exactly $expectedSize cells", expectedSize, positions.size)

            for (i in 0 until positions.size - 1) {
                val current = positions[i]
                val next = positions[i + 1]
                assertTrue("Step $i in Level $id from $current to $next must be strictly orthogonally adjacent", current.isOrthogonalNeighbor(next))
            }
        }
    }

    // 4. Full playable-cell coverage
    @Test
    fun `test all 50 solutions cover 100 percent of required cells without duplicates`() {
        for (id in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(id)
            val sol = CuratedFirst50Levels.SOLUTIONS.getValue(id)

            val solutionSet = sol.positions.toSet()
            assertEquals("Level $id solution must contain no duplicate cell visits", sol.positions.size, solutionSet.size)
            assertEquals("Level $id solution must cover all required playable cells", def.requiredCells, solutionSet)
        }
    }

    // 5. Solvability via authoritative CompletionValidator
    @Test
    fun `test all 50 levels pass authoritative puzzle engine and completion validator`() {
        for (id in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(id)
            val sol = CuratedFirst50Levels.SOLUTIONS.getValue(id)
            val engine = PuzzleEngine(def)

            for (i in sol.positions.indices) {
                val pos = sol.positions[i]
                val result = if (i == 0) {
                    engine.process(PuzzleAction.StartPath(pos))
                } else {
                    engine.process(PuzzleAction.ExtendPath(pos))
                }
                assertTrue("Step $i in Level $id at $pos must be accepted by engine", result.isAccepted)
            }

            assertEquals("Level $id must be COMPLETED", GameStatus.COMPLETED, engine.currentState.gameStatus)
            val validation = CompletionValidator.validate(def, engine.currentState.currentPath)
            assertTrue("Level $id must pass authoritative CompletionValidator", validation.isSuccess)
        }
    }

    // 6. No exact duplicate level fingerprints
    @Test
    fun `test all 50 levels have distinct canonical fingerprints`() {
        val seenFingerprints = mutableSetOf<String>()
        for (id in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(id)
            val fp = PuzzleFingerprint.computeSha256(def)
            assertFalse("Level $id has duplicate fingerprint with an earlier level", seenFingerprints.contains(fp))
            seenFingerprints.add(fp)
        }
        assertEquals(50, seenFingerprints.size)
    }

    // 7. Appropriate difficulty progression
    @Test
    fun `test difficulty progression from 4x4 introductory to 5x5 strategic`() {
        for (id in 1..20) {
            val def = CuratedFirst50Levels.LEVELS.getValue(id)
            assertEquals("Levels 1..20 must have 4x4 grid dimensions", 4, def.gridDimensions.rows)
            assertEquals("Levels 1..20 must have 4x4 grid dimensions", 4, def.gridDimensions.columns)
            assertEquals(16, def.totalRequiredCells)
        }
        for (id in 21..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(id)
            assertEquals("Levels 21..50 must have 5x5 grid dimensions", 5, def.gridDimensions.rows)
            assertEquals("Levels 21..50 must have 5x5 grid dimensions", 5, def.gridDimensions.columns)
            assertEquals(25, def.totalRequiredCells)
        }
    }

    // 8. Chapter mapping
    @Test
    fun `test levels 1 to 25 belong to Chapter 1 and levels 26 to 50 belong to Chapter 2`() {
        for (id in 1..25) {
            val chapter = ProgressionPlan.getChapterForLevel(id)
            assertEquals("Level $id must belong to Chapter 1", 1, chapter.chapterId)
            assertEquals("First Steps", chapter.title)
        }
        for (id in 26..50) {
            val chapter = ProgressionPlan.getChapterForLevel(id)
            assertEquals("Level $id must belong to Chapter 2", 2, chapter.chapterId)
            assertEquals("Smart Turns", chapter.title)
        }
    }

    // 9. Existing guest progress compatibility (Level 1 preserved)
    @Test
    fun `test level 1 definition matches PackagedPuzzles LEVEL_1 exactly`() {
        val lvl1 = CuratedFirst50Levels.LEVELS.getValue(1)
        assertEquals(PackagedPuzzles.LEVEL_1.puzzleId, lvl1.puzzleId)
        assertEquals(PackagedPuzzles.LEVEL_1.checkpoints, lvl1.checkpoints)
        assertEquals(PackagedPuzzles.LEVEL_1.requiredCells, lvl1.requiredCells)
        assertEquals(PackagedPuzzles.SOLUTION_1_ROUTE.positions, CuratedFirst50Levels.SOLUTIONS.getValue(1).positions)
    }

    // 10. Hint validity
    @Test
    fun `test hint engine suggests legal move along solution for every level`() {
        val hintEngine = PuzzleHintEngine()
        for (id in 1..50) {
            val def = CuratedFirst50Levels.LEVELS.getValue(id)
            val sol = CuratedFirst50Levels.SOLUTIONS.getValue(id)
            val startPos = sol.positions.first()

            val request = HintRequest(
                puzzleId = def.puzzleId,
                puzzleVersion = def.puzzleVersion,
                definition = def,
                currentPath = com.zynpath.game.core.puzzle.model.PuzzlePath.single(startPos),
                gameMode = GameMode.SOLO,
                configuration = HintConfiguration()
            )

            val result = hintEngine.computeHint(request)
            assertTrue("Level $id hint must produce NextMove", result is HintResult.NextMove)
            val nextMove = (result as HintResult.NextMove).nextMove
            assertTrue(
                "Level $id hint from start must be a valid step adjacent to start",
                nextMove == sol.positions[1] || (nextMove in def.requiredCells && startPos.isOrthogonallyAdjacentTo(nextMove))
            )
        }
    }

    // 11. Level 25 Chapter 1 milestone
    @Test
    fun `test level 25 is recognized as Chapter 1 climax milestone`() {
        val exp = ProgressionPlan.getLevelExperience(25)
        assertNotNull("Level 25 must have milestone indicator", exp.milestoneIndicator)
        assertEquals(MilestoneType.CHAPTER_CLIMAX, exp.milestoneIndicator?.type)
        assertEquals("First Steps Mastered", exp.milestoneIndicator?.milestoneTitle)
        assertEquals("Chapter 1 Completed", exp.milestoneIndicator?.milestoneSubtitle)
        assertEquals(2, exp.milestoneIndicator?.celebrationTier)
    }

    // 12. Level 50 Chapter 2 milestone
    @Test
    fun `test level 50 is recognized as Chapter 2 climax milestone`() {
        val exp = ProgressionPlan.getLevelExperience(50)
        assertNotNull("Level 50 must have milestone indicator", exp.milestoneIndicator)
        assertEquals(MilestoneType.CHAPTER_CLIMAX, exp.milestoneIndicator?.type)
        assertEquals("Sharp Thinker", exp.milestoneIndicator?.milestoneTitle)
        assertEquals("Chapter 2 Completed", exp.milestoneIndicator?.milestoneSubtitle)
        assertEquals(2, exp.milestoneIndicator?.celebrationTier)
    }
}
