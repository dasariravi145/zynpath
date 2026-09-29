package com.zynpath.game.core.puzzle.session

import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive test suite validating Solo session creation, restoration,
 * malformed snapshot safety, timer lifecycle under backgrounding, undo, and reset.
 *
 * Implements Prompt 46 Sections 53-59:
 * - Session creation from valid puzzle.
 * - Durable restoration of active session.
 * - Graceful fallback / recovery on malformed snapshots.
 * - Monotonic timer lifecycle and pause/resume duration freezing during app backgrounding.
 * - Undo stack behavior and state restoration.
 * - Reset clears active path to starting checkpoint 1 without deleting saved progression.
 */
class SoloSessionAndTimerComprehensiveTest {

    private lateinit var definition: PuzzleDefinition
    private lateinit var testTime: TestTimeProvider
    private lateinit var timer: GameplayTimer

    @Before
    fun setUp() {
        definition = PackagedPuzzles.LEVEL_1_PUZZLE
        testTime = TestTimeProvider(currentMonotonicMs = 100_000L, currentWallClockMs = 1_700_000_000_000L)
        timer = GameplayTimer(testTime)
    }

    // =========================================================================
    // 1. Session Creation & Restoration (Sections 53, 54, 55)
    // =========================================================================

    @Test
    fun `test session creation from valid puzzle produces legal starting state`() {
        val snapshot = GameplaySessionSnapshot.createNew(
            levelId = 1,
            worldId = 1,
            puzzleId = definition.puzzleId,
            puzzleVersion = definition.puzzleVersion
        )

        assertEquals(1, snapshot.levelId)
        assertEquals(1, snapshot.worldId)
        assertEquals(definition.puzzleId, snapshot.puzzleId)
        assertEquals(SessionStatus.NOT_STARTED, snapshot.status)
        assertTrue(snapshot.path.isEmpty())
        assertEquals(0L, snapshot.elapsedActiveTimeMs)
    }

    @Test
    fun `test session restoration replays partial legal moves accurately`() {
        val fullSolution = PackagedPuzzles.SOLUTION_1_ROUTE.positions
        val partialPath = fullSolution.take(5)

        val snapshot = GameplaySessionSnapshot(
            sessionId = "session_46_test",
            levelId = 1,
            worldId = 1,
            puzzleId = definition.puzzleId,
            puzzleVersion = definition.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = partialPath,
            elapsedActiveTimeMs = 8_200L,
            revision = 5L
        )

        val restoration = GameplaySessionValidator.validateAndReplay(snapshot, definition, expectedLevelId = 1)
        assertTrue("Legal partial path snapshot must restore successfully", restoration is SessionRestorationResult.Success)

        val success = restoration as SessionRestorationResult.Success
        assertEquals(5, success.restoredGameState.coveredCellCount)
        assertEquals(partialPath, success.restoredGameState.currentPath.positions)
        assertEquals(partialPath.last(), success.restoredGameState.currentEndpoint)
    }

    @Test
    fun `test malformed snapshot with diagonal moves fails safely without crash`() {
        val corruptedPath = listOf(
            GridPosition(0, 0),
            GridPosition(1, 1) // Illegal diagonal move!
        )

        val badSnapshot = GameplaySessionSnapshot(
            sessionId = "session_corrupt",
            levelId = 1,
            worldId = 1,
            puzzleId = definition.puzzleId,
            puzzleVersion = definition.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = corruptedPath,
            elapsedActiveTimeMs = 1200L
        )

        val restoration = GameplaySessionValidator.validateAndReplay(badSnapshot, definition, expectedLevelId = 1)
        assertTrue("Corrupted diagonal snapshot must be rejected", restoration is SessionRestorationResult.Invalid)

        val invalid = restoration as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.ILLEGAL_MOVE_REPLAY, invalid.reason)
    }

    @Test
    fun `test malformed snapshot with out of bounds position fails safely`() {
        val badSnapshot = GameplaySessionSnapshot(
            sessionId = "session_oob",
            levelId = 1,
            worldId = 1,
            puzzleId = definition.puzzleId,
            puzzleVersion = definition.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = listOf(GridPosition(0, 0), GridPosition(-1, 0)),
            elapsedActiveTimeMs = 500L
        )

        val restoration = GameplaySessionValidator.validateAndReplay(badSnapshot, definition, expectedLevelId = 1)
        assertTrue("Out of bounds snapshot must be rejected safely", restoration is SessionRestorationResult.Invalid)
    }

    // =========================================================================
    // 2. Timer Lifecycle and App Backgrounding (Sections 56, 57)
    // =========================================================================

    @Test
    fun `test app backgrounding and resume does not accumulate paused duration`() {
        timer.start()
        testTime.advanceTimeMs(4000L)
        assertEquals(4000L, timer.elapsedDurationMs())

        // App goes to background -> pause timer
        val pausedAt = timer.pause()
        assertEquals(4000L, pausedAt)
        assertFalse(timer.isRunning)

        // Device remains in background for 5 minutes (300,000 ms)
        testTime.advanceTimeMs(300_000L)

        // While paused, elapsed duration must strictly remain 4000ms!
        assertEquals("Background duration must not be counted in active gameplay time", 4000L, timer.elapsedDurationMs())

        // App returns to foreground -> resume timer
        timer.resume()
        assertTrue(timer.isRunning)

        // Active play continues for 2 seconds
        testTime.advanceTimeMs(2000L)
        assertEquals(6000L, timer.elapsedDurationMs())
    }

    @Test
    fun `test timer completion freezes final duration permanently`() {
        timer.start()
        testTime.advanceTimeMs(14_500L)

        // Game completed -> stop timer
        val finalDuration = timer.pause()
        assertEquals(14_500L, finalDuration)

        // Additional time passes after victory screen displayed
        testTime.advanceTimeMs(10_000L)
        assertEquals("Frozen timer duration must not advance after game completion", 14_500L, timer.elapsedDurationMs())
    }

    // =========================================================================
    // 3. Undo and Reset Behavior (Sections 58, 59)
    // =========================================================================

    @Test
    fun `test undo restores previous legal path state`() {
        val engine = PuzzleEngine(definition)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))

        assertEquals(3, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 2), engine.currentState.currentEndpoint)

        // Undo one step
        val undoResult = engine.process(PuzzleAction.BacktrackOne)
        assertTrue(undoResult.isAccepted)
        assertEquals(2, engine.currentState.coveredCellCount)
        assertEquals(GridPosition(0, 1), engine.currentState.currentEndpoint)
        assertEquals(listOf(GridPosition(0, 0), GridPosition(0, 1)), engine.currentState.currentPath.positions)
    }

    @Test
    fun `test reset clears active path back to checkpoint 1 without deleting state`() {
        val engine = PuzzleEngine(definition)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 2)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3)))

        assertEquals(4, engine.currentState.coveredCellCount)

        // Reset the board
        val resetResult = engine.process(PuzzleAction.Reset)
        assertTrue(resetResult.isAccepted)

        // Current state resets to empty path (ready for fresh start)
        assertTrue("Reset must clear the active path", engine.currentState.currentPath.isEmpty)
        assertEquals(0, engine.currentState.coveredCellCount)
        assertNull(engine.currentState.currentEndpoint)
        assertEquals(1, engine.currentState.nextRequiredCheckpoint)
    }
}
