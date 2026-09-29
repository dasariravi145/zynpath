package com.zynpath.game.core.account.repository

import com.zynpath.game.core.account.model.AccountDeletionResult
import com.zynpath.game.core.account.model.AccountSettings
import com.zynpath.game.core.account.model.BlockedPlayerItem
import com.zynpath.game.core.account.model.DataExportResult
import com.zynpath.game.core.account.model.ProfileVisibility
import kotlinx.coroutines.flow.StateFlow

/**
 * Authoritative boundary for player settings, privacy preferences, provider linking,
 * data export foundation, local cache and storage hygiene, and account deletion.
 *
 * Implements Prompt 32 Sections 5-60.
 */
interface AccountRepository {
    val accountSettings: StateFlow<AccountSettings?>
    val linkedProviders: StateFlow<List<String>>
    val blockedPlayers: StateFlow<List<BlockedPlayerItem>>
    val isSyncing: StateFlow<Boolean>
    val lastError: StateFlow<String?>

    suspend fun refreshAccountSettings(): Boolean

    suspend fun updatePrivacy(
        visibility: ProfileVisibility,
        allowZynpathIdSearch: Boolean,
        allowFriendRequests: Boolean
    ): Boolean

    suspend fun setHighContrast(enabled: Boolean)

    suspend fun setTouchSensitivity(sensitivity: Float)

    suspend fun unlinkProvider(provider: String): Result<List<String>>

    suspend fun requestDataExport(): Result<DataExportResult>

    suspend fun deleteAccount(): Result<AccountDeletionResult>

    suspend fun unblockPlayer(targetPlayerId: String): Boolean

    suspend fun clearDisposableCache(): Long

    suspend fun clearGuestProgress(): Boolean

    suspend fun signOut()

    fun clearError()
}
