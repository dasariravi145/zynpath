package com.zynpath.game.feature.daily

import com.zynpath.game.core.puzzle.daily.DailyChallengeAvailability
import com.zynpath.game.core.puzzle.daily.DailyChallengeDefinition
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.engine.PuzzleGameState
import com.zynpath.game.core.puzzle.model.PuzzleBoardState

/**
 * UI State for Daily Challenge screen.
 */
sealed interface DailyChallengeUiState {
    data object Loading : DailyChallengeUiState

    data class Error(val message: String) : DailyChallengeUiState

    data class Ready(
        val challenge: DailyChallengeDefinition,
        val availability: DailyChallengeAvailability,
        val gameState: PuzzleGameState,
        val boardState: PuzzleBoardState,
        val elapsedTimeMs: Long,
        val moveCount: Int,
        val currentStreak: Int,
        val maxStreak: Int,
        val isNewPersonalBest: Boolean = false,
        val personalBestTimeMs: Long? = null,
        val lastRejectionReason: MoveRejectionReason? = null,
        val isTapInputMode: Boolean = false,
        val isReducedMotion: Boolean = false,
        val isSfxEnabled: Boolean = true,
        val isHapticsEnabled: Boolean = true,
        val isPaused: Boolean = false,
        val showRulesDialog: Boolean = false,
        val verificationStatus: com.zynpath.game.core.puzzle.daily.DailyVerificationStatus = com.zynpath.game.core.puzzle.daily.DailyVerificationStatus.LOCAL_COMPLETION,
        val isOnlineAttempt: Boolean = false,
        val serverAttemptId: String? = null,
        val isLeaderboardEligible: Boolean = false,
        val isValidatingOnline: Boolean = false,
        val onlineResult: com.zynpath.game.core.puzzle.daily.DailyChallengeOnlineResult? = null,
        val showResultDialog: Boolean = false,
        val coinBalance: Int = 0,
        val totalStars: Int = 0,
        val isDailyLoginClaimed: Boolean = false,
        val isDailyLoginAdBonusClaimed: Boolean = false,
        val isDailyChallengeAdBonusClaimed: Boolean = false,
        val isDailyChallengeClearClaimed: Boolean = false
    ) : DailyChallengeUiState
}
