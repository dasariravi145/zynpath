package com.zynpath.game.core.puzzle.hint

import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.fixtures.CuratedDifficultyFixtures
import com.zynpath.game.core.puzzle.fixtures.SamplePuzzleFixtures
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Benchmark measurements for [PuzzleHintEngine] across 4x4, 5x5, 6x6, 7x7, and 8x8 boards.
 *
 * Implements Prompt 14 Section 40:
 * Measures representative hint searches with bounded budgets and records actual elapsed times.
 */
class PuzzleHintBenchmarkTest {

    private lateinit var hintEngine: PuzzleHintEngine

    @Before
    fun setUp() {
        hintEngine = PuzzleHintEngine()
    }

    private fun measureHintExecution(name: String, definition: PuzzleDefinition): Long {
        val engine = PuzzleEngine(definition)
        val startCheckpoint = definition.checkpoints.find { it.number == 1 }!!
        engine.process(com.zynpath.game.core.puzzle.engine.PuzzleAction.StartPath(startCheckpoint.position), 0L)

        val request = HintRequest(
            puzzleId = definition.puzzleId,
            puzzleVersion = definition.puzzleVersion,
            definition = definition,
            gameState = engine.currentState,
            configuration = HintConfiguration.DEFAULT
        )

        val start = System.nanoTime()
        val result = hintEngine.computeHint(request)
        val elapsedMs = (System.nanoTime() - start) / 1_000_000L

        println("BENCHMARK [$name]: result=${result::class.simpleName}, elapsed=${elapsedMs}ms")
        assertTrue("Hint computation must return valid outcome", result is HintResult.NextMove || result is HintResult.SearchInconclusive)
        return elapsedMs
    }

    @Test
    fun `benchmark 4x4 hint search`() {
        val elapsed = measureHintExecution("4x4 Beginner", CuratedDifficultyFixtures.beginner4x4)
        assertTrue("4x4 hint must complete under 500ms, took ${elapsed}ms", elapsed < 500L)
    }

    @Test
    fun `benchmark 5x5 clean hint search`() {
        val elapsed = measureHintExecution("5x5 Clean", CuratedDifficultyFixtures.clean5x5)
        assertTrue("5x5 clean hint must complete under 1000ms, took ${elapsed}ms", elapsed < 1000L)
    }

    @Test
    fun `benchmark 5x5 walls hint search`() {
        val elapsed = measureHintExecution("5x5 Walls", CuratedDifficultyFixtures.walls5x5)
        assertTrue("5x5 walls hint must complete under 1000ms, took ${elapsed}ms", elapsed < 1000L)
    }

    @Test
    fun `benchmark 6x6 hint search`() {
        val elapsed = measureHintExecution("6x6 Sample", SamplePuzzleFixtures.puzzle6x6Valid)
        assertTrue("6x6 hint must complete under 2000ms, took ${elapsed}ms", elapsed < 2000L)
    }

    @Test
    fun `benchmark 7x7 hint search`() {
        val elapsed = measureHintExecution("7x7 Sample", SamplePuzzleFixtures.puzzle7x7Valid)
        assertTrue("7x7 hint must complete under 3000ms, took ${elapsed}ms", elapsed < 3000L)
    }

    @Test
    fun `benchmark 8x8 hint search`() {
        val elapsed = measureHintExecution("8x8 Sample", SamplePuzzleFixtures.puzzle8x8Valid)
        assertTrue("8x8 hint must complete under 4000ms, took ${elapsed}ms", elapsed < 4000L)
    }
}
