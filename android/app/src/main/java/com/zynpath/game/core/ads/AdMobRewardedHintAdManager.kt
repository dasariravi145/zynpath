package com.zynpath.game.core.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.zynpath.game.core.ads.config.AdConfiguration
import com.zynpath.game.core.ads.consent.AdConsentManager
import com.zynpath.game.core.ads.model.AdState
import com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdCallback
import com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdContract
import com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.ref.WeakReference
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-ready AdMob Rewarded Video implementation of [RewardedHintAdContract].
 *
 * Implements Prompt 31 Tasks 5, 6, 7, 8, 12, 13, & 14:
 * - Real Google Mobile Ads Rewarded Ad SDK integration.
 * - Authoritative reward callback verification: reward credit is ONLY granted when
 *   the SDK's OnUserEarnedRewardListener executes.
 * - Idempotent, level-specific reward assignment preventing duplicate credits or multiple ads on rapid clicks.
 * - Safe lifecycle management with WeakReference to prevent Activity leaks.
 * - AdMob configuration safety separating debug test ad units from production.
 * - Non-blocking failure and unavailability handling.
 */
@Singleton
class AdMobRewardedHintAdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adConsentManager: AdConsentManager
) : RewardedHintAdContract, Application.ActivityLifecycleCallbacks {

    private val tag = "AdMobRewardedHintAd"
    private val mainHandler = Handler(Looper.getMainLooper())

    private var currentActivityRef: WeakReference<Activity>? = null

    private val _adState = MutableStateFlow(AdState.NOT_INITIALIZED)
    override val adState: StateFlow<AdState> = _adState.asStateFlow()

    private val _lastLoadError = MutableStateFlow<String?>(null)
    override val lastLoadError: StateFlow<String?> = _lastLoadError.asStateFlow()

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    private var isSdkInitialized = false
    private var isInitializingSdk = false

    private val isAdShowing = AtomicBoolean(false)

    init {
        (context.applicationContext as? Application)?.registerActivityLifecycleCallbacks(this)
        ensureSdkInitialized {
            preloadRewardedAd()
        }
    }

    private fun ensureSdkInitialized(onInitialized: () -> Unit) {
        if (isSdkInitialized) {
            onInitialized()
            return
        }
        if (isInitializingSdk) return

        isInitializingSdk = true
        mainHandler.post {
            try {
                MobileAds.initialize(context) {
                    isSdkInitialized = true
                    isInitializingSdk = false
                    Log.i(tag, "Google Mobile Ads SDK initialized for Level Hint Ads")
                    onInitialized()
                }
            } catch (e: Exception) {
                isInitializingSdk = false
                _adState.value = AdState.ERROR
                _lastLoadError.value = "Failed to initialize AdMob SDK: ${e.message}"
                Log.e(tag, "Failed to initialize MobileAds SDK for hints", e)
            }
        }
    }

    override fun isRewardedAdReady(): Boolean {
        return rewardedAd != null && !isAdShowing.get()
    }

    override fun retryLoadingAd() {
        Log.i(tag, "User triggered retryLoadingAd")
        isLoading = false
        rewardedAd = null
        _adState.value = AdState.LOADING
        _lastLoadError.value = null
        preloadRewardedAd()
    }

    override fun preloadRewardedAd() {
        if (!AdConfiguration.isAdConfigured) {
            Log.d(tag, "Preload skipped: AdMob rewarded ad unit ID is not configured")
            _adState.value = AdState.UNAVAILABLE
            _lastLoadError.value = "AdMob unit not configured"
            return
        }

        if (!adConsentManager.canRequestAds()) {
            Log.d(tag, "Preload skipped: User consent required for ad requests")
            _adState.value = AdState.CONSENT_REQUIRED
            _lastLoadError.value = "User consent required for ads"
            return
        }

        if (rewardedAd != null) {
            Log.d(tag, "Preload skipped: Rewarded ad is already cached and ready")
            _adState.value = AdState.READY
            _lastLoadError.value = null
            return
        }

        if (isLoading) {
            Log.d(tag, "Preload skipped: Rewarded ad load is already in-flight")
            _adState.value = AdState.LOADING
            return
        }

        ensureSdkInitialized {
            loadAdInternal()
        }
    }

    private fun loadAdInternal() {
        val adUnitId = AdConfiguration.rewardedAdUnitId
        if (adUnitId.isBlank()) {
            Log.w(tag, "Cannot load rewarded ad: AdUnit ID is blank")
            _adState.value = AdState.UNAVAILABLE
            _lastLoadError.value = "AdUnit ID is blank"
            return
        }

        isLoading = true
        _adState.value = AdState.LOADING
        _lastLoadError.value = null

        mainHandler.post {
            try {
                val adRequest = AdRequest.Builder().build()
                RewardedAd.load(
                    context,
                    adUnitId,
                    adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedAd) {
                            Log.i(tag, "AdMob rewarded ad loaded successfully")
                            rewardedAd = ad
                            isLoading = false
                            _adState.value = AdState.READY
                            _lastLoadError.value = null
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            val errorDiagnostic = when (loadAdError.code) {
                                AdRequest.ERROR_CODE_NO_FILL -> "No ad inventory available (code 3: NO_FILL)"
                                AdRequest.ERROR_CODE_NETWORK_ERROR -> "Network error loading ad (code 2). Check device internet."
                                AdRequest.ERROR_CODE_INVALID_REQUEST -> "Invalid ad request (code 1)"
                                AdRequest.ERROR_CODE_INTERNAL_ERROR -> "AdMob internal error (code 0)"
                                else -> "AdMob error ${loadAdError.code}: ${loadAdError.message}"
                            }
                            Log.w(tag, "AdMob rewarded ad failed to load: $errorDiagnostic")
                            rewardedAd = null
                            isLoading = false
                            _adState.value = AdState.ERROR
                            _lastLoadError.value = errorDiagnostic
                        }
                    }
                )
            } catch (e: Exception) {
                isLoading = false
                _adState.value = AdState.ERROR
                _lastLoadError.value = "AdMob exception: ${e.message}"
                Log.e(tag, "Exception while triggering RewardedAd.load", e)
            }
        }
    }


    override fun showRewardedHintAd(levelId: Int, callback: RewardedHintAdCallback) {
        showRewardedHintAdInternal(activity = null, levelId = levelId, callback = callback)
    }

    override fun showRewardedHintAd(activity: Activity, levelId: Int, callback: RewardedHintAdCallback) {
        showRewardedHintAdInternal(activity = activity, levelId = levelId, callback = callback)
    }

    private fun showRewardedHintAdInternal(
        activity: Activity?,
        levelId: Int,
        callback: RewardedHintAdCallback
    ) {
        // 1. Concurrency / Rapid-tap guard (Task 8)
        if (!isAdShowing.compareAndSet(false, true)) {
            Log.w(tag, "Rejected duplicate rewarded ad request for level $levelId: ad already presenting")
            callback.onRewardDenied(
                RewardedHintAdResult.Cancelled(
                    levelId = levelId,
                    reason = "Another ad presentation is already in progress"
                )
            )
            return
        }

        // 2. Configuration safety check (Task 6)
        if (!AdConfiguration.isAdConfigured) {
            isAdShowing.set(false)
            Log.w(tag, "Rewarded ad show failed: ad unit ID is unconfigured in current build environment")
            callback.onRewardDenied(
                RewardedHintAdResult.Unavailable(
                    levelId = levelId,
                    message = "No video available right now. Please try again later."
                )
            )
            return
        }

        // 3. Consent check (Task 14)
        if (!adConsentManager.canRequestAds()) {
            isAdShowing.set(false)
            Log.w(tag, "Rewarded ad show failed: consent not obtained")
            callback.onRewardDenied(
                RewardedHintAdResult.Unavailable(
                    levelId = levelId,
                    message = "Consent is required to view rewarded advertisements."
                )
            )
            return
        }

        // 4. Availability check
        val currentAd = rewardedAd
        if (currentAd == null) {
            isAdShowing.set(false)
            Log.i(tag, "Rewarded ad show requested but no ad was cached. Initiating background preload.")
            preloadRewardedAd()
            callback.onRewardDenied(
                RewardedHintAdResult.Unavailable(
                    levelId = levelId,
                    message = "No video available right now. Please try again later."
                )
            )
            return
        }

        // 5. Activity resolution & lifecycle safety (Task 13)
        val hostActivity = activity ?: currentActivityRef?.get()
        if (hostActivity == null || hostActivity.isFinishing || hostActivity.isDestroyed) {
            isAdShowing.set(false)
            Log.e(tag, "No active, non-finishing Activity available to display rewarded ad")
            callback.onRewardDenied(
                RewardedHintAdResult.Unavailable(
                    levelId = levelId,
                    message = "No video available right now. Please try again later."
                )
            )
            return
        }

        // Consume cached ad reference so it cannot be reused
        rewardedAd = null
        _adState.value = AdState.SHOWING

        val rewardClaimId = "lvl_${levelId}_ad_claim_${UUID.randomUUID()}"
        var userEarnedReward = false
        val completionHandled = AtomicBoolean(false)

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(tag, "Rewarded ad presented full-screen for level $levelId")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(tag, "Rewarded ad failed to show for level $levelId: ${adError.message} (code ${adError.code})")
                isAdShowing.set(false)
                _adState.value = AdState.ERROR
                _lastLoadError.value = "Failed to show ad: ${adError.message}"
                if (completionHandled.compareAndSet(false, true)) {
                    callback.onRewardDenied(
                        RewardedHintAdResult.Failed(
                            levelId = levelId,
                            errorCode = adError.code,
                            errorMessage = adError.message
                        )
                    )
                }
                preloadRewardedAd()
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(tag, "Rewarded ad dismissed for level $levelId. Earned: $userEarnedReward")
                isAdShowing.set(false)
                _adState.value = if (userEarnedReward) AdState.REWARD_EARNED else AdState.DISMISSED_WITHOUT_REWARD
                if (completionHandled.compareAndSet(false, true)) {
                    if (userEarnedReward) {
                        callback.onRewardConfirmed(
                            RewardedHintAdResult.Success(
                                rewardClaimId = rewardClaimId,
                                levelId = levelId,
                                hintsGranted = 1
                            )
                        )
                    } else {
                        callback.onRewardDenied(
                            RewardedHintAdResult.Skipped(
                                levelId = levelId,
                                message = "Ad closed before completion threshold"
                            )
                        )
                    }
                }
                preloadRewardedAd()
            }
        }

        mainHandler.post {
            try {
                currentAd.show(hostActivity) { rewardItem: RewardItem ->
                    // Task 7: Reward callback is authoritative
                    Log.i(
                        tag,
                        "AdMob OnUserEarnedRewardListener fired for level $levelId: " +
                                "amount=${rewardItem.amount}, type=${rewardItem.type}, claimId=$rewardClaimId"
                    )
                    userEarnedReward = true
                }
            } catch (e: Exception) {
                isAdShowing.set(false)
                _adState.value = AdState.ERROR
                _lastLoadError.value = "Exception showing ad: ${e.message}"
                Log.e(tag, "Exception during RewardedAd.show for level $levelId", e)
                if (completionHandled.compareAndSet(false, true)) {
                    callback.onRewardDenied(
                        RewardedHintAdResult.Failed(
                            levelId = levelId,
                            errorCode = -1,
                            errorMessage = e.message ?: "Playback failure"
                        )
                    )
                }
                preloadRewardedAd()
            }
        }
    }

    // --- Application.ActivityLifecycleCallbacks implementation ---

    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivityRef?.get() === activity) {
            currentActivityRef = null
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivityRef?.get() === activity) {
            currentActivityRef = null
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}
