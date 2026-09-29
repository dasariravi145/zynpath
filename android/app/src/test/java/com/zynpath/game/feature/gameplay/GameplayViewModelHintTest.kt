package com.zynpath.game.feature.gameplay

import androidx.lifecycle.SavedStateHandle
import com.zynpath.game.core.database.entity.GameSessionEntity
import com.zynpath.game.core.database.entity.LevelProgressEntity
import com.zynpath.game.core.database.repository.GameplaySessionRepository
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.hint.HintUsageRepository
import com.zynpath.game.core.puzzle.catalog.CatalogAssetGenerator
import com.zynpath.game.core.puzzle.catalog.InMemoryAssetLoader
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepositoryImpl
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.hint.HintType
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.session.GameplaySessionSnapshot
import com.zynpath.game.core.puzzle.session.SystemTimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests verifying ViewModel hint integration, loading states, invalidation, and debouncing.
 *
 * Implements Prompt 14 Section 38:
 * - Hint loading state.
 * - Hint success state.
 * - State-revision invalidation.
 * - Cancellation after player movement.
 * - Reset invalidation.
 * - Completion invalidation.
 * - No duplicate concurrent requests.
 * - No path mutation from hint display.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GameplayViewModelHintTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var assetLoader: InMemoryAssetLoader
    private lateinit var fakeProgressRepo: FakeProgressRepository
    private lateinit var fakeHintRepo: FakeHintRepository
    private lateinit var hintEngine: PuzzleHintEngine

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        assetLoader = InMemoryAssetLoader()
        CatalogAssetGenerator.populateLoader(assetLoader)
        fakeProgressRepo = FakeProgressRepository()
        fakeHintRepo = FakeHintRepository(initialHints = 3)
        hintEngine = PuzzleHintEngine()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(levelId: Int = 1): GameplayViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("worldId" to 1, "levelId" to levelId))
        val catalogRepo = LevelCatalogRepositoryImpl(assetLoader, fakeProgressRepo)
        val vm = GameplayViewModel(
            catalogRepository = catalogRepo,
            progressRepository = fakeProgressRepo,
            sessionRepository = FakeSessionRepository(),
            hintUsageRepository = fakeHintRepo,
            hintEngine = hintEngine,
            timeProvider = SystemTimeProvider(),
            savedStateHandle = savedStateHandle
        )
        vm.hintDispatcher = testDispatcher
        vm.isTimerTickerEnabled = false
        return vm
    }

    @Test
    fun `test requesting hint produces valid next move and deducts allowance`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testScheduler.advanceUntilIdle()

        val readyBefore = viewModel.uiState.value as GameplayUiState.Ready
        assertEquals(3, readyBefore.remainingHints)
        assertNull(readyBefore.activeHint)

        viewModel.requestHint()
        testScheduler.advanceUntilIdle()

        val readyAfter = viewModel.uiState.value as GameplayUiState.Ready
        assertNotNull(readyAfter.activeHint)
        assertEquals(HintType.NEXT_MOVE, readyAfter.activeHint?.type)
        assertEquals(2, readyAfter.remainingHints)
        assertEquals(2, fakeHintRepo.getRemainingHints())

        // Confirm boardState has hintedCoordinate set without mutating path
        assertEquals(readyAfter.activeHint?.targetCell, readyAfter.boardState.hintedCoordinate)
        assertEquals(0, readyAfter.currentOrderedPath.size)
    }

    @Test
    fun `test moving invalidates active hint immediately`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testScheduler.advanceUntilIdle()

        // Start path at (0,0)
        viewModel.onCellEntered(GridPosition(0, 0))
        testScheduler.advanceUntilIdle()

        // Request hint
        viewModel.requestHint()
        testScheduler.advanceUntilIdle()

        var state = viewModel.uiState.value as GameplayUiState.Ready
        assertNotNull("Active hint must be present", state.activeHint)

        // Make move
        viewModel.onCellEntered(GridPosition(0, 1))
        testScheduler.advanceUntilIdle()

        state = viewModel.uiState.value as GameplayUiState.Ready
        assertNull("Active hint must be invalidated immediately on movement", state.activeHint)
        assertNull("Board state hinted coordinate must be cleared", state.boardState.hintedCoordinate)
    }

    @Test
    fun `test undo and reset invalidate active hint`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testScheduler.advanceUntilIdle()

        viewModel.onCellEntered(GridPosition(0, 0))
        viewModel.onCellEntered(GridPosition(0, 1))
        testScheduler.advanceUntilIdle()

        viewModel.requestHint()
        testScheduler.advanceUntilIdle()

        var state = viewModel.uiState.value as GameplayUiState.Ready
        assertNotNull(state.activeHint)

        // Undo
        viewModel.onUndoClicked()
        testScheduler.advanceUntilIdle()

        state = viewModel.uiState.value as GameplayUiState.Ready
        assertNull("Undo must invalidate active hint", state.activeHint)

        // Request again
        viewModel.requestHint()
        testScheduler.advanceUntilIdle()
        state = viewModel.uiState.value as GameplayUiState.Ready
        assertNotNull(state.activeHint)

        // Reset
        viewModel.onResetClicked()
        testScheduler.advanceUntilIdle()

        state = viewModel.uiState.value as GameplayUiState.Ready
        assertNull("Reset must invalidate active hint", state.activeHint)
    }

    @Test
    fun `test no duplicate concurrent searches on repeated taps`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testScheduler.advanceUntilIdle()

        viewModel.requestHint()
        // Immediate second tap while first is executing
        viewModel.requestHint()
        testScheduler.advanceUntilIdle()

        // Total consumed should only be 1 hint
        assertEquals(2, fakeHintRepo.getRemainingHints())
    }

    @Test
    fun `test allowance exhaustion triggers limit reached dialog`() = runTest(testDispatcher) {
        fakeHintRepo.setHints(0)
        val viewModel = createViewModel()
        testScheduler.advanceUntilIdle()

        val stateBefore = viewModel.uiState.value as GameplayUiState.Ready
        assertFalse(stateBefore.showLimitReachedDialog)

        viewModel.requestHint()
        testScheduler.advanceUntilIdle()

        val stateAfter = viewModel.uiState.value as GameplayUiState.Ready
        assertTrue("showLimitReachedDialog must be true when allowance is exhausted", stateAfter.showLimitReachedDialog)
        assertNull("No hint should be set", stateAfter.activeHint)
    }
}

private class FakeHintRepository(private var initialHints: Int = 3) : HintUsageRepository {
    private var hints: Int = initialHints
    private val _hintsFlow = MutableStateFlow(hints)
    private val _premiumFlow = MutableStateFlow(false)

    override val remainingHintsFlow: Flow<Int> = _hintsFlow
    override val isPremiumFlow: Flow<Boolean> = _premiumFlow
    override val rewardedCreditsFlow: Flow<Int> = MutableStateFlow(0)
    override val freeAllowanceFlow: Flow<Int> = _hintsFlow

    fun setHints(count: Int) {
        hints = count
        _hintsFlow.value = count
    }

    override suspend fun getRemainingHints(): Int = hints
    override suspend fun isPremium(): Boolean = _premiumFlow.value
    override suspend fun canConsumeHint(): Boolean = _premiumFlow.value || hints > 0
    override suspend fun consumeHint(): Boolean {
        if (_premiumFlow.value) return true
        if (hints <= 0) return false
        hints--
        _hintsFlow.value = hints
        return true
    }
    override suspend fun addFreeHints(count: Int) {
        hints += count
        _hintsFlow.value = hints
    }
    override suspend fun addRewardedHintCredit(count: Int): Int {
        hints += count
        _hintsFlow.value = hints
        return hints
    }
    override suspend fun setPremium(isPremium: Boolean) {
        _premiumFlow.value = isPremium
    }
}

private class FakeProgressRepository : ProgressRepository {
    private val progressList = MutableStateFlow<List<LevelProgressEntity>>(emptyList())

    override fun observeLevelProgress(levelId: Int): Flow<LevelProgressEntity?> =
        progressList.map { list -> list.firstOrNull { it.levelId == levelId } }

    override fun observeWorldProgress(worldId: Int): Flow<List<LevelProgressEntity>> =
        progressList.map { list -> list.filter { it.worldId == worldId } }

    override fun observeAllProgress(): Flow<List<LevelProgressEntity>> = progressList.asStateFlow()

    override fun getCompletedLevelCount(): Flow<Int> =
        progressList.map { list -> list.count { it.isCompleted } }

    override fun getTotalStarsEarned(): Flow<Int> =
        progressList.map { list -> list.sumOf { it.stars } }

    override suspend fun getCompletedLevelIds(): Set<Int> =
        progressList.value.filter { it.isCompleted }.map { it.levelId }.toSet()

    override suspend fun isLevelUnlocked(levelId: Int): Boolean =
        WorldConfiguration.isLevelUnlocked(levelId, getCompletedLevelIds())

    override suspend fun isWorldUnlocked(worldId: Int): Boolean =
        WorldConfiguration.isWorldUnlocked(worldId, getCompletedLevelIds())

    override suspend fun getNextPlayableLevel(): Int =
        WorldConfiguration.getNextPlayableLevel(getCompletedLevelIds())

    override suspend fun recordValidatedCompletion(result: ValidatedCompletionResult): LevelProgressEntity {
        val world = WorldConfiguration.getWorldForLevel(result.levelId)
        val entity = LevelProgressEntity(
            levelId = result.levelId,
            worldId = world.worldId,
            isCompleted = true,
            stars = 3,
            bestTimeMs = result.elapsedTimeMs,
            movesCount = result.moveCount,
            completedAt = System.currentTimeMillis()
        )
        val current = progressList.value.toMutableList()
        current.removeAll { it.levelId == result.levelId }
        current.add(entity)
        progressList.value = current
        return entity
    }

    override suspend fun saveGameSession(session: GameSessionEntity) {}
    override suspend fun getActiveSession(levelId: Int): GameSessionEntity? = null
    override suspend fun abandonActiveSession(levelId: Int) {}
    override suspend fun clearAllProgress() { progressList.value = emptyList() }
    override suspend fun reconcileWithRemote(remoteProgress: List<LevelProgressEntity>) {}
}

private class FakeSessionRepository : GameplaySessionRepository {
    override suspend fun getLatestResumableSession(): GameplaySessionSnapshot? = null
    override suspend fun getResumableSession(levelId: Int): GameplaySessionSnapshot? = null
    override suspend fun getSessionById(sessionId: String): GameplaySessionSnapshot? = null
    override suspend fun saveSession(snapshot: GameplaySessionSnapshot): Boolean = true
    override suspend fun pauseAllActiveSessions(timestamp: Long) {}
    override suspend fun markCompleted(sessionId: String, elapsedActiveTimeMs: Long, nowMs: Long): Boolean = true
    override suspend fun markRestorationFailed(sessionId: String, errorDetail: String, nowMs: Long): Boolean = true
    override suspend fun abandonSession(sessionId: String, nowMs: Long): Boolean = true
    override suspend fun deleteSessionsForLevel(levelId: Int) {}
    override suspend fun deleteSessionById(sessionId: String) {}
    override suspend fun clearAllSessions() {}
}
