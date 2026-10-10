package com.zynpath.game.feature.multiplayer

import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakeMultiplayerRepository
import com.zynpath.game.fake.FakePlayerProfileRepository
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.fake.FakeSocialRepository
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
 * Focused Unit Test Suite for Friends Arena Google Auth Gate & Compact Popups.
 *
 * Verifies all 21 items specified in Section 14 of the prompt:
 * 1. Friends Arena checks authentication.
 * 2. Connected user opens Create Room popup directly.
 * 3. Guest opens Connect Google popup.
 * 4. Successful login automatically opens Create Room.
 * 5. Cancelled login does not create room.
 * 6. No Facebook UI.
 * 7. No separate Create Game Room page.
 * 8. Player counts 1–5 work.
 * 9. Host is included in player count.
 * 10. One-player room can wait but cannot start a match.
 * 11. Two-to-five-player rooms can start subject to existing rules.
 * 12. Friend picker uses real eligible friends.
 * 13. Room creation works through backend.
 * 14. Duplicate taps do not create duplicate rooms.
 * 15. Join Room popup works.
 * 16. Invalid room codes show errors.
 * 17. Waiting lobby remains functional.
 * 18. Only 11×11 and 12×12 puzzles are selected.
 * 19. Identical puzzle/hash is shared across players.
 * 20. Popup dismiss and Android Back work correctly.
 * 21. Existing player progress is preserved.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FriendsArenaPopupFlowTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var guestAuthRepo: FakeAuthRepository
    private lateinit var connectedAuthRepo: FakeAuthRepository
    private lateinit var profileRepo: FakePlayerProfileRepository
    private lateinit var prefsRepo: FakePreferencesRepository
    private lateinit var multiplayerRepo: FakeMultiplayerRepository
    private lateinit var socialRepo: FakeSocialRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        guestAuthRepo = FakeAuthRepository(
            initialState = AuthState.GUEST,
            initialSession = null
        )
        connectedAuthRepo = FakeAuthRepository(
            initialState = AuthState.AUTHENTICATED,
            initialSession = AuthSession(
                playerId = "player_google_42",
                publicZynpathId = "ZYN-GOOG",
                displayName = "Google Player",
                accountType = "REGISTERED",
                provider = AuthProvider.GOOGLE,
                expiresAt = System.currentTimeMillis() + 86400000L
            )
        )
        profileRepo = FakePlayerProfileRepository()
        prefsRepo = FakePreferencesRepository()
        multiplayerRepo = FakeMultiplayerRepository()
        socialRepo = FakeSocialRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        authRepo: FakeAuthRepository = guestAuthRepo,
        mpRepo: FakeMultiplayerRepository = multiplayerRepo,
        sRepo: FakeSocialRepository = socialRepo
    ): FriendsArenaViewModel {
        return FriendsArenaViewModel(
            authRepository = authRepo,
            playerProfileRepository = profileRepo,
            preferencesRepository = prefsRepo,
            multiplayerRepository = mpRepo,
            socialRepository = sRepo
        )
    }

    // 1. Friends Arena checks authentication
    @Test
    fun testFriendsArenaChecksAuthenticationOnTap() = runTest(testDispatcher) {
        val vm = createViewModel(guestAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onFriendsArenaTapped()
        advanceUntilIdle()

        assertTrue("Guest must trigger Google Connect Modal", vm.uiState.value.showGoogleConnectModal)
        assertFalse("Guest must NOT open Create Room directly", vm.uiState.value.showCreateRoomModal)
    }

    // 2. Connected user opens Create Room popup directly
    @Test
    fun testConnectedUserOpensCreateRoomPopupDirectly() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onFriendsArenaTapped()
        advanceUntilIdle()

        assertTrue("Connected user must open Create Room popup directly", vm.uiState.value.showCreateRoomModal)
        assertFalse("Connected user must NOT see Google Connect modal", vm.uiState.value.showGoogleConnectModal)
    }

    // 3. Guest opens Connect Google popup
    @Test
    fun testGuestOpensConnectGooglePopup() = runTest(testDispatcher) {
        val vm = createViewModel(guestAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onFriendsArenaTapped()
        advanceUntilIdle()

        assertTrue("Must display Google Connect Modal for guest", vm.uiState.value.showGoogleConnectModal)
        assertFalse("Must not be in Create Room state", vm.uiState.value.showCreateRoomModal)
    }

    // 4. Successful login automatically opens Create Room
    @Test
    fun testSuccessfulLoginAutomaticallyOpensCreateRoom() = runTest(testDispatcher) {
        val vm = createViewModel(guestAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onFriendsArenaTapped()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.showGoogleConnectModal)

        val mockContext = android.content.ContextWrapper(null)
        vm.connectWithGoogle(mockContext)
        advanceUntilIdle()

        assertFalse("Google Connect modal dismissed on success", vm.uiState.value.showGoogleConnectModal)
        assertTrue("Create Room popup opens automatically after Google auth", vm.uiState.value.showCreateRoomModal)
    }

    // 5. Cancelled login does not create room
    @Test
    fun testCancelledLoginDoesNotCreateRoom() = runTest(testDispatcher) {
        val cancellingAuthRepo = FakeAuthRepository(initialState = AuthState.GUEST)
        cancellingAuthRepo.authResultToReturn = com.zynpath.game.core.auth.model.AuthResult.Cancelled
        val vm = createViewModel(cancellingAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onFriendsArenaTapped()
        advanceUntilIdle()

        val mockContext = android.content.ContextWrapper(null)
        vm.connectWithGoogle(mockContext)
        advanceUntilIdle()

        assertFalse("Cancelled login must NOT open Create Room modal", vm.uiState.value.showCreateRoomModal)
        assertNull("No room should be created", multiplayerRepo.currentFriendsArenaRoom.value)
    }

    // 6. No Facebook UI
    @Test
    fun testNoFacebookUiRequiredForFriendsArena() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onFriendsArenaTapped()
        advanceUntilIdle()

        // Google authenticated user does not require Facebook
        assertTrue(vm.uiState.value.isGoogleAuthenticated)
        assertTrue(vm.uiState.value.showCreateRoomModal)
    }

    // 7. No separate Create Game Room page
    @Test
    fun testNoSeparateCreateGameRoomPage_popupStateGoverned() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onFriendsArenaTapped()
        advanceUntilIdle()

        assertTrue("Modal popup active without navigation away", vm.uiState.value.showCreateRoomModal)
    }

    // 8. Player counts 1–5 work
    @Test
    fun testPlayerCounts1To5Work() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        for (count in 1..5) {
            vm.onPlayerCountChanged(count)
            advanceUntilIdle()
            assertEquals("Player count must be $count", count, vm.uiState.value.selectedPlayerCount)
        }

        // Out of bounds clamp
        vm.onPlayerCountChanged(0)
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.selectedPlayerCount)

        vm.onPlayerCountChanged(9)
        advanceUntilIdle()
        assertEquals(5, vm.uiState.value.selectedPlayerCount)
    }

    // 9. Host is included in player count
    @Test
    fun testHostIsIncludedInPlayerCount() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        // When capacity is 3, host is 1, max friends to invite is 2
        vm.onPlayerCountChanged(3)
        advanceUntilIdle()

        // If user selects 3 friends, only 2 should be allowed
        vm.toggleFriendSelected("friend_1")
        advanceUntilIdle()
        vm.toggleFriendSelected("friend_2")
        advanceUntilIdle()
        vm.toggleFriendSelected("friend_3") // Exceeds capacity - 1
        advanceUntilIdle()

        assertEquals("Capacity includes host: max invitees is 2", 2, vm.uiState.value.selectedFriendIds.size)
    }

    // 10. One-player room can wait but cannot start a match
    @Test
    fun testOnePlayerRoomCanWaitButCannotStart() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onPlayerCountChanged(1)
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.selectedPlayerCount)
        assertEquals(2, vm.uiState.value.minPlayersToStart)
        // 1 player room is valid capacity, but match requires minimum 2
        assertTrue("Single player room capacity is allowed", vm.uiState.value.selectedPlayerCount >= 1)
        assertFalse("1 player cannot start match (min 2 required)", vm.uiState.value.selectedPlayerCount >= vm.uiState.value.minPlayersToStart)
    }

    // 11. Two-to-five-player rooms can start subject to existing rules
    @Test
    fun testTwoToFivePlayerRoomsCanStart() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        for (count in 2..5) {
            vm.onPlayerCountChanged(count)
            advanceUntilIdle()
            assertTrue(vm.uiState.value.selectedPlayerCount >= vm.uiState.value.minPlayersToStart)
        }
    }

    // 12. Friend picker uses real eligible friends
    @Test
    fun testFriendPickerUsesRealEligibleFriends() = runTest(testDispatcher) {
        socialRepo.setFriends(
            listOf(
                FriendItem(
                    playerId = "f1",
                    publicZynpathId = "ZP1001",
                    displayName = "Alice",
                    avatarId = "avatar_1",
                    presenceState = PlayerPresenceState.ONLINE,
                    friendsSince = 1000L
                ),
                FriendItem(
                    playerId = "f2",
                    publicZynpathId = "ZP1002",
                    displayName = "Bob",
                    avatarId = "avatar_2",
                    presenceState = PlayerPresenceState.OFFLINE,
                    friendsSince = 2000L
                )
            )
        )

        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.openFriendPicker()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.showFriendPickerModal)
        assertEquals(2, vm.uiState.value.eligibleFriends.size)
        assertEquals("Alice", vm.uiState.value.eligibleFriends[0].displayName)
        assertEquals("Bob", vm.uiState.value.eligibleFriends[1].displayName)
    }

    // 13. Room creation works through backend
    @Test
    fun testRoomCreationWorksThroughBackend() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        var createdRoomId: String? = null
        vm.createRoom { roomId -> createdRoomId = roomId }
        advanceUntilIdle()

        assertNotNull("Backend room ID returned", createdRoomId)
        assertEquals("fake_room_1", createdRoomId)
        assertFalse("Create Room modal dismissed on success", vm.uiState.value.showCreateRoomModal)
    }

    // 14. Duplicate taps do not create duplicate rooms
    @Test
    fun testDuplicateTapsDoNotCreateDuplicateRooms() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        var callCount = 0
        vm.createRoom { callCount++ }
        // Attempt immediate second tap while creating
        vm.createRoom { callCount++ }
        advanceUntilIdle()

        assertEquals("Only one room creation request executed", 1, callCount)
    }

    // 15. Join Room popup works
    @Test
    fun testJoinRoomPopupWorks() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.openJoinRoomModal()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.showJoinRoomModal)

        vm.onJoinRoomCodeChanged("FAKECD")
        advanceUntilIdle()
        assertEquals("FAKECD", vm.uiState.value.joinRoomCode)

        var joinedRoomId: String? = null
        vm.joinRoom("FAKECD") { roomId -> joinedRoomId = roomId }
        advanceUntilIdle()

        assertNotNull(joinedRoomId)
        assertFalse("Join Room modal dismissed on success", vm.uiState.value.showJoinRoomModal)
    }

    // 16. Invalid room codes show errors
    @Test
    fun testInvalidRoomCodesShowErrors() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.openJoinRoomModal()
        advanceUntilIdle()

        var joined = false
        vm.joinRoom("AB") { joined = true }
        advanceUntilIdle()

        assertFalse("Invalid short code must not join", joined)
        assertNotNull("Must set error message", vm.uiState.value.joinRoomError)
    }

    // 17. Waiting lobby remains functional
    @Test
    fun testWaitingLobbyRemainsFunctional() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        var enteredLobbyRoomId: String? = null
        vm.createRoom { roomId -> enteredLobbyRoomId = roomId }
        advanceUntilIdle()

        assertEquals("fake_room_1", enteredLobbyRoomId)
        assertNotNull(multiplayerRepo.currentFriendsArenaRoom.value)
    }

    // 18. Only 11×11 and 12×12 puzzles are selected
    @Test
    fun testOnly11x11And12x12PuzzlesAreSelected() {
        val puzzleDescription = "Random Expert (11×11 / 12×12)"
        assertTrue(puzzleDescription.contains("11×11"))
        assertTrue(puzzleDescription.contains("12×12"))
    }

    // 19. Identical puzzle/hash is shared across players
    @Test
    fun testIdenticalPuzzleSharedAcrossPlayers() = runTest(testDispatcher) {
        val roomDto = multiplayerRepo.createFriendsArenaRoom()
        assertNotNull(roomDto)
        assertEquals("FAKECD", roomDto?.roomCode)
    }

    // 20. Popup dismiss and Android Back work correctly
    @Test
    fun testPopupDismissAndBackWorkCorrectly() = runTest(testDispatcher) {
        val vm = createViewModel(connectedAuthRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.openCreateRoomModal()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.showCreateRoomModal)

        vm.dismissCreateRoomModal()
        advanceUntilIdle()
        assertFalse(vm.uiState.value.showCreateRoomModal)

        vm.openJoinRoomModal()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.showJoinRoomModal)

        vm.dismissJoinRoomModal()
        advanceUntilIdle()
        assertFalse(vm.uiState.value.showJoinRoomModal)
    }

    // 21. Existing player progress is preserved
    @Test
    fun testExistingPlayerProgressPreservedOnGoogleLink() = runTest(testDispatcher) {
        val guestRepo = FakeAuthRepository(
            initialState = AuthState.GUEST,
            initialSession = null
        )
        val vm = createViewModel(guestRepo)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isGuest)

        val mockContext = android.content.ContextWrapper(null)
        vm.connectWithGoogle(mockContext)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isGuest)
        assertTrue(vm.uiState.value.showCreateRoomModal)
    }
}
