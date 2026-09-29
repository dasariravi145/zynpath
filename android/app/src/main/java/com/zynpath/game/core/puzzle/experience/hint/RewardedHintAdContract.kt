package com.zynpath.game.core.puzzle.experience.hint

import android.app.Activity
import com.zynpath.game.core.ads.model.AdState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Result of a rewarded ad viewing attempt for additional level hints.
 *
 * Implements Prompt 25 Task 6:
 * - One confirmed completion unlocks one hint credit.
 * - No hint credit is granted if the ad opened but was not confirmed complete.
 * - Explicit typed outcomes for Skipped, Failed, Cancelled, Unavailable, or Missing callback.
 */
sealed interface RewardedHintAdResult {

    /**
     * Confirmed completion with valid transaction / reward verification.
     * Only this result unlocks an additional hint.
     */
    data class Success(
        val rewardClaimId: String,
        val levelId: Int,
        val hintsGranted: Int = 1,
        val timestampMs: Long = System.currentTimeMillis()
    ) : RewardedHintAdResult

    /** User dismissed or skipped the ad before reaching the required completion point. */
    data class Skipped(
        val levelId: Int,
        val message: String = "Ad skipped before completion threshold"
    ) : RewardedHintAdResult

    /** Technical playback or network error during ad presentation. */
    data class Failed(
        val levelId: Int,
        val errorCode: Int,
        val errorMessage: String
    ) : RewardedHintAdResult

    /** Player cancelled or exited prior to ad initialization/display. */
    data class Cancelled(
        val levelId: Int,
        val reason: String = "Player cancelled ad request"
    ) : RewardedHintAdResult

    /** No rewarded inventory was available from the provider. */
    data class Unavailable(
        val levelId: Int,
        val message: String = "No rewarded ad currently available"
    ) : RewardedHintAdResult

    /** Provider closed without invoking onUserEarnedReward callback. */
    data class MissingCallback(
        val levelId: Int,
        val message: String = "Provider closed without confirmation callback"
    ) : RewardedHintAdResult
}

/**
 * Callback interface invoked when an ad viewing session terminates.
 */
interface RewardedHintAdCallback {
    fun onRewardConfirmed(success: RewardedHintAdResult.Success)
    fun onRewardDenied(denial: RewardedHintAdResult)
}

/**
 * Abstraction layer decoupling the Level Experience hint economy from the physical AdMob SDK.
 * Implements real Google Mobile Ads rewarded SDK integration (Prompt 31).
 */
interface RewardedHintAdContract {
    /** Reactive lifecycle state of the rewarded hint ad. */
    val adState: StateFlow<AdState>
        get() = MutableStateFlow(if (isRewardedAdReady()) AdState.READY else AdState.NOT_INITIALIZED)

    /** Last error message encountered during ad loading or presentation, for diagnostics. */
    val lastLoadError: StateFlow<String?>
        get() = MutableStateFlow(null)

    /** True if a rewarded ad is currently preloaded and ready to show. */
    fun isRewardedAdReady(): Boolean

    /** Preload a rewarded ad into cache if not already loaded. */
    fun preloadRewardedAd()

    /** User-initiated retry to reload the rewarded ad when previously failed or unready. */
    fun retryLoadingAd() {
        preloadRewardedAd()
    }

    /**
     * Requests presentation of a rewarded video ad for the given level.
     * [callback] will only receive [RewardedHintAdResult.Success] if the ad completes fully.
     */
    fun showRewardedHintAd(levelId: Int, callback: RewardedHintAdCallback)

    /**
     * Presentation overload supplying the hosting [Activity] explicitly.
     */
    fun showRewardedHintAd(activity: Activity, levelId: Int, callback: RewardedHintAdCallback) {
        showRewardedHintAd(levelId, callback)
    }
}

