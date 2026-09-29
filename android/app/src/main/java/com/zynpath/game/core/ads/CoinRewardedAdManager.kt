package com.zynpath.game.core.ads

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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.zynpath.game.core.ads.config.AdConfiguration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRewardedAdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tag = "CoinRewardedAdManager"
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded: StateFlow<Boolean> = _isAdLoaded.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var rewardedAd: RewardedAd? = null
    private val isShowing = AtomicBoolean(false)

    init {
        mainHandler.post {
            try {
                MobileAds.initialize(context) {
                    preloadAd()
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to initialize MobileAds SDK for coins", e)
            }
        }
    }

    fun preloadAd() {
        if (_isLoading.value || _isAdLoaded.value) return

        _isLoading.value = true
        mainHandler.post {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                AdConfiguration.rewardedAdUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        _isLoading.value = false
                        _isAdLoaded.value = true
                        Log.i(tag, "Coin Rewarded Ad successfully loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        _isLoading.value = false
                        _isAdLoaded.value = false
                        Log.w(tag, "Coin Rewarded Ad failed to load: ${error.message}")
                    }
                }
            )
        }
    }

    fun showCoinRewardedAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        val ad = rewardedAd
        if (ad == null || !_isAdLoaded.value) {
            preloadAd()
            onFailed("Video ad is still loading. Please try again in a moment.")
            return
        }

        if (!isShowing.compareAndSet(false, true)) {
            onFailed("Another ad is currently being displayed.")
            return
        }

        var rewardGranted = false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                _isAdLoaded.value = false
                rewardedAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                isShowing.set(false)
                preloadAd()
                if (!rewardGranted) {
                    onFailed("Ad closed before completion. No coins earned.")
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                isShowing.set(false)
                _isAdLoaded.value = false
                rewardedAd = null
                preloadAd()
                onFailed("Ad presentation failed: ${adError.message}")
            }
        }

        ad.show(activity) { _ ->
            rewardGranted = true
            onRewarded()
        }
    }
}
