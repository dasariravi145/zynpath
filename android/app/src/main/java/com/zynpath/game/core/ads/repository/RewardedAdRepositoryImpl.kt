package com.zynpath.game.core.ads.repository

import android.app.Activity
import android.content.Context
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
import com.zynpath.game.core.ads.model.RewardResult
import com.zynpath.game.core.ads.network.AdRewardApiService
import com.zynpath.game.core.auth.repository.AuthRepository
import com.zynpath.game.core.auth.storage.SecureTokenStorage
import com.zynpath.game.core.hint.HintUsageRepository
import com.zynpath.game.core.premium.FeatureAccessPolicy
import com.zynpath.game.core.premium.SubscriptionEntitlementRepository
import com.zynpath.game.core.premium.model.PremiumFeatureKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adConsentManager: AdConsentManager,
    private val subscriptionEntitlementRepository: SubscriptionEntitlementRepository,
    private val hintUsageRepository: HintUsageRepository,
    private val authRepository: AuthRepository,
    private val secureTokenStorage: SecureTokenStorage,
    private val adRewardApiService: AdRewardApiService
) : RewardedAdRepository {

    private val tag = "RewardedAdRepository"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _adState = MutableStateFlow(AdState.NOT_INITIALIZED)
    override val adState: StateFlow<AdState> = _adState.asStateFlow()

    override val isRewardedAdAvailable: Boolean
        get() = _adState.value == AdState.READY && rewardedAd != null

    private val _dailyRewardedAdsRemaining = MutableStateFlow(AdConfiguration.MAX_DAILY_REWARDED_HINTS)
    override val dailyRewardedAdsRemaining: StateFlow<Int> = _dailyRewardedAdsRemaining.asStateFlow()

    private var rewardedAd: RewardedAd? = null
    private var isInitializing = false
    private var isSdkInitialized = false
    private val rewardTimestamps = CopyOnWriteArrayList<Long>()

    init {
        // Monitor entitlement: if player becomes active Premium, suppress ads and release any cached ad
        scope.launch {
            subscriptionEntitlementRepository.entitlement.collect { entitlement ->
                if (FeatureAccessPolicy.isFeatureUnlocked(PremiumFeatureKey.AD_FREE, null, entitlement)) {
                    Log.i(tag, "Premium active: suppressing rewarded ads and clearing cache")
                    rewardedAd = null
                    _adState.value = AdState.NOT_INITIALIZED
                }
            }
        }
    }

    private fun ensureSdkInitialized(onInitialized: () -> Unit) {
        if (isSdkInitialized) {
            onInitialized()
            return
        }
        if (isInitializing) return

        isInitializing = true
        mainHandler.post {
            try {
                MobileAds.initialize(context) {
                    isSdkInitialized = true
                    isInitializing = false
                    Log.i(tag, "Google Mobile Ads SDK initialized successfully")
                    onInitialized()
                }
            } catch (e: Exception) {
                isInitializing = false
                Log.e(tag, "Failed to initialize MobileAds SDK", e)
            }
        }
    }

    override fun preloadRewardedAd() {
        val entitlement = subscriptionEntitlementRepository.entitlement.value
        if (FeatureAccessPolicy.isFeatureUnlocked(PremiumFeatureKey.AD_FREE, null, entitlement)) {
            Log.d(tag, "Preload skipped: active Premium subscription")
            return
        }

        if (!adConsentManager.canRequestAds()) {
            _adState.value = AdState.CONSENT_REQUIRED
            return
        }

        pruneDailyTimestamps()

        if (rewardedAd != null && _adState.value == AdState.READY) {
            return // Ad already cached
        }

        if (_adState.value == AdState.LOADING) {
            return // Load already in flight
        }

        ensureSdkInitialized {
            loadAdInternal()
        }
    }

    private fun loadAdInternal() {
        _adState.value = AdState.LOADING
        val adRequest = AdRequest.Builder().build()
        val adUnitId = AdConfiguration.rewardedAdUnitId

        Log.d(tag, "Loading rewarded ad with unit ID: $adUnitId")

        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.i(tag, "Rewarded ad loaded successfully")
                    rewardedAd = ad
                    _adState.value = AdState.READY
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(tag, "Rewarded ad failed to load: code=${loadAdError.code}, message=${loadAdError.message}")
                    rewardedAd = null
                    _adState.value = AdState.UNAVAILABLE
                }
            }
        )
    }

    override suspend fun showRewardedAd(activity: Activity): RewardResult {
        // 1. Premium suppression check
        val entitlement = subscriptionEntitlementRepository.entitlement.value
        if (FeatureAccessPolicy.isFeatureUnlocked(PremiumFeatureKey.AD_FREE, null, entitlement)) {
            return RewardResult.BlockedByPolicy
        }

        // 2. Daily bookkeeping
        pruneDailyTimestamps()

        // 3. Ad availability check
        val currentAd = rewardedAd
        if (currentAd == null || _adState.value != AdState.READY) {
            preloadRewardedAd()
            return RewardResult.Unavailable
        }

        val resultDeferred = CompletableDeferred<RewardResult>()
        var userEarnedReward = false
        var grantedEventId: String? = null

        _adState.value = AdState.SHOWING

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(tag, "Rewarded ad presented full-screen")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(tag, "Rewarded ad failed to show: ${adError.message}")
                rewardedAd = null
                _adState.value = AdState.ERROR
                resultDeferred.complete(RewardResult.Failed(adError.message))
                preloadRewardedAd()
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(tag, "Rewarded ad dismissed. User earned reward: $userEarnedReward")
                rewardedAd = null

                if (userEarnedReward && grantedEventId != null) {
                    _adState.value = AdState.REWARD_EARNED
                    resultDeferred.complete(
                        RewardResult.Success(
                            rewardEventId = grantedEventId!!,
                            amount = AdConfiguration.HINTS_PER_REWARDED_AD,
                            rewardType = "SOLO_HINT"
                        )
                    )
                } else {
                    _adState.value = AdState.DISMISSED_WITHOUT_REWARD
                    resultDeferred.complete(RewardResult.DismissedWithoutReward)
                }

                preloadRewardedAd()
            }
        }

        currentAd.show(activity) { rewardItem: RewardItem ->
            // Section 25: Grant reward only when SDK reports reward item
            userEarnedReward = true
            val eventId = UUID.randomUUID().toString()
            grantedEventId = eventId
            val now = System.currentTimeMillis()

            rewardTimestamps.add(now)
            pruneDailyTimestamps()

            Log.i(tag, "User earned reward: amount=${rewardItem.amount}, type=${rewardItem.type}, eventId=$eventId")

            // Update local hint credit balance immediately
            scope.launch {
                hintUsageRepository.addRewardedHintCredit(AdConfiguration.HINTS_PER_REWARDED_AD)

                // Asynchronously attempt server-side verification if authenticated
                val token = secureTokenStorage.getSessionToken()
                if (!token.isNullOrBlank()) {
                    adRewardApiService.verifyReward(
                        authToken = token,
                        rewardEventId = eventId,
                        transactionId = null,
                        adUnitId = AdConfiguration.rewardedAdUnitId,
                        amount = AdConfiguration.HINTS_PER_REWARDED_AD
                    )
                }
            }
        }

        return resultDeferred.await()
    }

    override suspend fun showRewardedAd(): Boolean {
        // Fallback interface requirement from Prompt 14
        return false
    }

    private fun pruneDailyTimestamps() {
        val now = System.currentTimeMillis()
        val oneDayAgo = now - 24 * 60 * 60 * 1000L
        rewardTimestamps.removeIf { it < oneDayAgo }
        _dailyRewardedAdsRemaining.value = 999
    }
}
