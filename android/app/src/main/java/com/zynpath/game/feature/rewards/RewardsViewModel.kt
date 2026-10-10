package com.zynpath.game.feature.rewards

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.ads.CoinRewardedAdManager
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.economy.EconomyConfig
import com.zynpath.game.core.economy.WalletRepository
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

import com.zynpath.game.core.puzzle.daily.DailyChallengeAvailability
import com.zynpath.game.core.puzzle.daily.DailyChallengeClock
import com.zynpath.game.core.puzzle.daily.DailyChallengeRepository

data class RewardsUiState(
    val walletBalance: Int = 0,
    val isDailyClaimed: Boolean = false,
    val isClaimingDaily: Boolean = false,
    val dailyRewardAmount: Int = EconomyConfig.DAILY_LOGIN_CLAIM_REWARD,
    val isDailyAdClaimed: Boolean = false,
    val isWatchingDailyAd: Boolean = false,
    val dailyAdRewardAmount: Int = EconomyConfig.DAILY_LOGIN_AD_BONUS,
    val remainingCoinAds: Int = EconomyConfig.MAX_DAILY_COIN_ADS,
    val maxCoinAds: Int = EconomyConfig.MAX_DAILY_COIN_ADS,
    val isAdLoaded: Boolean = false,
    val isAdLoading: Boolean = false,
    val levelsCompletedCount: Int = 0,
    val worldsCompletedCount: Int = 0,
    val totalStars: Int = 0,
    val rewardAnimationCoins: Int = 0,
    val snackbarMessage: String? = null,
    val challengeAvailability: DailyChallengeAvailability = DailyChallengeAvailability.AVAILABLE,
    val streakDays: Int = 0,
    val challengeDateKey: String = "",
    val challengeGrid: String = "5×5"
)

@HiltViewModel
class RewardsViewModel(
    private val walletRepository: WalletRepository,
    private val coinRewardedAdManager: com.zynpath.game.core.ads.CoinRewardedAdHandler,
    private val preferencesRepository: PreferencesRepository,
    private val progressRepository: ProgressRepository,
    private val dailyChallengeRepository: DailyChallengeRepository? = null,
    private val clock: DailyChallengeClock? = null,
    private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    @Inject
    constructor(
        walletRepository: WalletRepository,
        coinRewardedAdManager: CoinRewardedAdManager,
        preferencesRepository: PreferencesRepository,
        progressRepository: ProgressRepository,
        dailyChallengeRepository: DailyChallengeRepository,
        clock: DailyChallengeClock
    ) : this(
        walletRepository,
        coinRewardedAdManager as com.zynpath.game.core.ads.CoinRewardedAdHandler,
        preferencesRepository,
        progressRepository,
        dailyChallengeRepository,
        clock,
        Dispatchers.IO
    )

    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    private fun getTodayUtc(): String = LocalDate.now(ZoneOffset.UTC).toString()

    init {
        observeWallet()
        observeAds()
        observeDailyChallenge()
        loadRewardsStatus()
        coinRewardedAdManager.preloadAd()
    }

    private fun observeDailyChallenge() {
        val dailyRepo = dailyChallengeRepository ?: return
        viewModelScope.launch {
            dailyRepo.observeTodaySummary().collectLatest { summary ->
                _uiState.update {
                    it.copy(
                        challengeAvailability = summary.availability,
                        streakDays = summary.currentStreak,
                        challengeDateKey = summary.definition.dateKey,
                        challengeGrid = "${summary.definition.gridDimensions.rows}×${summary.definition.gridDimensions.columns}"
                    )
                }
            }
        }
    }

    private fun observeWallet() {
        viewModelScope.launch {
            walletRepository.balanceFlow.collectLatest { balance ->
                _uiState.update { it.copy(walletBalance = balance) }
            }
        }
    }

    private fun observeAds() {
        viewModelScope.launch {
            coinRewardedAdManager.isAdLoaded.collectLatest { loaded ->
                _uiState.update { it.copy(isAdLoaded = loaded) }
            }
        }
        viewModelScope.launch {
            coinRewardedAdManager.isLoading.collectLatest { loading ->
                _uiState.update { it.copy(isAdLoading = loading) }
            }
        }
    }

    fun loadRewardsStatus() {
        viewModelScope.launch(ioDispatcher) {
            val today = getTodayUtc()

            // 1. Concurrent fast wallet state reads
            val isClaimedDeferred = async { walletRepository.isDailyLoginClaimed(today) }
            val isDailyAdClaimedDeferred = async { walletRepository.isDailyLoginAdBonusClaimed(today) }
            val remainingAdsDeferred = async { walletRepository.getRemainingDailyCoinAds(today) }
            val balanceDeferred = async { walletRepository.getCurrentBalance() }

            val isClaimed = isClaimedDeferred.await()
            val isDailyAdClaimed = isDailyAdClaimedDeferred.await()
            val remainingAds = remainingAdsDeferred.await()
            val balance = balanceDeferred.await()

            // Immediate fast update for primary actionable UI
            _uiState.update {
                it.copy(
                    walletBalance = balance,
                    isDailyClaimed = isClaimed,
                    isDailyAdClaimed = isDailyAdClaimed,
                    remainingCoinAds = remainingAds
                )
            }

            // 2. Secondary progression stats in background without blocking primary reward actions
            val allCompletedIds = progressRepository.getCompletedLevelIds()
            val completedWorlds = (1..6).count { wId ->
                val w = WorldConfiguration.getWorld(wId)
                w.levelRange.all { it in allCompletedIds }
            }
            val stars = progressRepository.getTotalStarsEarned().firstOrNull() ?: 0

            _uiState.update {
                it.copy(
                    levelsCompletedCount = allCompletedIds.size,
                    worldsCompletedCount = completedWorlds,
                    totalStars = stars
                )
            }
        }
    }

    fun claimDailyLoginReward() {
        val today = getTodayUtc()
        if (_uiState.value.isDailyClaimed || _uiState.value.isClaimingDaily) {
            return
        }

        _uiState.update { it.copy(isClaimingDaily = true) }

        viewModelScope.launch(ioDispatcher) {
            val isAlreadyClaimed = walletRepository.isDailyLoginClaimed(today)
            if (isAlreadyClaimed) {
                _uiState.update {
                    it.copy(
                        isDailyClaimed = true,
                        isClaimingDaily = false,
                        snackbarMessage = "Daily reward already claimed today!"
                    )
                }
                return@launch
            }

            val result = walletRepository.claimDailyLoginReward(today)
            if (result.isSuccess) {
                val newBalance = result.getOrDefault(_uiState.value.walletBalance + EconomyConfig.DAILY_LOGIN_CLAIM_REWARD)
                _uiState.update {
                    it.copy(
                        walletBalance = newBalance,
                        isDailyClaimed = true,
                        isClaimingDaily = false,
                        rewardAnimationCoins = EconomyConfig.DAILY_LOGIN_CLAIM_REWARD,
                        snackbarMessage = "+${EconomyConfig.DAILY_LOGIN_CLAIM_REWARD} Coins Collected!"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isClaimingDaily = false,
                        snackbarMessage = "Failed to claim reward: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun watchRewardedCoinAd(activity: Activity) {
        val today = getTodayUtc()
        if (_uiState.value.remainingCoinAds <= 0) {
            _uiState.update {
                it.copy(
                    snackbarMessage = "Daily limit reached (${EconomyConfig.MAX_DAILY_COIN_ADS}/${EconomyConfig.MAX_DAILY_COIN_ADS} video claims)."
                )
            }
            return
        }

        coinRewardedAdManager.showCoinRewardedAd(
            activity = activity,
            onRewarded = {
                viewModelScope.launch(ioDispatcher) {
                    val res = walletRepository.claimCoinAdReward(today)
                    if (res.isSuccess) {
                        val remaining = walletRepository.getRemainingDailyCoinAds(today)
                        _uiState.update {
                            it.copy(
                                walletBalance = res.getOrDefault(it.walletBalance + EconomyConfig.REWARDED_COIN_AD_REWARD),
                                remainingCoinAds = remaining,
                                rewardAnimationCoins = EconomyConfig.REWARDED_COIN_AD_REWARD,
                                snackbarMessage = "+${EconomyConfig.REWARDED_COIN_AD_REWARD} Coins Earned from Video!"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                snackbarMessage = "Ad credit failed: ${res.exceptionOrNull()?.message}"
                            )
                        }
                    }
                }
            },
            onFailed = { errorMsg ->
                _uiState.update { it.copy(snackbarMessage = errorMsg) }
            }
        )
    }

    fun watchDailyAd(activity: Activity) {
        val today = getTodayUtc()
        if (_uiState.value.isDailyAdClaimed || _uiState.value.isWatchingDailyAd) {
            return
        }

        if (!coinRewardedAdManager.isAdLoaded.value) {
            _uiState.update {
                it.copy(
                    snackbarMessage = "Video ad is still loading. Please try again in a moment."
                )
            }
            coinRewardedAdManager.preloadAd()
            return
        }

        _uiState.update { it.copy(isWatchingDailyAd = true) }

        coinRewardedAdManager.showCoinRewardedAd(
            activity = activity,
            onRewarded = {
                viewModelScope.launch(ioDispatcher) {
                    if (walletRepository.isDailyLoginAdBonusClaimed(today)) {
                        _uiState.update {
                            it.copy(
                                isDailyAdClaimed = true,
                                isWatchingDailyAd = false,
                                snackbarMessage = "Daily ad bonus already claimed today!"
                            )
                        }
                        return@launch
                    }

                    val res = walletRepository.claimDailyLoginAdBonus(today)
                    if (res.isSuccess) {
                        val rewardAmount = EconomyConfig.DAILY_LOGIN_AD_BONUS
                        val newBalance = res.getOrDefault(_uiState.value.walletBalance + rewardAmount)
                        _uiState.update {
                            it.copy(
                                walletBalance = newBalance,
                                isDailyAdClaimed = true,
                                isWatchingDailyAd = false,
                                rewardAnimationCoins = rewardAmount,
                                snackbarMessage = "+$rewardAmount Coins Earned from Video!"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isWatchingDailyAd = false,
                                snackbarMessage = "Ad credit failed: ${res.exceptionOrNull()?.message}"
                            )
                        }
                    }
                }
            },
            onFailed = { errorMsg ->
                _uiState.update {
                    it.copy(
                        isWatchingDailyAd = false,
                        snackbarMessage = errorMsg.ifBlank { "Ad closed before completion. No coins earned." }
                    )
                }
            }
        )
    }

    fun clearAnimationCoins() {
        _uiState.update { it.copy(rewardAnimationCoins = 0) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
