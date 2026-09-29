package com.zynpath.game.core.account.repository

import android.content.Context
import com.zynpath.game.core.account.api.AccountApiService
import com.zynpath.game.core.account.model.AccountDeletionResult
import com.zynpath.game.core.account.model.AccountSettings
import com.zynpath.game.core.account.model.BlockedPlayerItem
import com.zynpath.game.core.account.model.DataExportResult
import com.zynpath.game.core.account.model.PlayerPrivacySettings
import com.zynpath.game.core.account.model.ProfileVisibility
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.database.ZynpathDatabase
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.network.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountApiService: AccountApiService,
    private val authRepository: AuthRepository,
    private val secureTokenStorage: SecureTokenStorage,
    private val preferencesRepository: PreferencesRepository,
    private val database: ZynpathDatabase,
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AccountRepository {

    private val scope = CoroutineScope(ioDispatcher)

    private val _accountSettings = MutableStateFlow<AccountSettings?>(null)
    override val accountSettings: StateFlow<AccountSettings?> = _accountSettings.asStateFlow()

    private val _linkedProviders = MutableStateFlow<List<String>>(emptyList())
    override val linkedProviders: StateFlow<List<String>> = _linkedProviders.asStateFlow()

    private val _blockedPlayers = MutableStateFlow<List<BlockedPlayerItem>>(emptyList())
    override val blockedPlayers: StateFlow<List<BlockedPlayerItem>> = _blockedPlayers.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    override val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    override val lastError: StateFlow<String?> = _lastError.asStateFlow()

    init {
        scope.launch {
            authRepository.currentSession.collect { session ->
                if (session != null) {
                    refreshAccountSettings()
                } else {
                    _accountSettings.value = null
                    _linkedProviders.value = emptyList()
                    _blockedPlayers.value = emptyList()
                }
            }
        }
    }

    override suspend fun refreshAccountSettings(): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken()
        if (token == null) {
            _accountSettings.value = null
            _linkedProviders.value = emptyList()
            _blockedPlayers.value = emptyList()
            return@withContext false
        }

        _isSyncing.value = true
        try {
            when (val res = accountApiService.getAccountSettings(token)) {
                is NetworkResult.Success -> {
                    _accountSettings.value = res.data
                    _linkedProviders.value = res.data.linkedProviders
                    preferencesRepository.setProfileVisibility(res.data.privacySettings.profileVisibility.name)
                    preferencesRepository.setAllowZynpathIdSearch(res.data.privacySettings.allowZynpathIdSearch)
                    preferencesRepository.setAllowFriendRequests(res.data.privacySettings.allowFriendRequests)
                }
                is NetworkResult.Error -> _lastError.value = res.message
                is NetworkResult.Exception -> _lastError.value = res.throwable.message
            }

            when (val blocksRes = accountApiService.getBlockedPlayers(token)) {
                is NetworkResult.Success -> _blockedPlayers.value = blocksRes.data
                is NetworkResult.Error -> {}
                is NetworkResult.Exception -> {}
            }
            true
        } finally {
            _isSyncing.value = false
        }
    }

    override suspend fun updatePrivacy(
        visibility: ProfileVisibility,
        allowZynpathIdSearch: Boolean,
        allowFriendRequests: Boolean
    ): Boolean = withContext(ioDispatcher) {
        preferencesRepository.setProfileVisibility(visibility.name)
        preferencesRepository.setAllowZynpathIdSearch(allowZynpathIdSearch)
        preferencesRepository.setAllowFriendRequests(allowFriendRequests)

        val token = secureTokenStorage.getSessionToken()
        if (token != null) {
            val remoteSettings = PlayerPrivacySettings(visibility, allowZynpathIdSearch, allowFriendRequests)
            when (val res = accountApiService.updatePrivacySettings(token, remoteSettings)) {
                is NetworkResult.Success -> {
                    _accountSettings.value = _accountSettings.value?.copy(privacySettings = res.data)
                    true
                }
                is NetworkResult.Error -> {
                    _lastError.value = res.message
                    false
                }
                is NetworkResult.Exception -> {
                    _lastError.value = res.throwable.message
                    false
                }
            }
        } else {
            true
        }
    }

    override suspend fun setHighContrast(enabled: Boolean) = withContext(ioDispatcher) {
        preferencesRepository.setHighContrast(enabled)
    }

    override suspend fun setTouchSensitivity(sensitivity: Float) = withContext(ioDispatcher) {
        preferencesRepository.setTouchSensitivity(sensitivity)
    }

    override suspend fun unlinkProvider(provider: String): Result<List<String>> = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken()
            ?: return@withContext Result.failure(IllegalStateException("Not authenticated"))

        when (val result = accountApiService.unlinkProvider(token, provider)) {
            is NetworkResult.Success -> {
                _linkedProviders.value = result.data
                _accountSettings.value = _accountSettings.value?.copy(linkedProviders = result.data)
                Result.success(result.data)
            }
            is NetworkResult.Error -> Result.failure(Exception(result.message))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }

    override suspend fun requestDataExport(): Result<DataExportResult> = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken()
            ?: return@withContext Result.failure(IllegalStateException("Not authenticated"))

        when (val result = accountApiService.requestDataExport(token)) {
            is NetworkResult.Success -> Result.success(result.data)
            is NetworkResult.Error -> Result.failure(Exception(result.message))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }

    override suspend fun deleteAccount(): Result<AccountDeletionResult> = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken()
            ?: return@withContext Result.failure(IllegalStateException("Not authenticated"))

        when (val result = accountApiService.deleteAccount(token)) {
            is NetworkResult.Success -> {
                val deletedPlayerId = secureTokenStorage.getSessionMetadata()?.playerId
                _accountSettings.value = null
                _linkedProviders.value = emptyList()
                _blockedPlayers.value = emptyList()
                if (deletedPlayerId != null) {
                    try {
                        database.syncOperationDao().deleteOperationsForOwner(deletedPlayerId)
                    } catch (_: Exception) {}
                }
                authRepository.signOut()
                Result.success(result.data)
            }
            is NetworkResult.Error -> Result.failure(Exception(result.message))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }

    override suspend fun unblockPlayer(targetPlayerId: String): Boolean = withContext(ioDispatcher) {
        val token = secureTokenStorage.getSessionToken() ?: return@withContext false
        when (val res = accountApiService.unblockPlayer(token, targetPlayerId)) {
            is NetworkResult.Success -> {
                _blockedPlayers.value = _blockedPlayers.value.filterNot { it.playerId == targetPlayerId }
                true
            }
            is NetworkResult.Error -> {
                _lastError.value = res.message
                false
            }
            is NetworkResult.Exception -> {
                _lastError.value = res.throwable.message
                false
            }
        }
    }

    override suspend fun clearDisposableCache(): Long = withContext(ioDispatcher) {
        var bytesFreed = 0L
        fun deleteDir(dir: File?): Long {
            if (dir == null || !dir.exists()) return 0L
            var total = 0L
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    total += deleteDir(file)
                } else {
                    total += file.length()
                    file.delete()
                }
            }
            return total
        }
        bytesFreed += deleteDir(context.cacheDir)
        bytesFreed += deleteDir(context.externalCacheDir)
        bytesFreed
    }

    override suspend fun clearGuestProgress(): Boolean = withContext(ioDispatcher) {
        try {
            database.clearAllTables()
            preferencesRepository.clearAllPreferences()
            true
        } catch (e: Exception) {
            _lastError.value = e.message
            false
        }
    }

    override suspend fun signOut() = withContext(ioDispatcher) {
        _accountSettings.value = null
        _linkedProviders.value = emptyList()
        _blockedPlayers.value = emptyList()
        authRepository.signOut()
    }

    override fun clearError() {
        _lastError.value = null
    }
}
