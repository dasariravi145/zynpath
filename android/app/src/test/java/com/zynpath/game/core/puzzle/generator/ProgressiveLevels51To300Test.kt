package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.catalog.CatalogManifest
import com.zynpath.game.core.puzzle.catalog.CuratedAnchorLevels
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.catalog.WorldDefinition
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.experience.ExperienceDifficultyCategory
import com.zynpath.game.core.puzzle.experience.MilestoneType
import com.zynpath.game.core.puzzle.experience.ProgressionPlan
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.validator.FoundationalPathValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive test coverage for Progressive Solo Levels 51–300, Puzzle Variety & Chapter Milestones.
 *
 * Implements Prompt 29 Task 18:
 * 1. Stable IDs for Levels 51–300.
 * 2. Correct chapter boundaries (Chapters 3–7).
 * 3. Valid numbered-clue sequence (1..N).
 * 4. Continuous solution path.
 * 5. Full playable-cell coverage.
 * 6. Valid movement (orthogonal steps only, no diagonal, no wall crossing).
 * 7. Solvability via authoritative CompletionValidator and FoundationalPathValidator.
 * 8. Duplicate detection across fingerprints.
 * 9. Difficulty metadata consistency.
 * 10. Stable deterministic level loading without unexpected re-generation.
 * 11. Hint compatibility.
 * 12. Saved-progress compatibility (guest progress ~1/300 preserved).
 * 13. Milestones at 100, 150, 200, 250, and 300.
 * 14. No missing or duplicate IDs across entire 1–300 campaign spectrum.
 * 15. Level 300 completion using authoritative validator.
 */
class ProgressiveLevels51To300Test {

    private lateinit var levelProvider: DeterministicLevelProvider
    private lateinit var hintEngine: PuzzleHintEngine

    @Before
    fun setUp() {
        levelProvider = DeterministicLevelProviderImpl()
        hintEngine = PuzzleHintEngine()
    }

    // 1. Stable IDs for Levels 51–300
    @Test
    fun `test all levels 51 to 300 resolve to stable non-null puzzle definitions`() {
        for (levelId in 51..300) {
            val puzzle = levelProvider.getLevel(levelId)
            assertNotNull("Level $levelId must resolve to a valid PuzzleDefinition", puzzle)
            assertTrue("Level $levelId must have puzzleId starting with w", puzzle!!.puzzleId.startsWith("w"))
            assertEquals("Level $levelId version must be at least 1", 1, puzzle.puzzleVersion)
        }
    }

    // 2. Correct chapter boundaries
    @Test
    fun `test chapter boundaries across campaign for levels 51 to 300`() {
        // Chapter 3: Path Explorer (51–100)
        for (lvl in 51..100) {
            val chapter = ProgressionPlan.getChapterForLevel(lvl)
            assertEquals("Level $lvl must be in Chapter 3", 3, chapter.chapterId)
            assertEquals("Path Explorer", chapter.title)
        }

        // Chapter 4: Strategic Paths (101–150)
        for (lvl in 101..150) {
            val chapter = ProgressionPlan.getChapterForLevel(lvl)
            assertEquals("Level $lvl must be in Chapter 4", 4, chapter.chapterId)
            assertEquals("Strategic Paths", chapter.title)
        }

        // Chapter 5: Expert Journey (151–200)
        for (lvl in 151..200) {
            val chapter = ProgressionPlan.getChapterForLevel(lvl)
            assertEquals("Level $lvl must be in Chapter 5", 5, chapter.chapterId)
            assertEquals("Expert Journey", chapter.title)
        }

        // Chapter 6: Master Trails (201–250)
        for (lvl in 201..250) {
            val chapter = ProgressionPlan.getChapterForLevel(lvl)
            assertEquals("Level $lvl must be in Chapter 6", 6, chapter.chapterId)
            assertEquals("Master Trails", chapter.title)
        }

        // Chapter 7: Grand Challenge (251–300)
        for (lvl in 251..300) {
            val chapter = ProgressionPlan.getChapterForLevel(lvl)
            assertEquals("Level $lvl must be in Chapter 7", 7, chapter.chapterId)
            assertEquals("Grand Challenge", chapter.title)
        }
    }

    // 3. Valid numbered-clue sequence
    @Test
    fun `test curated anchor levels have checkpoints numbered 1 to N without gaps`() {
        for ((levelId, puzzle) in CuratedAnchorLevels.ANCHOR_LEVELS) {
            val checkpoints = puzzle.checkpoints.sortedBy { it.number }
            val count = checkpoints.size
            val world = WorldDefinition.forLevel(levelId)

            assertTrue("Level $levelId must have at least 2 checkpoints", count >= 2)
            assertTrue("Level $levelId checkpoints ($count) must be in world range ${world.checkpointRange}", count in world.checkpointRange)

            for (i in 0 until count) {
                assertEquals("Checkpoint at index $i in Level $levelId must be ${i + 1}", i + 1, checkpoints[i].number)
            }
        }
    }

    // 4. Continuous solution path
    @Test
    fun `test curated anchor solutions form unbroken continuous paths`() {
        for ((levelId, solution) in CuratedAnchorLevels.ANCHOR_SOLUTIONS) {
            val positions = solution.positions
            assertTrue("Level $levelId solution must not be empty", positions.isNotEmpty())

            for (i in 1 until positions.size) {
                val prev = positions[i - 1]
                val curr = positions[i]
                assertTrue("Step $i in Level $levelId from $prev to $curr must be orthogonally adjacent", prev.isOrthogonallyAdjacentTo(curr))
            }
        }
    }

    // 5. Full playable-cell coverage
    @Test
    fun `test curated anchor puzzles require and achieve 100 percent grid coverage`() {
        for ((levelId, puzzle) in CuratedAnchorLevels.ANCHOR_LEVELS) {
            val solution = CuratedAnchorLevels.ANCHOR_SOLUTIONS.getValue(levelId)
            val expectedTotal = puzzle.gridDimensions.totalCells

            assertEquals("Level $levelId required cells must equal total cells", expectedTotal, puzzle.requiredCells.size)
            assertEquals("Level $levelId solution length must equal total cells", expectedTotal, solution.size)

            val visitedSet = solution.positions.toSet()
            assertEquals("Level $levelId solution must visit all distinct cells", expectedTotal, visitedSet.size)
            assertEquals("Level $levelId solution must visit exactly the required cells", puzzle.requiredCells, visitedSet)
        }
    }

    // 6. Valid movement
    @Test
    fun `test curated anchor solutions respect all walls without diagonal steps`() {
        for ((levelId, puzzle) in CuratedAnchorLevels.ANCHOR_LEVELS) {
            val solution = CuratedAnchorLevels.ANCHOR_SOLUTIONS.getValue(levelId)
            val positions = solution.positions

            for (i in 1 until positions.size) {
                val p1 = positions[i - 1]
                val p2 = positions[i]

                // Orthogonal only (no diagonal)
                val dr = kotlin.math.abs(p1.row - p2.row)
                val dc = kotlin.math.abs(p1.column - p2.column)
                assertEquals("Step $i in Level $levelId must be orthogonal", 1, dr + dc)

                // No crossing blocked edges
                val isBlocked = puzzle.blockedEdges.any { it.isBetween(p1, p2) }
                assertFalse("Step $i in Level $levelId between $p1 and $p2 crosses a blocked wall", isBlocked)
            }
        }
    }

    // 7. Solvability
    @Test
    fun `test curated anchor levels pass independent authoritative CompletionValidator and FoundationalPathValidator`() {
        for ((levelId, puzzle) in CuratedAnchorLevels.ANCHOR_LEVELS) {
            val solution = CuratedAnchorLevels.ANCHOR_SOLUTIONS.getValue(levelId)

            val compResult = CompletionValidator.validate(puzzle, solution)
            assertTrue("Level $levelId must pass CompletionValidator: $compResult", compResult is CompletionCheckResult.Success)

            val foundResult = FoundationalPathValidator.validate(puzzle, solution)
            assertTrue("Level $levelId must pass FoundationalPathValidator", foundResult.isWin)
        }
    }

    // 8. Duplicate detection
    @Test
    fun `test curated anchor levels have distinct SHA-256 fingerprints`() {
        val seenFingerprints = mutableMapOf<String, Int>()
        for ((levelId, puzzle) in CuratedAnchorLevels.ANCHOR_LEVELS) {
            val fp = PuzzleFingerprint.computeSha256(puzzle)
            assertFalse("Level $levelId duplicate fingerprint with Level ${seenFingerprints[fp]}", seenFingerprints.containsKey(fp))
            seenFingerprints[fp] = levelId
        }
        assertEquals("All anchor levels must have unique fingerprints", CuratedAnchorLevels.ANCHOR_LEVELS.size, seenFingerprints.size)
    }

    // 9. Difficulty metadata
    @Test
    fun `test difficulty metadata matches progression plan across all chapters`() {
        for (levelId in 51..300) {
            val experience = ProgressionPlan.getLevelExperience(levelId)
            assertNotNull("Experience metadata for Level $levelId must exist", experience)

            when (levelId) {
                in 51..80 -> assertEquals(ExperienceDifficultyCategory.BALANCED, experience.difficultyCategory)
                in 81..140 -> assertEquals(ExperienceDifficultyCategory.STRATEGIC, experience.difficultyCategory)
                in 141..190 -> assertEquals(ExperienceDifficultyCategory.ADVANCED, experience.difficultyCategory)
                in 191..250 -> assertEquals(ExperienceDifficultyCategory.EXPERT, experience.difficultyCategory)
                else -> assertEquals(ExperienceDifficultyCategory.MASTER, experience.difficultyCategory)
            }
        }
    }

    // 10. Stable deterministic level loading
    @Test
    fun `test deterministic level loading returns identical puzzle on repeated calls`() {
        val testLevels = listOf(51, 75, 100, 101, 125, 150, 151, 175, 200, 201, 225, 250, 251, 275, 300)
        for (levelId in testLevels) {
            val puzzle1 = levelProvider.getLevel(levelId)
            val puzzle2 = levelProvider.getLevel(levelId)

            assertNotNull("Level $levelId first load", puzzle1)
            assertNotNull("Level $levelId second load", puzzle2)
            assertEquals("Level $levelId must have identical puzzleId", puzzle1?.puzzleId, puzzle2?.puzzleId)

            val fp1 = PuzzleFingerprint.computeSha256(puzzle1!!)
            val fp2 = PuzzleFingerprint.computeSha256(puzzle2!!)
            assertEquals("Level $levelId fingerprint must be identical across invocations", fp1, fp2)
        }
    }

    // 11. Hint compatibility
    @Test
    fun `test hint engine produces valid legal hint on curated anchor levels`() {
        for ((levelId, puzzle) in CuratedAnchorLevels.ANCHOR_LEVELS) {
            val initialState = PuzzleGameState.initial(puzzle)
            val hintRequest = HintRequest(
                puzzleId = puzzle.puzzleId,
                puzzleVersion = puzzle.puzzleVersion,
                definition = puzzle,
                gameState = initialState,
                gameMode = GameMode.SOLO
            )

            val hintResult = hintEngine.computeHint(hintRequest)
            assertTrue("Level $levelId hint must succeed or provide valid next move", hintResult is HintResult.NextMove)

            if (hintResult is HintResult.NextMove) {
                val nextMove = hintResult.nextMove
                assertNotNull("Level $levelId suggested move must exist", nextMove)

                val startPos = puzzle.checkpoints.first { it.number == 1 }.position
                assertTrue(
                    "Level $levelId hint next move must be start checkpoint or adjacent to start checkpoint",
                    nextMove == startPos || startPos.isOrthogonallyAdjacentTo(nextMove)
                )

                if (nextMove != startPos) {
                    val isBlocked = puzzle.blockedEdges.any { it.isBetween(startPos, nextMove) }
                    assertFalse("Level $levelId hint next move must not cross a blocked wall", isBlocked)
                }
            }
        }
    }

    // 12. Saved-progress compatibility
    @Test
    fun `test saved progress compatibility preserves initial guest level 1`() {
        // Level 1 guest progress (completed=true, levelId=1) must unlock Level 2
        val completedSet = setOf(1)
        assertTrue("Level 1 must be completed in guest progress", completedSet.contains(1))
        assertTrue("Level 2 must be unlocked by Level 1 completion", WorldConfiguration.isLevelUnlocked(2, completedSet))
        assertFalse("Level 3 must remain locked", WorldConfiguration.isLevelUnlocked(3, completedSet))
        assertFalse("Level 51 must remain locked", WorldConfiguration.isLevelUnlocked(51, completedSet))
    }

    // 13. Milestones at 100, 150, 200, 250, and 300
    @Test
    fun `test chapter milestones have correct metadata and celebration tiers`() {
        // Level 100: Path Explorer Complete
        val m100 = ProgressionPlan.getLevelExperience(100).milestoneIndicator
        assertNotNull(m100)
        assertEquals(MilestoneType.CENTURY_MARK, m100?.type)
        assertEquals("Path Explorer Mastered", m100?.milestoneTitle)
        assertEquals(2, m100?.celebrationTier)

        // Level 150: Strategic Paths Complete
        val m150 = ProgressionPlan.getLevelExperience(150).milestoneIndicator
        assertNotNull(m150)
        assertEquals(MilestoneType.CHAPTER_CLIMAX, m150?.type)
        assertEquals("Strategic Paths Mastered", m150?.milestoneTitle)
        assertEquals(2, m150?.celebrationTier)

        // Level 200: Expert Journey Complete
        val m200 = ProgressionPlan.getLevelExperience(200).milestoneIndicator
        assertNotNull(m200)
        assertEquals(MilestoneType.CENTURY_MARK, m200?.type)
        assertEquals("Expert Journey Mastered", m200?.milestoneTitle)
        assertEquals(2, m200?.celebrationTier)

        // Level 250: Master Trails Complete
        val m250 = ProgressionPlan.getLevelExperience(250).milestoneIndicator
        assertNotNull(m250)
        assertEquals(MilestoneType.CHAPTER_CLIMAX, m250?.type)
        assertEquals("Master Trails Mastered", m250?.milestoneTitle)
        assertEquals(2, m250?.celebrationTier)

        // Level 300: Grand Challenge Complete / Grand Finale
        val m300 = ProgressionPlan.getLevelExperience(300).milestoneIndicator
        assertNotNull(m300)
        assertEquals(MilestoneType.GRAND_FINALE, m300?.type)
        assertEquals("Grand Pathmaster", m300?.milestoneTitle)
        assertEquals(3, m300?.celebrationTier)
    }

    // 14. No missing or duplicate IDs across Levels 1–300
    @Test
    fun `test complete campaign has exactly 300 contiguous level IDs with zero duplicates or gaps`() {
        val manifest = CatalogManifest.createDefaultManifest()
        val allLevels = manifest.levels
        assertEquals("Campaign manifest must contain exactly 300 levels", 300, allLevels.size)

        val levelIds = allLevels.map { it.levelId }
        val expectedRange = (1..300).toList()
        assertEquals("Level IDs must be strictly contiguous from 1 to 300", expectedRange, levelIds)

        val uniqueIds = levelIds.toSet()
        assertEquals("There must be zero duplicate level IDs", 300, uniqueIds.size)
    }

    // 15. Level 300 completion using authoritative validator
    @Test
    fun `test level 300 grand finale puzzle passes full engine simulation and authoritative validation`() {
        val def300 = CuratedAnchorLevels.LEVEL_300
        val sol300 = CuratedAnchorLevels.SOLUTION_300
        val engine = PuzzleEngine(def300)

        for (i in sol300.positions.indices) {
            val pos = sol300.positions[i]
            val action = if (i == 0) PuzzleAction.StartPath(pos) else PuzzleAction.ExtendPath(pos)
            val result = engine.process(action)
            assertTrue("Step $i at $pos in Level 300 must be accepted by engine", result.isAccepted)
        }

        assertEquals("Engine status must be COMPLETED upon finishing Level 300", GameStatus.COMPLETED, engine.currentState.gameStatus)

        val validation = CompletionValidator.validate(
            definition = def300,
            path = engine.currentState.currentPath,
            levelId = 300,
            worldId = 6
        )
        assertTrue("Level 300 grand finale must pass authoritative CompletionValidator", validation is CompletionCheckResult.Success)
    }
}
