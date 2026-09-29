package com.zynpath.game.core.ads.repository

import android.app.Activity
import com.zynpath.game.core.ads.model.AdState
import com.zynpath.game.core.ads.model.RewardResult
import com.zynpath.game.core.hint.RewardedHintProvider
import kotlinx.coroutines.flow.StateFlow

/**
 * Lifecycle-aware repository managing rewarded ad loading, state, presentation, and reward granting.
 *
 * Implements Prompt 29:
 * - Section 11: Lifecycle-aware repository.
 * - Section 12: Explicit ad states.
 * - Section 24: Full-screen lifecycle management.
 * - Section 25: Grants rewards only upon SDK confirmation.
 * - Section 26: Idempotent reward processing.
 * - Section 39: Complete ad suppression for active Premium subscribers.
 */
interface RewardedAdRepository : RewardedHintProvider {
    val adState: StateFlow<AdState>
    override val isRewardedAdAvailable: Boolean
    val dailyRewardedAdsRemaining: StateFlow<Int>

    fun preloadRewardedAd()
    suspend fun showRewardedAd(activity: Activity): RewardResult
}

/**
 * No-op implementation of [RewardedAdRepository] for testing and offline fallbacks.
 */
class NoOpRewardedAdRepository : RewardedAdRepository {
    override val adState: StateFlow<AdState> = kotlinx.coroutines.flow.MutableStateFlow(AdState.UNAVAILABLE)
    override val isRewardedAdAvailable: Boolean = false
    override val dailyRewardedAdsRemaining: StateFlow<Int> = kotlinx.coroutines.flow.MutableStateFlow(0)
    override fun preloadRewardedAd() {}
    override suspend fun showRewardedAd(activity: Activity): RewardResult = RewardResult.Unavailable
    override suspend fun showRewardedAd(): Boolean = false
}

