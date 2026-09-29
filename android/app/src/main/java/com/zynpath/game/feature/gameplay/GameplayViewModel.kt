package com.zynpath.game.feature.gameplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.Activity
import com.zynpath.game.core.ads.model.AdState
import com.zynpath.game.core.ads.model.RewardResult
import com.zynpath.game.core.ads.repository.NoOpRewardedAdRepository
import com.zynpath.game.core.ads.repository.RewardedAdRepository
import com.zynpath.game.core.database.repository.GameplaySessionRepository
import com.zynpath.game.core.database.repository.ProgressRepository
import com.zynpath.game.core.hint.HintPresentation
import com.zynpath.game.core.hint.HintSessionStatistics
import com.zynpath.game.core.hint.HintUsageRepository
import com.zynpath.game.core.puzzle.catalog.CatalogLoadResult
import com.zynpath.game.core.puzzle.catalog.LevelCatalogRepository
import com.zynpath.game.core.puzzle.engine.GameStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleAction
import com.zynpath.game.core.puzzle.engine.PuzzleEngine
import com.zynpath.game.core.puzzle.engine.PuzzleEngineResult
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.hint.GameMode
import com.zynpath.game.core.puzzle.hint.HintRequest
import com.zynpath.game.core.puzzle.hint.HintResult
import com.zynpath.game.core.puzzle.hint.HintType
import com.zynpath.game.core.puzzle.hint.PuzzleHintEngine
import com.zynpath.game.core.puzzle.model.GridPosition
import com.zynpath.game.core.puzzle.model.PuzzleBoardState
import com.zynpath.game.core.puzzle.model.PuzzleDefinition
import com.zynpath.game.core.puzzle.model.ValidatedCompletionResult
import com.zynpath.game.core.puzzle.session.GameplaySessionSnapshot
import com.zynpath.game.core.puzzle.session.GameplaySessionValidator
import com.zynpath.game.core.puzzle.session.GameplayTimer
import com.zynpath.game.core.puzzle.session.SessionRestorationResult
import com.zynpath.game.core.puzzle.session.SessionStatus
import com.zynpath.game.core.puzzle.session.SystemTimeProvider
import com.zynpath.game.core.puzzle.session.TimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import com.zynpath.game.core.audio.NoOpAudioManager
import com.zynpath.game.core.audio.ZynpathAudioManager
import com.zynpath.game.core.audio.model.ZynpathAudioEvent
import com.zynpath.game.core.haptics.NoOpHapticManager
import com.zynpath.game.core.haptics.ZynpathHapticManager
import com.zynpath.game.core.haptics.model.ZynpathHapticEvent
import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.datastore.UserPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.zynpath.game.core.error.ErrorClassifier
import com.zynpath.game.core.error.ZynpathDiagnostics
import com.zynpath.game.core.error.ZynpathError
import javax.inject.Inject

/**
 * Result of catalog resolution for the next sequential level (Prompt 15 Section 22).
 */
sealed class NextLevelResolution {
    data class Available(val worldId: Int, val levelId: Int) : NextLevelResolution()
    data class WorldComplete(val nextWorldId: Int) : NextLevelResolution()
    data class Locked(val worldId: Int, val levelId: Int) : NextLevelResolution()
    data class Unavailable(val levelId: Int, val message: String) : NextLevelResolution()
    object CatalogCompleted : NextLevelResolution()
}

sealed class GameplayUiState {
    object Loading : GameplayUiState()
    object Paused : GameplayUiState()

    data class Ready(
        val worldId: Int,
        val levelId: Int,
        val definition: PuzzleDefinition,
        val gameState: PuzzleGameState,
        val boardState: PuzzleBoardState,
        val elapsedTimeMs: Long = 0L,
        val isUndoAvailable: Boolean = gameState.currentPath.size > 1,
        val isResetAvailable: Boolean = !gameState.currentPath.isEmpty,
        val lastRejectionReason: MoveRejectionReason? = gameState.lastRejection,
        val completionResult: ValidatedCompletionResult? = gameState.completionResult,
        val isRestoredSession: Boolean = false,
        val sessionId: String = "",
        val activeHint: HintPresentation? = null,
        val isHintLoading: Boolean = false,
        val remainingHints: Int = 3,
        val isUnlimitedHints: Boolean = false,
        val hintMessage: String? = null,
        val showRecoveryDialog: Boolean = false,
        val showLimitReachedDialog: Boolean = false,
        val isRewardedAdAvailable: Boolean = false,
        val isAdRewardLoading: Boolean = false,
        val rewardedAdState: AdState = AdState.NOT_INITIALIZED,
        val rewardedAdError: String? = null,
        val dailyRewardedAdsRemaining: Int = 5,
        val rewardSnackbarMessage: String? = null,
        val isTapInputMode: Boolean = false,
        val isReducedMotion: Boolean = false,
        val isSfxEnabled: Boolean = true,
        val isHapticsEnabled: Boolean = true,
        val personalBestTimeMs: Long? = null,
        val isNewPersonalBest: Boolean = false,
        val lastReachedCheckpoint: Int? = null,
        val worldCelebration: com.zynpath.game.core.puzzle.model.WorldDefinition? = null,
        val starsEarned: Int = 0,
        val totalStars: Int = 0,
        val levelHintState: com.zynpath.game.core.puzzle.experience.hint.LevelHintState? = null,
        val experienceMetadata: com.zynpath.game.core.puzzle.experience.LevelExperienceMetadata? = null,
        val coinsEarned: Int = 0,
        val isWorldRewardBonusEligible: Boolean = false,
        val isRewardRunActive: Boolean = false,
        val undoCount: Int = 0,
        val resetCount: Int = 0
    ) : GameplayUiState() {
        val currentOrderedPath: List<GridPosition> get() = gameState.currentPath.positions
        val currentEndpoint: GridPosition? get() = gameState.currentEndpoint
        val nextRequiredCheckpoint: Int get() = gameState.nextRequiredCheckpoint
        val coveredCellCount: Int get() = gameState.coveredCellCount
        val totalRequiredCells: Int get() = definition.totalRequiredCells
        val gameStatus: GameStatus get() = gameState.gameStatus
        val isCompleted: Boolean get() = gameState.isCompleted
    }

    data class ContentUnavailable(
        val worldId: Int,
        val levelId: Int,
        val message: String
    ) : GameplayUiState()

    data class Error(
        val worldId: Int,
        val levelId: Int,
        val message: String
    ) : GameplayUiState()
}

/**
 * ViewModel managing active puzzle gameplay, session lifecycle, timer, touch actions, safe restoration,
 * and solution-aware hint analysis.
 *
 * Implements Prompts 12, 13, and 14:
 * - Durable Room session persistence with monotonic revision write ordering.
 * - Monotonic high-precision [GameplayTimer] with pause/resume idempotency.
 * - Solution-aware hint calculation off the main thread via [PuzzleHintEngine].
 * - Hint lifetime tied to game state revision with immediate invalidation upon movement.
 * - Free Solo hint allowance checking and atomic deduction via [HintUsageRepository].
 */
@HiltViewModel
class GameplayViewModel @Inject constructor(
    private val catalogRepository: LevelCatalogRepository,
    private val progressRepository: ProgressRepository,
    private val sessionRepository: GameplaySessionRepository,
    private val hintUsageRepository: HintUsageRepository,
    private val hintEngine: PuzzleHintEngine,
    private val timeProvider: TimeProvider,
    private val preferencesRepository: PreferencesRepository,
    private val rewardedAdRepository: RewardedAdRepository,
    savedStateHandle: SavedStateHandle,
    private val audioManager: ZynpathAudioManager = NoOpAudioManager(),
    private val hapticManager: ZynpathHapticManager = NoOpHapticManager(),
    private val levelHintRepository: com.zynpath.game.core.puzzle.experience.hint.LevelHintRepository? = null,
    private val rewardedHintAdContract: com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdContract? = null,
    val walletRepository: com.zynpath.game.core.economy.WalletRepository? = null,
    val coinRewardedAdManager: com.zynpath.game.core.ads.CoinRewardedAdManager? = null,
    val interstitialAdManager: com.zynpath.game.core.ads.InterstitialAdManager? = null
) : ViewModel() {

    // Overload for testing with levelHintRepository & rewardedHintAdContract
    constructor(
        catalogRepository: LevelCatalogRepository,
        progressRepository: ProgressRepository,
        sessionRepository: GameplaySessionRepository,
        hintUsageRepository: HintUsageRepository,
        hintEngine: PuzzleHintEngine,
        timeProvider: TimeProvider,
        savedStateHandle: SavedStateHandle,
        levelHintRepository: com.zynpath.game.core.puzzle.experience.hint.LevelHintRepository?,
        rewardedHintAdContract: com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdContract? = null
    ) : this(
        catalogRepository,
        progressRepository,
        sessionRepository,
        hintUsageRepository,
        hintEngine,
        timeProvider,
        InMemoryPreferencesRepository(),
        NoOpRewardedAdRepository(),
        savedStateHandle,
        NoOpAudioManager(),
        NoOpHapticManager(),
        levelHintRepository,
        rewardedHintAdContract
    )

    // Overload for testing or callers without explicit preferences repo
    constructor(
        catalogRepository: LevelCatalogRepository,
        progressRepository: ProgressRepository,
        sessionRepository: GameplaySessionRepository,
        hintUsageRepository: HintUsageRepository,
        hintEngine: PuzzleHintEngine,
        timeProvider: TimeProvider,
        savedStateHandle: SavedStateHandle
    ) : this(
        catalogRepository,
        progressRepository,
        sessionRepository,
        hintUsageRepository,
        hintEngine,
        timeProvider,
        InMemoryPreferencesRepository(),
        NoOpRewardedAdRepository(),
        savedStateHandle
    )

    constructor(
        catalogRepository: LevelCatalogRepository,
        progressRepository: ProgressRepository,
        sessionRepository: GameplaySessionRepository,
        timeProvider: TimeProvider,
        savedStateHandle: SavedStateHandle
    ) : this(
        catalogRepository,
        progressRepository,
        sessionRepository,
        InMemoryHintUsageRepository(),
        PuzzleHintEngine(),
        timeProvider,
        InMemoryPreferencesRepository(),
        NoOpRewardedAdRepository(),
        savedStateHandle
    )

    constructor(
        catalogRepository: LevelCatalogRepository,
        progressRepository: ProgressRepository,
        sessionRepository: GameplaySessionRepository,
        savedStateHandle: SavedStateHandle
    ) : this(
        catalogRepository,
        progressRepository,
        sessionRepository,
        InMemoryHintUsageRepository(),
        PuzzleHintEngine(),
        SystemTimeProvider(),
        InMemoryPreferencesRepository(),
        NoOpRewardedAdRepository(),
        savedStateHandle
    ) {
        isTimerTickerEnabled = false
    }

    constructor(
        catalogRepository: LevelCatalogRepository,
        progressRepository: ProgressRepository,
        savedStateHandle: SavedStateHandle,
        sessionRepository: GameplaySessionRepository
    ) : this(
        catalogRepository,
        progressRepository,
        sessionRepository,
        savedStateHandle
    ) {
        isTimerTickerEnabled = false
    }

    constructor(
        catalogRepository: LevelCatalogRepository,
        progressRepository: ProgressRepository,
        savedStateHandle: SavedStateHandle
    ) : this(
        catalogRepository,
        progressRepository,
        object : GameplaySessionRepository {
            override suspend fun getLatestResumableSession(): GameplaySessionSnapshot? = null
            override suspend fun getResumableSession(levelId: Int): GameplaySessionSnapshot? = null
            override suspend fun getSessionById(sessionId: String): GameplaySessionSnapshot? = null
            override suspend fun saveSession(snapshot: GameplaySessionSnapshot): Boolean = true
            override suspend fun pauseAllActiveSessions(timestamp: Long) {}
            override suspend fun markCompleted(sessionId: String, elapsedActiveTimeMs: Long, nowMs: Long): Boolean = true
            override suspend fun markRestorationFailed(sessionId: String, errorDetail: String, nowMs: Long): Boolean = true
            override suspend fun abandonSession(sessionId: String, nowMs: Long): Boolean = true
            override suspend fun deleteSessionsForLevel(levelId: Int) {}
            override suspend fun deleteSessionById(sessionId: String) {}
            override suspend fun clearAllSessions() {}
        },
        InMemoryHintUsageRepository(),
        PuzzleHintEngine(),
        SystemTimeProvider(),
        InMemoryPreferencesRepository(),
        NoOpRewardedAdRepository(),
        savedStateHandle
    ) {
        isTimerTickerEnabled = false
    }

    private val rawWorldId: Int = savedStateHandle.get<Int>("worldId") ?: 1
    private val rawLevelId: Int = savedStateHandle.get<Int>("levelId") ?: 1
    val levelId: Int = if (rawLevelId in 1..com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorld(rawWorldId).totalLevels &&
        rawLevelId !in com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorld(rawWorldId).levelRange) {
        com.zynpath.game.core.puzzle.model.WorldConfiguration.toGlobalLevel(rawWorldId, rawLevelId)
    } else {
        rawLevelId
    }
    val worldId: Int = com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorldForLevel(levelId).worldId

    private val _uiState = MutableStateFlow<GameplayUiState>(GameplayUiState.Loading)
    val uiState: StateFlow<GameplayUiState> = _uiState.asStateFlow()

    private var engine: PuzzleEngine? = null
    private var isCompletionHandled: Boolean = false
    private var timerJob: Job? = null
    private var saveJob: Job? = null
    private var hintJob: Job? = null
    private val _elapsedTimeMs = MutableStateFlow(0L)
    val elapsedTimeMs: StateFlow<Long> = _elapsedTimeMs.asStateFlow()
    private var lastReadyState: GameplayUiState.Ready? = null
    var hintDispatcher: CoroutineDispatcher = Dispatchers.Default
    private var isHintComputing: Boolean = false
    var isTimerTickerEnabled: Boolean = true

    fun onPauseClicked() {
        val ready = (_uiState.value as? GameplayUiState.Ready)
        if (ready != null) {
            lastReadyState = ready
        }
        onPauseGame()
        _uiState.value = GameplayUiState.Paused
    }

    fun onResumeClicked() {
        val ready = lastReadyState
        if (ready != null) {
            _uiState.value = ready
        }
        onResumeGame()
    }

    private var currentSnapshot: GameplaySessionSnapshot? = null
    private var currentRevision: Long = 1L
    private var gameplayTimer: GameplayTimer = GameplayTimer(timeProvider, 0L)
    private val hintSessionStats = HintSessionStatistics()
    private var undoCount: Int = 0
    private var resetCount: Int = 0
    private var isRewardRun: Boolean = true

    init {
        observePreferences()
        observeRewardedAds()
        observeLevelHints()
        rewardedAdRepository.preloadRewardedAd()
        rewardedHintAdContract?.preloadRewardedAd()
        loadLevel()
    }

    private fun observeLevelHints() {
        val repo = levelHintRepository ?: return
        viewModelScope.launch {
            repo.observeLevelHintState(levelId).collectLatest { levelHint ->
                val currentReady = _uiState.value as? GameplayUiState.Ready ?: return@collectLatest
                _uiState.value = currentReady.copy(
                    levelHintState = levelHint,
                    remainingHints = if (currentReady.isUnlimitedHints) 999 else levelHint.totalAvailableHints
                )
            }
        }
    }

    private fun observeRewardedAds() {
        val hintContract = rewardedHintAdContract
        if (hintContract != null) {
            viewModelScope.launch {
                hintContract.adState.collectLatest { adState ->
                    val currentReady = _uiState.value as? GameplayUiState.Ready ?: return@collectLatest
                    val isHintAdReady = adState == AdState.READY || hintContract.isRewardedAdReady()
                    _uiState.value = currentReady.copy(
                        rewardedAdState = adState,
                        isRewardedAdAvailable = isHintAdReady,
                        isAdRewardLoading = (currentReady.isAdRewardLoading && adState == AdState.SHOWING) || adState == AdState.LOADING
                    )
                }
            }
            viewModelScope.launch {
                hintContract.lastLoadError.collectLatest { error ->
                    val currentReady = _uiState.value as? GameplayUiState.Ready ?: return@collectLatest
                    _uiState.value = currentReady.copy(
                        rewardedAdError = error
                    )
                }
            }
        } else {
            viewModelScope.launch {
                rewardedAdRepository.adState.collectLatest { adState ->
                    val currentReady = _uiState.value as? GameplayUiState.Ready ?: return@collectLatest
                    val isHintAdReady = adState == AdState.READY
                    _uiState.value = currentReady.copy(
                        rewardedAdState = adState,
                        isRewardedAdAvailable = isHintAdReady,
                        isAdRewardLoading = currentReady.isAdRewardLoading || adState == AdState.LOADING
                    )
                }
            }
        }
        viewModelScope.launch {
            rewardedAdRepository.dailyRewardedAdsRemaining.collectLatest { remaining ->
                val currentReady = _uiState.value as? GameplayUiState.Ready ?: return@collectLatest
                _uiState.value = currentReady.copy(
                    dailyRewardedAdsRemaining = remaining
                )
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.userPreferencesFlow.collectLatest { prefs ->
                val currentReady = _uiState.value as? GameplayUiState.Ready ?: return@collectLatest
                _uiState.value = currentReady.copy(
                    isTapInputMode = prefs.isTapInputMode,
                    isReducedMotion = prefs.isReducedMotion,
                    isSfxEnabled = prefs.isSfxEnabled,
                    isHapticsEnabled = prefs.isHapticsEnabled
                )
            }
        }
    }

    fun loadLevel() {
        viewModelScope.launch {
            _uiState.value = GameplayUiState.Loading
            timerJob?.cancel()
            saveJob?.cancel()
            hintJob?.cancel()
            hintSessionStats.reset()
            _elapsedTimeMs.value = 0L
            isCompletionHandled = false

            // 1. Confirm level exists in catalog
            val levelDef = catalogRepository.getLevel(levelId)
            if (levelDef == null) {
                _uiState.value = GameplayUiState.Error(
                    worldId = worldId,
                    levelId = levelId,
                    message = "Level $levelId does not exist in catalog"
                )
                return@launch
            }

            // 2. Confirm level is unlocked by progression
            val isUnlocked = progressRepository.isLevelUnlocked(levelId)
            if (!isUnlocked) {
                _uiState.value = GameplayUiState.Error(
                    worldId = worldId,
                    levelId = levelId,
                    message = "Level $levelId is locked by progression"
                )
                return@launch
            }

            // 3. Load puzzle asset
            when (val loadResult = catalogRepository.loadPuzzle(levelId)) {
                is CatalogLoadResult.Success -> {
                    val definition = loadResult.definition

                    // Enforce Prompt 13 Section 10: Pause any other active sessions
                    sessionRepository.pauseAllActiveSessions(timeProvider.wallClockTimeMs())

                    val remainingHints = hintUsageRepository.getRemainingHints()
                    val isUnlimited = hintUsageRepository.isPremium()
                    val experienceMetadata = com.zynpath.game.core.puzzle.experience.ProgressionPlan.getLevelExperience(levelId)
                    val levelHintState = levelHintRepository?.getLevelHintState(levelId)
                        ?: com.zynpath.game.core.puzzle.experience.hint.LevelHintState.initial(levelId)

                    // Load user preferences and persisted personal best and stars
                    val userPrefs = preferencesRepository.userPreferencesFlow.firstOrNull() ?: UserPreferences()
                    val existingProgress = progressRepository.observeLevelProgress(levelId).firstOrNull()
                    val personalBest = existingProgress?.bestTimeMs?.takeIf { it > 0L }
                    val starsEarned = existingProgress?.stars ?: 0
                    val totalStars = progressRepository.getTotalStarsEarned().firstOrNull() ?: 0

                    // Solo Economy: Manage paid attempt and Reward Run vs Free Practice
                    if (levelId >= 4 && walletRepository != null) {
                        val alreadyActive = walletRepository.isLevelPaidAttemptActive(levelId)
                        val alreadyCompleted = existingProgress?.isCompleted == true
                        if (alreadyActive) {
                            isRewardRun = true
                        } else if (!alreadyCompleted && walletRepository.canAfford(com.zynpath.game.core.economy.EconomyConfig.SOLO_REWARD_RUN_ENTRY_FEE)) {
                            val entryRes = walletRepository.commitSoloRewardRunEntry(levelId)
                            isRewardRun = entryRes.isSuccess
                        } else {
                            isRewardRun = false // Free Practice
                        }
                    } else {
                        isRewardRun = true
                    }
                    undoCount = 0
                    resetCount = 0

                    // Check for existing resumable session
                    val existingSession = sessionRepository.getResumableSession(levelId)
                    if (existingSession != null) {
                        val restorationResult = GameplaySessionValidator.validateAndReplay(
                            snapshot = existingSession,
                            definition = definition,
                            expectedLevelId = levelId
                        )

                        when (restorationResult) {
                            is SessionRestorationResult.Success -> {
                                val restoredEngine = restorationResult.restoredEngine
                                engine = restoredEngine
                                currentSnapshot = existingSession
                                currentRevision = existingSession.revision
                                gameplayTimer = GameplayTimer(timeProvider, existingSession.elapsedActiveTimeMs)
                                _elapsedTimeMs.value = existingSession.elapsedActiveTimeMs

                                val isPaused = existingSession.status == SessionStatus.PAUSED
                                if (!isPaused && restoredEngine.currentState.isInProgress) {
                                    gameplayTimer.start()
                                    startTimerTicker()
                                }

                                val initialGameState = restoredEngine.currentState
                                _uiState.value = GameplayUiState.Ready(
                                    worldId = worldId,
                                    levelId = levelId,
                                    definition = definition,
                                    gameState = initialGameState,
                                    boardState = initialGameState.toBoardState(),
                                    elapsedTimeMs = existingSession.elapsedActiveTimeMs,
                                    isUndoAvailable = initialGameState.currentPath.size > 1,
                                    isResetAvailable = !initialGameState.currentPath.isEmpty,
                                    isRestoredSession = existingSession.path.isNotEmpty(),
                                    sessionId = existingSession.sessionId,
                                    remainingHints = remainingHints,
                                    isUnlimitedHints = isUnlimited,
                                    isTapInputMode = userPrefs.isTapInputMode,
                                    isReducedMotion = userPrefs.isReducedMotion,
                                    isSfxEnabled = userPrefs.isSfxEnabled,
                                    isHapticsEnabled = userPrefs.isHapticsEnabled,
                                    personalBestTimeMs = personalBest,
                                    starsEarned = starsEarned,
                                    totalStars = totalStars,
                                    levelHintState = levelHintState,
                                    experienceMetadata = experienceMetadata,
                                    coinsEarned = 0,
                                    isWorldRewardBonusEligible = false,
                                    isRewardRunActive = isRewardRun,
                                    undoCount = undoCount,
                                    resetCount = resetCount
                                )
                                return@launch
                            }
                            is SessionRestorationResult.Invalid -> {
                                sessionRepository.markRestorationFailed(
                                    existingSession.sessionId,
                                    restorationResult.detailMessage,
                                    timeProvider.wallClockTimeMs()
                                )
                                ZynpathDiagnostics.recordError(
                                    eventType = "SESSION_RESTORATION_FAILED",
                                    error = ZynpathError.corruptSession(),
                                    additionalMetadata = mapOf(
                                        "levelId" to levelId.toString(),
                                        "reason" to restorationResult.reason.name,
                                        "detail" to restorationResult.detailMessage
                                    )
                                )
                            }
                            is SessionRestorationResult.NotFound -> {
                                // Fall through to initialize fresh clean session
                            }
                        }
                    }

                    // Create fresh session
                    val newSnapshot = GameplaySessionSnapshot.createNew(
                        levelId = levelId,
                        worldId = worldId,
                        puzzleId = definition.puzzleId,
                        puzzleVersion = definition.puzzleVersion,
                        nowMs = timeProvider.wallClockTimeMs()
                    )
                    currentSnapshot = newSnapshot
                    currentRevision = newSnapshot.revision
                    sessionRepository.saveSession(newSnapshot)

                    val newEngine = PuzzleEngine(definition, levelId = levelId, worldId = worldId)
                    engine = newEngine
                    gameplayTimer = GameplayTimer(timeProvider, 0L)

                    val initialGameState = newEngine.currentState
                    val boardState = initialGameState.toBoardState()
                    _uiState.value = GameplayUiState.Ready(
                        worldId = worldId,
                        levelId = levelId,
                        definition = definition,
                        gameState = initialGameState,
                        boardState = boardState,
                        elapsedTimeMs = 0L,
                        isUndoAvailable = false,
                        isResetAvailable = false,
                        isRestoredSession = false,
                        sessionId = newSnapshot.sessionId,
                        remainingHints = remainingHints,
                        isUnlimitedHints = isUnlimited,
                        isTapInputMode = userPrefs.isTapInputMode,
                        isReducedMotion = userPrefs.isReducedMotion,
                        isSfxEnabled = userPrefs.isSfxEnabled,
                        isHapticsEnabled = userPrefs.isHapticsEnabled,
                        personalBestTimeMs = personalBest,
                        starsEarned = starsEarned,
                        totalStars = totalStars,
                        levelHintState = levelHintState,
                        experienceMetadata = experienceMetadata,
                        coinsEarned = 0,
                        isWorldRewardBonusEligible = false,
                        isRewardRunActive = isRewardRun,
                        undoCount = undoCount,
                        resetCount = resetCount
                    )
                }
                is CatalogLoadResult.AssetUnavailable -> {
                    _uiState.value = GameplayUiState.ContentUnavailable(
                        worldId = worldId,
                        levelId = levelId,
                        message = "Level $levelId content is pending future catalog expansion"
                    )
                }
                is CatalogLoadResult.AssetInvalid -> {
                    _uiState.value = GameplayUiState.Error(
                        worldId = worldId,
                        levelId = levelId,
                        message = "Asset integrity validation failed: ${loadResult.errorSummary}"
                    )
                }
                is CatalogLoadResult.LevelNotFound -> {
                    _uiState.value = GameplayUiState.Error(
                        worldId = worldId,
                        levelId = levelId,
                        message = "Level $levelId was not found"
                    )
                }
            }
        }
    }

    /**
     * Handles touch input when pointer enters [position].
     * Returns true if the action was accepted by the engine, or false if rejected.
     */
    fun onCellEntered(position: GridPosition): Boolean {
        val currentReady = (_uiState.value as? GameplayUiState.Ready) ?: return false
        val activeEngine = engine ?: return false
        val currentState = activeEngine.currentState

        if (currentState.isCompleted || currentState.isPaused) {
            return false
        }

        val currentElapsed = gameplayTimer.elapsedDurationMs()

        // 1. If path is empty, must start at checkpoint #1
        if (currentState.isNotStarted || currentState.currentPath.isEmpty) {
            val result = activeEngine.process(PuzzleAction.StartPath(position), currentElapsed)
            return if (result.isAccepted) {
                audioManager.playEvent(ZynpathAudioEvent.PATH_START)
                hapticManager.performHaptic(ZynpathHapticEvent.PATH_START)
                gameplayTimer.start()
                startTimerTicker()

                currentRevision++
                hintJob?.cancel()

                currentSnapshot = currentSnapshot?.copy(
                    status = SessionStatus.ACTIVE,
                    path = result.state.currentPath.positions,
                    moveCount = 1,
                    elapsedActiveTimeMs = currentElapsed,
                    revision = currentRevision,
                    lastUpdatedAt = timeProvider.wallClockTimeMs()
                )
                flushSessionSave()

                _uiState.value = currentReady.copy(
                    gameState = result.state,
                    boardState = result.state.toBoardState(hintedCoordinate = null),
                    isUndoAvailable = result.state.currentPath.size > 1,
                    isResetAvailable = !result.state.currentPath.isEmpty,
                    lastRejectionReason = null,
                    activeHint = null,
                    isHintLoading = false,
                    hintMessage = null,
                    showRecoveryDialog = false
                )
                true
            } else {
                audioManager.playEvent(ZynpathAudioEvent.INVALID_MOVE)
                hapticManager.performHaptic(ZynpathHapticEvent.INVALID_MOVE)
                val reason = (result as? PuzzleEngineResult.Rejected)?.reason
                _uiState.value = currentReady.copy(lastRejectionReason = reason)
                false
            }
        }

        // 2. Tapping or dragging on current endpoint: no-op accepted
        if (position == currentState.currentEndpoint) {
            return true
        }

        // 3. Backtracking: Dragging back to an earlier cell already on the active path
        if (currentState.currentPath.contains(position)) {
            val result = activeEngine.process(PuzzleAction.BacktrackTo(position), currentElapsed)
            return if (result.isAccepted) {
                audioManager.playEvent(ZynpathAudioEvent.UNDO)
                hapticManager.performHaptic(ZynpathHapticEvent.UNDO)
                undoCount++
                currentRevision++
                hintJob?.cancel()

                currentSnapshot = currentSnapshot?.copy(
                    path = result.state.currentPath.positions,
                    moveCount = result.state.moveCount,
                    elapsedActiveTimeMs = currentElapsed,
                    revision = currentRevision,
                    lastUpdatedAt = timeProvider.wallClockTimeMs()
                )
                flushSessionSave()

                _uiState.value = currentReady.copy(
                    gameState = result.state,
                    boardState = result.state.toBoardState(hintedCoordinate = null),
                    isUndoAvailable = result.state.currentPath.size > 1,
                    isResetAvailable = !result.state.currentPath.isEmpty,
                    lastRejectionReason = null,
                    activeHint = null,
                    isHintLoading = false,
                    hintMessage = null,
                    showRecoveryDialog = false,
                    undoCount = undoCount
                )
                true
            } else {
                false
            }
        }

        // 4. Forward move
        val result = activeEngine.process(PuzzleAction.ExtendPath(position), currentElapsed)
        return if (result.isAccepted) {
            val isCompleted = result.state.isCompleted
            currentRevision++
            hintJob?.cancel()

            val isCheckpointReached = currentReady.definition.getCheckpointAt(position) != null
            val checkpointNumber = if (isCheckpointReached) currentReady.definition.getCheckpointAt(position) else null

            if (isCompleted) {
                audioManager.playEvent(ZynpathAudioEvent.PUZZLE_COMPLETED)
                hapticManager.performHaptic(ZynpathHapticEvent.COMPLETION)
            } else if (isCheckpointReached) {
                audioManager.playEvent(ZynpathAudioEvent.CHECKPOINT_REACHED)
                hapticManager.performHaptic(ZynpathHapticEvent.CHECKPOINT_REACHED)
            } else {
                audioManager.playEvent(ZynpathAudioEvent.VALID_MOVE)
                hapticManager.performHaptic(ZynpathHapticEvent.VALID_MOVE)
            }

            currentSnapshot = currentSnapshot?.copy(
                path = result.state.currentPath.positions,
                moveCount = result.state.moveCount,
                elapsedActiveTimeMs = currentElapsed,
                revision = currentRevision,
                lastUpdatedAt = timeProvider.wallClockTimeMs()
            )

            if (isCheckpointReached || isCompleted) {
                flushSessionSave()
            } else {
                scheduleDebouncedSave()
            }

            _uiState.value = currentReady.copy(
                gameState = result.state,
                boardState = result.state.toBoardState(hintedCoordinate = null),
                isUndoAvailable = result.state.currentPath.size > 1,
                isResetAvailable = !result.state.currentPath.isEmpty,
                lastRejectionReason = null,
                lastReachedCheckpoint = checkpointNumber,
                completionResult = if (isCompleted) null else result.state.completionResult,
                activeHint = null,
                isHintLoading = false,
                hintMessage = null,
                showRecoveryDialog = false
            )

            if (isCompleted) {
                handleValidatedCompletion(result.state.completionResult)
            }
            true
        } else {
            audioManager.playEvent(ZynpathAudioEvent.INVALID_MOVE)
            hapticManager.performHaptic(ZynpathHapticEvent.INVALID_MOVE)
            val reason = (result as? PuzzleEngineResult.Rejected)?.reason
            _uiState.value = currentReady.copy(
                lastRejectionReason = reason,
                lastReachedCheckpoint = null
            )
            false
        }
    }

    /**
     * Retracts to a specific earlier position on the path (Prompt 14 Section 15 & 16).
     */
    fun onConfirmRollback(targetPosition: GridPosition) {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        val activeEngine = engine ?: return
        dismissRecoveryDialog()

        val path = activeEngine.currentState.currentPath.positions
        val targetIndex = path.indexOf(targetPosition)
        if (targetIndex < 0 || targetIndex >= path.size - 1) return

        val stepsToRetract = path.size - 1 - targetIndex
        val currentElapsed = gameplayTimer.elapsedDurationMs()
        var latestState = activeEngine.currentState
        var success = false
        for (i in 0 until stepsToRetract) {
            val res = activeEngine.process(PuzzleAction.BacktrackOne, currentElapsed)
            if (res.isAccepted) {
                latestState = res.state
                success = true
            } else {
                success = false
                break
            }
        }

        if (success) {
            val stepsToRetract = (path.size - 1) - targetIndex
            undoCount += stepsToRetract
            audioManager.playEvent(ZynpathAudioEvent.UNDO)
            hapticManager.performHaptic(ZynpathHapticEvent.UNDO)
            currentRevision++
            hintJob?.cancel()
            currentSnapshot = currentSnapshot?.copy(
                path = latestState.currentPath.positions,
                moveCount = latestState.moveCount,
                elapsedActiveTimeMs = currentElapsed,
                revision = currentRevision,
                lastUpdatedAt = timeProvider.wallClockTimeMs()
            )
            flushSessionSave()

            _uiState.value = currentReady.copy(
                gameState = latestState,
                boardState = latestState.toBoardState(hintedCoordinate = null),
                isUndoAvailable = latestState.currentPath.size > 1,
                isResetAvailable = !latestState.currentPath.isEmpty,
                lastRejectionReason = null,
                activeHint = null,
                isHintLoading = false,
                hintMessage = null,
                showRecoveryDialog = false,
                undoCount = undoCount
            )
        }
    }

    fun onRetractTo(targetPosition: GridPosition) = onConfirmRollback(targetPosition)

    /**
     * Requests a solution-aware hint for the current gameplay state.
     *
     * Implements Prompt 14:
     * - Respects current path and checks solvability continuation (Sections 9 & 10).
     * - Reuses already visible hint for same state without re-consuming (Section 27).
     * - Verifies free allowance or premium entitlement before search (Sections 22 & 23).
     * - Consumes hint ONLY when valid NEXT_MOVE or RECOVERY_REQUIRED is delivered (Section 26).
     * - Operates off the Android main thread (Section 19).
     */
    fun requestHint() {
        val currentReady = (_uiState.value as? GameplayUiState.Ready) ?: return
        val activeEngine = engine ?: return

        // 1. Guard against completed games or already active hint requests (synchronous debouncing)
        if (currentReady.isCompleted || isHintComputing || currentReady.isHintLoading) {
            return
        }

        // 2. Reuse active hint without re-consuming if state has not changed (Section 27)
        if (currentReady.activeHint != null && currentReady.activeHint.stateRevision == currentRevision) {
            return
        }

        isHintComputing = true
        val revisionAtRequest = currentRevision
        _uiState.value = currentReady.copy(
            isHintLoading = true,
            hintMessage = null
        )

        hintJob?.cancel()
        hintJob = viewModelScope.launch(hintDispatcher) {
            try {
                // 3. Entitlement & Allowance check (Prompt 25 & Prompt 27: 2 free hints per level)
                val isPremium = hintUsageRepository.isPremium()
                val (canConsume, remaining, levelHint) = if (levelHintRepository != null) {
                    val state = levelHintRepository.getLevelHintState(levelId)
                    val can = isPremium || state.canConsumeHint
                    val rem = if (isPremium) 999 else state.totalAvailableHints
                    Triple(can, rem, state)
                } else {
                    val can = hintUsageRepository.canConsumeHint()
                    val rem = if (isPremium) 999 else hintUsageRepository.getRemainingHints()
                    Triple(can, rem, com.zynpath.game.core.puzzle.experience.hint.LevelHintState.initial(levelId))
                }

                if (!canConsume) {
                    val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                    _uiState.value = latestReady.copy(
                        isHintLoading = false,
                        showLimitReachedDialog = true,
                        remainingHints = 0,
                        isUnlimitedHints = false,
                        levelHintState = levelHint
                    )
                    return@launch
                }

                _uiState.value = (_uiState.value as? GameplayUiState.Ready)?.copy(
                    remainingHints = remaining,
                    isUnlimitedHints = isPremium,
                    levelHintState = levelHint
                ) ?: _uiState.value

                hintSessionStats.recordRequest()

                // 4. Asynchronous hint computation
                val request = HintRequest(
                    puzzleId = currentReady.definition.puzzleId,
                    puzzleVersion = currentReady.definition.puzzleVersion,
                    definition = currentReady.definition,
                    gameState = activeEngine.currentState,
                    currentOrderedPath = activeEngine.currentState.currentPath.positions,
                    nextRequiredCheckpoint = activeEngine.currentState.nextRequiredCheckpoint,
                    gameMode = GameMode.SOLO
                )

                val result = hintEngine.computeHintAsync(request, hintDispatcher)

                // Discard stale result if player moved or state changed during computation (Section 18)
                if (currentRevision != revisionAtRequest) {
                    return@launch
                }

                when (result) {
                    is HintResult.NextMove -> {
                        // Section 26: Consume hint only upon successful delivery of verified result
                        hintUsageRepository.consumeHint()
                        levelHintRepository?.consumeHint(levelId)
                        val updatedLevelHint = levelHintRepository?.getLevelHintState(levelId)
                        hintSessionStats.recordDelivery(isRecovery = false)
                        audioManager.playEvent(ZynpathAudioEvent.HINT_USED)
                        val newRemaining = hintUsageRepository.getRemainingHints()

                        val presentation = HintPresentation(
                            type = HintType.NEXT_MOVE,
                            targetCell = result.nextMove,
                            stateRevision = revisionAtRequest
                        )

                        val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latestReady.copy(
                            isHintLoading = false,
                            activeHint = presentation,
                            boardState = latestReady.gameState.toBoardState(hintedCoordinate = result.nextMove),
                            remainingHints = newRemaining,
                            hintMessage = null,
                            levelHintState = updatedLevelHint ?: latestReady.levelHintState
                        )
                    }

                    is HintResult.RecoveryRequired -> {
                        hintUsageRepository.consumeHint()
                        levelHintRepository?.consumeHint(levelId)
                        val updatedLevelHint = levelHintRepository?.getLevelHintState(levelId)
                        hintSessionStats.recordDelivery(isRecovery = true)
                        audioManager.playEvent(ZynpathAudioEvent.HINT_USED)
                        val newRemaining = hintUsageRepository.getRemainingHints()

                        val presentation = HintPresentation(
                            type = HintType.RECOVERY_REQUIRED,
                            targetCell = null,
                            explanation = result.explanation,
                            stepsToRetract = result.stepsToRetract,
                            rollbackPosition = result.recommendedRollbackPosition,
                            stateRevision = revisionAtRequest
                        )

                        val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latestReady.copy(
                            isHintLoading = false,
                            activeHint = presentation,
                            hintMessage = result.explanation,
                            showRecoveryDialog = true,
                            remainingHints = newRemaining,
                            levelHintState = updatedLevelHint ?: latestReady.levelHintState
                        )
                    }

                    is HintResult.SearchInconclusive -> {
                        val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latestReady.copy(
                            isHintLoading = false,
                            hintMessage = result.message
                        )
                    }

                    is HintResult.UsageLimitReached -> {
                        val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latestReady.copy(
                            isHintLoading = false,
                            showLimitReachedDialog = true,
                            remainingHints = 0
                        )
                    }

                    is HintResult.HintNotAvailable -> {
                        val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latestReady.copy(
                            isHintLoading = false,
                            hintMessage = result.reason
                        )
                    }

                    is HintResult.AlreadyCompleted -> {
                        val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latestReady.copy(
                            isHintLoading = false,
                            hintMessage = "Puzzle is already completed"
                        )
                    }

                    is HintResult.InvalidState -> {
                        val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latestReady.copy(
                            isHintLoading = false,
                            hintMessage = result.reason
                        )
                    }

                    is HintResult.Cancelled -> {
                        val latestReady = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latestReady.copy(isHintLoading = false)
                    }
                }
            } finally {
                isHintComputing = false
            }
        }
    }

    fun dismissRecoveryDialog() {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        _uiState.value = currentReady.copy(showRecoveryDialog = false)
    }

    fun dismissLimitReachedDialog() {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        _uiState.value = currentReady.copy(showLimitReachedDialog = false)
    }

    fun retryLoadingRewardedAd() {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        _uiState.value = currentReady.copy(
            isAdRewardLoading = true,
            rewardedAdError = null
        )
        if (rewardedHintAdContract != null) {
            rewardedHintAdContract.retryLoadingAd()
        } else {
            rewardedAdRepository.preloadRewardedAd()
        }
    }

    fun watchRewardedAdForHint(activity: Activity) {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        if (currentReady.isUnlimitedHints) return
        if (currentReady.isAdRewardLoading) return // Guard against duplicate rapid clicks (Task 8)

        viewModelScope.launch {
            _uiState.value = currentReady.copy(isAdRewardLoading = true)

            val hintContract = rewardedHintAdContract
            if (hintContract != null) {
                hintContract.showRewardedHintAd(
                    activity,
                    levelId,
                    object : com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdCallback {
                        override fun onRewardConfirmed(success: com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult.Success) {
                            viewModelScope.launch {
                                val recorded = levelHintRepository?.recordConfirmedReward(levelId, success.rewardClaimId) ?: false
                                val updatedLevelHint = levelHintRepository?.getLevelHintState(levelId)
                                val newRemaining = hintUsageRepository.getRemainingHints()
                                val latest = _uiState.value as? GameplayUiState.Ready ?: return@launch
                                _uiState.value = latest.copy(
                                    isAdRewardLoading = false,
                                    remainingHints = newRemaining,
                                    levelHintState = updatedLevelHint ?: latest.levelHintState,
                                    showLimitReachedDialog = false,
                                    rewardSnackbarMessage = if (recorded) "+1 Hint credit earned for Level $levelId!" else "Hint reward already credited."
                                )
                            }
                        }

                        override fun onRewardDenied(denial: com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult) {
                            viewModelScope.launch {
                                val latest = _uiState.value as? GameplayUiState.Ready ?: return@launch
                                val msg = when (denial) {
                                    is com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult.Skipped -> "Ad closed before completion. No hint earned."
                                    is com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult.Unavailable -> "No video available right now. Please try again later."
                                    is com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult.Failed -> "No video available right now. Please try again later."
                                    is com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult.Cancelled -> "Ad playback cancelled."
                                    is com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult.MissingCallback -> "No video confirmation received. Please try again."
                                    is com.zynpath.game.core.puzzle.experience.hint.RewardedHintAdResult.Success -> null
                                }
                                _uiState.value = latest.copy(
                                    isAdRewardLoading = false,
                                    rewardSnackbarMessage = msg
                                )
                            }
                        }
                    }
                )
            } else {
                when (val result = rewardedAdRepository.showRewardedAd(activity)) {
                    is RewardResult.Success -> {
                        val claimId = "lvl_${levelId}_ad_claim_${java.util.UUID.randomUUID()}"
                        val recorded = levelHintRepository?.recordConfirmedReward(levelId, claimId) ?: false
                        val updatedLevelHint = levelHintRepository?.getLevelHintState(levelId)
                        val newRemaining = hintUsageRepository.getRemainingHints()
                        val latest = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latest.copy(
                            isAdRewardLoading = false,
                            remainingHints = newRemaining,
                            levelHintState = updatedLevelHint ?: latest.levelHintState,
                            showLimitReachedDialog = false,
                            rewardSnackbarMessage = if (recorded) "+1 Hint credit earned for Level $levelId!" else "Hint reward already credited."
                        )
                    }
                    is RewardResult.DismissedWithoutReward -> {
                        val latest = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latest.copy(
                            isAdRewardLoading = false,
                            rewardSnackbarMessage = "Ad closed before completion. No hint earned."
                        )
                    }
                    is RewardResult.LimitReached -> {
                        val latest = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latest.copy(
                            isAdRewardLoading = false,
                            rewardSnackbarMessage = "Daily limit reached for rewarded ads (5/day)."
                        )
                    }
                    is RewardResult.Unavailable, is RewardResult.Failed -> {
                        val latest = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latest.copy(
                            isAdRewardLoading = false,
                            rewardSnackbarMessage = "No video available right now. Please try again later."
                        )
                    }
                    is RewardResult.BlockedByPolicy -> {
                        val latest = _uiState.value as? GameplayUiState.Ready ?: return@launch
                        _uiState.value = latest.copy(
                            isAdRewardLoading = false,
                            rewardSnackbarMessage = "Ads are disabled by policy."
                        )
                    }
                }
            }
        }
    }

    fun clearRewardSnackbar() {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        _uiState.value = currentReady.copy(rewardSnackbarMessage = null)
    }

    fun getHintStatistics(): HintSessionStatistics = hintSessionStats

    /**
     * Called when user lifts finger from puzzle board.
     * Ensures any pending debounced path changes are immediately flushed to Room.
     */
    fun onPointerReleased() {
        flushSessionSave()
    }

    /**
     * Retracts the path by one step (Undo).
     */
    fun onUndoClicked() {
        val currentReady = (_uiState.value as? GameplayUiState.Ready) ?: return
        val activeEngine = engine ?: return

        if (!currentReady.isUndoAvailable) return

        val currentElapsed = gameplayTimer.elapsedDurationMs()
        val result = activeEngine.process(PuzzleAction.BacktrackOne, currentElapsed)
        if (result.isAccepted) {
            undoCount++
            audioManager.playEvent(ZynpathAudioEvent.UNDO)
            hapticManager.performHaptic(ZynpathHapticEvent.UNDO)
            isCompletionHandled = false
            currentRevision++
            hintJob?.cancel()

            currentSnapshot = currentSnapshot?.copy(
                path = result.state.currentPath.positions,
                moveCount = result.state.moveCount,
                elapsedActiveTimeMs = currentElapsed,
                revision = currentRevision,
                lastUpdatedAt = timeProvider.wallClockTimeMs()
            )
            flushSessionSave()

            _uiState.value = currentReady.copy(
                gameState = result.state,
                boardState = result.state.toBoardState(hintedCoordinate = null),
                isUndoAvailable = result.state.currentPath.size > 1,
                isResetAvailable = !result.state.currentPath.isEmpty,
                lastRejectionReason = null,
                completionResult = null,
                activeHint = null,
                isHintLoading = false,
                hintMessage = null,
                showRecoveryDialog = false,
                undoCount = undoCount
            )
        }
    }

    /**
     * Resets the entire path back to initial state.
     */
    fun onResetClicked() {
        val currentReady = (_uiState.value as? GameplayUiState.Ready) ?: return
        val activeEngine = engine ?: return

        val result = activeEngine.process(PuzzleAction.ResetPath, 0L)
        if (result.isAccepted) {
            resetCount++
            audioManager.playEvent(ZynpathAudioEvent.RESET)
            hapticManager.performHaptic(ZynpathHapticEvent.RESET)
            timerJob?.cancel()
            hintJob?.cancel()
            gameplayTimer.reset()
            _elapsedTimeMs.value = 0L
            isCompletionHandled = false

            currentRevision++
            currentSnapshot = currentSnapshot?.copy(
                status = SessionStatus.NOT_STARTED,
                path = emptyList(),
                moveCount = 0,
                elapsedActiveTimeMs = 0L,
                revision = currentRevision,
                lastUpdatedAt = timeProvider.wallClockTimeMs()
            )
            flushSessionSave()

            _uiState.value = currentReady.copy(
                gameState = result.state,
                boardState = result.state.toBoardState(hintedCoordinate = null),
                elapsedTimeMs = 0L,
                isUndoAvailable = false,
                isResetAvailable = false,
                lastRejectionReason = null,
                completionResult = null,
                activeHint = null,
                isHintLoading = false,
                hintMessage = null,
                showRecoveryDialog = false,
                resetCount = resetCount
            )
        }
    }

    /**
     * Pauses gameplay and the elapsed timer.
     */
    fun onPauseGame() {
        val currentReady = (_uiState.value as? GameplayUiState.Ready) ?: return
        val activeEngine = engine ?: return

        val frozenElapsed = gameplayTimer.pause()
        timerJob?.cancel()
        hintJob?.cancel()

        val result = activeEngine.process(PuzzleAction.PauseGame, frozenElapsed)
        if (result.isAccepted) {
            currentRevision++
            currentSnapshot = currentSnapshot?.copy(
                status = SessionStatus.PAUSED,
                elapsedActiveTimeMs = frozenElapsed,
                revision = currentRevision,
                lastUpdatedAt = timeProvider.wallClockTimeMs()
            )
            flushSessionSave()

            _uiState.value = currentReady.copy(
                gameState = result.state,
                boardState = result.state.toBoardState(hintedCoordinate = null),
                elapsedTimeMs = frozenElapsed,
                isHintLoading = false
            )
        }
    }

    /**
     * Resumes gameplay and restarts the elapsed timer.
     */
    fun onResumeGame() {
        val currentReady = (_uiState.value as? GameplayUiState.Ready) ?: return
        val activeEngine = engine ?: return

        val currentElapsed = gameplayTimer.resume()
        startTimerTicker()

        val result = activeEngine.process(PuzzleAction.ResumeGame, currentElapsed)
        if (result.isAccepted) {
            currentRevision++
            currentSnapshot = currentSnapshot?.copy(
                status = SessionStatus.ACTIVE,
                elapsedActiveTimeMs = currentElapsed,
                revision = currentRevision,
                lastUpdatedAt = timeProvider.wallClockTimeMs()
            )
            flushSessionSave()

            _uiState.value = currentReady.copy(
                gameState = result.state,
                boardState = result.state.toBoardState(hintedCoordinate = currentReady.activeHint?.targetCell)
            )
        }
    }

    /**
     * Called when the app enters the background (ON_PAUSE / ON_STOP).
     * Pauses active gameplay, stops timer, and guarantees state is persisted to Room.
     */
    fun onAppBackgrounded() {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        if (currentReady.isCompleted || currentReady.gameState.isPaused) return

        onPauseGame()
    }

    /**
     * Called when leaving the screen or navigating away.
     */
    fun onNavigatedAway() {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        if (currentReady.isCompleted || currentReady.gameState.isPaused) return

        val frozenElapsed = gameplayTimer.pause()
        timerJob?.cancel()
        hintJob?.cancel()
        currentRevision++
        currentSnapshot = currentSnapshot?.copy(
            status = SessionStatus.PAUSED,
            elapsedActiveTimeMs = frozenElapsed,
            revision = currentRevision,
            lastUpdatedAt = timeProvider.wallClockTimeMs()
        )
        flushSessionSave()
    }

    private fun startTimerTicker() {
        if (!isTimerTickerEnabled || timerJob?.isActive == true) return
        timerJob = viewModelScope.launch {
            var lastEmittedSec = -1L
            while (isActive && gameplayTimer.isRunning) {
                delay(200L) // Responsive poll interval without spamming recompositions (Prompt 37)
                val elapsed = gameplayTimer.elapsedDurationMs()
                val currentSec = elapsed / 1000L
                _elapsedTimeMs.value = elapsed
                if (currentSec != lastEmittedSec) {
                    lastEmittedSec = currentSec
                    val currentReady = _uiState.value as? GameplayUiState.Ready
                    if (currentReady != null && currentReady.gameState.isInProgress && !currentReady.gameState.isPaused) {
                        _uiState.value = currentReady.copy(elapsedTimeMs = elapsed)
                    }
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
        val snapshot = currentSnapshot ?: return
        viewModelScope.launch {
            sessionRepository.saveSession(snapshot)
        }
    }

    /**
     * Prompt 13 Sections 28 & 29: Idempotent completion recording.
     */
    private fun handleValidatedCompletion(completionResult: ValidatedCompletionResult?) {
        if (completionResult == null) return
        if (isCompletionHandled) return

        val activeDef = engine?.definition ?: return
        if (completionResult.levelId != levelId || completionResult.puzzleId != activeDef.puzzleId) {
            return
        }

        isCompletionHandled = true
        val finalTimeMs = gameplayTimer.stop()
        timerJob?.cancel()
        saveJob?.cancel()
        hintJob?.cancel()

        viewModelScope.launch {
            currentRevision++
            try {
                currentSnapshot?.let { snap ->
                    val finalPath = engine?.currentState?.currentPath?.positions ?: snap.path
                    val completedSnapshot = snap.copy(
                        status = SessionStatus.COMPLETED,
                        elapsedActiveTimeMs = finalTimeMs,
                        path = finalPath,
                        moveCount = completionResult.moveCount,
                        revision = currentRevision,
                        lastUpdatedAt = timeProvider.wallClockTimeMs()
                    )
                    sessionRepository.saveSession(completedSnapshot)
                }

                val currentReady = _uiState.value as? GameplayUiState.Ready
                val existingBest = currentReady?.personalBestTimeMs
                val isNewBest = existingBest == null || existingBest <= 0L || finalTimeMs < existingBest
                val effectiveBest = if (isNewBest) finalTimeMs else existingBest

                val completedResultWithUndo = completionResult.copy(
                    levelId = levelId,
                    worldId = worldId,
                    elapsedTimeMs = finalTimeMs.coerceAtLeast(1L),
                    undoCount = undoCount,
                    resetCount = resetCount
                )

                // Prompt 38 Section 19: Durably commit completion to database BEFORE showing irreversible celebration UI
                progressRepository.recordValidatedCompletion(completedResultWithUndo)

                // Persist current level and world for resuming and app restarts (World Progression Safety)
                preferencesRepository.setLastSelectedLevel(levelId)
                preferencesRepository.setLastSelectedWorld(worldId)

                // Economy: Calculate first-clear and world-completion rewards
                var coinsAwarded = 0
                val isFirstClear = walletRepository?.isFirstClearRewardEligible(levelId) == true
                if (isFirstClear && isRewardRun) {
                    val clearRes = walletRepository?.grantSoloFirstClearReward(levelId)
                    if (clearRes?.isSuccess == true) {
                        coinsAwarded += com.zynpath.game.core.economy.EconomyConfig.SOLO_FIRST_CLEAR_REWARD
                    }
                    walletRepository?.clearActivePaidAttempt(levelId)
                    interstitialAdManager?.onFirstTimeLevelCompleted()
                }

                // Prompt 41 Section 11-13: Detect first-time world completion celebration
                val world = com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorldForLevel(levelId)
                val allCompletedIds = progressRepository.getCompletedLevelIds()
                val isWorldComplete = world.levelRange.all { it in allCompletedIds || it == levelId }
                val userPrefs = preferencesRepository.userPreferencesFlow.firstOrNull()
                val celebrationTipKey = "world_celebrated_${world.worldId}"
                val alreadyCelebrated = userPrefs?.seenFeatureTips?.contains(celebrationTipKey) == true
                val worldCelebrationToTrigger = if (isWorldComplete && !alreadyCelebrated) world else null

                var isWorldAdBonusEligible = false
                if (isWorldComplete && walletRepository?.isWorldCompletionClaimed(world.worldId) == false) {
                    val worldRes = walletRepository.claimWorldCompletionReward(world.worldId)
                    if (worldRes.isSuccess) {
                        coinsAwarded += com.zynpath.game.core.economy.EconomyConfig.WORLD_COMPLETION_BASE_REWARD
                    }
                }
                if (isWorldComplete && walletRepository?.isWorldCompletionAdBonusClaimed(world.worldId) == false) {
                    isWorldAdBonusEligible = true
                }

                val latestReady = _uiState.value as? GameplayUiState.Ready
                if (latestReady != null) {
                    val calculatedStars = com.zynpath.game.core.puzzle.model.StarRatingPolicy.calculateStars(
                        hintCount = completedResultWithUndo.hintCount,
                        undoResetCount = completedResultWithUndo.undoResetCount
                    )
                    val totalStarsUpdated = progressRepository.getTotalStarsEarned().firstOrNull() ?: (latestReady.totalStars + calculatedStars)
                    _uiState.value = latestReady.copy(
                        elapsedTimeMs = finalTimeMs,
                        isNewPersonalBest = isNewBest,
                        personalBestTimeMs = effectiveBest,
                        completionResult = completedResultWithUndo,
                        worldCelebration = worldCelebrationToTrigger,
                        starsEarned = maxOf(latestReady.starsEarned, calculatedStars),
                        totalStars = totalStarsUpdated,
                        coinsEarned = coinsAwarded,
                        isWorldRewardBonusEligible = isWorldAdBonusEligible,
                        undoCount = undoCount,
                        resetCount = resetCount
                    )
                }
            } catch (e: Exception) {
                val classified = ErrorClassifier.classify(e)
                ZynpathDiagnostics.recordError("SOLO_COMPLETION_PERSISTENCE_FAILED", classified, mapOf("levelId" to levelId.toString()))
                // Even if an unexpected error occurs during post-processing, present completion state
                val latestReady = _uiState.value as? GameplayUiState.Ready
                if (latestReady != null) {
                    _uiState.value = latestReady.copy(
                        elapsedTimeMs = finalTimeMs,
                        completionResult = completionResult
                    )
                }
            }
        }
    }

    /**
     * Dismisses the first-time world completion celebration dialog and persists the seen state.
     * Prevents celebration replay on subsequent level completions or navigations (Prompt 41 Section 13).
     */
    fun dismissWorldCelebration() {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        val worldId = currentReady.worldCelebration?.worldId
        _uiState.value = currentReady.copy(worldCelebration = null)
        if (worldId != null) {
            viewModelScope.launch {
                preferencesRepository.markFeatureTipSeen("world_celebrated_$worldId")
            }
        }
    }

    /**
     * Toggles between continuous drag and discrete tap input modes (Prompt 15 Section 26).
     * Preserves active engine state without resetting the puzzle.
     */
    fun toggleInputMode() {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        val newMode = !currentReady.isTapInputMode
        _uiState.value = currentReady.copy(isTapInputMode = newMode)
        viewModelScope.launch {
            preferencesRepository.setTapInputMode(newMode)
        }
    }

    /**
     * Replays the exact same puzzle identity and version, resetting the attempt and timer
     * while preserving historical completion records and personal bests (Prompt 15 Section 23).
     */
    fun onReplayLevel() {
        val currentReady = (_uiState.value as? GameplayUiState.Ready) ?: return
        val definition = currentReady.definition

        timerJob?.cancel()
        saveJob?.cancel()
        hintJob?.cancel()
        gameplayTimer.reset()
        _elapsedTimeMs.value = 0L
        isCompletionHandled = false
        undoCount = 0
        resetCount = 0

        val newEngine = PuzzleEngine(definition, levelId = levelId, worldId = worldId)
        engine = newEngine
        currentRevision++

        val newSnapshot = GameplaySessionSnapshot.createNew(
            levelId = levelId,
            worldId = worldId,
            puzzleId = definition.puzzleId,
            puzzleVersion = definition.puzzleVersion,
            nowMs = timeProvider.wallClockTimeMs()
        )
        currentSnapshot = newSnapshot
        flushSessionSave()

        val initialGameState = newEngine.currentState
        _uiState.value = currentReady.copy(
            gameState = initialGameState,
            boardState = initialGameState.toBoardState(hintedCoordinate = null),
            elapsedTimeMs = 0L,
            isUndoAvailable = false,
            isResetAvailable = false,
            isRestoredSession = false,
            sessionId = newSnapshot.sessionId,
            lastRejectionReason = null,
            completionResult = null,
            activeHint = null,
            isHintLoading = false,
            hintMessage = null,
            showRecoveryDialog = false,
            isNewPersonalBest = false,
            coinsEarned = 0,
            isWorldRewardBonusEligible = false,
            undoCount = 0,
            resetCount = 0
        )
    }

    /**
     * Watches a rewarded video ad to claim the +25 Coins World Completion Ad Bonus.
     */
    fun watchAdForWorldBonus(activity: Activity) {
        val currentReady = _uiState.value as? GameplayUiState.Ready ?: return
        if (!currentReady.isWorldRewardBonusEligible) return
        val world = com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorldForLevel(levelId)

        viewModelScope.launch {
            val adMgr = coinRewardedAdManager ?: return@launch
            adMgr.showCoinRewardedAd(
                activity = activity,
                onRewarded = {
                    viewModelScope.launch {
                        val res = walletRepository?.claimWorldCompletionAdBonus(world.worldId)
                        if (res?.isSuccess == true) {
                            val latest = _uiState.value as? GameplayUiState.Ready ?: return@launch
                            _uiState.value = latest.copy(
                                coinsEarned = latest.coinsEarned + com.zynpath.game.core.economy.EconomyConfig.WORLD_COMPLETION_AD_BONUS,
                                isWorldRewardBonusEligible = false,
                                rewardSnackbarMessage = "+25 Coins World Bonus claimed!"
                            )
                        }
                    }
                },
                onFailed = { msg ->
                    val latest = _uiState.value as? GameplayUiState.Ready ?: return@showCoinRewardedAd
                    _uiState.value = latest.copy(
                        rewardSnackbarMessage = msg
                    )
                }
            )
        }
    }

    /**
     * Resolves destination using authoritative ProgressionDestinationResolver.
     */
    suspend fun resolveProgressionDestination(): com.zynpath.game.core.puzzle.model.NextDestinationResolution {
        val completed = progressRepository.getCompletedLevelIds()
        return com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver.resolve(worldId, levelId, completed)
    }

    /**
     * Checks if a world is unlocked according to authoritative progression.
     */
    suspend fun isWorldUnlocked(targetWorldId: Int): Boolean {
        return progressRepository.isWorldUnlocked(targetWorldId)
    }

    /**
     * Resolves the next level through the catalog, verifying unlocked progression and asset availability (Prompt 15 Section 22).
     */
    suspend fun resolveNextLevel(): NextLevelResolution {
        val completed = progressRepository.getCompletedLevelIds()
        val destinationResolution = com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver.resolve(worldId, levelId, completed)

        return when (val dest = destinationResolution.destination) {
            is com.zynpath.game.core.puzzle.model.ProgressionDestination.JourneyComplete -> {
                NextLevelResolution.CatalogCompleted
            }
            is com.zynpath.game.core.puzzle.model.ProgressionDestination.NextWorldEntry -> {
                val nextWorldId = dest.worldId
                val isUnlocked = progressRepository.isWorldUnlocked(nextWorldId)
                if (isUnlocked) {
                    NextLevelResolution.WorldComplete(nextWorldId)
                } else {
                    NextLevelResolution.Locked(nextWorldId, com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorld(nextWorldId).startLevel)
                }
            }
            is com.zynpath.game.core.puzzle.model.ProgressionDestination.NextLevel -> {
                val nextLevelId = dest.levelId
                if (nextLevelId > com.zynpath.game.core.puzzle.model.WorldConfiguration.TOTAL_LEVELS) {
                    return NextLevelResolution.CatalogCompleted
                }
                val nextLevelDef = catalogRepository.getLevel(nextLevelId)
                    ?: return NextLevelResolution.Unavailable(nextLevelId, "Level $nextLevelId does not exist in catalog")

                val isUnlocked = progressRepository.isLevelUnlocked(nextLevelId)
                if (!isUnlocked) {
                    return NextLevelResolution.Locked(nextLevelDef.worldId, nextLevelId)
                }

                when (val load = catalogRepository.loadPuzzle(nextLevelId)) {
                    is CatalogLoadResult.Success -> {
                        NextLevelResolution.Available(nextLevelDef.worldId, nextLevelId)
                    }
                    is CatalogLoadResult.AssetUnavailable -> {
                        NextLevelResolution.Unavailable(nextLevelId, "Level $nextLevelId content is pending catalog release")
                    }
                    is CatalogLoadResult.AssetInvalid -> {
                        NextLevelResolution.Unavailable(nextLevelId, "Level $nextLevelId asset integrity validation failed")
                    }
                    is CatalogLoadResult.LevelNotFound -> {
                        NextLevelResolution.Unavailable(nextLevelId, "Level $nextLevelId not found in catalog")
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        saveJob?.cancel()
        hintJob?.cancel()
        gameplayTimer.stop()
    }
}

/**
 * Fallback in-memory implementation of [HintUsageRepository] for testing.
 */
private class InMemoryHintUsageRepository(
    private var freeHints: Int = 3,
    private var rewardedCredits: Int = 0,
    private var isPremiumUser: Boolean = false
) : HintUsageRepository {
    override val remainingHintsFlow: Flow<Int> = MutableStateFlow(freeHints + rewardedCredits)
    override val isPremiumFlow: Flow<Boolean> = MutableStateFlow(isPremiumUser)
    override val rewardedCreditsFlow: Flow<Int> = MutableStateFlow(rewardedCredits)
    override val freeAllowanceFlow: Flow<Int> = MutableStateFlow(freeHints)

    override suspend fun getRemainingHints(): Int = if (isPremiumUser) Int.MAX_VALUE else freeHints + rewardedCredits
    override suspend fun isPremium(): Boolean = isPremiumUser
    override suspend fun canConsumeHint(): Boolean = isPremiumUser || (freeHints + rewardedCredits) > 0
    override suspend fun consumeHint(): Boolean {
        if (isPremiumUser) return true
        if (freeHints > 0) {
            freeHints--
            return true
        }
        if (rewardedCredits > 0) {
            rewardedCredits--
            return true
        }
        return false
    }
    override suspend fun addFreeHints(count: Int) { freeHints += count }
    override suspend fun addRewardedHintCredit(count: Int): Int {
        rewardedCredits += count
        return rewardedCredits
    }
    override suspend fun setPremium(isPremium: Boolean) { isPremiumUser = isPremium }
}

/**
 * Fallback in-memory implementation of [PreferencesRepository] for testing.
 */
private class InMemoryPreferencesRepository(
    initialPrefs: UserPreferences = UserPreferences()
) : PreferencesRepository {
    private val prefsFlow = MutableStateFlow(initialPrefs)
    override val userPreferencesFlow: Flow<UserPreferences> = prefsFlow.asStateFlow()
    override suspend fun setOnboardingCompleted(completed: Boolean) { prefsFlow.value = prefsFlow.value.copy(isOnboardingCompleted = completed) }
    override suspend fun setTutorialCompleted(completed: Boolean) { prefsFlow.value = prefsFlow.value.copy(isTutorialCompleted = completed) }
    override suspend fun setTutorialStage(stage: Int) { prefsFlow.value = prefsFlow.value.copy(tutorialStage = stage) }
    override suspend fun setTutorialSkipped(skipped: Boolean) { prefsFlow.value = prefsFlow.value.copy(isTutorialSkipped = skipped) }
    override suspend fun markFeatureTipSeen(tipId: String) { prefsFlow.value = prefsFlow.value.copy(seenFeatureTips = prefsFlow.value.seenFeatureTips + tipId) }
    override suspend fun resetTutorialProgress() { prefsFlow.value = prefsFlow.value.copy(tutorialStage = 1, isTutorialCompleted = false, isTutorialSkipped = false) }
    override suspend fun setSfxEnabled(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(isSfxEnabled = enabled) }
    override suspend fun setMusicEnabled(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(isMusicEnabled = enabled) }
    override suspend fun setHapticsEnabled(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(isHapticsEnabled = enabled) }
    override suspend fun setThemePreference(theme: String) { prefsFlow.value = prefsFlow.value.copy(themePreference = theme) }
    override suspend fun setReducedMotion(reduced: Boolean) { prefsFlow.value = prefsFlow.value.copy(isReducedMotion = reduced) }
    override suspend fun setSelectedLanguage(language: String) { prefsFlow.value = prefsFlow.value.copy(selectedLanguage = language) }
    override suspend fun setLastSelectedWorld(worldId: Int) { prefsFlow.value = prefsFlow.value.copy(lastSelectedWorld = worldId) }
    override suspend fun setLastSelectedLevel(levelId: Int) { prefsFlow.value = prefsFlow.value.copy(lastSelectedLevel = levelId) }
    override suspend fun setFreeHintsRemaining(hints: Int) { prefsFlow.value = prefsFlow.value.copy(freeHintsRemaining = hints) }
    override suspend fun setRewardedHintCredits(credits: Int) { prefsFlow.value = prefsFlow.value.copy(rewardedHintCredits = credits) }
    override suspend fun addRewardedHintCredits(delta: Int) {
        val cur = prefsFlow.value.rewardedHintCredits
        prefsFlow.value = prefsFlow.value.copy(rewardedHintCredits = (cur + delta).coerceAtLeast(0))
    }
    override suspend fun setPremium(isPremium: Boolean) { prefsFlow.value = prefsFlow.value.copy(isPremium = isPremium) }
    override suspend fun setTapInputMode(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(isTapInputMode = enabled) }
    override suspend fun setEquippedTheme(themeId: String) { prefsFlow.value = prefsFlow.value.copy(equippedThemeId = themeId) }
    override suspend fun setEquippedPathEffect(pathEffectId: String) { prefsFlow.value = prefsFlow.value.copy(equippedPathEffectId = pathEffectId) }
    override suspend fun setEquippedAvatarFrame(avatarFrameId: String) { prefsFlow.value = prefsFlow.value.copy(equippedAvatarFrameId = avatarFrameId) }
    override suspend fun setEquippedCosmetics(themeId: String?, pathEffectId: String?, avatarFrameId: String?) {
        prefsFlow.value = prefsFlow.value.copy(
            equippedThemeId = themeId ?: prefsFlow.value.equippedThemeId,
            equippedPathEffectId = pathEffectId ?: prefsFlow.value.equippedPathEffectId,
            equippedAvatarFrameId = avatarFrameId ?: prefsFlow.value.equippedAvatarFrameId
        )
    }
    override suspend fun setFriendAlertsEnabled(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(isFriendAlertsEnabled = enabled) }
    override suspend fun setMultiplayerAlertsEnabled(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(isMultiplayerAlertsEnabled = enabled) }
    override suspend fun setDailyReminderEnabled(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(isDailyReminderEnabled = enabled) }
    override suspend fun setDailyReminderTime(hour: Int, minute: Int) { prefsFlow.value = prefsFlow.value.copy(dailyReminderHour = hour, dailyReminderMinute = minute) }
    override suspend fun setPushToken(token: String) { prefsFlow.value = prefsFlow.value.copy(pushToken = token) }
    override suspend fun setProfileVisibility(visibility: String) { prefsFlow.value = prefsFlow.value.copy(profileVisibility = visibility) }
    override suspend fun setAllowZynpathIdSearch(allow: Boolean) { prefsFlow.value = prefsFlow.value.copy(allowZynpathIdSearch = allow) }
    override suspend fun setAllowFriendRequests(allow: Boolean) { prefsFlow.value = prefsFlow.value.copy(allowFriendRequests = allow) }
    override suspend fun setHighContrast(enabled: Boolean) { prefsFlow.value = prefsFlow.value.copy(isHighContrast = enabled) }
    override suspend fun setTouchSensitivity(sensitivity: Float) { prefsFlow.value = prefsFlow.value.copy(touchSensitivity = sensitivity) }
    override suspend fun clearAllPreferences() { prefsFlow.value = UserPreferences() }
}
