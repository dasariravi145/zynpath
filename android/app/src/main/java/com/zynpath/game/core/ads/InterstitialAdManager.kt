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
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.zynpath.game.core.ads.config.AdConfiguration
import com.zynpath.game.core.economy.EconomyConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import com.zynpath.game.core.datastore.PreferencesRepository
import javax.inject.Singleton

@Singleton
class InterstitialAdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository
) {
    private val tag = "InterstitialAdManager"
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded: StateFlow<Boolean> = _isAdLoaded.asStateFlow()

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private val isShowing = AtomicBoolean(false)

    // Tracking constraints
    private var lastShownTimestampMs: Long = 0L
    private var sessionInterstitialsShown: Int = 0
    private var lastRecordedDateUtc: String = LocalDate.now(ZoneOffset.UTC).toString()
    private var dailyInterstitialsShown: Int = 0
    private var distinctCompletionsCount: Int = 0

    fun onFirstTimeLevelCompleted() {
        distinctCompletionsCount++
    }

    suspend fun isMilestoneEligible(distinctCount: Int): Boolean {
        if (distinctCount <= 0 || distinctCount % EconomyConfig.INTERSTITIAL_SOLO_FREQUENCY != 0) {
            return false
        }
        val lastHandled = preferencesRepository.getLastHandledInterstitialMilestone()
        return distinctCount > lastHandled
    }

    suspend fun markMilestoneHandled(milestone: Int) {
        preferencesRepository.setLastHandledInterstitialMilestone(milestone)
    }

    fun isAdReadyToShow(): Boolean {
        val isMilestone = distinctCompletionsCount > 0 && (distinctCompletionsCount % EconomyConfig.INTERSTITIAL_SOLO_FREQUENCY == 0)
        return isMilestone && canShowInterstitial()
    }

    init {
        mainHandler.post {
            try {
                MobileAds.initialize(context) {
                    preloadAd()
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to initialize MobileAds SDK for interstitials", e)
            }
        }
    }

    fun preloadAd() {
        if (isLoading || _isAdLoaded.value) return

        isLoading = true
        mainHandler.post {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                AdConfiguration.interstitialAdUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isLoading = false
                        _isAdLoaded.value = true
                        Log.i(tag, "Interstitial Ad successfully loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        isLoading = false
                        _isAdLoaded.value = false
                        Log.w(tag, "Interstitial Ad failed to load: ${error.message}")
                    }
                }
            )
        }
    }

    /**
     * Checks if ad is ready to show.
     * The approved every-3rd first-time completion rule governs interstitial opportunity cadence.
     */
    fun canShowInterstitial(): Boolean {
        return _isAdLoaded.value && interstitialAd != null
    }

    /**
     * Evaluates milestone eligibility (every 3rd distinct first-time solo level completion).
     */
    fun shouldTriggerMilestoneInterstitial(
        completedDistinctCount: Int
    ): Boolean {
        val isMilestone = (completedDistinctCount > 0 && completedDistinctCount % EconomyConfig.INTERSTITIAL_SOLO_FREQUENCY == 0)
        return isMilestone && canShowInterstitial()
    }

    fun showInterstitial(
        activity: Activity,
        onClosed: () -> Unit = {}
    ) {
        val ad = interstitialAd
        if (ad == null || !canShowInterstitial()) {
            onClosed()
            return
        }

        if (!isShowing.compareAndSet(false, true)) {
            onClosed()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                _isAdLoaded.value = false
                interstitialAd = null
                lastShownTimestampMs = System.currentTimeMillis()
                sessionInterstitialsShown++
                dailyInterstitialsShown++
            }

            override fun onAdDismissedFullScreenContent() {
                isShowing.set(false)
                preloadAd()
                onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                isShowing.set(false)
                _isAdLoaded.value = false
                interstitialAd = null
                preloadAd()
                onClosed()
            }
        }

        ad.show(activity)
    }

    private fun checkDailyReset() {
        val today = LocalDate.now(ZoneOffset.UTC).toString()
        if (today != lastRecordedDateUtc) {
            lastRecordedDateUtc = today
            dailyInterstitialsShown = 0
        }
    }
}
