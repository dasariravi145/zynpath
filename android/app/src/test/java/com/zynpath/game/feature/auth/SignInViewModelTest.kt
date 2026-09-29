package com.zynpath.game.feature.auth

import android.content.Context
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.fake.FakeAuthRepository
import com.zynpath.game.fake.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import android.content.ContextWrapper
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockContext = ContextWrapper(null)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_defaultsToGuest_andReflectsPreferences() = runTest {
        val authRepo = FakeAuthRepository(initialState = AuthState.GUEST)
        val prefRepo = FakePreferencesRepository(UserPreferences(isReducedMotion = true))

        val viewModel = SignInViewModel(authRepo, prefRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AuthState.GUEST, state.authState)
        assertTrue(state.isReducedMotion)
        assertTrue(state.isGoogleAvailable)
        assertTrue(state.isFacebookAvailable)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun signInWithGoogle_triggersAuthentication_andSucceeds() = runTest {
        val authRepo = FakeAuthRepository(initialState = AuthState.GUEST)
        val prefRepo = FakePreferencesRepository()

        val viewModel = SignInViewModel(authRepo, prefRepo)
        advanceUntilIdle()

        viewModel.signInWithGoogle(mockContext)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AuthState.AUTHENTICATED, state.authState)
        assertEquals("player_google_1", state.currentSession?.playerId)
        assertEquals(AuthProvider.GOOGLE, state.currentSession?.provider)
    }

    @Test
    fun signInWithFacebook_triggersAuthentication_andSucceeds() = runTest {
        val authRepo = FakeAuthRepository(initialState = AuthState.GUEST)
        val prefRepo = FakePreferencesRepository()

        val viewModel = SignInViewModel(authRepo, prefRepo)
        advanceUntilIdle()

        viewModel.signInWithFacebook(mockContext)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AuthState.AUTHENTICATED, state.authState)
        assertEquals("player_fb_1", state.currentSession?.playerId)
        assertEquals(AuthProvider.FACEBOOK, state.currentSession?.provider)
    }

    @Test
    fun dismissDialogsAndErrors_clearsTransientState() = runTest {
        val authRepo = FakeAuthRepository()
        val prefRepo = FakePreferencesRepository()

        val viewModel = SignInViewModel(authRepo, prefRepo)
        advanceUntilIdle()

        viewModel.clearError()
        viewModel.dismissConflictDialog()
        viewModel.dismissUnconfiguredNotice()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.errorMessage)
        assertNull(state.conflictDialogMessage)
        assertNull(state.unconfiguredProviderNotice)
    }

    @Test
    fun signOut_resetsAuthStateToGuest() = runTest {
        val authRepo = FakeAuthRepository()
        val prefRepo = FakePreferencesRepository()

        val viewModel = SignInViewModel(authRepo, prefRepo)
        advanceUntilIdle()

        // Sign in first
        viewModel.signInWithGoogle(mockContext)
        advanceUntilIdle()
        assertEquals(AuthState.AUTHENTICATED, viewModel.uiState.value.authState)

        // Sign out
        viewModel.signOut()
        advanceUntilIdle()
        assertEquals(AuthState.GUEST, viewModel.uiState.value.authState)
        assertNull(viewModel.uiState.value.currentSession)
    }
}
