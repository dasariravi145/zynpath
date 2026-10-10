package com.zynpath.game.feature.daily

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Settings
import com.zynpath.game.core.designsystem.components.ZynpathMasterCurrencyPill
import com.zynpath.game.core.designsystem.components.ZynpathMasterSegmentedTabs
import com.zynpath.game.core.economy.EconomyConfig
import com.zynpath.game.core.designsystem.theme.RefCyanNeon
import com.zynpath.game.core.designsystem.theme.RefGoldPrimary
import com.zynpath.game.core.designsystem.theme.RefGreenClaim
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.designsystem.theme.RefNavySurface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameRewardBadge
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.components.GameTopBar
import com.zynpath.game.core.designsystem.components.RewardTier
import com.zynpath.game.core.designsystem.components.RewardType
import com.zynpath.game.core.designsystem.components.StatusBadge
import com.zynpath.game.core.designsystem.components.ZynpathConfirmationDialog
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.core.designsystem.feedback.SoundFeedbackManager
import com.zynpath.game.core.designsystem.feedback.rememberSoundFeedbackManager
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.puzzle.daily.DailyChallengeAvailability
import com.zynpath.game.core.puzzle.daily.DailyChallengeDefinition
import com.zynpath.game.core.puzzle.daily.DailyChallengeOnlineResult
import com.zynpath.game.core.puzzle.daily.DailyVerificationStatus
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.ui.PuzzleBoard

/**
 * Premium Daily Challenge Screen.
 *
 * Implements Prompt 08/24:
 * - Deep navy fantasy game environment with ambient star particles.
 * - Royal-blue game panels (GamePanel) and electric-cyan glowing accents.
 * - Hero section with formatted challenge date, challenge status, and lightweight Compose number-path art.
 * - Challenge Card displaying actual challenge availability (PLAY CHALLENGE, CONTINUE, or COMPLETED).
 * - Progress & Rewards showing current streak, best streak, and personal best time.
 * - Interactive PuzzleBoard with touch drawing and tap mode.
 * - Daily pause, rules, and celebratory completion dialogs.
 */
@Composable
fun DailyChallengeScreen(
    onBackClick: () -> Unit,
    onNavigateToLeaderboard: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DailyChallengeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val soundFeedback = rememberSoundFeedbackManager()

    BackHandler {
        viewModel.onNavigatedAway()
        onBackClick()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onNavigatedAway()
        }
    }

    GameScreenBackground(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is DailyChallengeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = GameElectricCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "LOADING DAILY QUEST...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameElectricCyan,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
            is DailyChallengeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GamePanel(
                        modifier = Modifier.widthIn(max = 440.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Daily Challenge Unavailable",
                                color = Color(0xFFFF5252),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = state.message,
                                color = TextMuted,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            GamePrimaryButton(
                                text = "RETRY",
                                onClick = { viewModel.loadTodayChallenge() }
                            )
                            GameSecondaryButton(
                                text = "RETURN HOME",
                                onClick = onBackClick
                            )
                        }
                    }
                }
            }
            is DailyChallengeUiState.Ready -> {
                DailyChallengeReadyScreen(
                    state = state,
                    viewModel = viewModel,
                    soundFeedback = soundFeedback,
                    onBackClick = {
                        viewModel.onNavigatedAway()
                        onBackClick()
                    },
                    onNavigateToLeaderboard = onNavigateToLeaderboard
                )
            }
        }
    }
}

/**
 * Handles presentation for ready state: directly renders the active interactive gameplay board
 * with quick access to rules and leaderboard modal.
 */
@Composable
private fun DailyChallengeReadyScreen(
    state: DailyChallengeUiState.Ready,
    viewModel: DailyChallengeViewModel,
    soundFeedback: SoundFeedbackManager,
    onBackClick: () -> Unit,
    onNavigateToLeaderboard: (String) -> Unit
) {
    var showLeaderboardModal by rememberSaveable { mutableStateOf(false) }

    DailyChallengeActiveBoardView(
        state = state,
        viewModel = viewModel,
        soundFeedback = soundFeedback,
        onBackToOverview = onBackClick,
        onNavigateToLeaderboard = {
            showLeaderboardModal = true
        }
    )

    // Rules Dialog (Shared between Hub and Board)
    if (state.showRulesDialog) {
        ZynpathConfirmationDialog(
            title = "Daily Challenge Rules",
            message = "1. Begin at checkpoint #1.\n" +
                    "2. Move orthogonally (up, down, left, right).\n" +
                    "3. Visit checkpoints in strictly ascending order (1 -> 2 -> ... -> N).\n" +
                    "4. Walls cannot be crossed.\n" +
                    "5. Victory requires covering 100% of cells AND ending at the final checkpoint.\n\n" +
                    "• Hints are disabled in Daily Challenge mode for competitive fairness.\n" +
                    "• Undo and Reset are free and unpenalized.",
            confirmButtonText = "Got it",
            onConfirm = { viewModel.toggleRulesDialog(false) },
            onDismiss = { viewModel.toggleRulesDialog(false) }
        )
    }

    if (showLeaderboardModal) {
        LeaderboardModal(
            dateKey = state.challenge.dateKey,
            onDismiss = { showLeaderboardModal = false }
        )
    }
}

/**
 * Active interactive gameplay board view when player is playing the challenge.
 */
@Composable
private fun DailyChallengeActiveBoardView(
    state: DailyChallengeUiState.Ready,
    viewModel: DailyChallengeViewModel,
    soundFeedback: SoundFeedbackManager,
    onBackToOverview: () -> Unit,
    onNavigateToLeaderboard: (String) -> Unit
) {
    val formattedTime = formatTime(state.elapsedTimeMs)
    val coverageFraction = state.gameState.coverageFraction

    val rejectionMessage = when (state.lastRejectionReason) {
        MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE -> "Start at Checkpoint #1"
        MoveRejectionReason.OUT_OF_BOUNDS -> "Stay within grid boundary"
        MoveRejectionReason.EXCLUDED_CELL -> "Cell is excluded"
        MoveRejectionReason.NON_ADJACENT -> "Move orthogonally (up, down, left, right)"
        MoveRejectionReason.BLOCKED_BY_WALL -> "Blocked by wall edge"
        MoveRejectionReason.CELL_ALREADY_VISITED -> "Cell already on path"
        MoveRejectionReason.WRONG_CHECKPOINT_ORDER -> "Visit checkpoints in ascending order"
        MoveRejectionReason.PREMATURE_FINAL_CHECKPOINT -> "Fill every cell first"
        MoveRejectionReason.GAME_ALREADY_COMPLETED -> "Puzzle already completed"
        MoveRejectionReason.GAME_PAUSED -> "Game is paused"
        MoveRejectionReason.INVALID_ACTION -> "Invalid action"
        null -> null
    }

    LaunchedEffect(state.lastRejectionReason) {
        if (state.lastRejectionReason != null) {
            soundFeedback.playInvalidMove()
        }
    }

    LaunchedEffect(state.gameState.isCompleted) {
        if (state.gameState.isCompleted) {
            soundFeedback.playPuzzleCompleted()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            GameTopBar(
                title = "DAILY: ${state.challenge.dateKey}",
                subtitle = "${state.challenge.gridDimensions.rows}×${state.challenge.gridDimensions.columns} • ${state.challenge.difficultyTier}",
                onBackClick = onBackToOverview,
                actions = {
                    // Tap / Drag input mode toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F1A36))
                            .border(1.dp, ForestMint.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable { viewModel.toggleInputMode() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (state.isTapInputMode) "TAP" else "DRAG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestMint
                        )
                    }

                    IconButton(onClick = { viewModel.toggleRulesDialog(true) }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Rules", tint = TextSecondary)
                    }

                    IconButton(onClick = { viewModel.onPause() }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause", tint = TextPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Stats Panel
            Column(modifier = Modifier.fillMaxWidth()) {
                GamePanel(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DailyStatPill(
                            title = "Coverage",
                            value = "${state.gameState.coveredCellCount}/${state.gameState.totalRequiredCells}",
                            accent = PathCyanGlow
                        )
                        DailyStatPill(
                            title = "Checkpoints",
                            value = "${state.gameState.visitedCheckpointCount}/${state.boardState.maxCheckpointNumber}",
                            accent = ForestMint
                        )
                        DailyStatPill(
                            title = "Time",
                            value = formattedTime,
                            accent = AccentGold
                        )
                        DailyStatPill(
                            title = "Streak",
                            value = "${state.currentStreak} d",
                            accent = Color(0xFFFF7043),
                            icon = Icons.Default.LocalFireDepartment
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF0F1A36))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(coverageFraction)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GameElectricCyan, ForestMint)
                                )
                            )
                    )
                }
            }

            // Interactive Responsive Board Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                PuzzleBoard(
                    boardState = state.boardState,
                    isTapMode = state.isTapInputMode,
                    isReducedMotion = state.isReducedMotion,
                    lastRejectionReason = state.lastRejectionReason,
                    onCellEntered = { pos -> viewModel.onCellEntered(pos) },
                    modifier = Modifier.fillMaxSize()
                )

                // Error Pill
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = rejectionMessage != null,
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { it / 2 }
                    ) {
                        if (rejectionMessage != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xDD3A1D1D))
                                    .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .semantics {
                                        liveRegion = LiveRegionMode.Polite
                                        contentDescription = "Move rejected: $rejectionMessage"
                                    }
                            ) {
                                Text(
                                    text = rejectionMessage,
                                    color = Color(0xFFFF8A80),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Controls Bar (Undo, Reset, Return to Hub)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameSecondaryButton(
                    text = "UNDO",
                    onClick = { viewModel.onUndo() },
                    enabled = state.gameState.currentPath.size > 1 && !state.gameState.isCompleted,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = null,
                            tint = if (state.gameState.currentPath.size > 1) GameElectricCyan else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                GameSecondaryButton(
                    text = "RESET",
                    onClick = { viewModel.onReset() },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Pause Dialog
    if (state.isPaused && !state.gameState.isCompleted) {
        DailyPauseDialog(
            challengeDate = state.challenge.dateKey,
            onDismiss = { viewModel.onResume() },
            onResumeClick = { viewModel.onResume() },
            onResetClick = {
                viewModel.onResume()
                viewModel.onReset()
            },
            onHomeClick = onBackToOverview
        )
    }

    // Server Validation Progress Overlay
    if (state.isValidatingOnline) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F1A36))
                    .border(1.dp, ForestMint.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                CircularProgressIndicator(color = ForestMint, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Server Validation in Progress",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Verifying canonical path and timing...",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }

    // Completion Dialog
    if (state.gameState.isCompleted) {
        DailyChallengeCompletionDialog(
            challenge = state.challenge,
            solveTimeMs = state.elapsedTimeMs,
            personalBestTimeMs = state.personalBestTimeMs,
            currentStreak = state.currentStreak,
            isNewBest = state.isNewPersonalBest,
            isReducedMotion = state.isReducedMotion,
            verificationStatus = state.verificationStatus,
            isLeaderboardEligible = state.isLeaderboardEligible,
            onlineResult = state.onlineResult,
            isFirstClearBonusAwarded = state.isFirstClearBonusAwarded,
            onLeaderboardClick = { onNavigateToLeaderboard(state.challenge.dateKey) },
            onReplay = { viewModel.onReplayChallenge() },
            onHome = onBackToOverview
        )
    }
}

@Composable
private fun DailyStatPill(
    title: String,
    value: String,
    accent: Color,
    icon: ImageVector? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun DailyPauseDialog(
    challengeDate: String,
    onDismiss: () -> Unit,
    onResumeClick: () -> Unit,
    onResetClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F1A36))
                .border(1.dp, ForestMint.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Challenge Paused",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "$challengeDate • Timer paused",
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                GamePrimaryButton(
                    text = "RESUME CHALLENGE",
                    onClick = onResumeClick
                )

                GameSecondaryButton(
                    text = "RESTART ATTEMPT",
                    onClick = onResetClick
                )

                TextButton(onClick = onHomeClick) {
                    Text(
                        text = "Return to Overview",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyChallengeCompletionDialog(
    challenge: DailyChallengeDefinition,
    solveTimeMs: Long,
    personalBestTimeMs: Long?,
    currentStreak: Int,
    isNewBest: Boolean,
    isReducedMotion: Boolean,
    verificationStatus: DailyVerificationStatus,
    isLeaderboardEligible: Boolean,
    onlineResult: DailyChallengeOnlineResult?,
    isFirstClearBonusAwarded: Boolean,
    onLeaderboardClick: () -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit
) {
    val formattedSolveTime = formatTime(solveTimeMs)

    Dialog(
        onDismissRequest = onHome,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .widthIn(max = 360.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF80A1635),
                            GameMidnightBlue
                        )
                    )
                )
                .border(GameBorders.modalGoldCyan, RoundedCornerShape(22.dp))
                .glossyTopHighlight(cornerRadius = 22.dp, highlightAlpha = 0.20f)
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                // 1. Trophy Icon (compact 44.dp)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AccentGold.copy(alpha = 0.16f))
                        .border(1.dp, AccentGold.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_game_trophy),
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // 2. Title & Date
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DAILY COMPLETED!",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = GameGoldHighlight,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${challenge.dateKey} • ${challenge.difficultyTier}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                // 3. Compact Stats Row (Time & Streak)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F1A36))
                        .border(1.dp, Color(0xFF1B2E58), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TIME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(formattedSolveTime, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Box(modifier = Modifier.size(1.dp, 22.dp).background(Color(0xFF253760)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("STREAK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text("$currentStreak Days", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                        }
                    }
                }

                // First Clear Bonus Pill (if awarded)
                if (isFirstClearBonusAwarded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E3A8A).copy(alpha = 0.45f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_game_coin),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+${EconomyConfig.DAILY_CHALLENGE_FIRST_CLEAR_REWARD} COINS EARNED!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RefGoldPrimary
                        )
                    }
                }

                // 4. Action Buttons
                // [ LEADERBOARD ] (44.dp height)
                GamePrimaryButton(
                    text = "LEADERBOARD",
                    onClick = onLeaderboardClick,
                    height = 44.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                // [ REPLAY ]  [ DONE ] (Row of secondary buttons, 42.dp height)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameSecondaryButton(
                        text = "REPLAY",
                        onClick = onReplay,
                        height = 42.dp,
                        modifier = Modifier.weight(1f)
                    )
                    GameSecondaryButton(
                        text = "DONE",
                        onClick = onHome,
                        height = 42.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
