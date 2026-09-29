package com.zynpath.game.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.account.model.AccountDeletionResult
import com.zynpath.game.core.account.model.AccountSettings
import com.zynpath.game.core.account.model.BlockedPlayerItem
import com.zynpath.game.core.account.model.DataExportResult
import com.zynpath.game.core.account.model.ProfileVisibility
import com.zynpath.game.core.account.repository.AccountRepository
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthResult
import com.zynpath.game.core.auth.model.AuthSession
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.datastore.UserPreferences
import com.zynpath.game.core.notification.reminder.DailyReminderScheduler
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import com.zynpath.game.core.premium.model.PremiumEntitlement
import com.zynpath.game.core.audio.NoOpAudioManager
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.haptics.NoOpHapticManager
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val dailyReminderScheduler: DailyReminderScheduler,
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    private val entitlementRepository: SubscriptionEntitlementRepository,
    private val audioManager: ZynpathAudioManager = NoOpAudioManager(),
    private val hapticManager: ZynpathHapticManager = NoOpHapticManager()
) : ViewModel() {

    // Convenience constructor for unit testing
    constructor(preferencesRepository: PreferencesRepository) : this(
        preferencesRepository = preferencesRepository,
        dailyReminderScheduler = object : DailyReminderScheduler {
            override suspend fun scheduleDailyReminder(hourOfDay: Int, minute: Int) {}
            override suspend fun cancelDailyReminder() {}
            override suspend fun rescheduleIfEnabled() {}
        },
        accountRepository = createNoOpAccountRepository(),
        authRepository = createNoOpAuthRepository(),
        entitlementRepository = createNoOpEntitlementRepository(),
        audioManager = NoOpAudioManager(),
        hapticManager = NoOpHapticManager()
    )

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val accountSettings: StateFlow<AccountSettings?> = accountRepository.accountSettings
    val linkedProviders: StateFlow<List<String>> = accountRepository.linkedProviders
    val blockedPlayers: StateFlow<List<BlockedPlayerItem>> = accountRepository.blockedPlayers
    val authState: StateFlow<AuthState> = authRepository.authState
    val currentSession: StateFlow<AuthSession?> = authRepository.currentSession
    val isSyncing: StateFlow<Boolean> = accountRepository.isSyncing

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _lastExportJson = MutableStateFlow<String?>(null)
    val lastExportJson: StateFlow<String?> = _lastExportJson.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.refreshAccountSettings()
        }
    }

    // --- Audio Settings ---

    fun toggleSfx() {
        viewModelScope.launch {
            val current = userPreferences.value.isSfxEnabled
            val newEnabled = !current
            preferencesRepository.setSfxEnabled(newEnabled)
            if (newEnabled) {
                audioManager.playEvent(ZynpathAudioEvent.BUTTON_TAP)
            }
            hapticManager.triggerHaptic(ZynpathHapticEvent.BUTTON_CONFIRM)
        }
    }

    fun toggleMusic() {
        viewModelScope.launch {
            val current = userPreferences.value.isMusicEnabled
            preferencesRepository.setMusicEnabled(!current)
            audioManager.playEvent(ZynpathAudioEvent.BUTTON_TAP)
            hapticManager.triggerHaptic(ZynpathHapticEvent.BUTTON_CONFIRM)
        }
    }

    fun toggleHaptics() {
        viewModelScope.launch {
            val current = userPreferences.value.isHapticsEnabled
            val newEnabled = !current
            preferencesRepository.setHapticsEnabled(newEnabled)
            audioManager.playEvent(ZynpathAudioEvent.BUTTON_TAP)
            if (newEnabled) {
                hapticManager.triggerHaptic(ZynpathHapticEvent.BUTTON_CONFIRM)
            }
        }
    }

    // --- Tactile, Appearance & Accessibility ---

    fun toggleReducedMotion() {
        viewModelScope.launch {
            val current = userPreferences.value.isReducedMotion
            preferencesRepository.setReducedMotion(!current)
        }
    }

    fun toggleHighContrast() {
        viewModelScope.launch {
            val current = userPreferences.value.isHighContrast
            accountRepository.setHighContrast(!current)
        }
    }

    fun setTouchSensitivity(sensitivity: Float) {
        viewModelScope.launch {
            accountRepository.setTouchSensitivity(sensitivity)
        }
    }

    fun toggleTapInputMode() {
        viewModelScope.launch {
            val current = userPreferences.value.isTapInputMode
            preferencesRepository.setTapInputMode(!current)
        }
    }

    fun setThemePreference(theme: String) {
        viewModelScope.launch {
            preferencesRepository.setThemePreference(theme)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesRepository.setSelectedLanguage(language)
        }
    }

    fun setTutorialCompleted(completed: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setTutorialCompleted(completed)
        }
    }

    // --- Notification Preferences ---

    fun toggleFriendAlerts() {
        viewModelScope.launch {
            val current = userPreferences.value.isFriendAlertsEnabled
            preferencesRepository.setFriendAlertsEnabled(!current)
        }
    }

    fun toggleMultiplayerAlerts() {
        viewModelScope.launch {
            val current = userPreferences.value.isMultiplayerAlertsEnabled
            preferencesRepository.setMultiplayerAlertsEnabled(!current)
        }
    }

    fun toggleDailyReminder() {
        viewModelScope.launch {
            val current = userPreferences.value.isDailyReminderEnabled
            val newEnabled = !current
            preferencesRepository.setDailyReminderEnabled(newEnabled)
            if (newEnabled) {
                dailyReminderScheduler.scheduleDailyReminder(
                    userPreferences.value.dailyReminderHour,
                    userPreferences.value.dailyReminderMinute
                )
            } else {
                dailyReminderScheduler.cancelDailyReminder()
            }
        }
    }

    fun setDailyReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.setDailyReminderTime(hour, minute)
            if (userPreferences.value.isDailyReminderEnabled) {
                dailyReminderScheduler.scheduleDailyReminder(hour, minute)
            }
        }
    }

    // --- Privacy Controls ---

    fun setProfileVisibility(visibility: ProfileVisibility) {
        viewModelScope.launch {
            val prefs = userPreferences.value
            val success = accountRepository.updatePrivacy(
                visibility = visibility,
                allowZynpathIdSearch = prefs.allowZynpathIdSearch,
                allowFriendRequests = prefs.allowFriendRequests
            )
            if (success) {
                _uiMessage.value = "Profile visibility updated to ${visibility.name}"
            } else {
                _uiMessage.value = "Failed to sync visibility with server (saved locally)"
            }
        }
    }

    fun toggleAllowZynpathIdSearch() {
        viewModelScope.launch {
            val prefs = userPreferences.value
            val newAllow = !prefs.allowZynpathIdSearch
            val success = accountRepository.updatePrivacy(
                visibility = ProfileVisibility.fromString(prefs.profileVisibility),
                allowZynpathIdSearch = newAllow,
                allowFriendRequests = prefs.allowFriendRequests
            )
            if (!success) {
                _uiMessage.value = "Failed to sync discovery with server (saved locally)"
            }
        }
    }

    fun toggleAllowFriendRequests() {
        viewModelScope.launch {
            val prefs = userPreferences.value
            val newAllow = !prefs.allowFriendRequests
            val success = accountRepository.updatePrivacy(
                visibility = ProfileVisibility.fromString(prefs.profileVisibility),
                allowZynpathIdSearch = prefs.allowZynpathIdSearch,
                allowFriendRequests = newAllow
            )
            if (!success) {
                _uiMessage.value = "Failed to sync friend requests with server (saved locally)"
            }
        }
    }

    fun unblockPlayer(targetPlayerId: String) {
        viewModelScope.launch {
            val ok = accountRepository.unblockPlayer(targetPlayerId)
            if (ok) {
                _uiMessage.value = "Player unblocked"
            } else {
                _uiMessage.value = "Failed to unblock player"
            }
        }
    }

    // --- Account Management ---

    fun refreshAccountSettings() {
        viewModelScope.launch {
            accountRepository.refreshAccountSettings()
        }
    }

    fun unlinkProvider(provider: String) {
        viewModelScope.launch {
            val result = accountRepository.unlinkProvider(provider)
            result.onSuccess {
                _uiMessage.value = "$provider unlinked successfully"
            }.onFailure { error ->
                _uiMessage.value = error.message ?: "Failed to unlink $provider"
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            accountRepository.signOut()
            _uiMessage.value = "Signed out. Returned to Guest mode."
        }
    }

    fun requestDataExport() {
        viewModelScope.launch {
            val result = accountRepository.requestDataExport()
            result.onSuccess { export ->
                _lastExportJson.value = export.jsonContent
                _uiMessage.value = "Data export generated successfully (Schema v${export.schemaVersion})"
            }.onFailure { error ->
                _uiMessage.value = error.message ?: "Data export unavailable"
            }
        }
    }

    fun clearExportJson() {
        _lastExportJson.value = null
    }

    fun deleteAccount(onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            val result = accountRepository.deleteAccount()
            result.onSuccess { res ->
                _uiMessage.value = "Account deleted. Notice: ${res.googlePlaySubscriptionNotice}"
                onDeleted()
            }.onFailure { error ->
                _uiMessage.value = error.message ?: "Failed to delete account"
            }
        }
    }

    // --- Data & Storage Hygiene ---

    fun clearCache() {
        viewModelScope.launch {
            val bytesFreed = accountRepository.clearDisposableCache()
            val kbFreed = bytesFreed / 1024
            _uiMessage.value = if (kbFreed > 0) "Cache cleared: $kbFreed KB freed" else "Cache is already empty"
        }
    }

    fun clearGuestProgress(onCleared: () -> Unit = {}) {
        viewModelScope.launch {
            val ok = accountRepository.clearGuestProgress()
            if (ok) {
                _uiMessage.value = "Local guest progress cleared"
                onCleared()
            } else {
                _uiMessage.value = "Failed to clear local guest progress"
            }
        }
    }

    // --- Premium Management ---

    fun restorePurchases() {
        viewModelScope.launch {
            val res = entitlementRepository.refreshEntitlement()
            res.onSuccess { ent ->
                if (ent.isPremiumActive) {
                    _uiMessage.value = "Premium entitlement active"
                } else {
                    _uiMessage.value = "No active subscription found for this account"
                }
            }.onFailure { error ->
                _uiMessage.value = error.message ?: "Failed to check purchases"
            }
        }
    }

    fun dismissMessage() {
        _uiMessage.value = null
    }
}

private fun createNoOpAccountRepository(): AccountRepository {
    return object : AccountRepository {
        override val accountSettings = MutableStateFlow<AccountSettings?>(null)
        override val linkedProviders = MutableStateFlow<List<String>>(emptyList())
        override val blockedPlayers = MutableStateFlow<List<BlockedPlayerItem>>(emptyList())
        override val isSyncing = MutableStateFlow(false)
        override val lastError = MutableStateFlow<String?>(null)

        override suspend fun refreshAccountSettings() = true
        override suspend fun updatePrivacy(visibility: ProfileVisibility, allowZynpathIdSearch: Boolean, allowFriendRequests: Boolean) = true
        override suspend fun setHighContrast(enabled: Boolean) {}
        override suspend fun setTouchSensitivity(sensitivity: Float) {}
        override suspend fun unlinkProvider(provider: String) = Result.success(emptyList<String>())
        override suspend fun requestDataExport() = Result.success(DataExportResult(1, System.currentTimeMillis(), "{}"))
        override suspend fun deleteAccount() = Result.success(AccountDeletionResult("SUCCESS", "", System.currentTimeMillis(), ""))
        override suspend fun unblockPlayer(targetPlayerId: String) = true
        override suspend fun clearDisposableCache() = 0L
        override suspend fun clearGuestProgress() = true
        override suspend fun signOut() {}
        override fun clearError() {}
    }
}

private fun createNoOpAuthRepository(): AuthRepository {
    return object : AuthRepository {
        override val authState = MutableStateFlow(AuthState.GUEST)
        override val currentSession = MutableStateFlow<AuthSession?>(null)
        override fun isProviderConfigured(provider: AuthProvider) = false
        override suspend fun signInWithGoogle(context: android.content.Context) = AuthResult.Cancelled
        override suspend fun signInWithFacebook(context: android.content.Context) = AuthResult.Cancelled
        override suspend fun linkGuestWithGoogle(context: android.content.Context) = AuthResult.Cancelled
        override suspend fun linkGuestWithFacebook(context: android.content.Context) = AuthResult.Cancelled
        override suspend fun restoreSession() = false
        override suspend fun refreshSession() = false
        override suspend fun signOut() {}
    }
}

private fun createNoOpEntitlementRepository(): SubscriptionEntitlementRepository {
    return object : SubscriptionEntitlementRepository {
        override val entitlement = MutableStateFlow(PremiumEntitlement.free())
        override suspend fun refreshEntitlement() = Result.success(entitlement.value)
        override suspend fun verifyPurchase(purchaseToken: String, productId: String, basePlanId: String) = Result.success(entitlement.value)
        override suspend fun restorePurchases(purchaseTokens: List<String>) = Result.success(entitlement.value)
        override suspend fun onAccountSwitched(newAccountId: String?) {}
        override suspend fun clearEntitlement() {}
    }
}
