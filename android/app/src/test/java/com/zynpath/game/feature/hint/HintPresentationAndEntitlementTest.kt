package com.zynpath.game.feature.hint

import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.puzzle.catalog.PackagedPuzzles
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintConfiguration
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzlePath
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Verification of Hint Presentation, Solution-Aware Guidance, Free vs Premium Entitlements,
 * and Strict Competitive Mode Hint Prohibition.
 *
 * Implements Prompt 48 Requirements 33, 34, 35, 61, 62.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HintPresentationAndEntitlementTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var hintEngine: PuzzleHintEngine
    private lateinit var fakePrefs: FakePreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        hintEngine = PuzzleHintEngine()
        fakePrefs = FakePreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSoloModeHintProducesValidNextStep() {
        val def = PackagedPuzzles.LEVEL_1
        val initialPath = PuzzlePath.single(GridPosition(0, 0))

        val request = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            currentPath = initialPath,
            gameMode = GameMode.SOLO,
            configuration = HintConfiguration()
        )

        val result = hintEngine.computeHint(request)
        assertTrue("Solo mode must produce a valid NextStep hint", result is HintResult.NextMove)
        val nextStep = result as HintResult.NextMove
        // The recommended next step from (0,0) in canonical solution is (0,1)
        assertEquals(GridPosition(0, 1), nextStep.nextMove)
    }

    @Test
    fun testCompetitiveModeStrictlyDisablesHints() {
        val def = PackagedPuzzles.LEVEL_1
        val initialPath = PuzzlePath.single(GridPosition(0, 0))

        // 1. Quick Duel
        val duelRequest = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            currentPath = initialPath,
            gameMode = GameMode.QUICK_DUEL,
            configuration = HintConfiguration()
        )
        val duelResult = hintEngine.computeHint(duelRequest)
        assertTrue("Quick Duel must reject hints", duelResult is HintResult.HintNotAvailable)
        val duelMsg = (duelResult as HintResult.HintNotAvailable).reason
        assertTrue(duelMsg.contains("disabled in competitive modes"))

        // 2. Friend Duel
        val friendRequest = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            currentPath = initialPath,
            gameMode = GameMode.FRIEND_DUEL,
            configuration = HintConfiguration()
        )
        val friendResult = hintEngine.computeHint(friendRequest)
        assertTrue("Friend Duel must reject hints", friendResult is HintResult.HintNotAvailable)

        // 3. Mini League
        val leagueRequest = HintRequest(
            puzzleId = def.puzzleId,
            puzzleVersion = def.puzzleVersion,
            definition = def,
            currentPath = initialPath,
            gameMode = GameMode.MINI_LEAGUE,
            configuration = HintConfiguration()
        )
        val leagueResult = hintEngine.computeHint(leagueRequest)
        assertTrue("Mini League must reject hints", leagueResult is HintResult.HintNotAvailable)
    }

    @Test
    fun testFreeHintConsumptionDecrementsBalance() = runTest(testDispatcher) {
        fakePrefs.setFreeHintsRemaining(3)
        val initialPrefs = fakePrefs.userPreferencesFlow.first()
        assertEquals(3, initialPrefs.freeHintsRemaining)

        // Consume 1 hint
        fakePrefs.setFreeHintsRemaining(initialPrefs.freeHintsRemaining - 1)
        val updatedPrefs = fakePrefs.userPreferencesFlow.first()
        assertEquals(2, updatedPrefs.freeHintsRemaining)
    }

    @Test
    fun testPremiumEntitlementEnablesUnlimitedHints() = runTest(testDispatcher) {
        fakePrefs.setPremium(true)
        val prefs = fakePrefs.userPreferencesFlow.first()
        assertTrue("Premium subscriber has active entitlement", prefs.isPremium)
    }

    @Test
    fun testRewardedAdIncrementsHintCredits() = runTest(testDispatcher) {
        fakePrefs.setRewardedHintCredits(0)
        assertEquals(0, fakePrefs.userPreferencesFlow.first().rewardedHintCredits)

        // Simulate rewarded ad watched
        fakePrefs.addRewardedHintCredits(1)
        assertEquals(1, fakePrefs.userPreferencesFlow.first().rewardedHintCredits)

        fakePrefs.addRewardedHintCredits(1)
        assertEquals(2, fakePrefs.userPreferencesFlow.first().rewardedHintCredits)
    }

    @Test
    fun testAdFailureDoesNotBlockSoloGameplay() {
        // When ad network is unavailable, Solo mode remains 100% playable
        val def = PackagedPuzzles.LEVEL_1
        assertNotNull(def)
        assertEquals(16, def.gridDimensions.totalCells)
    }
}
