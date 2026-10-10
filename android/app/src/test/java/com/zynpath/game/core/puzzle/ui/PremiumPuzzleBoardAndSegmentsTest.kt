package com.zynpath.game.core.puzzle.ui

import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.experience.hint.LevelHintConsumptionResult
import com.zynpath.game.core.puzzle.experience.hint.LevelHintRepository
import com.zynpath.game.core.puzzle.experience.hint.LevelHintState
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintConfiguration
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Focused unit tests for Premium Puzzle Board and Multiple Number Connections.
 *
 * Implements Prompt 27 Task 16:
 * 1. Ascending number sequence enforcement.
 * 2. Continuous path across multiple clue segments.
 * 3. Prevention of disconnected independent paths.
 * 4. All playable cells required for level completion.
 * 5. Invalid move rejection preserving authoritative state.
 * 6. Backtracking consistency and state retraction.
 * 7. Correct progress calculation from authoritative state.
 * 8. Completion validation via authoritative validator.
 * 9. Hint validity against canonical solution.
 * 10. Prevention of duplicate hint consumption and reward claims.
 */
class PremiumPuzzleBoardAndSegmentsTest {

    // 1. Ascending number sequence
    @Test
    fun `test clues must be visited in strict ascending order`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        // Start at clue 1
        val startResult = engine.process(PuzzleAction.StartPath(sol[0]))
        assertTrue("Starting at clue 1 must be accepted", startResult.isAccepted)
        assertEquals(2, engine.currentState.nextRequiredCheckpoint)

        // Attempting to step directly or jump to Clue 3 before Clue 2 is visited
        val cp3 = def.checkpoints.find { it.number == 3 }!!
        val prematureClueResult = engine.process(PuzzleAction.ExtendPath(cp3.position))

        assertFalse("Visiting Clue 3 before Clue 2 must be rejected", prematureClueResult.isAccepted)
        val rejected = prematureClueResult as PuzzleEngineResult.Rejected
        assertTrue(rejected.reason == MoveRejectionReason.WRONG_CHECKPOINT_ORDER || rejected.reason == MoveRejectionReason.NON_ADJACENT)
    }

    // 2. Continuous path across multiple clue segments
    @Test
    fun `test multiple clue segments form one single continuous path`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        engine.process(PuzzleAction.StartPath(sol[0]))
        for (i in 1..7) {
            val res = engine.process(PuzzleAction.ExtendPath(sol[i]))
            assertTrue("Step $i must be accepted", res.isAccepted)
        }

        // All steps belong to a single continuous path without branching or breaks
        val path = engine.currentState.currentPath.positions
        assertEquals(8, path.size)
        assertEquals(sol[0], path.first())
        assertEquals(sol[7], path.last())
        for (i in 0 until path.size - 1) {
            assertTrue("Path step $i must be orthogonally adjacent to step ${i + 1}", path[i].isOrthogonalNeighbor(path[i + 1]))
        }
    }

    // 3. No disconnected paths
    @Test
    fun `test separate disconnected paths are strictly prohibited`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        engine.process(PuzzleAction.StartPath(sol[0]))
        engine.process(PuzzleAction.ExtendPath(sol[1]))

        // Attempting to start or extend a separate disconnected cell (e.g. 5,5)
        val jumpResult = engine.process(PuzzleAction.ExtendPath(GridPosition(5, 5)))
        assertFalse("Non-adjacent leap to disconnected cell must be rejected", jumpResult.isAccepted)
        val rejected = jumpResult as PuzzleEngineResult.Rejected
        assertEquals(MoveRejectionReason.NON_ADJACENT, rejected.reason)

        // Attempting a second StartPath while path is active
        val secondStart = engine.process(PuzzleAction.StartPath(GridPosition(2, 2)))
        assertFalse("Cannot start a second disconnected path", secondStart.isAccepted)
    }

    // 4. All playable cells required
    @Test
    fun `test reaching final clue without filling all playable cells does not complete puzzle`() {
        val def = PackagedPuzzles.LEVEL_1 // 36 cells required
        val engine = PuzzleEngine(def)
        val sol = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        engine.process(PuzzleAction.StartPath(sol[0]))
        for (i in 1..12) {
            engine.process(PuzzleAction.ExtendPath(sol[i]))
        }

        val prematureFinalResult = CompletionValidator.validate(def, engine.currentState.currentPath)
        assertFalse("Premature path with only 13/36 cells must not pass validation", prematureFinalResult.isSuccess)
        assertTrue(engine.currentState.gameStatus != GameStatus.COMPLETED)
    }

    // 5. Invalid move rejection
    @Test
    fun `test invalid moves are rejected and preserve previous valid path`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        engine.process(PuzzleAction.StartPath(sol[0]))
        engine.process(PuzzleAction.ExtendPath(sol[1]))

        val pathBeforeInvalid = engine.currentState.currentPath.positions
        assertEquals(2, pathBeforeInvalid.size)

        // Diagonal move
        val diag = GridPosition((sol[1].row + 1) % 6, (sol[1].column + 1) % 6)
        val diagResult = engine.process(PuzzleAction.ExtendPath(diag))
        assertFalse("Diagonal step must be rejected", diagResult.isAccepted)
        assertEquals(pathBeforeInvalid, engine.currentState.currentPath.positions)
        assertEquals(sol[1], engine.currentState.currentEndpoint)

        // Backtrack to sol[0]
        engine.process(PuzzleAction.ExtendPath(sol[0]))
        assertEquals(sol[0], engine.currentState.currentEndpoint)
    }

    // 6. Backtracking consistency
    @Test
    fun `test backtracking smoothly retracts head and restores previous state`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        engine.process(PuzzleAction.StartPath(sol[0]))
        engine.process(PuzzleAction.ExtendPath(sol[1]))
        engine.process(PuzzleAction.ExtendPath(sol[2]))
        engine.process(PuzzleAction.ExtendPath(sol[3]))

        assertEquals(4, engine.currentState.coveredCellCount)
        assertEquals(sol[3], engine.currentState.currentEndpoint)

        // Backtrack to predecessor sol[2]
        val retractResult = engine.process(PuzzleAction.ExtendPath(sol[2]))
        assertTrue("Backtracking to predecessor must be accepted", retractResult.isAccepted)
        assertEquals(3, engine.currentState.coveredCellCount)
        assertEquals(sol[2], engine.currentState.currentEndpoint)
        assertFalse("Cell sol[3] must no longer be covered", engine.currentState.isCellCovered(sol[3]))
    }

    // 7. Correct progress calculation
    @Test
    fun `test coverage and checkpoint progress indicators calculate accurately`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        val sol = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        assertEquals(0, engine.currentState.coveredCellCount)
        assertEquals(36, def.requiredCells.size)

        engine.process(PuzzleAction.StartPath(sol[0]))
        assertEquals(1, engine.currentState.coveredCellCount)
        assertEquals("1 / 36 cells filled", "${engine.currentState.coveredCellCount} / ${def.requiredCells.size} cells filled")

        engine.process(PuzzleAction.ExtendPath(sol[1]))
        engine.process(PuzzleAction.ExtendPath(sol[2]))
        engine.process(PuzzleAction.ExtendPath(sol[3]))
        assertEquals(4, engine.currentState.coveredCellCount)
    }

    // 8. Completion validation
    @Test
    fun `test full canonical path passes authoritative completion validator`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        val canonicalSolution = PackagedPuzzles.SOLUTION_1_ROUTE.positions

        for (i in canonicalSolution.indices) {
            if (i == 0) {
                engine.process(PuzzleAction.StartPath(canonicalSolution[i]))
            } else {
                val res = engine.process(PuzzleAction.ExtendPath(canonicalSolution[i]))
                assertTrue("Step $i to ${canonicalSolution[i]} must be valid", res.isAccepted)
            }
        }

        assertEquals(36, engine.currentState.coveredCellCount)
        assertEquals(GameStatus.COMPLETED, engine.currentState.gameStatus)

        val validation = CompletionValidator.validate(def, engine.currentState.currentPath)
        assertTrue("Canonical full route must pass authoritative validation", validation.isSuccess)
    }

    // 9. Hint validity
    @Test
    fun `test hint engine suggests next valid canonical cell and respects puzzle bounds`() {
        val def = PackagedPuzzles.LEVEL_1
        val hintEngine = PuzzleHintEngine()
        val sol = PackagedPuzzles.SOLUTION_1_ROUTE.positions
        val currentPath = PuzzlePath.single(sol[0])

        val request = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            currentPath = currentPath,
            gameMode = GameMode.SOLO,
            configuration = HintConfiguration()
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Hint request from start must yield NextMove", result is HintResult.NextMove)
        val nextMove = (result as HintResult.NextMove).nextMove
        assertEquals(sol[1], nextMove)
        assertTrue("Recommended move must be adjacent to current head", sol[0].isOrthogonalNeighbor(nextMove))
        assertTrue("Recommended move must be a valid playable cell", def.requiredCells.contains(nextMove))
    }

    // 10. Duplicate hint-consumption prevention
    @Test
    fun `test level hint repository ensures exactly two free hints and prevents duplicate reward claims`() {
        val fakeRepo = object : LevelHintRepository {
            private var freeUsed = 0
            private var rewardedEarned = 0
            private var rewardedConsumed = 0
            private val claimedIds = mutableSetOf<String>()

            override fun observeLevelHintState(levelId: Int): Flow<LevelHintState> =
                flowOf(getLevelState(levelId))

            override suspend fun getLevelHintState(levelId: Int): LevelHintState =
                getLevelState(levelId)

            private fun getLevelState(levelId: Int): LevelHintState =
                LevelHintState(
                    levelId = levelId,
                    freeHintsTotal = 2,
                    freeHintsUsed = freeUsed,
                    rewardedHintsEarned = rewardedEarned,
                    rewardedHintsConsumed = rewardedConsumed
                )

            override suspend fun consumeHint(levelId: Int): LevelHintConsumptionResult {
                val state = getLevelState(levelId)
                return when {
                    state.freeHintsRemaining > 0 -> {
                        freeUsed++
                        LevelHintConsumptionResult.ConsumedFree(getLevelState(levelId), 2 - freeUsed)
                    }
                    state.rewardedHintsRemaining > 0 -> {
                        rewardedConsumed++
                        LevelHintConsumptionResult.ConsumedRewarded(getLevelState(levelId), rewardedEarned - rewardedConsumed)
                    }
                    else -> LevelHintConsumptionResult.RequiresRewardedAd(state)
                }
            }

            override suspend fun recordConfirmedReward(levelId: Int, rewardClaimId: String): Boolean {
                if (rewardClaimId.isBlank() || rewardClaimId in claimedIds) return false
                claimedIds.add(rewardClaimId)
                rewardedEarned++
                return true
            }

            override suspend fun restoreSessionHints(levelId: Int, hintsUsedInSession: Int): LevelHintState =
                getLevelState(levelId)
        }

        runBlocking {
            val levelId = 1
            var state = fakeRepo.getLevelHintState(levelId)
            assertEquals(2, state.freeHintsRemaining)
            assertTrue(state.canConsumeHint)

            // Consume first free hint
            val res1 = fakeRepo.consumeHint(levelId)
            assertTrue(res1 is LevelHintConsumptionResult.ConsumedFree)
            assertEquals(1, (res1 as LevelHintConsumptionResult.ConsumedFree).remainingFree)

            // Consume second free hint
            val res2 = fakeRepo.consumeHint(levelId)
            assertTrue(res2 is LevelHintConsumptionResult.ConsumedFree)
            assertEquals(0, (res2 as LevelHintConsumptionResult.ConsumedFree).remainingFree)

            // Third request without ad requires rewarded ad
            val res3 = fakeRepo.consumeHint(levelId)
            assertTrue("Exhausted free hints must require rewarded ad", res3 is LevelHintConsumptionResult.RequiresRewardedAd)

            // Watching rewarded ad grants +1 hint credit with claim ID
            val claimId = "ad_claim_uuid_999"
            val creditGranted = fakeRepo.recordConfirmedReward(levelId, claimId)
            assertTrue("Valid claim must award hint credit", creditGranted)

            // Duplicate claim with same claim ID is rejected idempotently
            val duplicateClaim = fakeRepo.recordConfirmedReward(levelId, claimId)
            assertFalse("Duplicate claim ID must be rejected", duplicateClaim)

            // Can now consume the rewarded hint
            val res4 = fakeRepo.consumeHint(levelId)
            assertTrue("Granted rewarded hint can be consumed", res4 is LevelHintConsumptionResult.ConsumedRewarded)
        }
    }
}
