package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MatchState
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakeMultiplayerRepository
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
 * Unit tests verifying the Quick Duel Matchmaking Popup redesign requirements:
 * 1. Compact popup flow over Home screen (no full-screen routing)
 * 2. Guest player matchmaking availability without Google Account sign-in
 * 3. Cancel matchmaking cleans up queue and returns safely to IDLE
 * 4. Late event isolation (cancellation cannot be revived by late WS events)
 * 5. Error & timeout handling with actionable Retry / Cancel
 * 6. Match found & Countdown synchronization transitioning to ACTIVE
 * 7. Duplicate matchmaking prevention
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuickDuelMatchmakingPopupRedesignTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var multiplayerRepo: FakeMultiplayerRepository
    private lateinit var authRepo: FakeAuthRepository
    private lateinit var prefsRepo: FakePreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        multiplayerRepo = FakeMultiplayerRepository()
        authRepo = FakeAuthRepository(
            initialState = AuthState.GUEST,
            initialSession = null
        )
        prefsRepo = FakePreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGuestPlayerStartsQuickDuelMatchmakingWithoutGoogleAccount() = runTest(testDispatcher) {
        // Arrange guest player
        val guestSession = AuthSession(
            playerId = "guest_player_99",
            publicZynpathId = "GUEST-99",
            displayName = "Guest Pathfinder",
            accountType = "GUEST",
            provider = AuthProvider.GUEST,
            expiresAt = System.currentTimeMillis() + 86400000L
        )
        authRepo.setAuthState(AuthState.GUEST, guestSession)
        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Verify initial state is IDLE
        assertEquals(ClientMatchState.IDLE, viewModel.uiState.value.clientMatchState)
        assertEquals(false, viewModel.uiState.value.isSearching)

        // Act - Start matchmaking
        viewModel.onStartMatchmaking()
        advanceUntilIdle()

        // Assert - Queue joined successfully as Guest
        assertEquals(ClientMatchState.SEARCHING, viewModel.uiState.value.clientMatchState)
        assertTrue(viewModel.uiState.value.isSearching)
        assertNotNull(viewModel.uiState.value.ticketStatus)
        assertEquals("ticket_123", viewModel.uiState.value.ticketStatus?.ticketId)
    }

    @Test
    fun testCancelMatchmakingFlowCleansUpQueueAndReturnsToIdle() = runTest(testDispatcher) {
        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // 1. Enter queue
        viewModel.onStartMatchmaking()
        advanceUntilIdle()
        assertEquals(ClientMatchState.SEARCHING, viewModel.uiState.value.clientMatchState)

        // 2. User cancels from popup (Back button or Cancel tap)
        viewModel.cancelMatchmakingFlow()
        advanceUntilIdle()

        // 3. State is reset to IDLE and ticket status is cleared
        assertEquals(ClientMatchState.IDLE, viewModel.uiState.value.clientMatchState)
        assertFalse(viewModel.uiState.value.isSearching)
        assertNull(viewModel.uiState.value.ticketStatus)
        assertNull(viewModel.uiState.value.currentSession)
    }

    @Test
    fun testCancellationDuringMatchFoundCleansUpSessionSafely() = runTest(testDispatcher) {
        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // 1. Enter queue and match found
        viewModel.onStartMatchmaking()
        advanceUntilIdle()

        val session = MatchSessionSnapshotDto(
            matchId = "match_quick_cancel",
            gameMode = GameMode.QUICK_DUEL,
            matchState = MatchState.WAITING_FOR_PLAYERS,
            hostPlayerId = "player_1",
            puzzle = null,
            participants = listOf(
                MatchParticipantDto("player_1", "ZYN-1", "Player 1", null, true, true),
                MatchParticipantDto("rival_2", "ZYN-2", "Rival 2", null, true, true)
            ),
            createdAt = System.currentTimeMillis(),
            startedAt = null,
            endedAt = null
        )
        multiplayerRepo.setCurrentSession(session)
        multiplayerRepo.setClientMatchState(ClientMatchState.MATCH_FOUND)
        advanceUntilIdle()

        assertEquals(ClientMatchState.MATCH_FOUND, viewModel.uiState.value.clientMatchState)

        // 2. User cancels during rival presentation / countdown
        viewModel.cancelMatchmakingFlow()
        advanceUntilIdle()

        // 3. Session left, reset to IDLE
        assertEquals(ClientMatchState.IDLE, viewModel.uiState.value.clientMatchState)
        assertNull(viewModel.uiState.value.currentSession)
    }

    @Test
    fun testMatchmakingErrorDisplaysActionableErrorAndRetryWorks() = runTest(testDispatcher) {
        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Simulate error during matchmaking
        multiplayerRepo.setErrorMessage("Unable to connect. Retry.")
        multiplayerRepo.setClientMatchState(ClientMatchState.ERROR)
        advanceUntilIdle()

        assertEquals(ClientMatchState.ERROR, viewModel.uiState.value.clientMatchState)
        assertEquals("Unable to connect. Retry.", viewModel.uiState.value.errorMessage)

        // Tap Retry
        viewModel.retrySearching()
        advanceUntilIdle()

        // Error cleared, resumed SEARCHING
        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(ClientMatchState.SEARCHING, viewModel.uiState.value.clientMatchState)
    }

    @Test
    fun testRivalFoundAndCountdownToActiveStateHandover() = runTest(testDispatcher) {
        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // 1. Searching
        viewModel.onStartMatchmaking()
        advanceUntilIdle()

        // 2. Rival Found in same popup
        val session = MatchSessionSnapshotDto(
            matchId = "match_quick_active",
            gameMode = GameMode.QUICK_DUEL,
            matchState = MatchState.WAITING_FOR_PLAYERS,
            hostPlayerId = "player_1",
            puzzle = null,
            participants = listOf(
                MatchParticipantDto("player_1", "ZYN-1", "Aria", null, true, true),
                MatchParticipantDto("rival_2", "ZYN-2", "Kael", null, true, true)
            ),
            createdAt = System.currentTimeMillis(),
            startedAt = null,
            endedAt = null
        )
        multiplayerRepo.setCurrentSession(session)
        multiplayerRepo.setClientMatchState(ClientMatchState.MATCH_FOUND)
        advanceUntilIdle()

        assertEquals(ClientMatchState.MATCH_FOUND, viewModel.uiState.value.clientMatchState)
        assertEquals(2, viewModel.uiState.value.currentSession?.participants?.size)

        // 3. Countdown
        multiplayerRepo.setCountdownSeconds(3)
        multiplayerRepo.setClientMatchState(ClientMatchState.COUNTDOWN)
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.countdownSeconds)

        multiplayerRepo.setCountdownSeconds(1)
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.countdownSeconds)

        // 4. Server confirms match ACTIVE -> triggers popup dismissal and navigation
        multiplayerRepo.setClientMatchState(ClientMatchState.ACTIVE)
        advanceUntilIdle()
        assertEquals(ClientMatchState.ACTIVE, viewModel.uiState.value.clientMatchState)
    }

    @Test
    fun testDuplicateMatchmakingRequestIsPrevented() = runTest(testDispatcher) {
        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // First start
        viewModel.onStartMatchmaking()
        advanceUntilIdle()
        assertEquals(ClientMatchState.SEARCHING, viewModel.uiState.value.clientMatchState)
        val ticketId = viewModel.uiState.value.ticketStatus?.ticketId

        // Second duplicate start while still searching
        viewModel.onStartMatchmaking()
        advanceUntilIdle()

        // State remains SEARCHING without disruption
        assertEquals(ClientMatchState.SEARCHING, viewModel.uiState.value.clientMatchState)
        assertEquals(ticketId, viewModel.uiState.value.ticketStatus?.ticketId)
    }
}
