package com.zynpath.game.core.account.api

import com.zynpath.game.core.account.model.AccountDeletionResult
import com.zynpath.game.core.account.model.AccountSettings
import com.zynpath.game.core.account.model.BlockedPlayerItem
import com.zynpath.game.core.account.model.DataExportResult
import com.zynpath.game.core.account.model.PlayerPrivacySettings
import com.zynpath.game.core.network.NetworkResult

/**
 * Authoritative remote contract for player settings, privacy preferences, provider linking,
 * data export foundation, and account deletion workflow.
 *
 * Implements Prompt 32 Sections 5-60.
 */
interface AccountApiService {
    fun getBaseUrl(): String

    suspend fun getAccountSettings(sessionToken: String): NetworkResult<AccountSettings>

    suspend fun updatePrivacySettings(
        sessionToken: String,
        settings: PlayerPrivacySettings
    ): NetworkResult<PlayerPrivacySettings>

    suspend fun getLinkedProviders(sessionToken: String): NetworkResult<List<String>>

    suspend fun unlinkProvider(
        sessionToken: String,
        provider: String
    ): NetworkResult<List<String>>

    suspend fun requestDataExport(sessionToken: String): NetworkResult<DataExportResult>

    suspend fun deleteAccount(sessionToken: String): NetworkResult<AccountDeletionResult>

    suspend fun getBlockedPlayers(sessionToken: String): NetworkResult<List<BlockedPlayerItem>>

    suspend fun unblockPlayer(sessionToken: String, targetPlayerId: String): NetworkResult<Unit>
}
