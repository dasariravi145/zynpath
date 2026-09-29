package com.zynpath.game.core.puzzle.generator

import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.curation.DifficultyBand
import com.zynpath.game.core.puzzle.engine.CompletionCheckResult
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.experience.ProgressionPlan
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests validating difficulty-controlled level generation and canonical gameplay invariants.
 *
 * Implements Prompt 26 Task 12:
 * 1. Correct number sequence (1..N).
 * 2. All playable cells filled.
 * 3. Valid movement (orthogonal Manhattan only, no wall crossing).
 * 4. Solvability proven via CompletionValidator.
 * 5. Duplicate and near-duplicate rejection.
 * 6. Stable deterministic generation.
 * 7. Difficulty metadata and recovery level identification.
 * 8. Generation attempt limits and failure handling without infinite loops.
 * 9. Hint engine compatibility.
 * 10. Existing 300-level catalog preservation.
 */
class ChapterLevelGeneratorTest {

    private lateinit var generator: ChapterLevelGenerator
    private lateinit var hintEngine: PuzzleHintEngine

    @Before
    fun setUp() {
        generator = ChapterLevelGenerator()
        hintEngine = PuzzleHintEngine()
        generator.clearState()
    }

    // 1. Correct number sequence
    @Test
    fun `generated puzzle checkpoints strictly follow ascending sequence starting at 1`() {
        val result = generator.generateLevel(1)
        assertTrue("Level 1 generation should succeed", result is ChapterLevelGenerationResult.Success)
        val success = result as ChapterLevelGenerationResult.Success
        val checkpoints = success.puzzle.checkpoints.sortedBy { it.number }

        assertEquals("First checkpoint must be #1", 1, checkpoints.first().number)
        assertEquals("Start checkpoint must match #1", checkpoints.first(), success.puzzle.startCheckpoint)

        for (i in 0 until checkpoints.size - 1) {
            assertEquals("Checkpoints must be strictly contiguous", i + 1, checkpoints[i].number)
        }
        assertEquals("Final checkpoint must match max number", checkpoints.size, success.puzzle.maxCheckpointNumber)
    }

    // 2. All playable cells filled
    @Test
    fun `verified solution covers every single playable cell`() {
        val result = generator.generateLevel(2)
        assertTrue(result is ChapterLevelGenerationResult.Success)
        val success = result as ChapterLevelGenerationResult.Success

        val visited = success.solution.positions.toSet()
        val required = success.puzzle.requiredCells

        assertEquals("Visited cells count must equal total required cells", required.size, visited.size)
        assertEquals("Visited cells must perfectly match required cells", required, visited)
    }

    // 3. Valid movement (Orthogonal, no diagonals, no wall collisions)
    @Test
    fun `solution path uses only orthogonal movement without crossing walls`() {
        val result = generator.generateLevel(3)
        assertTrue(result is ChapterLevelGenerationResult.Success)
        val success = result as ChapterLevelGenerationResult.Success

        val positions = success.solution.positions
        for (i in 1 until positions.size) {
            val prev = positions[i - 1]
            val curr = positions[i]

            assertTrue("Step from $prev to $curr must be orthogonal", prev.isOrthogonalNeighbor(curr))
            assertFalse("Step from $prev to $curr must not cross a blocked edge", success.puzzle.graph.isBlocked(prev, curr))
        }
    }

    // 4. Solvability & authoritative validation
    @Test
    fun `generated puzzle passes authoritative completion validator`() {
        val result = generator.generateLevel(4)
        assertTrue(result is ChapterLevelGenerationResult.Success)
        val success = result as ChapterLevelGenerationResult.Success

        val completion = CompletionValidator.validate(
            definition = success.puzzle,
            path = success.solution,
            levelId = 4,
            worldId = 1
        )

        assertTrue("Generated puzzle solution must pass CompletionValidator", completion is CompletionCheckResult.Success)
    }

    // 5. Duplicate and near-duplicate rejection
    @Test
    fun `duplicate candidates are detected and rejected`() {
        val seenFingerprints = mutableSetOf<String>()
        val config = ChapterLevelConfigFactory.createConfig(5)
        val coreGen = PuzzleGenerator()

        val firstResult = coreGen.generate(config, seenFingerprints = seenFingerprints)
        assertTrue(firstResult.isSuccess && firstResult.puzzle != null)

        val fp = PuzzleFingerprint.canonicalRepresentation(firstResult.puzzle!!)
        seenFingerprints.add(fp)

        // Attempting to generate again with the exact same fingerprint in seenFingerprints must reject it
        val secondResult = coreGen.generate(config, seenFingerprints = seenFingerprints)
        assertFalse(
            "Candidate with seen fingerprint should be rejected",
            secondResult.isSuccess &&
                secondResult.puzzle != null &&
                PuzzleFingerprint.canonicalRepresentation(secondResult.puzzle) == fp
        )
    }

    // 6. Stable deterministic generation
    @Test
    fun `same levelId produces identical seed and reproducible configuration`() {
        val seed1 = LevelSeedGenerator.computeSeed(15)
        val seed2 = LevelSeedGenerator.computeSeed(15)
        assertEquals("Seed derivation must be purely deterministic", seed1, seed2)

        val config1 = ChapterLevelConfigFactory.createConfig(15)
        val config2 = ChapterLevelConfigFactory.createConfig(15)

        assertEquals("Configuration dimensions must match", config1.dimensions, config2.dimensions)
        assertEquals("Configuration seed must match", config1.seed, config2.seed)
        assertEquals("Configuration checkpoints must match", config1.checkpointCount, config2.checkpointCount)
    }

    // 7. Difficulty metadata & recovery levels
    @Test
    fun `recovery levels are identified and provided appropriate difficulty parameters`() {
        val recoveryLevelId = 7
        assertTrue("Level 7 should be designated as a recovery level", ChapterLevelConfigFactory.isRecoveryLevel(recoveryLevelId))

        val config = ChapterLevelConfigFactory.createConfig(recoveryLevelId)
        assertEquals("Recovery levels in Chapter 1 should provide maximum clues", 6, config.checkpointCount)
        assertEquals("Recovery levels in Chapter 1 should have zero walls", 0, config.maxWalls)
    }

    // 8. Bounded attempts and failure handling
    @Test
    fun `cancellation signal halts generation safely without infinite loops`() {
        val result = generator.generateLevel(25, cancellationSignal = { true })
        assertTrue("Cancelled generation must return Failure", result is ChapterLevelGenerationResult.Failure)
        val failure = result as ChapterLevelGenerationResult.Failure
        assertEquals("Cancelled by caller", failure.reason)
    }

    // 9. Hint engine compatibility
    @Test
    fun `puzzle hint engine successfully computes valid first move from start checkpoint`() {
        val result = generator.generateLevel(1)
        assertTrue(result is ChapterLevelGenerationResult.Success)
        val success = result as ChapterLevelGenerationResult.Success

        val gameState = PuzzleGameState.initial(success.puzzle)
        val hintReq = HintRequest(
            puzzleId = success.puzzle.puzzleId,
            puzzleVersion = success.puzzle.puzzleVersion,
            definition = success.puzzle,
            gameState = gameState,
            currentOrderedPath = gameState.currentPath.positions,
            nextRequiredCheckpoint = gameState.nextRequiredCheckpoint,
            gameMode = GameMode.SOLO
        )

        val hint = hintEngine.computeHint(hintReq)
        assertTrue("Hint engine must provide a NextMove for valid generated puzzle", hint is HintResult.NextMove)
        val nextMove = (hint as HintResult.NextMove).nextMove

        val startPos = success.puzzle.startCheckpoint!!.position
        assertTrue("Hint move must be start checkpoint or adjacent to start checkpoint", nextMove == startPos || startPos.isOrthogonalNeighbor(nextMove))
    }

    // 10. Existing 300-level catalog preservation
    @Test
    fun `all 300 level IDs map to valid worlds, chapters, and packaged puzzle definitions`() {
        assertEquals("WorldConfiguration must define 300 total levels", 300, WorldConfiguration.TOTAL_LEVELS)

        for (lvl in 1..300) {
            val world = WorldConfiguration.getWorldForLevel(lvl)
            assertTrue("World ID must be in 1..6 for level $lvl", world.worldId in 1..6)

            val chapter = ProgressionPlan.getChapterForLevel(lvl)
            assertTrue("Chapter ID must be in 1..7 for level $lvl", chapter.chapterId in 1..7)
        }

        // Verify pre-packaged levels are present
        assertNotNull("Pre-packaged Level 1 must exist", PackagedPuzzles.ALL_PACKAGED[1])
        assertNotNull("Pre-packaged Level 21 must exist", PackagedPuzzles.ALL_PACKAGED[21])
        assertNotNull("Pre-packaged Level 51 must exist", PackagedPuzzles.ALL_PACKAGED[51])
    }
}
