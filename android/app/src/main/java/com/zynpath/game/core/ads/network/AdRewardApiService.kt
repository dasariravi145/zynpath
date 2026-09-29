package com.zynpath.game.core.ads.network

import com.zynpath.game.core.ads.model.RewardVerificationStatus
import com.zynpath.game.core.network.NetworkResult

/**
 * Server verification response payload.
 */
data class ServerVerifyRewardResponse(
    val status: RewardVerificationStatus,
    val granted: Boolean,
    val rewardEventId: String,
    val newRewardedCredits: Int,
    val totalAvailableHints: Int,
    val message: String
)

/**
 * Server hint credit balance response payload.
 */
data class ServerHintBalanceResponse(
    val freeHintsRemaining: Int,
    val rewardedCredits: Int,
    val totalAvailable: Int,
    val isPremium: Boolean,
    val dailyRewardedAdsRemaining: Int
)

/**
 * Network API service for submitting and verifying rewarded ad hint rewards.
 *
 * Implements Prompt 29 Section 27 & 47.
 */
interface AdRewardApiService {
    fun getBaseUrl(): String

    suspend fun verifyReward(
        authToken: String?,
        rewardEventId: String,
        transactionId: String?,
        adUnitId: String,
        amount: Int = 1
    ): NetworkResult<ServerVerifyRewardResponse>

    suspend fun getBalance(authToken: String?): NetworkResult<ServerHintBalanceResponse>
}
