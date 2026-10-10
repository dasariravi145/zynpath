package com.zynpath.game.feature.friends

import android.content.ContextWrapper
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakeSocialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FriendsUiAndFlowTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockContext = ContextWrapper(null)
    private lateinit var socialRepo: FakeSocialRepository
    private lateinit var authRepo: FakeAuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        socialRepo = FakeSocialRepository()
        authRepo = FakeAuthRepository(initialState = AuthState.GUEST)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGuestTapsFriends_triggersGoogleModal_andCanDismiss() = runTest(testDispatcher) {
        val viewModel = FriendsViewModel(socialRepo, authRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // 1. Initial State: Unauthenticated guest
        assertFalse("Player must not be authenticated", viewModel.uiState.value.isAuthenticated)
        assertTrue("Guest player should see Google connect modal on entry", viewModel.uiState.value.showGoogleConnectModal)

        // 2. Dismissing modal leaves player on Friends screen with guest notice banner
        viewModel.dismissGoogleConnectModal()
        advanceUntilIdle()

        assertFalse("Modal must be dismissed", viewModel.uiState.value.showGoogleConnectModal)
        assertFalse("Player remains unauthenticated", viewModel.uiState.value.isAuthenticated)

        // 3. Tapping Connect Google in banner re-opens the modal
        viewModel.openGoogleConnectModal()
        advanceUntilIdle()
        assertTrue("Modal must be displayed again", viewModel.uiState.value.showGoogleConnectModal)
    }

    @Test
    fun testGuestConnectsGoogle_updatesToAuthenticated_andClosesModal() = runTest(testDispatcher) {
        val viewModel = FriendsViewModel(socialRepo, authRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Perform Google connection
        viewModel.connectWithGoogle(mockContext)
        advanceUntilIdle()

        assertTrue("Player must be authenticated after Google connect", viewModel.uiState.value.isAuthenticated)
        assertFalse("Modal must close after successful connection", viewModel.uiState.value.showGoogleConnectModal)
        assertEquals("ZYN-GOOG", viewModel.uiState.value.myPublicId)
        assertEquals("Google Player", viewModel.uiState.value.myDisplayName)
        assertNull("No error message on success", viewModel.uiState.value.googleConnectError)
    }

    @Test
    fun testConnectedPlayerDoesNotSeeGoogleModal() = runTest(testDispatcher) {
        val connectedAuthRepo = FakeAuthRepository(
            initialState = AuthState.AUTHENTICATED,
            initialSession = AuthSession(
                playerId = "player_google_1",
                publicZynpathId = "ZYN-GOOG",
                displayName = "Google Player",
                accountType = "REGISTERED",
                provider = AuthProvider.GOOGLE,
                expiresAt = System.currentTimeMillis() + 86400000L
            )
        )
        val viewModel = FriendsViewModel(socialRepo, connectedAuthRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        assertTrue("Connected player is authenticated", viewModel.uiState.value.isAuthenticated)
        assertFalse("Connected player does NOT see Google modal", viewModel.uiState.value.showGoogleConnectModal)
    }

    @Test
    fun testEmptyFriendsList_showsEmptyStateNotFakeData() = runTest(testDispatcher) {
        val connectedAuthRepo = FakeAuthRepository(
            initialState = AuthState.AUTHENTICATED,
            initialSession = AuthSession(
                playerId = "player_google_1",
                publicZynpathId = "ZYN-GOOG",
                displayName = "Google Player",
                accountType = "REGISTERED",
                provider = AuthProvider.GOOGLE,
                expiresAt = System.currentTimeMillis() + 86400000L
            )
        )
        val viewModel = FriendsViewModel(socialRepo, connectedAuthRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        // Verify friend list contains only real friends (empty in this case, never fake names like Rahul/Priya)
        assertTrue("Friends list must be empty initially", viewModel.uiState.value.friends.isEmpty())

        // Add real friends
        val realFriends = listOf(
            FriendItem(
                playerId = "friend_real_1",
                publicZynpathId = "ZYN-FRND1",
                displayName = "Real Friend Alice",
                avatarId = "avatar_1",
                presenceState = PlayerPresenceState.ONLINE,
                friendsSince = System.currentTimeMillis()
            )
        )
        socialRepo.setFriends(realFriends)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.friends.size)
        assertEquals("Real Friend Alice", viewModel.uiState.value.friends.first().displayName)
    }
}
