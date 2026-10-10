package com.zynpath.game.feature.profile

import android.content.Context
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.cosmetics.model.CosmeticAccessDecision
import com.zynpath.game.core.cosmetics.model.CosmeticItem
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.cosmetics.repository.CosmeticsRepository
import com.zynpath.game.core.designsystem.theme.ClassicMidnightPalette
import com.zynpath.game.core.designsystem.theme.ZynpathColorPalette
import com.zynpath.game.core.player.AccountType
import com.zynpath.game.core.player.DisplayNameValidationResult
import com.zynpath.game.core.player.PlayerProfile
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakeMultiplayerRepository
import com.zynpath.game.fake.FakePlayerProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
import android.content.ContextWrapper

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeProfileRepo: FakePlayerProfileRepository
    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeMultiplayerRepo: FakeMultiplayerRepository
    private lateinit var fakeCosmeticsRepo: CosmeticsRepository
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockContext = ContextWrapper(null)

        fakeProfileRepo = FakePlayerProfileRepository(
            initialProfile = PlayerProfile(
                playerId = "player_456",
                displayName = "Pathfinder",
                avatarId = "avatar_compass",
                createdAt = 1000L,
                lastActiveAt = 2000L,
                accountType = AccountType.GUEST,
                publicZynpathId = "ZYN-GUEST"
            )
        )

        fakeAuthRepo = FakeAuthRepository(
            initialState = AuthState.GUEST
        )

        fakeMultiplayerRepo = FakeMultiplayerRepository()

        fakeCosmeticsRepo = object : CosmeticsRepository {
            override val equippedCosmeticsFlow: Flow<EquippedCosmetics> =
                MutableStateFlow(EquippedCosmetics(avatarFrameId = "frame_default_slate"))
            override val activePaletteFlow: Flow<ZynpathColorPalette> =
                MutableStateFlow(ClassicMidnightPalette)
            override val catalogWithDecisionsFlow: Flow<List<Pair<CosmeticItem, CosmeticAccessDecision>>> =
                MutableStateFlow(emptyList())

            override suspend fun getAccessDecision(item: CosmeticItem): CosmeticAccessDecision =
                CosmeticAccessDecision.AVAILABLE
            override suspend fun equipCosmetic(cosmeticId: String): Boolean = true
            override suspend fun resetToDefaults() {}
            override suspend fun syncWithBackend(): Result<EquippedCosmetics> =
                Result.success(EquippedCosmetics())
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ProfileViewModel {
        return ProfileViewModel(
            profileRepository = fakeProfileRepo,
            authRepository = fakeAuthRepo,
            multiplayerRepository = fakeMultiplayerRepo,
            cosmeticsRepository = fakeCosmeticsRepo
        )
    }

    @Test
    fun testInitialGuestProfileAndAuthoritativeStatisticsLoaded() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.profile)
        assertEquals("Pathfinder", state.profile?.displayName)
        assertEquals("avatar_compass", state.profile?.avatarId)
        assertTrue(state.profile?.isGuest == true)
        assertFalse(state.accountLinkingState.isLinked)

        // Statistics authoritative values
        assertEquals(5, state.statistics.completedSoloLevels)
        assertEquals(300, state.statistics.totalAvailableSoloLevels)
        assertEquals(15, state.statistics.totalStarsEarned)

        job.cancel()
    }

    @Test
    fun testDisplayNameEditingValidationAndPersistence() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.startEditingName()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isEditingName)
        assertEquals("Pathfinder", viewModel.uiState.value.nameInput)

        // Invalid empty name
        viewModel.onNameInputChanged("")
        viewModel.saveName()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isEditingName)
        assertTrue(viewModel.uiState.value.nameValidationResult is DisplayNameValidationResult.Empty)

        // Invalid short name
        viewModel.onNameInputChanged("A")
        viewModel.saveName()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isEditingName)
        assertTrue(viewModel.uiState.value.nameValidationResult is DisplayNameValidationResult.TooShort)

        // Valid name
        viewModel.onNameInputChanged("StarSeeker")
        viewModel.saveName()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isEditingName)
        assertEquals("StarSeeker", viewModel.uiState.value.profile?.displayName)
        assertEquals("Display name updated", viewModel.uiState.value.snackbarMessage)

        job.cancel()
    }

    @Test
    fun testCancelEditingNameRestoresState() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.startEditingName()
        viewModel.onNameInputChanged("Temporary")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isEditingName)

        viewModel.cancelEditingName()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isEditingName)
        assertEquals("Pathfinder", viewModel.uiState.value.profile?.displayName)

        job.cancel()
    }

    @Test
    fun testAvatarSelectionUpdatesAndClosesPicker() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.toggleAvatarPicker(true)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isAvatarPickerOpen)

        viewModel.selectAvatar("avatar_star")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isAvatarPickerOpen)
        assertEquals("avatar_star", viewModel.uiState.value.profile?.avatarId)
        assertEquals("Avatar updated", viewModel.uiState.value.snackbarMessage)

        job.cancel()
    }

    @Test
    fun testConnectWithGoogleLinksAccount() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.accountLinkingState.isLinked)

        var successCallbackCalled = false
        viewModel.connectWithGoogle(mockContext) {
            successCallbackCalled = true
        }
        advanceUntilIdle()

        assertTrue(successCallbackCalled)
        assertFalse(viewModel.uiState.value.isGoogleConnecting)
        assertNull(viewModel.uiState.value.googleConnectError)
        assertEquals(AuthState.AUTHENTICATED, fakeAuthRepo.authState.value)

        job.cancel()
    }

    @Test
    fun testSignOutRestoresGuest() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.connectWithGoogle(mockContext)
        advanceUntilIdle()

        viewModel.signOut()
        advanceUntilIdle()

        assertEquals(AuthState.GUEST, fakeAuthRepo.authState.value)
        assertEquals("Signed out. Restored to offline guest mode.", viewModel.uiState.value.snackbarMessage)

        job.cancel()
    }

    @Test
    fun testDismissConflictAndUnconfiguredDialogs() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val job = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.dismissConflictDialog()
        viewModel.dismissUnconfiguredNotice()
        viewModel.clearSnackbar()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.conflictDialogMessage)
        assertNull(viewModel.uiState.value.unconfiguredNotice)
        assertNull(viewModel.uiState.value.snackbarMessage)

        job.cancel()
    }
}
