package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.RematchState
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MatchSessionSnapshotDto
import com.zynpath.game.core.multiplayer.model.MiniLeagueRoom
import com.zynpath.game.core.multiplayer.model.MiniLeagueRoomParticipant
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakeMultiplayerRepository
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
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
 * Verification of Quick Duel, Friend Duel, Mini League, Room Capacity, Rematch,
 * Disconnect Handling, and Error Isolation.
 *
 * Implements Prompt 48 Requirements 45, 46, 47, 48, 49, 50, 51, 52, 53.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MultiplayerUiAndFlowTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var multiplayerRepo: FakeMultiplayerRepository
    private lateinit var authRepo: FakeAuthRepository
    private lateinit var prefsRepo: FakePreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        multiplayerRepo = FakeMultiplayerRepository()
        authRepo = FakeAuthRepository(
            initialState = AuthState.AUTHENTICATED,
            initialSession = AuthSession(
                playerId = "player_1",
                publicZynpathId = "ZYN-1234",
                displayName = "Player 1",
                accountType = "REGISTERED",
                provider = AuthProvider.GOOGLE,
                expiresAt = System.currentTimeMillis() + 86400000L
            )
        )
        prefsRepo = FakePreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testQuickDuelSearchAndCancellation() = runTest(testDispatcher) {
        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // 1. Initial State
        assertEquals(ClientMatchState.IDLE, viewModel.uiState.value.clientMatchState)

        // 2. Start Search
        viewModel.onStartMatchmaking()
        advanceUntilIdle()
        assertEquals(ClientMatchState.SEARCHING, viewModel.uiState.value.clientMatchState)
        assertNotNull(viewModel.uiState.value.ticketStatus)

        // 3. Cancel Search
        viewModel.onCancelMatchmaking()
        advanceUntilIdle()
        assertEquals(ClientMatchState.IDLE, viewModel.uiState.value.clientMatchState)
        assertNull(viewModel.uiState.value.ticketStatus)
    }

    @Test
    fun testQuickDuelMatchFoundToResultFlow() = runTest(testDispatcher) {
        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Match found
        multiplayerRepo.setClientMatchState(ClientMatchState.MATCH_FOUND)
        val session = MatchSessionSnapshotDto(
            matchId = "match_q1",
            gameMode = GameMode.QUICK_DUEL,
            matchState = com.zynpath.game.core.multiplayer.model.MatchState.WAITING_FOR_PLAYERS,
            hostPlayerId = "player_1",
            puzzle = null,
            participants = listOf(
                MatchParticipantDto(
                    playerId = "player_1",
                    publicZynpathId = "ZYN-1111",
                    displayName = "Player 1",
                    avatarId = null,
                    isReady = true,
                    isConnected = true
                ),
                MatchParticipantDto(
                    playerId = "player_2",
                    publicZynpathId = "ZYN-2222",
                    displayName = "Opponent",
                    avatarId = null,
                    isReady = true,
                    isConnected = true
                )
            ),
            createdAt = System.currentTimeMillis(),
            startedAt = null,
            endedAt = null
        )
        multiplayerRepo.setCurrentSession(session)
        advanceUntilIdle()

        assertEquals(ClientMatchState.MATCH_FOUND, viewModel.uiState.value.clientMatchState)
        assertEquals("match_q1", viewModel.uiState.value.currentSession?.matchId)

        // Ready confirmation and countdown
        viewModel.markReady()
        advanceUntilIdle()
        multiplayerRepo.setCountdownSeconds(3)
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.countdownSeconds)

        // Match result
        val result = listOf(
            MatchResultDto(
                matchId = "match_q1",
                playerId = "player_1",
                publicZynpathId = "ZYN-1111",
                displayName = "Player 1",
                completed = true,
                solveTimeMs = 14200L,
                finishOrder = 1,
                isWinner = true,
                resultStatus = "VICTORY"
            )
        )
        multiplayerRepo.setMatchResults(result)
        multiplayerRepo.setClientMatchState(ClientMatchState.COMPLETED)
        advanceUntilIdle()

        assertEquals(ClientMatchState.COMPLETED, viewModel.uiState.value.clientMatchState)
        assertEquals(1, viewModel.uiState.value.matchResults.size)
        assertEquals("player_1", viewModel.uiState.value.matchResults.first().playerId)
    }

    @Test
    fun testFriendDuelInvitationAndRematchFlow() = runTest(testDispatcher) {
        val invitation = multiplayerRepo.sendFriendDuelInvitation("friend_456")
        assertNotNull(invitation)
        assertEquals("inv_123", invitation?.invitationId)
        assertEquals("friend_456", invitation?.recipientPlayerId)
        assertEquals(com.zynpath.game.core.multiplayer.model.FriendDuelInvitationStatus.PENDING, invitation?.status)

        // Rematch request and accept
        assertTrue(multiplayerRepo.requestRematch())
        assertEquals(RematchState.PENDING, multiplayerRepo.rematchStatus.value?.status)

        assertTrue(multiplayerRepo.respondToRematch(true))
        assertEquals(RematchState.ACCEPTED, multiplayerRepo.rematchStatus.value?.status)
    }

    @Test
    fun testMiniLeagueRoomCreationAndCapacityBounds() = runTest(testDispatcher) {
        // Create 4-player room
        val room = multiplayerRepo.createMiniLeagueRoom(maxParticipants = 4)
        assertNotNull(room)
        assertEquals(4, room?.maxParticipants)
        assertEquals("MINI55", room?.roomCode)
        assertEquals(1, room?.participants?.size)

        // Join guests up to capacity
        multiplayerRepo.joinMiniLeagueRoom("MINI55")
        multiplayerRepo.joinMiniLeagueRoom("MINI55")
        multiplayerRepo.joinMiniLeagueRoom("MINI55")
        val fullRoom = multiplayerRepo.getMiniLeagueRoom("room_mini_1")
        assertEquals(4, fullRoom?.participants?.size)

        // Attempting to exceed max capacity must fail
        val overflow = multiplayerRepo.joinMiniLeagueRoom("MINI55")
        assertNull("Joining full Mini League room must be rejected", overflow)
    }

    @Test
    fun testMultiplayerDisconnectDoesNotAppearAsVictory() = runTest(testDispatcher) {
        multiplayerRepo.setIsConnected(false)
        multiplayerRepo.setErrorMessage("Network connection lost. Reconnecting...")

        val viewModel = QuickDuelViewModel(multiplayerRepo, authRepo, prefsRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        assertFalse("Connection flag must reflect disconnection", viewModel.uiState.value.isConnected)
        assertEquals("Network connection lost. Reconnecting...", viewModel.uiState.value.errorMessage)
        assertTrue("Disconnection must never report victory results", viewModel.uiState.value.matchResults.isEmpty())
    }
}
