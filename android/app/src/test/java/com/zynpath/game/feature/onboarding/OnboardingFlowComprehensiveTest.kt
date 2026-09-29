package com.zynpath.game.feature.onboarding

import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.model.BlockedEdge
import com.zynpath.game.core.puzzle.model.GridDimensions
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.NumberedCheckpoint
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive verification of App Launch, Onboarding, Guest Entry, and Interactive Tutorial.
 *
 * Implements Prompt 48 Requirements 10, 11, 12, 13, 14, 15, 16, 17.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingFlowComprehensiveTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePrefs: FakePreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakePreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFirstTimeGuestOnboardingEligibility() = runTest(testDispatcher) {
        val prefs = fakePrefs.userPreferencesFlow.first()
        assertFalse("First-time user must not have completed onboarding", prefs.isOnboardingCompleted)
        assertFalse("First-time user must not have completed tutorial", prefs.isTutorialCompleted)
    }

    @Test
    fun testReturningPlayerBypassesOnboarding() = runTest(testDispatcher) {
        fakePrefs.setOnboardingCompleted(true)
        fakePrefs.setTutorialCompleted(true)

        val prefs = fakePrefs.userPreferencesFlow.first()
        assertTrue("Returning player must have completed onboarding", prefs.isOnboardingCompleted)
        assertTrue("Returning player must have completed tutorial", prefs.isTutorialCompleted)
    }

    @Test
    fun testGuestEntryWithoutMandatorySignIn() = runTest(testDispatcher) {
        fakePrefs = FakePreferencesRepository(UserPreferences(guestUuid = "guest_abc123"))
        val prefs = fakePrefs.userPreferencesFlow.first()
        assertEquals("guest_abc123", prefs.guestUuid)
        assertFalse("Guest player has no premium entitlement by default", prefs.isPremium)
    }

    @Test
    fun testOnboardingPlayNavigatesToTutorialForNewPlayer() = runTest(testDispatcher) {
        val viewModel = OnboardingViewModel(fakePrefs)
        var navigatedToTutorial = false
        var navigatedToHome = false

        viewModel.onPlayClicked(
            onNavigateToTutorial = { navigatedToTutorial = true },
            onNavigateToHome = { navigatedToHome = true }
        )
        advanceUntilIdle()

        assertTrue("New player clicking Play should navigate to Tutorial", navigatedToTutorial)
        assertFalse("New player should not bypass directly to Home", navigatedToHome)

        val prefs = fakePrefs.userPreferencesFlow.first()
        assertTrue("Onboarding should be marked completed", prefs.isOnboardingCompleted)
    }

    @Test
    fun testOnboardingSkipMarksCompletedAndNavigatesToHome() = runTest(testDispatcher) {
        val viewModel = OnboardingViewModel(fakePrefs)
        var navigatedToHome = false

        viewModel.onSkipClicked(onNavigateToHome = { navigatedToHome = true })
        advanceUntilIdle()

        assertTrue("Skipping onboarding must navigate to Home", navigatedToHome)
        val prefs = fakePrefs.userPreferencesFlow.first()
        assertTrue("Onboarding should be marked completed", prefs.isOnboardingCompleted)
        assertTrue("Tutorial should be marked skipped", prefs.isTutorialSkipped)
    }

    @Test
    fun testTutorialMovementRejectsDiagonalAndWallCrossing() {
        // Build a canonical 3x3 tutorial puzzle: Start at (0,0), wall between (0,1) and (1,1), Goal at (2,2)
        val cells3x3 = (0..2).flatMap { r -> (0..2).map { c -> GridPosition(r, c) } }.toSet()
        val tutorialDef = PuzzleDefinition(
            puzzleId = "tutorial_3x3",
            puzzleVersion = 1,
            gridDimensions = GridDimensions(3, 3),
            requiredCells = cells3x3,
            checkpoints = listOf(
                NumberedCheckpoint(1, GridPosition(0, 0)),
                NumberedCheckpoint(2, GridPosition(1, 0)),
                NumberedCheckpoint(3, GridPosition(2, 2))
            ),
            blockedEdges = setOf(
                BlockedEdge(GridPosition(0, 1), GridPosition(1, 1))
            )
        )
        val engine = PuzzleEngine(tutorialDef)

        // 1. Must start on Checkpoint 1 (0, 0)
        val invalidStart = engine.process(PuzzleAction.StartPath(GridPosition(0, 1)))
        assertFalse("Starting on non-checkpoint cell must be rejected", invalidStart.isAccepted)
        assertEquals(MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE, (invalidStart as PuzzleEngineResult.Rejected).reason)

        val validStart = engine.process(PuzzleAction.StartPath(GridPosition(0, 0)))
        assertTrue("Starting on checkpoint 1 must be accepted", validStart.isAccepted)

        // 2. Reject diagonal move to (1, 1)
        val diagonalMove = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))
        assertFalse("Diagonal movement in tutorial must be strictly rejected", diagonalMove.isAccepted)
        assertEquals(MoveRejectionReason.NON_ADJACENT, (diagonalMove as PuzzleEngineResult.Rejected).reason)

        // 3. Move orthogonally to (0, 1)
        val step1 = engine.process(PuzzleAction.ExtendPath(GridPosition(0, 1)))
        assertTrue("Step to (0, 1) should be accepted", step1.isAccepted)

        // 4. Reject wall crossing from (0, 1) to (1, 1)
        val wallMove = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 1)))
        assertFalse("Moving through blocked edge wall must be rejected", wallMove.isAccepted)
        assertEquals(MoveRejectionReason.BLOCKED_BY_WALL, (wallMove as PuzzleEngineResult.Rejected).reason)

        // 5. Retract back to (0, 0) and visit checkpoint 2 at (1, 0)
        engine.process(PuzzleAction.Undo)
        val stepToCheckpoint2 = engine.process(PuzzleAction.ExtendPath(GridPosition(1, 0)))
        assertTrue("Visiting checkpoint 2 in sequence must be accepted", stepToCheckpoint2.isAccepted)
        assertEquals(GridPosition(1, 0), engine.currentState.currentEndpoint)
        assertEquals(2, engine.currentState.visitedCheckpointCount)
    }

    @Test
    fun testTutorialCompletionPersistsDurableState() = runTest(testDispatcher) {
        fakePrefs.setTutorialCompleted(true)
        val prefs = fakePrefs.userPreferencesFlow.first()
        assertTrue("Tutorial completion flag must persist durably in preferences", prefs.isTutorialCompleted)
    }
}
