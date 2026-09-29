package com.zynpath.game.feature.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zynpath.game.core.achievement.AchievementRepository
import com.zynpath.game.core.database.repository.GameplaySessionRepository
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.puzzle.daily.DailyChallengeAvailability
import com.zynpath.game.core.puzzle.daily.DailyChallengeClock
import com.zynpath.game.core.puzzle.daily.DailyChallengeDefinition
import com.zynpath.game.core.puzzle.daily.DailyChallengeOnlineResult
import com.zynpath.game.core.puzzle.daily.DailyChallengeRepository
import com.zynpath.game.core.puzzle.daily.DailyVerificationStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.audio.NoOpAudioManager
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.haptics.NoOpHapticManager
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.session.GameplaySessionSnapshot
import com.zynpath.game.core.puzzle.session.GameplayTimer
import com.zynpath.game.core.puzzle.session.SessionStatus
import com.zynpath.game.core.puzzle.session.TimeProvider
import android.app.Activity
import com.zynpath.game.core.economy.WalletRepository
import com.zynpath.game.core.ads.CoinRewardedAdManager
import com.zynpath.game.core.database.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * Authoritative ViewModel managing Daily Challenge lifecycle, input, persistence,
 * official competitive attempts, and server-side verification.
 *
 * Implements Prompt 16 & Prompt 25 Sections 17-22, 28-35, 41-43 & 48.
 */
@HiltViewModel
class DailyChallengeViewModel @Inject constructor(
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val clock: DailyChallengeClock,
    private val sessionRepository: GameplaySessionRepository,
    private val preferencesRepository: PreferencesRepository,
    private val timeProvider: TimeProvider,
    private val achievementRepository: AchievementRepository,
    private val audioManager: ZynpathAudioManager = NoOpAudioManager(),
    private val hapticManager: ZynpathHapticManager = NoOpHapticManager(),
    val walletRepository: WalletRepository? = null,
    val coinRewardedAdManager: CoinRewardedAdManager? = null,
    val progressRepository: ProgressRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<DailyChallengeUiState>(DailyChallengeUiState.Loading)
    val uiState: StateFlow<DailyChallengeUiState> = _uiState.asStateFlow()

    private var currentChallenge: DailyChallengeDefinition? = null
    private var engine: PuzzleEngine? = null
    private val gameplayTimer = GameplayTimer(timeProvider)
    private var timerJob: Job? = null
    private val completionLatch = AtomicBoolean(false)
    private var saveJob: Job? = null

    // Preferences cache
    private var isTapMode = false
    private var isReducedMotion = false
    private var isSfxEnabled = true
    private var isHapticsEnabled = true

    init {
        observeUserPreferences()
        observeEconomy()
        loadTodayChallenge()
    }

    private fun observeEconomy() {
        val wallet = walletRepository
        if (wallet != null) {
            viewModelScope.launch {
                wallet.balanceFlow.collectLatest { balance ->
                    val currentReady = _uiState.value as? DailyChallengeUiState.Ready
                    if (currentReady != null) {
                        _uiState.value = currentReady.copy(coinBalance = balance)
                    }
                }
            }
        }
        val progress = progressRepository
        if (progress != null) {
            viewModelScope.launch {
                progress.getTotalStarsEarned().collectLatest { stars ->
                    val currentReady = _uiState.value as? DailyChallengeUiState.Ready
                    if (currentReady != null) {
                        _uiState.value = currentReady.copy(totalStars = stars)
                    }
                }
            }
        }
    }

    private fun observeUserPreferences() {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collectLatest { prefs ->
                isTapMode = prefs.isTapInputMode
                isReducedMotion = prefs.isReducedMotion
                isSfxEnabled = prefs.isSfxEnabled
                isHapticsEnabled = prefs.isHapticsEnabled

                val currentReady = _uiState.value as? DailyChallengeUiState.Ready
                if (currentReady != null) {
                    _uiState.value = currentReady.copy(
                        isTapInputMode = isTapMode,
                        isReducedMotion = isReducedMotion,
                        isSfxEnabled = isSfxEnabled,
                        isHapticsEnabled = isHapticsEnabled
                    )
                }
            }
        }
    }

    fun loadTodayChallenge() {
        viewModelScope.launch {
            try {
                // 1. Resolve local challenge definition
                val localChallenge = dailyChallengeRepository.getTodayChallenge()

                // 2. Check if official challenge is available from backend
                val onlineChallengeResult = dailyChallengeRepository.getOfficialChallengeOnline(localChallenge.dateKey)
                val effectiveChallenge = if (onlineChallengeResult.isSuccess) {
                    onlineChallengeResult.getOrThrow()
                } else {
                    localChallenge
                }
                currentChallenge = effectiveChallenge

                // Check Room status for today
                val summary = dailyChallengeRepository.observeTodaySummary().firstOrNull()
                val currentStreak = summary?.currentStreak ?: 0
                val maxStreak = summary?.maxStreak ?: 0
                val personalBest = summary?.entity?.bestTimeMs?.takeIf { it > 0 }

                // Check stored verification status
                val initialVerification = try {
                    DailyVerificationStatus.valueOf(summary?.entity?.verificationStatus ?: "LOCAL_COMPLETION")
                } catch (e: Exception) {
                    DailyVerificationStatus.LOCAL_COMPLETION
                }
                val isEligible = summary?.entity?.isLeaderboardEligible ?: false

                // 3. Check for official competitive attempt
                var serverAttemptId: String? = summary?.entity?.serverAttemptId
                var isOnline = false

                val activeAttemptRes = dailyChallengeRepository.getActiveAttempt(effectiveChallenge.dateKey)
                if (activeAttemptRes.isSuccess && activeAttemptRes.getOrNull() != null) {
                    serverAttemptId = activeAttemptRes.getOrNull()!!.attemptId
                    isOnline = true
                } else if (summary?.availability != DailyChallengeAvailability.COMPLETED) {
                    val startRes = dailyChallengeRepository.startOfficialAttempt(effectiveChallenge.dateKey)
                    if (startRes.isSuccess) {
                        serverAttemptId = startRes.getOrThrow().attemptId
                        isOnline = true
                    }
                }

                // Check for saved session
                val sessionId = "daily_${effectiveChallenge.challengeId}"
                val savedSession = sessionRepository.getSessionById(sessionId)

                val newEngine = PuzzleEngine(effectiveChallenge.puzzleDefinition)
                var initialElapsed = 0L

                // Validate saved session
                if (savedSession != null &&
                    savedSession.puzzleId == effectiveChallenge.puzzleId &&
                    savedSession.puzzleVersion == effectiveChallenge.puzzleVersion &&
                    savedSession.status != SessionStatus.COMPLETED
                ) {
                    val restoredPositions = savedSession.path
                    if (restoredPositions.isNotEmpty()) {
                        val testEngine = PuzzleEngine(effectiveChallenge.puzzleDefinition)
                        var valid = true
                        for (coord in restoredPositions) {
                            val res = testEngine.process(PuzzleAction.ExtendPath(coord))
                            if (!res.isAccepted) {
                                valid = false
                                break
                            }
                        }
                        if (valid) {
                            for (coord in restoredPositions) {
                                newEngine.process(PuzzleAction.ExtendPath(coord))
                            }
                            initialElapsed = savedSession.elapsedActiveTimeMs
                        }
                    }
                }

                engine = newEngine

                // Record attempt start locally
                dailyChallengeRepository.recordAttemptStarted(effectiveChallenge)

                // Initialize timer with restored elapsed time
                gameplayTimer.reset()

                val currentGameState = newEngine.currentState
                val boardState = currentGameState.toBoardState()
                val availability = summary?.availability ?: DailyChallengeAvailability.AVAILABLE

                // Fetch personal online result if completed
                var onlineResult: DailyChallengeOnlineResult? = null
                var effectiveVerification = initialVerification
                if (availability == DailyChallengeAvailability.COMPLETED) {
                    val personalRes = dailyChallengeRepository.getPersonalResultOnline(effectiveChallenge.dateKey)
                    onlineResult = personalRes.getOrNull()

                    // If completed locally offline and we now have online connectivity, sync provisional record
                    if (onlineResult == null && initialVerification == DailyVerificationStatus.LOCAL_COMPLETION && onlineChallengeResult.isSuccess) {
                        val onlineDef = onlineChallengeResult.getOrNull()
                        if (onlineDef != null && onlineDef.puzzleFingerprint == effectiveChallenge.puzzleFingerprint && summary?.entity != null) {
                            val syncRes = dailyChallengeRepository.syncProvisional(
                                challenge = effectiveChallenge,
                                solveTimeMs = summary.entity.solveTimeMs,
                                completedAt = summary.entity.completedAt,
                                path = savedSession?.path?.map { "${it.row},${it.col}" },
                                movesCount = summary.entity.movesCount
                            )
                            if (syncRes.isSuccess) {
                                onlineResult = syncRes.getOrNull()
                                effectiveVerification = DailyVerificationStatus.PROVISIONAL
                            }
                        }
                    }
                }

                val todayUtc = clock.currentUtcDateKey()
                val isLoginClaimed = walletRepository?.isDailyLoginClaimed(todayUtc) == true
                val isLoginAdBonusClaimed = walletRepository?.isDailyLoginAdBonusClaimed(todayUtc) == true
                val isChallengeClearClaimed = walletRepository?.isDailyChallengeClearClaimed(todayUtc) == true
                val isChallengeAdBonusClaimed = walletRepository?.isDailyChallengeAdBonusClaimed(todayUtc) == true
                val coinBal = walletRepository?.getCurrentBalance() ?: 0
                val totalStars = progressRepository?.getTotalStarsEarned()?.firstOrNull() ?: 0

                _uiState.value = DailyChallengeUiState.Ready(
                    challenge = effectiveChallenge,
                    availability = availability,
                    gameState = currentGameState,
                    boardState = boardState,
                    elapsedTimeMs = initialElapsed,
                    moveCount = currentGameState.moveCount,
                    currentStreak = currentStreak,
                    maxStreak = maxStreak,
                    personalBestTimeMs = personalBest,
                    isTapInputMode = isTapMode,
                    isReducedMotion = isReducedMotion,
                    isSfxEnabled = isSfxEnabled,
                    isHapticsEnabled = isHapticsEnabled,
                    verificationStatus = effectiveVerification,
                    isOnlineAttempt = isOnline,
                    serverAttemptId = serverAttemptId,
                    isLeaderboardEligible = isEligible,
                    onlineResult = onlineResult,
                    coinBalance = coinBal,
                    totalStars = totalStars,
                    isDailyLoginClaimed = isLoginClaimed,
                    isDailyLoginAdBonusClaimed = isLoginAdBonusClaimed,
                    isDailyChallengeClearClaimed = isChallengeClearClaimed,
                    isDailyChallengeAdBonusClaimed = isChallengeAdBonusClaimed
                )

                if (!currentGameState.currentPath.isEmpty && !currentGameState.isCompleted) {
                    gameplayTimer.start()
                    startTimerTicker()
                }
            } catch (e: Exception) {
                _uiState.value = DailyChallengeUiState.Error(
                    message = "Failed to load today's challenge: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    fun onCellEntered(position: GridPosition): Boolean {
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return false
        val activeEngine = engine ?: return false

        if (activeEngine.currentState.isCompleted) return false

        // Auto-start timer on first move
        if (!gameplayTimer.isRunning) {
            gameplayTimer.start()
            startTimerTicker()
        }

        val elapsed = gameplayTimer.elapsedDurationMs()

        // Discrete tap backtracking check
        val path = activeEngine.currentState.currentPath.positions
        val secondToLast = if (path.size >= 2) path[path.size - 2] else null

        val result = if (position == secondToLast) {
            activeEngine.process(PuzzleAction.BacktrackTo(position), elapsed)
        } else {
            activeEngine.process(PuzzleAction.ExtendPath(position), elapsed)
        }

        return if (result.isAccepted) {
            val newState = result.state
            val newBoard = newState.toBoardState()
            val isCheckpoint = currentChallenge?.puzzleDefinition?.getCheckpointAt(position) != null

            if (newState.isCompleted) {
                audioManager.playEvent(ZynpathAudioEvent.PUZZLE_COMPLETED)
                hapticManager.performHaptic(ZynpathHapticEvent.COMPLETION)
            } else if (isCheckpoint) {
                audioManager.playEvent(ZynpathAudioEvent.CHECKPOINT_REACHED)
                hapticManager.performHaptic(ZynpathHapticEvent.CHECKPOINT_REACHED)
            } else if (position == secondToLast) {
                audioManager.playEvent(ZynpathAudioEvent.UNDO)
                hapticManager.performHaptic(ZynpathHapticEvent.UNDO)
            } else if (newState.currentPath.length == 1) {
                audioManager.playEvent(ZynpathAudioEvent.PATH_START)
                hapticManager.performHaptic(ZynpathHapticEvent.PATH_START)
            } else {
                audioManager.playEvent(ZynpathAudioEvent.VALID_MOVE)
                hapticManager.performHaptic(ZynpathHapticEvent.VALID_MOVE)
            }

            _uiState.value = currentReady.copy(
                gameState = newState,
                boardState = newBoard,
                moveCount = newState.moveCount,
                lastRejectionReason = null
            )

            scheduleDebouncedSave()

            if (newState.isCompleted) {
                handleVictory(newState)
            }
            true
        } else {
            audioManager.playEvent(ZynpathAudioEvent.INVALID_MOVE)
            hapticManager.performHaptic(ZynpathHapticEvent.INVALID_MOVE)
            val reason = (result as? PuzzleEngineResult.Rejected)?.reason
            _uiState.value = currentReady.copy(
                lastRejectionReason = reason
            )
            false
        }
    }

    private fun handleVictory(state: PuzzleGameState) {
        if (!completionLatch.compareAndSet(false, true)) return

        gameplayTimer.stop()
        val elapsed = gameplayTimer.elapsedDurationMs()
        val challenge = currentChallenge ?: return

        val currentReady = _uiState.value as? DailyChallengeUiState.Ready
        val serverAttemptId = currentReady?.serverAttemptId

        viewModelScope.launch {
            var finalVerification = DailyVerificationStatus.LOCAL_COMPLETION
            var finalEligible = false
            var onlineResult: DailyChallengeOnlineResult? = null

            if (serverAttemptId != null && currentReady.isOnlineAttempt) {
                _uiState.value = currentReady.copy(isValidatingOnline = true)

                val pathCoords = state.currentPath.positions.map { "${it.row},${it.col}" }
                val submitRes = dailyChallengeRepository.submitOfficialCompletion(
                    attemptId = serverAttemptId,
                    challenge = challenge,
                    pathCoordinates = pathCoords,
                    clientElapsedMs = elapsed
                )

                if (submitRes.isSuccess) {
                    val res = submitRes.getOrThrow()
                    finalVerification = res.verificationStatus
                    finalEligible = res.isLeaderboardEligible
                    onlineResult = res
                } else {
                    // Fall back gracefully to local completion
                    finalVerification = DailyVerificationStatus.LOCAL_COMPLETION
                }
            }

            val finalSolveTime = onlineResult?.solveTimeMs ?: elapsed

            val isNewBest = dailyChallengeRepository.recordCompletion(
                challenge = challenge,
                solveTimeMs = finalSolveTime,
                movesCount = state.moveCount,
                verificationStatus = finalVerification,
                serverAttemptId = serverAttemptId,
                isLeaderboardEligible = finalEligible
            )

            val summary = dailyChallengeRepository.observeTodaySummary().firstOrNull()
            val currentStreak = summary?.currentStreak ?: 1
            val maxStreak = summary?.maxStreak ?: currentStreak

            val todayUtc = clock.currentUtcDateKey()
            if (walletRepository != null && !walletRepository.isDailyChallengeClearClaimed(todayUtc)) {
                walletRepository.claimDailyChallengeReward(todayUtc)
            }

            val latestReady = _uiState.value as? DailyChallengeUiState.Ready
            if (latestReady != null) {
                _uiState.value = latestReady.copy(
                    gameState = state,
                    boardState = state.toBoardState(),
                    availability = DailyChallengeAvailability.COMPLETED,
                    elapsedTimeMs = finalSolveTime,
                    currentStreak = currentStreak,
                    maxStreak = maxStreak,
                    isNewPersonalBest = isNewBest,
                    verificationStatus = finalVerification,
                    isLeaderboardEligible = finalEligible,
                    isValidatingOnline = false,
                    onlineResult = onlineResult,
                    showResultDialog = true,
                    isDailyChallengeClearClaimed = true
                )
            }

            // Flush completed session state
            sessionRepository.saveSession(
                GameplaySessionSnapshot(
                    sessionId = "daily_${challenge.challengeId}",
                    levelId = 0,
                    worldId = 0,
                    puzzleId = challenge.puzzleId,
                    puzzleVersion = challenge.puzzleVersion,
                    catalogVersion = challenge.scheduleVersion,
                    status = SessionStatus.COMPLETED,
                    path = state.currentPath.positions,
                    elapsedActiveTimeMs = finalSolveTime,
                    moveCount = state.moveCount,
                    startedAt = timeProvider.wallClockTimeMs() - finalSolveTime,
                    lastUpdatedAt = timeProvider.wallClockTimeMs(),
                    revision = 1L,
                    snapshotSchemaVersion = 1
                )
            )

            // Evaluate achievements
            achievementRepository.evaluateAll()
            achievementRepository.evaluateDailyAchievements(
                isServerValidated = finalVerification == DailyVerificationStatus.SERVER_VALIDATED,
                isLeaderboardEligible = finalEligible
            )
        }
    }

    fun dismissResultDialog() {
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        _uiState.value = currentReady.copy(showResultDialog = false)
    }

    fun showResultDialog() {
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        _uiState.value = currentReady.copy(showResultDialog = true)
    }

    fun onUndo() {
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        val activeEngine = engine ?: return
        if (activeEngine.currentState.isCompleted) return

        val elapsed = gameplayTimer.elapsedDurationMs()
        val result = activeEngine.process(PuzzleAction.BacktrackOne, elapsed)
        if (result.isAccepted) {
            audioManager.playEvent(ZynpathAudioEvent.UNDO)
            hapticManager.performHaptic(ZynpathHapticEvent.UNDO)
            val newState = result.state
            _uiState.value = currentReady.copy(
                gameState = newState,
                boardState = newState.toBoardState(),
                moveCount = newState.moveCount,
                lastRejectionReason = null
            )
            scheduleDebouncedSave()
        } else {
            val reason = (result as? PuzzleEngineResult.Rejected)?.reason
            _uiState.value = currentReady.copy(lastRejectionReason = reason)
        }
    }

    fun onReset() {
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        val activeEngine = engine ?: return

        gameplayTimer.reset()
        completionLatch.set(false)

        val result = activeEngine.process(PuzzleAction.ResetPath, 0L)
        if (result.isAccepted) {
            audioManager.playEvent(ZynpathAudioEvent.RESET)
            hapticManager.performHaptic(ZynpathHapticEvent.RESET)
            val newState = result.state
            _uiState.value = currentReady.copy(
                gameState = newState,
                boardState = newState.toBoardState(),
                elapsedTimeMs = 0L,
                moveCount = 0,
                lastRejectionReason = null
            )
            scheduleDebouncedSave()
        }
    }

    fun onPause() {
        gameplayTimer.pause()
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        _uiState.value = currentReady.copy(isPaused = true)
        flushSessionSave()
    }

    fun onResume() {
        if (engine?.currentState?.isInProgress == true && !engine!!.currentState.isCompleted) {
            gameplayTimer.resume()
        }
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        _uiState.value = currentReady.copy(isPaused = false)
    }

    fun toggleInputMode() {
        viewModelScope.launch {
            val nextMode = !isTapMode
            preferencesRepository.setTapInputMode(nextMode)
        }
    }

    fun toggleRulesDialog(show: Boolean) {
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        _uiState.value = currentReady.copy(showRulesDialog = show)
    }

    fun onNavigatedAway() {
        gameplayTimer.pause()
        flushSessionSave()
    }

    fun onReplayChallenge() {
        onReset()
    }

    private fun startTimerTicker() {
        if (timerJob?.isActive == true) return
        timerJob = viewModelScope.launch {
            while (isActive && gameplayTimer.isRunning) {
                delay(100L)
                val elapsed = gameplayTimer.elapsedDurationMs()
                val currentReady = _uiState.value as? DailyChallengeUiState.Ready
                if (currentReady != null && !currentReady.isPaused && !currentReady.gameState.isCompleted) {
                    _uiState.value = currentReady.copy(elapsedTimeMs = elapsed)
                }
            }
        }
    }

    private fun scheduleDebouncedSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(300L)
            flushSessionSave()
        }
    }

    private fun flushSessionSave() {
        val challenge = currentChallenge ?: return
        val currentEngine = engine ?: return
        val elapsed = gameplayTimer.elapsedDurationMs()

        viewModelScope.launch {
            sessionRepository.saveSession(
                GameplaySessionSnapshot(
                    sessionId = "daily_${challenge.challengeId}",
                    levelId = 0,
                    worldId = 0,
                    puzzleId = challenge.puzzleId,
                    puzzleVersion = challenge.puzzleVersion,
                    catalogVersion = challenge.scheduleVersion,
                    status = if (currentEngine.currentState.isCompleted) SessionStatus.COMPLETED else SessionStatus.PAUSED,
                    path = currentEngine.currentState.currentPath.positions,
                    elapsedActiveTimeMs = elapsed,
                    moveCount = currentEngine.currentState.moveCount,
                    startedAt = timeProvider.wallClockTimeMs() - elapsed,
                    lastUpdatedAt = timeProvider.wallClockTimeMs(),
                    revision = 1L,
                    snapshotSchemaVersion = 1
                )
            )
        }
    }

    fun claimDailyLoginReward() {
        val wallet = walletRepository ?: return
        viewModelScope.launch {
            val today = clock.currentUtcDateKey()
            val res = wallet.claimDailyLoginReward(today)
            if (res.isSuccess) {
                val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return@launch
                _uiState.value = currentReady.copy(
                    isDailyLoginClaimed = true
                )
            }
        }
    }

    fun claimDailyLoginAdBonus(activity: Activity) {
        val wallet = walletRepository ?: return
        val adMgr = coinRewardedAdManager ?: return
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        if (currentReady.isDailyLoginAdBonusClaimed) return

        adMgr.showCoinRewardedAd(
            activity = activity,
            onRewarded = {
                viewModelScope.launch {
                    val today = clock.currentUtcDateKey()
                    val res = wallet.claimDailyLoginAdBonus(today)
                    if (res.isSuccess) {
                        val latest = _uiState.value as? DailyChallengeUiState.Ready ?: return@launch
                        _uiState.value = latest.copy(
                            isDailyLoginAdBonusClaimed = true
                        )
                    }
                }
            },
            onFailed = {}
        )
    }

    fun claimDailyChallengeAdBonus(activity: Activity) {
        val wallet = walletRepository ?: return
        val adMgr = coinRewardedAdManager ?: return
        val currentReady = _uiState.value as? DailyChallengeUiState.Ready ?: return
        if (currentReady.isDailyChallengeAdBonusClaimed) return

        adMgr.showCoinRewardedAd(
            activity = activity,
            onRewarded = {
                viewModelScope.launch {
                    val today = clock.currentUtcDateKey()
                    val res = wallet.claimDailyChallengeAdBonus(today)
                    if (res.isSuccess) {
                        val latest = _uiState.value as? DailyChallengeUiState.Ready ?: return@launch
                        _uiState.value = latest.copy(
                            isDailyChallengeAdBonusClaimed = true
                        )
                    }
                }
            },
            onFailed = {}
        )
    }
}
