package com.zynpath.game.core.auth.repository

import android.content.Context
import com.zynpath.game.core.auth.api.AuthApiService
import com.zynpath.game.core.auth.api.AuthResponseDto
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.provider.FacebookAuthClient
import com.zynpath.game.core.auth.provider.FacebookCredentialResult
import com.zynpath.game.core.auth.provider.GoogleAuthClient
import com.zynpath.game.core.auth.provider.GoogleCredentialResult
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.network.NetworkResult
import com.zynpath.game.core.player.AccountType
import com.zynpath.game.core.player.PlayerProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val googleAuthClient: GoogleAuthClient,
    private val facebookAuthClient: FacebookAuthClient,
    private val authApiService: AuthApiService,
    private val secureTokenStorage: SecureTokenStorage,
    private val profileRepository: PlayerProfileRepository,
    private val entitlementRepository: javax.inject.Provider<com.zynpath.game.core.premium.SubscriptionEntitlementRepository>,
    private val notificationRepository: javax.inject.Provider<com.zynpath.game.core.notification.repository.NotificationRepository>,
    private val syncCoordinatorProvider: javax.inject.Provider<com.zynpath.game.core.sync.coordinator.SyncCoordinator>
) : AuthRepository {

    private val refreshMutex = Mutex()
    private val _authState = MutableStateFlow(AuthState.GUEST)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    override val currentSession: StateFlow<AuthSession?> = _currentSession.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            restoreSession()
        }
    }

    override fun isProviderConfigured(provider: AuthProvider): Boolean {
        return when (provider) {
            AuthProvider.GOOGLE -> googleAuthClient.isConfigured()
            AuthProvider.FACEBOOK -> facebookAuthClient.isConfigured()
        }
    }

    override suspend fun signInWithGoogle(context: Context): AuthResult {
        _authState.value = AuthState.SIGNING_IN
        if (!googleAuthClient.isConfigured()) {
            _authState.value = AuthState.SIGN_IN_FAILED
            return AuthResult.ProviderNotConfigured(
                AuthProvider.GOOGLE,
                "Google Sign-In is not configured. Define default_web_client_id or GOOGLE_CLIENT_ID."
            )
        }

        return when (val credResult = googleAuthClient.getCredential(context)) {
            is GoogleCredentialResult.Success -> {
                exchangeProviderCredential(AuthProvider.GOOGLE, credResult.idToken)
            }
            is GoogleCredentialResult.Cancelled -> {
                _authState.value = if (_currentSession.value != null) AuthState.AUTHENTICATED else AuthState.GUEST
                AuthResult.Cancelled
            }
            is GoogleCredentialResult.NotConfigured -> {
                _authState.value = AuthState.SIGN_IN_FAILED
                AuthResult.ProviderNotConfigured(AuthProvider.GOOGLE, credResult.message)
            }
            is GoogleCredentialResult.Failure -> {
                _authState.value = AuthState.SIGN_IN_FAILED
                AuthResult.Error("PROVIDER_ERROR", credResult.message)
            }
        }
    }

    override suspend fun signInWithFacebook(context: Context): AuthResult {
        _authState.value = AuthState.SIGNING_IN
        if (!facebookAuthClient.isConfigured()) {
            _authState.value = AuthState.SIGN_IN_FAILED
            return AuthResult.ProviderNotConfigured(
                AuthProvider.FACEBOOK,
                "Facebook Login is not configured. Define facebook_app_id or FACEBOOK_APP_ID."
            )
        }

        return when (val credResult = facebookAuthClient.getCredential(context)) {
            is FacebookCredentialResult.Success -> {
                exchangeProviderCredential(AuthProvider.FACEBOOK, credResult.accessToken)
            }
            is FacebookCredentialResult.Cancelled -> {
                _authState.value = if (_currentSession.value != null) AuthState.AUTHENTICATED else AuthState.GUEST
                AuthResult.Cancelled
            }
            is FacebookCredentialResult.NotConfigured -> {
                _authState.value = AuthState.SIGN_IN_FAILED
                AuthResult.ProviderNotConfigured(AuthProvider.FACEBOOK, credResult.message)
            }
            is FacebookCredentialResult.Failure -> {
                _authState.value = AuthState.SIGN_IN_FAILED
                AuthResult.Error("PROVIDER_ERROR", credResult.message)
            }
        }
    }

    override suspend fun linkGuestWithGoogle(context: Context): AuthResult {
        _authState.value = AuthState.LINKING
        if (!googleAuthClient.isConfigured()) {
            _authState.value = AuthState.LINK_FAILED
            return AuthResult.ProviderNotConfigured(
                AuthProvider.GOOGLE,
                "Google Sign-In is not configured. Define default_web_client_id or GOOGLE_CLIENT_ID."
            )
        }

        return when (val credResult = googleAuthClient.getCredential(context)) {
            is GoogleCredentialResult.Success -> {
                executeGuestLinking(AuthProvider.GOOGLE, credResult.idToken)
            }
            is GoogleCredentialResult.Cancelled -> {
                _authState.value = AuthState.GUEST
                AuthResult.Cancelled
            }
            is GoogleCredentialResult.NotConfigured -> {
                _authState.value = AuthState.LINK_FAILED
                AuthResult.ProviderNotConfigured(AuthProvider.GOOGLE, credResult.message)
            }
            is GoogleCredentialResult.Failure -> {
                _authState.value = AuthState.LINK_FAILED
                AuthResult.Error("PROVIDER_ERROR", credResult.message)
            }
        }
    }

    override suspend fun linkGuestWithFacebook(context: Context): AuthResult {
        _authState.value = AuthState.LINKING
        if (!facebookAuthClient.isConfigured()) {
            _authState.value = AuthState.LINK_FAILED
            return AuthResult.ProviderNotConfigured(
                AuthProvider.FACEBOOK,
                "Facebook Login is not configured. Define facebook_app_id or FACEBOOK_APP_ID."
            )
        }

        return when (val credResult = facebookAuthClient.getCredential(context)) {
            is FacebookCredentialResult.Success -> {
                executeGuestLinking(AuthProvider.FACEBOOK, credResult.accessToken)
            }
            is FacebookCredentialResult.Cancelled -> {
                _authState.value = AuthState.GUEST
                AuthResult.Cancelled
            }
            is FacebookCredentialResult.NotConfigured -> {
                _authState.value = AuthState.LINK_FAILED
                AuthResult.ProviderNotConfigured(AuthProvider.FACEBOOK, credResult.message)
            }
            is FacebookCredentialResult.Failure -> {
                _authState.value = AuthState.LINK_FAILED
                AuthResult.Error("PROVIDER_ERROR", credResult.message)
            }
        }
    }

    private suspend fun exchangeProviderCredential(
        provider: AuthProvider,
        providerToken: String
    ): AuthResult {
        val currentProfile = profileRepository.getProfile()
        val networkResult = authApiService.exchangeToken(
            provider = provider,
            providerToken = providerToken,
            guestUuid = currentProfile.playerId
        )

        return handleAuthResponse(provider, networkResult, isLinking = false)
    }

    private suspend fun executeGuestLinking(
        provider: AuthProvider,
        providerToken: String
    ): AuthResult {
        val currentProfile = profileRepository.getProfile()
        val networkResult = authApiService.linkAccount(
            provider = provider,
            providerToken = providerToken,
            guestUuid = currentProfile.playerId,
            displayName = currentProfile.displayName
        )

        return handleAuthResponse(
            provider = provider,
            networkResult = networkResult,
            isLinking = true,
            guestPlayerId = currentProfile.playerId
        )
    }

    private suspend fun handleAuthResponse(
        provider: AuthProvider,
        networkResult: NetworkResult<AuthResponseDto>,
        isLinking: Boolean,
        guestPlayerId: String? = null
    ): AuthResult {
        return when (networkResult) {
            is NetworkResult.Success -> {
                val dto = networkResult.data
                // Securely persist session credentials
                secureTokenStorage.saveSession(
                    sessionToken = dto.sessionToken,
                    playerId = dto.playerId,
                    publicZynpathId = dto.publicZynpathId,
                    displayName = dto.displayName,
                    accountType = dto.accountType,
                    provider = provider.name,
                    expiresAt = dto.expiresAt
                )

                val session = AuthSession(
                    playerId = dto.playerId,
                    publicZynpathId = dto.publicZynpathId,
                    displayName = dto.displayName,
                    accountType = dto.accountType,
                    provider = provider,
                    expiresAt = dto.expiresAt
                )

                _currentSession.value = session
                _authState.value = AuthState.AUTHENTICATED

                // Update player profile linking status while preserving all local Solo & Daily progress
                profileRepository.updateAccountLinking(
                    accountType = AccountType.LINKED,
                    publicId = dto.publicZynpathId
                )

                // Enforce account entitlement isolation (Prompt 26 Section 38)
                try {
                    entitlementRepository.get().onAccountSwitched(dto.playerId)
                } catch (_: Exception) {}

                // Refresh notifications for newly authenticated account
                try {
                    notificationRepository.get().refreshNotifications()
                } catch (_: Exception) {}

                // Rebind or re-route offline sync queue for authenticated account (Prompt 35 Section 36 & 40)
                try {
                    if (isLinking && guestPlayerId != null) {
                        syncCoordinatorProvider.get().onGuestLinked(
                            guestPlayerId = guestPlayerId,
                            newPlayerId = dto.playerId
                        )
                    } else {
                        syncCoordinatorProvider.get().onAccountSwitched(dto.playerId)
                    }
                } catch (_: Exception) {}

                AuthResult.Success(session)
            }
            is NetworkResult.Error -> {
                if (networkResult.code == 409 || networkResult.message.contains("ACCOUNT_LINK_CONFLICT", ignoreCase = true)) {
                    _authState.value = if (isLinking) AuthState.LINK_FAILED else AuthState.SIGN_IN_FAILED
                    AuthResult.Conflict(provider, networkResult.message)
                } else {
                    _authState.value = if (isLinking) AuthState.LINK_FAILED else AuthState.SIGN_IN_FAILED
                    AuthResult.Error("HTTP_${networkResult.code}", networkResult.message)
                }
            }
            is NetworkResult.Exception -> {
                _authState.value = if (isLinking) AuthState.LINK_FAILED else AuthState.SIGN_IN_FAILED
                AuthResult.Error("NETWORK_ERROR", networkResult.throwable.message ?: "Network error")
            }
        }
    }

    override suspend fun restoreSession(): Boolean {
        val meta = secureTokenStorage.getSessionMetadata()
        val token = secureTokenStorage.getSessionToken()

        if (meta == null || token == null) {
            _authState.value = AuthState.GUEST
            return false
        }

        // If session expired or expires within 1 hour, attempt background refresh
        val now = System.currentTimeMillis()
        if (now > meta.expiresAt || (meta.expiresAt - now) < 3600000L) {
            val refreshed = refreshSession()
            if (refreshed) {
                return true
            }
            if (now > meta.expiresAt) {
                // Expired and could not be refreshed
                secureTokenStorage.clearSession()
                _currentSession.value = null
                _authState.value = AuthState.SESSION_EXPIRED
                return false
            }
        }

        val provider = try {
            AuthProvider.valueOf(meta.provider)
        } catch (e: Exception) {
            AuthProvider.GOOGLE
        }

        val session = AuthSession(
            playerId = meta.playerId,
            publicZynpathId = meta.publicZynpathId,
            displayName = meta.displayName,
            accountType = meta.accountType,
            provider = provider,
            expiresAt = meta.expiresAt
        )

        _currentSession.value = session
        _authState.value = AuthState.AUTHENTICATED

        // Refresh entitlement for restored session
        try {
            entitlementRepository.get().refreshEntitlement()
        } catch (_: Exception) {}

        // Refresh notifications for restored session
        try {
            notificationRepository.get().refreshNotifications()
        } catch (_: Exception) {}

        // Bind active sync owner to restored session (Prompt 35 Section 40)
        try {
            syncCoordinatorProvider.get().onAccountSwitched(session.playerId)
        } catch (_: Exception) {}

        return true
    }

    override suspend fun refreshSession(): Boolean = refreshMutex.withLock {
        val token = secureTokenStorage.getSessionToken() ?: return@withLock false
        val meta = secureTokenStorage.getSessionMetadata() ?: return@withLock false

        when (val result = authApiService.refreshSession(token)) {
            is NetworkResult.Success -> {
                val dto = result.data
                val provider = try {
                    AuthProvider.valueOf(meta.provider)
                } catch (e: Exception) {
                    AuthProvider.GOOGLE
                }

                secureTokenStorage.saveSession(
                    sessionToken = dto.sessionToken,
                    playerId = dto.playerId,
                    publicZynpathId = dto.publicZynpathId,
                    displayName = dto.displayName,
                    accountType = dto.accountType,
                    provider = provider.name,
                    expiresAt = dto.expiresAt
                )

                val session = AuthSession(
                    playerId = dto.playerId,
                    publicZynpathId = dto.publicZynpathId,
                    displayName = dto.displayName,
                    accountType = dto.accountType,
                    provider = provider,
                    expiresAt = dto.expiresAt
                )
                _currentSession.value = session
                _authState.value = AuthState.AUTHENTICATED
                true
            }
            is NetworkResult.Error -> {
                if (result.code == 401 || result.code == 403) {
                    // Session permanently invalidated or revoked
                    secureTokenStorage.clearSession()
                    _currentSession.value = null
                    _authState.value = AuthState.SESSION_EXPIRED
                    try {
                        syncCoordinatorProvider.get().onSignOut()
                    } catch (_: Exception) {}
                }
                false
            }
            is NetworkResult.Exception -> {
                // Transient network failure during refresh: preserve existing session if still within validity
                false
            }
        }
    }

    override suspend fun signOut() {
        val token = secureTokenStorage.getSessionToken()
        if (token != null) {
            try {
                authApiService.signOut(token)
            } catch (_: Exception) {}
        }

        secureTokenStorage.clearSession()
        _currentSession.value = null
        _authState.value = AuthState.GUEST

        // Return local account status to Guest while preserving all gameplay data
        profileRepository.updateAccountLinking(
            accountType = AccountType.GUEST,
            publicId = null
        )

        // Clear entitlement on sign-out to prevent leaking into guest session
        try {
            entitlementRepository.get().clearEntitlement()
        } catch (_: Exception) {}

        // Isolate account notifications and unregister push token
        try {
            notificationRepository.get().onSignOut()
        } catch (_: Exception) {}

        // Isolate sync queue and switch back to guest mode (Prompt 35 Section 41)
        try {
            syncCoordinatorProvider.get().onSignOut()
        } catch (_: Exception) {}
    }
}
