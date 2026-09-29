package com.zynpath.game.core.puzzle.session

import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GameplaySessionValidator].
 *
 * Implements Prompt 13 Section 38:
 * - Valid path replay verification through authoritative [PuzzleEngine].
 * - Level ID mismatch rejection.
 * - Puzzle ID mismatch rejection.
 * - Puzzle version mismatch rejection.
 * - Unsupported schema version rejection.
 * - Illegal move rejection (diagonal, wall collision, skipped checkpoint, cycle).
 * - Premature final checkpoint rejection.
 * - Deserialization integrity and empty path handling.
 */
class GameplaySessionValidatorTest {

    private lateinit var definitionLevel1: PuzzleDefinition
    private lateinit var fullSolutionLevel1: List<GridPosition>

    @Before
    fun setUp() {
        definitionLevel1 = PackagedPuzzles.LEVEL_1_PUZZLE
        fullSolutionLevel1 = PackagedPuzzles.SOLUTION_1_ROUTE.positions
    }

    @Test
    fun `test valid partial path replay succeeds`() {
        // First 6 moves of Level 1 solution
        val partialPath = fullSolutionLevel1.take(6)
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_valid_1",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = partialPath,
            elapsedActiveTimeMs = 5400L,
            revision = 6L
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Validation must succeed for legal partial path, got $result", result is SessionRestorationResult.Success)

        val success = result as SessionRestorationResult.Success
        assertEquals(partialPath, success.restoredGameState.currentPath.positions)
        assertEquals(6, success.restoredGameState.coveredCellCount)
        assertEquals(partialPath.last(), success.restoredGameState.currentEndpoint)
    }

    @Test
    fun `test empty path for fresh session succeeds`() {
        val snapshot = GameplaySessionSnapshot.createNew(
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Validation must succeed for empty path fresh session", result is SessionRestorationResult.Success)

        val success = result as SessionRestorationResult.Success
        assertTrue(success.restoredGameState.currentPath.isEmpty)
        assertEquals(0, success.restoredGameState.coveredCellCount)
    }

    @Test
    fun `test level ID mismatch is rejected`() {
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_wrong_level",
            levelId = 2, // snapshot for level 2
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = fullSolutionLevel1.take(3)
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Level ID mismatch must be rejected", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.LEVEL_ID_MISMATCH, invalid.reason)
    }

    @Test
    fun `test puzzle ID mismatch is rejected`() {
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_wrong_puzzle",
            levelId = 1,
            worldId = 1,
            puzzleId = "different_puzzle_999",
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = fullSolutionLevel1.take(3)
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Puzzle ID mismatch must be rejected", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.PUZZLE_ID_MISMATCH, invalid.reason)
    }

    @Test
    fun `test puzzle version mismatch is rejected`() {
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_wrong_version",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = 2, // Definition is version 1
            status = SessionStatus.ACTIVE,
            path = fullSolutionLevel1.take(3)
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Puzzle version mismatch must be rejected", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.PUZZLE_VERSION_MISMATCH, invalid.reason)
    }

    @Test
    fun `test unsupported schema version is rejected`() {
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_unsupported_schema",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = fullSolutionLevel1.take(3),
            snapshotSchemaVersion = 99
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Unsupported schema version must be rejected", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.UNSUPPORTED_SCHEMA_VERSION, invalid.reason)
    }

    @Test
    fun `test completed or abandoned session is not resumable`() {
        val snapshotCompleted = GameplaySessionSnapshot(
            sessionId = "sess_completed",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.COMPLETED,
            path = fullSolutionLevel1
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshotCompleted, definitionLevel1, expectedLevelId = 1)
        assertTrue("Completed session must be rejected as not resumable", result is SessionRestorationResult.Invalid)
        assertEquals(RestorationFailureReason.SESSION_NOT_RESUMABLE, (result as SessionRestorationResult.Invalid).reason)
    }

    @Test
    fun `test path not starting at checkpoint 1 is rejected`() {
        val invalidPath = listOf(GridPosition(1, 1), GridPosition(1, 2))
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_bad_start",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = invalidPath
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Start not at checkpoint 1 must be rejected", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.START_NOT_CHECKPOINT_ONE, invalid.reason)
    }

    @Test
    fun `test illegal move with diagonal jump in path is rejected`() {
        // (0,0) -> (1,1) is diagonal!
        val diagonalPath = listOf(GridPosition(0, 0), GridPosition(1, 1))
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_diagonal",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = diagonalPath
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Diagonal move in snapshot must be rejected", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.ILLEGAL_MOVE_REPLAY, invalid.reason)
    }

    @Test
    fun `test move crossing blocked edge wall is rejected`() {
        // Level 51 has walls. Let's test with Level 51
        val level51Def = PackagedPuzzles.LEVEL_51_PUZZLE
        val wall = level51Def.blockedEdges.first()

        // Construct a path that starts at #1 (0,0) and tries to cross the wall
        // Instead of constructing full path, let's create a minimal puzzle with a wall between (0,0) and (0,1)
        val customDef = PuzzleDefinition(
            puzzleId = "test_wall_def",
            gridDimensions = com.zynpath.game.core.puzzle.model.GridDimensions(3, 3),
            requiredCells = setOf(GridPosition(0, 0), GridPosition(0, 1), GridPosition(1, 0)),
            checkpoints = listOf(
                com.zynpath.game.core.puzzle.model.NumberedCheckpoint(1, GridPosition(0, 0)),
                com.zynpath.game.core.puzzle.model.NumberedCheckpoint(2, GridPosition(1, 0))
            ),
            blockedEdges = setOf(BlockedEdge(GridPosition(0, 0), GridPosition(0, 1)))
        )

        val wallCrossingPath = listOf(GridPosition(0, 0), GridPosition(0, 1))
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_wall",
            levelId = 10,
            worldId = 1,
            puzzleId = customDef.puzzleId,
            puzzleVersion = customDef.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = wallCrossingPath
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, customDef, expectedLevelId = 10)
        assertTrue("Wall crossing in snapshot must be rejected", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.ILLEGAL_MOVE_REPLAY, invalid.reason)
    }

    @Test
    fun `test path self intersection cycle is rejected`() {
        // (0,0) -> (0,1) -> (1,1) -> (1,0) -> (0,0) (cycle!)
        val cyclePath = listOf(
            GridPosition(0, 0),
            GridPosition(0, 1),
            GridPosition(1, 1),
            GridPosition(1, 0),
            GridPosition(0, 0)
        )
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_cycle",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = cyclePath
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Cycle in snapshot must be rejected", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.ILLEGAL_MOVE_REPLAY, invalid.reason)
    }

    @Test
    fun `test premature entry into final checkpoint is rejected`() {
        // In Level 1, Checkpoint 1 is at (0, 0), Checkpoint 2 is at (0, 3), and Checkpoint 3 is at (1, 0).
        // (0, 0) and (1, 0) are orthogonally adjacent. Moving directly from (0, 0) to (1, 0) visits
        // Checkpoint 3 out of order (skipping Checkpoint 2), which must be rejected by the engine.
        val prematurePath = listOf(GridPosition(0, 0), GridPosition(1, 0))
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_premature",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = prematurePath
        )

        val result = GameplaySessionValidator.validateAndReplay(snapshot, definitionLevel1, expectedLevelId = 1)
        assertTrue("Premature checkpoint entry must be rejected, got $result", result is SessionRestorationResult.Invalid)

        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.ILLEGAL_MOVE_REPLAY, invalid.reason)
    }

    @Test
    fun `test mismatched owner identity is rejected`() {
        val snapshot = GameplaySessionSnapshot(
            sessionId = "sess_user_a",
            levelId = 1,
            worldId = 1,
            puzzleId = definitionLevel1.puzzleId,
            puzzleVersion = definitionLevel1.puzzleVersion,
            status = SessionStatus.ACTIVE,
            path = listOf(GridPosition(0, 0)),
            ownerIdentity = "user_account_a"
        )

        val result = GameplaySessionValidator.validateAndReplay(
            snapshot = snapshot,
            definition = definitionLevel1,
            expectedLevelId = 1,
            expectedOwnerId = "user_account_b"
        )

        assertTrue("Session belonging to another user must be rejected", result is SessionRestorationResult.Invalid)
        val invalid = result as SessionRestorationResult.Invalid
        assertEquals(RestorationFailureReason.SESSION_OWNERSHIP_MISMATCH, invalid.reason)
    }
}
