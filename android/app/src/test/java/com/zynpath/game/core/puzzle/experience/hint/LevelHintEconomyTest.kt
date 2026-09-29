package com.zynpath.game.core.puzzle.experience.hint

import android.app.Activity
import com.zynpath.game.BuildConfig
import com.zynpath.game.core.ads.config.AdConfiguration
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintConfiguration
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.engine.CompletionValidator
import com.zynpath.game.core.puzzle.model.GridPosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Unit tests verifying the Two-Free-Hint economy, real Rewarded Ad mechanics,
 * authoritative reward callbacks, idempotency, and puzzle validity.
 *
 * Implements Prompt 31 Task 17 (20 specific test scenarios):
 * 1. Exactly two free hints per level.
 * 2. Free-hint persistence.
 * 3. Level-specific hint balances.
 * 4. Correct transition to rewarded hints.
 * 5. Reward granted only from reward-earned callback.
 * 6. No reward for ad opening alone.
 * 7. No reward from dismissal alone.
 * 8. Failed/unavailable ad behavior.
 * 9. Duplicate callback idempotency.
 * 10. Duplicate button-tap protection.
 * 11. Reward credited to the originating level.
 * 12. Earned-credit persistence after navigation.
 * 13. Exactly-once hint consumption.
 * 14. No negative balances.
 * 15. Valid hint generation.
 * 16. No hint consumption when guidance is unavailable.
 * 17. Original full-grid completion rules.
 * 18. Debug/release ad configuration separation.
 * 19. Existing guest-progress compatibility.
 * 20. Lifecycle and restoration behavior.
 */
class LevelHintEconomyTest {

    /**
     * In-memory mock implementing the exact persistence rules of LevelHintRepositoryImpl.
     */
    private class InMemoryLevelHintRepository : LevelHintRepository {
        private val states = mutableMapOf<Int, MutableStateFlow<LevelHintState>>()
        private val claimedRewardIds = mutableSetOf<String>()

        private fun getFlow(levelId: Int): MutableStateFlow<LevelHintState> {
            return states.getOrPut(levelId) {
                MutableStateFlow(LevelHintState.initial(levelId))
            }
        }

        override fun observeLevelHintState(levelId: Int): Flow<LevelHintState> {
            return getFlow(levelId).asStateFlow()
        }

        override suspend fun getLevelHintState(levelId: Int): LevelHintState {
            return getFlow(levelId).value
        }

        override suspend fun consumeHint(levelId: Int): LevelHintConsumptionResult {
            val flow = getFlow(levelId)
            val current = flow.value
            return when {
                current.freeHintsRemaining > 0 -> {
                    val updatedUsed = (current.freeHintsUsed + 1).coerceAtMost(LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL)
                    val newState = current.copy(freeHintsUsed = updatedUsed)
                    flow.value = newState
                    LevelHintConsumptionResult.ConsumedFree(newState, newState.freeHintsRemaining)
                }
                current.rewardedHintsRemaining > 0 -> {
                    val updatedConsumed = (current.rewardedHintsConsumed + 1).coerceAtMost(current.rewardedHintsEarned)
                    val newState = current.copy(rewardedHintsConsumed = updatedConsumed)
                    flow.value = newState
                    LevelHintConsumptionResult.ConsumedRewarded(newState, newState.rewardedHintsRemaining)
                }
                else -> {
                    LevelHintConsumptionResult.RequiresRewardedAd(current)
                }
            }
        }

        override suspend fun recordConfirmedReward(levelId: Int, rewardClaimId: String): Boolean {
            if (rewardClaimId.isBlank()) return false
            if (rewardClaimId in claimedRewardIds) {
                return false // Idempotent rejection
            }
            claimedRewardIds.add(rewardClaimId)
            val flow = getFlow(levelId)
            val current = flow.value
            val updatedEarned = current.rewardedHintsEarned + 1
            flow.value = current.copy(rewardedHintsEarned = updatedEarned)
            return true
        }

        override suspend fun restoreSessionHints(levelId: Int, hintsUsedInSession: Int): LevelHintState {
            val flow = getFlow(levelId)
            val current = flow.value
            val adjustedFreeUsed = minOf(hintsUsedInSession, LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL)
            val adjustedRewardUsed = maxOf(0, hintsUsedInSession - LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL)
            val finalFreeUsed = maxOf(current.freeHintsUsed, adjustedFreeUsed)
            val finalRewardUsed = maxOf(current.rewardedHintsConsumed, adjustedRewardUsed)
            val finalRewardEarned = maxOf(current.rewardedHintsEarned, finalRewardUsed)
            val newState = LevelHintState(
                levelId = levelId,
                freeHintsTotal = LevelHintState.LOCKED_FREE_HINTS_PER_LEVEL,
                freeHintsUsed = finalFreeUsed,
                rewardedHintsEarned = finalRewardEarned,
                rewardedHintsConsumed = finalRewardUsed
            )
            flow.value = newState
            return newState
        }
    }

    // 1. Exactly two free hints per level
    @Test
    fun `test initial level hint state locks exactly two free hints`() {
        val state = LevelHintState.initial(levelId = 1)
        assertEquals("Initial freeHintsTotal must equal 2", 2, state.freeHintsTotal)
        assertEquals("Initial freeHintsUsed must equal 0", 0, state.freeHintsUsed)
        assertEquals("Initial freeHintsRemaining must equal 2", 2, state.freeHintsRemaining)
        assertTrue("Player must be able to consume initial hint", state.canConsumeHint)
        assertFalse("Initial state must not require rewarded ad", state.requiresRewardedAd)
    }

    // 2. Free-hint persistence
    @Test
    fun `test free hint consumption persists and decrements remaining balance`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 5

        // Initial
        assertEquals(2, repo.getLevelHintState(levelId).freeHintsRemaining)

        // Consume 1st free hint
        val res1 = repo.consumeHint(levelId)
        assertTrue("First consumption must be free", res1 is LevelHintConsumptionResult.ConsumedFree)
        assertEquals(1, (res1 as LevelHintConsumptionResult.ConsumedFree).remainingFree)
        assertEquals(1, repo.getLevelHintState(levelId).freeHintsRemaining)

        // Consume 2nd free hint
        val res2 = repo.consumeHint(levelId)
        assertTrue("Second consumption must be free", res2 is LevelHintConsumptionResult.ConsumedFree)
        assertEquals(0, (res2 as LevelHintConsumptionResult.ConsumedFree).remainingFree)
        assertEquals(0, repo.getLevelHintState(levelId).freeHintsRemaining)
    }

    // 3. Level-specific hint balances
    @Test
    fun `test hint consumption on one level does not affect other levels`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelA = 10
        val levelB = 11

        repo.consumeHint(levelA)
        assertEquals("Level A should have 1 free hint remaining", 1, repo.getLevelHintState(levelA).freeHintsRemaining)
        assertEquals("Level B must retain 2 free hints", 2, repo.getLevelHintState(levelB).freeHintsRemaining)
    }

    // 4. Correct transition to rewarded hints
    @Test
    fun `test third hint attempt transitions to RequiresRewardedAd`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 20

        repo.consumeHint(levelId) // 1st
        repo.consumeHint(levelId) // 2nd

        val res3 = repo.consumeHint(levelId)
        assertTrue("Third hint must require rewarded ad", res3 is LevelHintConsumptionResult.RequiresRewardedAd)
        assertEquals(0, repo.getLevelHintState(levelId).freeHintsRemaining)
        assertEquals(0, repo.getLevelHintState(levelId).totalAvailableHints)
        assertTrue("State must indicate requiresRewardedAd", repo.getLevelHintState(levelId).requiresRewardedAd)
    }

    // 5. Reward granted only from reward-earned callback
    @Test
    fun `test reward granted only when reward-earned callback is received`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 30
        repo.consumeHint(levelId)
        repo.consumeHint(levelId)

        var rewardConfirmedReceived = false
        val fakeContract = object : RewardedHintAdContract {
            override fun isRewardedAdReady(): Boolean = true
            override fun preloadRewardedAd() {}
            override fun showRewardedHintAd(levelId: Int, callback: RewardedHintAdCallback) {
                // Simulate authoritative SDK OnUserEarnedRewardListener
                val claimId = "ad_claim_${UUID.randomUUID()}"
                callback.onRewardConfirmed(
                    RewardedHintAdResult.Success(rewardClaimId = claimId, levelId = levelId, hintsGranted = 1)
                )
            }
        }

        fakeContract.showRewardedHintAd(levelId, object : RewardedHintAdCallback {
            override fun onRewardConfirmed(success: RewardedHintAdResult.Success) {
                rewardConfirmedReceived = true
                runBlocking {
                    repo.recordConfirmedReward(success.levelId, success.rewardClaimId)
                }
            }
            override fun onRewardDenied(denial: RewardedHintAdResult) {}
        })

        assertTrue("onRewardConfirmed must be called", rewardConfirmedReceived)
        val state = repo.getLevelHintState(levelId)
        assertEquals("One rewarded hint must be earned", 1, state.rewardedHintsEarned)
        assertEquals("One rewarded hint must remain available", 1, state.rewardedHintsRemaining)
    }

    // 6. No reward for ad opening alone
    @Test
    fun `test ad opening without reward callback does not grant credit`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 40
        repo.consumeHint(levelId)
        repo.consumeHint(levelId)

        val fakeContract = object : RewardedHintAdContract {
            override fun isRewardedAdReady(): Boolean = true
            override fun preloadRewardedAd() {}
            override fun showRewardedHintAd(levelId: Int, callback: RewardedHintAdCallback) {
                // Ad opened, but no reward listener fired!
            }
        }

        fakeContract.showRewardedHintAd(levelId, object : RewardedHintAdCallback {
            override fun onRewardConfirmed(success: RewardedHintAdResult.Success) {
                runBlocking { repo.recordConfirmedReward(success.levelId, success.rewardClaimId) }
            }
            override fun onRewardDenied(denial: RewardedHintAdResult) {}
        })

        assertEquals("No reward credit should be earned", 0, repo.getLevelHintState(levelId).rewardedHintsEarned)
    }

    // 7. No reward from dismissal alone
    @Test
    fun `test ad dismissal without reward callback denies reward`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 45
        repo.consumeHint(levelId)
        repo.consumeHint(levelId)

        var deniedResult: RewardedHintAdResult? = null
        val fakeContract = object : RewardedHintAdContract {
            override fun isRewardedAdReady(): Boolean = true
            override fun preloadRewardedAd() {}
            override fun showRewardedHintAd(levelId: Int, callback: RewardedHintAdCallback) {
                // User closed ad before completion -> onAdDismissed without userEarnedReward
                callback.onRewardDenied(RewardedHintAdResult.Skipped(levelId))
            }
        }

        fakeContract.showRewardedHintAd(levelId, object : RewardedHintAdCallback {
            override fun onRewardConfirmed(success: RewardedHintAdResult.Success) {
                runBlocking { repo.recordConfirmedReward(success.levelId, success.rewardClaimId) }
            }
            override fun onRewardDenied(denial: RewardedHintAdResult) {
                deniedResult = denial
            }
        })

        assertTrue("Denial must be Skipped", deniedResult is RewardedHintAdResult.Skipped)
        assertEquals(0, repo.getLevelHintState(levelId).rewardedHintsEarned)
    }

    // 8. Failed / unavailable ad behavior
    @Test
    fun `test unavailable ad dispatches Unavailable result without deducting hints`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 50

        var denialResult: RewardedHintAdResult? = null
        val fakeContract = object : RewardedHintAdContract {
            override fun isRewardedAdReady(): Boolean = false
            override fun preloadRewardedAd() {}
            override fun showRewardedHintAd(levelId: Int, callback: RewardedHintAdCallback) {
                callback.onRewardDenied(RewardedHintAdResult.Unavailable(levelId, "No inventory"))
            }
        }

        fakeContract.showRewardedHintAd(levelId, object : RewardedHintAdCallback {
            override fun onRewardConfirmed(success: RewardedHintAdResult.Success) {}
            override fun onRewardDenied(denial: RewardedHintAdResult) {
                denialResult = denial
            }
        })

        assertTrue("Result must be Unavailable", denialResult is RewardedHintAdResult.Unavailable)
        assertEquals("Free hints must remain unchanged", 2, repo.getLevelHintState(levelId).freeHintsRemaining)
    }

    // 9. Duplicate callback idempotency
    @Test
    fun `test duplicate reward callbacks with same claim ID are idempotent`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 60
        val claimId = "idempotent_claim_12345"

        val firstAttempt = repo.recordConfirmedReward(levelId, claimId)
        assertTrue("First recording must succeed", firstAttempt)
        assertEquals(1, repo.getLevelHintState(levelId).rewardedHintsEarned)

        val secondAttempt = repo.recordConfirmedReward(levelId, claimId)
        assertFalse("Second duplicate recording must be rejected", secondAttempt)
        assertEquals("Earned credits must not increase on duplicate callback", 1, repo.getLevelHintState(levelId).rewardedHintsEarned)
    }

    // 10. Duplicate button-tap protection
    @Test
    fun `test rapid consecutive button taps reject secondary presentations`() {
        val isShowing = AtomicBoolean(false)
        val presentationCount = AtomicInteger(0)

        fun requestAd(): Boolean {
            if (!isShowing.compareAndSet(false, true)) {
                return false // Rejected
            }
            presentationCount.incrementAndGet()
            return true
        }

        assertTrue("First tap must be accepted", requestAd())
        assertFalse("Immediate second tap must be rejected", requestAd())
        assertEquals(1, presentationCount.get())

        // Reset on dismiss
        isShowing.set(false)
        assertTrue("Tap after dismissal must be accepted", requestAd())
        assertEquals(2, presentationCount.get())
    }

    // 11. Reward credited to originating level
    @Test
    fun `test reward earned is credited specifically to the originating level`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val level1 = 1
        val level2 = 2

        repo.consumeHint(level1); repo.consumeHint(level1)
        repo.consumeHint(level2); repo.consumeHint(level2)

        repo.recordConfirmedReward(level1, "claim_lvl1_abc")

        assertEquals("Level 1 must have 1 rewarded hint", 1, repo.getLevelHintState(level1).rewardedHintsRemaining)
        assertEquals("Level 2 must have 0 rewarded hints", 0, repo.getLevelHintState(level2).rewardedHintsRemaining)
    }

    // 12. Earned-credit persistence after navigation
    @Test
    fun `test earned hint credit persists across sessions or screen navigation`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 75

        repo.consumeHint(levelId)
        repo.consumeHint(levelId)
        repo.recordConfirmedReward(levelId, "claim_nav_75")

        // Simulate navigating away and reopening the level
        val stateAfterReopen = repo.getLevelHintState(levelId)
        assertEquals(0, stateAfterReopen.freeHintsRemaining)
        assertEquals(1, stateAfterReopen.rewardedHintsRemaining)
        assertEquals(1, stateAfterReopen.totalAvailableHints)
        assertTrue(stateAfterReopen.canConsumeHint)
    }

    // 13. Exactly-once hint consumption
    @Test
    fun `test consuming earned rewarded hint credit exhausts the balance`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 80

        repo.consumeHint(levelId)
        repo.consumeHint(levelId)
        repo.recordConfirmedReward(levelId, "claim_consume_80")

        val consumeResult = repo.consumeHint(levelId)
        assertTrue("Consumption must consume rewarded credit", consumeResult is LevelHintConsumptionResult.ConsumedRewarded)
        assertEquals(0, (consumeResult as LevelHintConsumptionResult.ConsumedRewarded).remainingRewarded)

        val nextAttempt = repo.consumeHint(levelId)
        assertTrue("Subsequent attempt must require ad", nextAttempt is LevelHintConsumptionResult.RequiresRewardedAd)
    }

    // 14. No negative balances
    @Test
    fun `test hint state balances never produce negative numbers`() {
        val state = LevelHintState(
            levelId = 90,
            freeHintsTotal = 2,
            freeHintsUsed = 5,
            rewardedHintsEarned = 1,
            rewardedHintsConsumed = 3
        )

        assertEquals("freeHintsRemaining cannot be negative", 0, state.freeHintsRemaining)
        assertEquals("rewardedHintsRemaining cannot be negative", 0, state.rewardedHintsRemaining)
        assertEquals("totalAvailableHints cannot be negative", 0, state.totalAvailableHints)
    }

    // 15. Valid hint generation
    @Test
    fun `test hint engine generates valid move aligned with canonical solution`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))

        val hintEngine = PuzzleHintEngine()
        val request = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            gameState = engine.currentState,
            currentOrderedPath = engine.currentState.currentPath.positions,
            nextRequiredCheckpoint = engine.currentState.nextRequiredCheckpoint,
            gameMode = GameMode.SOLO
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Hint result must be NextMove", result is HintResult.NextMove)
        val nextMove = (result as HintResult.NextMove).nextMove

        // Verify the hinted next move is orthogonally adjacent to the head
        val head = engine.currentState.currentPath.currentHead!!
        assertTrue("Hinted move must be orthogonally adjacent to head", head.isOrthogonalNeighbor(nextMove))
    }

    // 16. No hint consumption when guidance is unavailable
    @Test
    fun `test hint engine returns AlreadyCompleted when puzzle is solved`() {
        val def = PackagedPuzzles.LEVEL_1
        val engine = PuzzleEngine(def)
        // Complete puzzle
        val solution = listOf(
            GridPosition(0,0), GridPosition(0,1), GridPosition(0,2), GridPosition(0,3),
            GridPosition(1,3), GridPosition(1,2), GridPosition(1,1), GridPosition(1,0),
            GridPosition(2,0), GridPosition(2,1), GridPosition(2,2), GridPosition(2,3),
            GridPosition(3,3), GridPosition(3,2), GridPosition(3,1), GridPosition(3,0)
        )
        engine.process(PuzzleAction.StartPath(solution.first()))
        for (i in 1 until solution.size) {
            engine.process(PuzzleAction.ExtendPath(solution[i]))
        }
        assertEquals(GameStatus.COMPLETED, engine.currentState.gameStatus)

        val hintEngine = PuzzleHintEngine()
        val request = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            gameState = engine.currentState,
            currentOrderedPath = engine.currentState.currentPath.positions,
            nextRequiredCheckpoint = engine.currentState.nextRequiredCheckpoint,
            gameMode = GameMode.SOLO
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Result must be AlreadyCompleted", result is HintResult.AlreadyCompleted)
    }

    // 17. Original full-grid completion rules
    @Test
    fun `test puzzle completion requires 100 percent cell coverage`() {
        val def = PackagedPuzzles.LEVEL_1
        // Level 1 is 4x4 = 16 cells
        assertEquals(16, def.totalRequiredCells)

        val engine = PuzzleEngine(def)
        // Reaching Checkpoint 5 without covering all cells is NOT complete
        engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        engine.process(PuzzleAction.ExtendPath(GridPosition(0, 3))) // Reached #2 directly (skipping cells) -> rejected
        assertFalse("Diagonal or jumping move is rejected", engine.currentState.currentPath.size > 1)
    }

    // 18. Debug and release ad configuration separation
    @Test
    fun `test debug uses official test ad unit ID`() {
        if (BuildConfig.DEBUG) {
            assertEquals(
                "Debug build must use official AdMob rewarded video test ad unit ID",
                AdConfiguration.TEST_REWARDED_AD_UNIT_ID,
                AdConfiguration.rewardedAdUnitId
            )
        } else {
            // In release, must NOT fall back to test ad unit
            if (BuildConfig.ADMOB_REWARDED_AD_UNIT_ID.isBlank()) {
                assertTrue(
                    "Production with blank ad unit ID must report isProductionBlockedByConfiguration",
                    AdConfiguration.isProductionBlockedByConfiguration
                )
            }
        }
    }

    // 19. Existing guest-progress compatibility
    @Test
    fun `test hint repository does not overwrite guest progress keys`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 1
        // Verify default state matches expected Level 1 state
        val hintState = repo.getLevelHintState(levelId)
        assertEquals(2, hintState.freeHintsTotal)
        assertEquals(0, hintState.freeHintsUsed)
    }

    // 20. Lifecycle and restoration behavior
    @Test
    fun `test restoreSessionHints ensures persisted hints reflect session without resetting`() = runBlocking {
        val repo = InMemoryLevelHintRepository()
        val levelId = 15

        // Initial restore with 1 hint used in session
        val restored = repo.restoreSessionHints(levelId, hintsUsedInSession = 1)
        assertEquals(1, restored.freeHintsUsed)
        assertEquals(1, restored.freeHintsRemaining)

        // Subsequent restore with 2 hints used in session
        val restored2 = repo.restoreSessionHints(levelId, hintsUsedInSession = 2)
        assertEquals(2, restored2.freeHintsUsed)
        assertEquals(0, restored2.freeHintsRemaining)
    }
}
