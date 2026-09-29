package com.zynpath.game.feature.gameplay

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.ZynpathMasterActionBadgeButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.zynpath.game.core.designsystem.animation.ZynpathAnimations.rejectionShake
import com.zynpath.game.core.designsystem.components.LevelDifficultyIndicator
import com.zynpath.game.core.designsystem.components.StatusBadge
import com.zynpath.game.core.designsystem.layout.rememberZynpathWindowInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameTopBar
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.components.StarCounter
import com.zynpath.game.core.designsystem.components.ZynpathConfirmationDialog
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.components.ZynpathSecondaryButton
import com.zynpath.game.core.designsystem.feedback.rememberSoundFeedbackManager
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.designsystem.theme.WarningAmber
import com.zynpath.game.core.designsystem.theme.ZynpathShapes
import com.zynpath.game.core.designsystem.theme.ChapterTheme
import com.zynpath.game.core.designsystem.theme.ChapterThemes
import com.zynpath.game.core.puzzle.engine.MoveRejectionReason
import com.zynpath.game.core.puzzle.hint.HintType
import com.zynpath.game.core.puzzle.model.SamplePuzzles
import com.zynpath.game.core.puzzle.model.WorldConfiguration
import com.zynpath.game.core.puzzle.ui.PuzzleBoard
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.launch

/**
 * Interactive Gameplay Screen connecting real catalog puzzle assets, the pure Kotlin [PuzzleEngine],
 * and Compose Canvas touch drawing with full interactive polish, completion celebrations, and accessibility.
 *
 * Implements Prompts 12, 13, 14, and 15:
 * - Real-time continuous touch path drawing and alternative discrete tap-to-move input.
 * - Live stats: Monotonic active elapsed time, personal best, cell coverage, and checkpoint progression.
 * - Non-color-only checkpoint indicators (visited, next required, final, upcoming).
 * - Offline native sound and haptic feedback on checkpoint reach, rejection, and victory.
 * - Back navigation with session flushing to prevent path loss.
 * - Validated victory overlay with celebratory particles, personal best display, Next Level, and Replay.
 * - Full accessibility with adequate touch targets (>=48dp), scalable typography, and screen reader semantics.
 */
@Composable
fun GameplayShellScreen(
    worldId: Int,
    levelId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameplayViewModel? = null,
    onNextLevelClick: ((worldId: Int, levelId: Int) -> Unit)? = null,
    onNextWorldClick: ((nextWorldId: Int) -> Unit)? = null,
    onJourneyCompleteClick: (() -> Unit)? = null,
    onHomeClick: (() -> Unit)? = null
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showPauseDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var nextLevelStatusMessage by remember { mutableStateOf<String?>(null) }
    var isResolvingNextLevel by remember { mutableStateOf(false) }

    val chapterTheme = remember(levelId) { ChapterThemes.getThemeForLevel(levelId) }
    val coroutineScope = rememberCoroutineScope()
    val liveUiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value

    // Lifecycle observer for Prompt 13: Pause and persist on backgrounding, flush on navigation away
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                viewModel?.onAppBackgrounded()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel?.onNavigatedAway()
        }
    }

    // Handle system back navigation safely (Prompt 15 Section 18)
    BackHandler {
        viewModel?.onNavigatedAway()
        onBackClick()
    }

    // 1. Content Unavailable State
    if (liveUiState is GameplayUiState.ContentUnavailable) {
        GameScreenBackground(modifier = modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    GameTopBar(
                        title = "World $worldId • Level $levelId",
                        subtitle = "Catalog Status",
                        onBackClick = onBackClick
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Puzzle Pending",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = liveUiState.message,
                        fontSize = 14.sp,
                        color = GameSecondaryText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ZynpathPrimaryButton(
                        text = "Return to Levels",
                        onClick = onBackClick
                    )
                }
            }
        }
        return
    }

    // 2. Load Error State
    if (liveUiState is GameplayUiState.Error) {
        GameScreenBackground(modifier = modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    GameTopBar(
                        title = "World $worldId • Level $levelId",
                        subtitle = "Load Error",
                        onBackClick = onBackClick
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Unable to Launch Level",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = liveUiState.message,
                        fontSize = 14.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    ZynpathPrimaryButton(
                        text = "Return to Level Selection",
                        onClick = onBackClick
                    )
                }
            }
        }
        return
    }

    // 3. Loading State
    if (liveUiState is GameplayUiState.Loading) {
        GameScreenBackground(modifier = modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    GameTopBar(
                        title = "World $worldId • Level $levelId",
                        subtitle = "Loading Puzzle...",
                        onBackClick = onBackClick
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = GameElectricCyan,
                            modifier = Modifier.size(44.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading Level $levelId...",
                            fontSize = 14.sp,
                            color = GameSecondaryText
                        )
                    }
                }
            }
        }
        return
    }

    // 4. Ready State
    val readyState = liveUiState as? GameplayUiState.Ready
    val currentBoard = readyState?.boardState ?: if (worldId == 1) SamplePuzzles.Sample4x4_Base else SamplePuzzles.Sample3x3_Base

    val hapticFeedback = LocalHapticFeedback.current
    val soundFeedback = rememberSoundFeedbackManager(isSfxEnabled = readyState?.isSfxEnabled ?: true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(readyState?.rewardSnackbarMessage) {
        readyState?.rewardSnackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel?.clearRewardSnackbar()
        }
    }

    // Sound & Haptic Feedback on Invalid Move (Prompt 15 Section 12 & 13)
    LaunchedEffect(readyState?.lastRejectionReason) {
        if (readyState?.lastRejectionReason != null) {
            soundFeedback.playInvalidMove()
            if (readyState.isHapticsEnabled) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    // Sound & Haptic Feedback on Checkpoint Reached (Prompt 15 Section 13)
    LaunchedEffect(readyState?.lastReachedCheckpoint) {
        if (readyState?.lastReachedCheckpoint != null) {
            soundFeedback.playCheckpointReached()
            if (readyState.isHapticsEnabled) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    // Sound & Haptic Feedback on Validated Victory (Prompt 15 Section 13 & 20)
    LaunchedEffect(readyState?.isCompleted) {
        if (readyState?.isCompleted == true) {
            soundFeedback.playPuzzleCompleted()
            if (readyState.isHapticsEnabled) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    val formattedTime = formatTime(readyState?.elapsedTimeMs ?: 0L)
    val coveredCellsText = "${currentBoard.coveredCellCount} / ${currentBoard.totalRequiredCells}"
    val checkpointsText = "${readyState?.gameState?.visitedCheckpointCount ?: 0} / ${currentBoard.maxCheckpointNumber}"
    val isTapMode = readyState?.isTapInputMode ?: false
    val windowInfo = rememberZynpathWindowInfo()

    // Friendly non-intrusive explanation of rejected movements
    val rejectionMessage = when (readyState?.lastRejectionReason) {
        MoveRejectionReason.START_MUST_BE_CHECKPOINT_ONE -> "Start at Checkpoint #1"
        MoveRejectionReason.OUT_OF_BOUNDS -> "Stay within grid boundary"
        MoveRejectionReason.EXCLUDED_CELL -> "Cell is excluded"
        MoveRejectionReason.NON_ADJACENT -> "Move orthogonally (up, down, left, right)"
        MoveRejectionReason.BLOCKED_BY_WALL -> "Blocked by wall edge"
        MoveRejectionReason.CELL_ALREADY_VISITED -> "Cell already on path"
        MoveRejectionReason.WRONG_CHECKPOINT_ORDER -> "Visit checkpoints in ascending order"
        MoveRejectionReason.PREMATURE_FINAL_CHECKPOINT -> "Cover all cells before final checkpoint"
        MoveRejectionReason.GAME_ALREADY_COMPLETED -> "Puzzle already completed"
        MoveRejectionReason.GAME_PAUSED -> "Game is paused"
        MoveRejectionReason.INVALID_ACTION -> "Invalid action"
        null -> null
    }

    GameScreenBackground(
        modifier = modifier.fillMaxSize(),
        isReducedMotion = readyState?.isReducedMotion ?: false
    ) {
        // Master Reference Panel 05: Scenic Jungle River Backdrop
        Image(
            painter = painterResource(id = R.drawable.bg_gameplay_scene),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Translucent dark scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x990A1128))
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                // Master Reference Panel 05 Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Pause Button ||
                    IconButton(
                        onClick = {
                            viewModel?.onPauseGame()
                            showPauseDialog = true
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF131D33))
                            .border(1.dp, Color(0xFF24324E), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause Game",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Center: Level $levelId + 3-star bar
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Level $levelId",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val starsEarned = (readyState?.starsEarned ?: 2).coerceIn(0, 3)
                            repeat(3) { index ->
                                Icon(
                                    imageVector = if (index < starsEarned) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = if (index < starsEarned) Color(0xFFFFC107) else Color(0xFF4B5563),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    // Right: Restart / Reset Button
                    IconButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF131D33))
                            .border(1.dp, Color(0xFF24324E), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Level",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
        if (windowInfo.isLandscape) {
            // Landscape layout: 2-column side-by-side (board on left, controls and stats on right)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left pane: Board square, centered
                Box(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    val boardAspectRatio = currentBoard.columnCount.toFloat() / currentBoard.rowCount.toFloat()
                    // Floating ambient backdrop platform
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.96f)
                            .aspectRatio(boardAspectRatio)
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        chapterTheme.ambientGlowColor,
                                        chapterTheme.primaryAccent.copy(alpha = 0.08f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    PuzzleBoard(
                        boardState = currentBoard,
                        modifier = Modifier
                            .fillMaxHeight(0.96f)
                            .rejectionShake(readyState?.lastRejectionReason, readyState?.isReducedMotion ?: false),
                        lastRejectionReason = readyState?.lastRejectionReason,
                        isInputEnabled = readyState?.let { !it.isCompleted && !it.gameState.isPaused } ?: true,
                        isTapMode = isTapMode,
                        isReducedMotion = readyState?.isReducedMotion ?: false,
                        onCellEntered = { position ->
                            viewModel?.onCellEntered(position) ?: false
                        },
                        onPointerReleased = {
                            viewModel?.onPointerReleased()
                        }
                    )
                }

                // Right pane: Stats, restoration, feedback, controls (scrollable if needed)
                Column(
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Stats Header Bar with Personal Best support (Glassmorphic Game HUD)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(GameBrushes.panelGlass)
                                .border(GameBorders.panel, RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatColumn(
                                    label = "TIME",
                                    value = formattedTime,
                                    valueColor = GameWhite,
                                    accessibilityLabel = "Elapsed time: $formattedTime"
                                )
                                Box(modifier = Modifier.size(1.dp, 24.dp).background(GameRoyalBlue.copy(alpha = 0.45f)))
                                StatColumn(
                                    label = "MOVES",
                                    value = "${readyState?.gameState?.moveCount ?: 0}",
                                    valueColor = GameWhite,
                                    accessibilityLabel = "Moves made: ${readyState?.gameState?.moveCount ?: 0}"
                                )
                                Box(modifier = Modifier.size(1.dp, 24.dp).background(GameRoyalBlue.copy(alpha = 0.45f)))
                                StatColumn(
                                    label = "COVERAGE",
                                    value = coveredCellsText,
                                    valueColor = GameElectricCyan,
                                    accessibilityLabel = "Coverage: ${currentBoard.coveredCellCount} of ${currentBoard.totalRequiredCells} cells"
                                )
                                Box(modifier = Modifier.size(1.dp, 24.dp).background(GameRoyalBlue.copy(alpha = 0.45f)))
                                StatColumn(
                                    label = "CHECKPOINTS",
                                    value = checkpointsText,
                                    valueColor = GameGoldHighlight,
                                    accessibilityLabel = "Checkpoints visited: ${readyState?.gameState?.visitedCheckpointCount ?: 0} of ${currentBoard.maxCheckpointNumber}"
                                )
                            }
                        }

                        // Restoration Indicator Badge
                        if (readyState?.isRestoredSession == true) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundElevated)
                                    .border(1.dp, PathCyanGlow.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Restored saved progress",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PathCyanGlow
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Number-to-Number Connection Trail (Prompt 27 Task 5)
                        NumberConnectionTrail(
                            maxCheckpointNumber = currentBoard.maxCheckpointNumber,
                            visitedCheckpointCount = readyState?.gameState?.visitedCheckpointCount ?: 0,
                            nextRequiredCheckpoint = readyState?.nextRequiredCheckpoint ?: 1,
                            isCompleted = readyState?.isCompleted ?: false,
                            isReducedMotion = readyState?.isReducedMotion ?: false
                        )

                        // Progress, Status & Move Feedback
                        val landscapeGuidance = when {
                            readyState?.isCompleted == true -> "Level Complete! Valid continuous path covering all cells."
                            rejectionMessage != null -> rejectionMessage
                            readyState?.hintMessage != null && !readyState.showRecoveryDialog -> readyState.hintMessage
                            readyState != null && readyState.nextRequiredCheckpoint > currentBoard.maxCheckpointNumber && currentBoard.coveredCellCount < currentBoard.totalRequiredCells -> {
                                "All clues reached! Cover remaining ${currentBoard.totalRequiredCells - currentBoard.coveredCellCount} cells to complete."
                            }
                            readyState != null && readyState.levelId in 1..5 -> {
                                when {
                                    currentBoard.coveredCellCount == 0 -> "Start at 1."
                                    readyState.nextRequiredCheckpoint <= currentBoard.maxCheckpointNumber -> {
                                        if (readyState.levelId == 1) "Connect to the next number."
                                        else if (readyState.levelId == 2) "Keep one continuous path. No diagonals."
                                        else "Connecting Clue #${(readyState.nextRequiredCheckpoint - 1).coerceAtLeast(1)} → #${readyState.nextRequiredCheckpoint}"
                                    }
                                    else -> "Fill every cell to finish."
                                }
                            }
                            readyState != null && currentBoard.coveredCellCount == 0 -> "Touch Clue #1 to start drawing your continuous path"
                            readyState != null && readyState.nextRequiredCheckpoint <= currentBoard.maxCheckpointNumber -> {
                                val prev = (readyState.nextRequiredCheckpoint - 1).coerceAtLeast(1)
                                "Connecting Clue #$prev → #${readyState.nextRequiredCheckpoint} (${currentBoard.coveredCellCount}/${currentBoard.totalRequiredCells} filled)"
                            }
                            else -> null
                        }

                        if (landscapeGuidance != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            val isErr = rejectionMessage != null
                            val isWarn = readyState != null && readyState.nextRequiredCheckpoint > currentBoard.maxCheckpointNumber && currentBoard.coveredCellCount < currentBoard.totalRequiredCells
                            val isSucc = readyState?.isCompleted == true
                            val pBg = when {
                                isErr -> Color(0x33FF5252)
                                isWarn -> Color(0x33FFB703)
                                isSucc -> Color(0x33FFC247)
                                else -> Color(0x2221D4FD)
                            }
                            val pBorder = when {
                                isErr -> Color(0x99FF5252)
                                isWarn -> Color(0x99FFB703)
                                isSucc -> AccentGold.copy(alpha = 0.8f)
                                else -> GameElectricCyan.copy(alpha = 0.5f)
                            }
                            val pColor = when {
                                isErr -> Color(0xFFFF8A80)
                                isWarn -> Color(0xFFFFD166)
                                isSucc -> AccentGold
                                else -> GameElectricCyan
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(pBg)
                                    .border(1.dp, pBorder, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                                    .semantics {
                                        liveRegion = LiveRegionMode.Polite
                                        contentDescription = landscapeGuidance
                                    }
                            ) {
                                Text(
                                    text = landscapeGuidance,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = pColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Controls in Landscape
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GameplayActionButton(
                            label = "Undo",
                            icon = Icons.AutoMirrored.Filled.Undo,
                            onClick = { viewModel?.onUndoClicked() },
                            enabled = readyState?.isUndoAvailable ?: false,
                            contentDescription = if (readyState?.isUndoAvailable == true) "Undo last move" else "Undo unavailable, board at initial state",
                            modifier = Modifier.weight(1f)
                        )
                        val hintLabel = when {
                            readyState?.isHintLoading == true -> "..."
                            readyState?.isAdRewardLoading == true -> "Ad..."
                            readyState?.isUnlimitedHints == true -> "Hint ∞"
                            readyState?.levelHintState != null -> {
                                val lvlHints = readyState.levelHintState
                                when {
                                    lvlHints.freeHintsRemaining == 2 -> "Hint (2)"
                                    lvlHints.freeHintsRemaining == 1 -> "Hint (1)"
                                    lvlHints.rewardedHintsRemaining > 0 -> "Hint (+1)"
                                    else -> "Watch Ad"
                                }
                            }
                            readyState != null -> "Hint (${readyState.remainingHints})"
                            else -> "Hint"
                        }
                        val isHintActive = readyState?.activeHint?.targetCell != null
                        GameplayActionButton(
                            label = hintLabel,
                            icon = Icons.Default.Lightbulb,
                            onClick = { viewModel?.requestHint() },
                            enabled = readyState?.let { !it.isCompleted && !it.isHintLoading && !it.isAdRewardLoading } ?: true,
                            isLoading = readyState?.isHintLoading ?: false,
                            highlight = isHintActive,
                            contentDescription = if (readyState?.isHintLoading == true) "Analyzing puzzle for hint" else "Request hint, $hintLabel",
                            modifier = Modifier.weight(1.35f)
                        )
                        GameplayActionButton(
                            label = "Reset",
                            icon = Icons.Default.Refresh,
                            onClick = { showResetDialog = true },
                            enabled = readyState?.isResetAvailable ?: false,
                            contentDescription = "Reset puzzle path back to Checkpoint #1",
                            modifier = Modifier.weight(1f)
                        )
                        GameplayActionButton(
                            label = "Pause",
                            icon = Icons.Default.Pause,
                            onClick = {
                                viewModel?.onPauseGame()
                                showPauseDialog = true
                            },
                            enabled = readyState?.let { !it.isCompleted } ?: true,
                            contentDescription = "Pause game",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            // Portrait Layout: Responsive for compact phones, large phones, and tablets
            val isCompact = windowInfo.isCompactPhone
            val verticalSpacing = if (isCompact) 4.dp else 6.dp

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 560.dp) // Constrain width on tablets/foldables so controls don't overstretch
                        .padding(horizontal = 16.dp, vertical = verticalSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Master Reference Panel 05: Floating Timer Capsule
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF111E38))
                            .border(1.2.dp, Color(0xFF243656), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Timer",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = formattedTime,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Restoration Indicator Badge if a verified saved path was resumed
                    if (readyState?.isRestoredSession == true) {
                        Spacer(modifier = Modifier.height(verticalSpacing))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BackgroundElevated)
                                .border(1.dp, PathCyanGlow.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Restored saved progress",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = PathCyanGlow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(verticalSpacing))

                    // Number-to-Number Connection Trail (Prompt 27 Task 5)
                    NumberConnectionTrail(
                        maxCheckpointNumber = currentBoard.maxCheckpointNumber,
                        visitedCheckpointCount = readyState?.gameState?.visitedCheckpointCount ?: 0,
                        nextRequiredCheckpoint = readyState?.nextRequiredCheckpoint ?: 1,
                        isCompleted = readyState?.isCompleted ?: false,
                        isReducedMotion = readyState?.isReducedMotion ?: false
                    )

                    Spacer(modifier = Modifier.height(verticalSpacing))

                    // Main PuzzleBoard Canvas Renderer with Ambient Radial Glow Platform
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        val boardAspectRatio = currentBoard.columnCount.toFloat() / currentBoard.rowCount.toFloat()
                        val isHeightConstrained = (maxWidth / maxHeight) > boardAspectRatio
                        val boardModifier = if (isHeightConstrained) {
                            Modifier.fillMaxHeight(0.96f).aspectRatio(boardAspectRatio)
                        } else {
                            Modifier.fillMaxWidth(0.96f).aspectRatio(boardAspectRatio)
                        }

                        // Floating ambient backdrop platform
                        Box(
                            modifier = boardModifier
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            chapterTheme.ambientGlowColor,
                                            chapterTheme.primaryAccent.copy(alpha = 0.08f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        PuzzleBoard(
                            boardState = currentBoard,
                            modifier = boardModifier
                                .rejectionShake(readyState?.lastRejectionReason, readyState?.isReducedMotion ?: false),
                            lastRejectionReason = readyState?.lastRejectionReason,
                            isInputEnabled = readyState?.let { !it.isCompleted && !it.gameState.isPaused } ?: true,
                            isTapMode = isTapMode,
                            isReducedMotion = readyState?.isReducedMotion ?: false,
                            onCellEntered = { position ->
                                viewModel?.onCellEntered(position) ?: false
                            },
                            onPointerReleased = {
                                viewModel?.onPointerReleased()
                            }
                        )
                    }

                    // Subtle Status, Connection & Move Feedback Pill Area
                    val portraitGuidance = when {
                        readyState?.isCompleted == true -> {
                            "Level Complete! Valid continuous path covering all cells."
                        }
                        rejectionMessage != null -> {
                            rejectionMessage
                        }
                        readyState?.hintMessage != null && !readyState.showRecoveryDialog -> {
                            readyState.hintMessage
                        }
                        readyState != null && readyState.nextRequiredCheckpoint > currentBoard.maxCheckpointNumber && currentBoard.coveredCellCount < currentBoard.totalRequiredCells -> {
                            "All clues reached! Cover remaining ${currentBoard.totalRequiredCells - currentBoard.coveredCellCount} cells to complete."
                        }
                        readyState != null && readyState.levelId in 1..5 -> {
                            when {
                                currentBoard.coveredCellCount == 0 -> "Start at 1."
                                readyState.nextRequiredCheckpoint <= currentBoard.maxCheckpointNumber -> {
                                    if (readyState.levelId == 1) "Connect to the next number."
                                    else if (readyState.levelId == 2) "Keep one continuous path. No diagonals."
                                    else "Connecting Clue #${(readyState.nextRequiredCheckpoint - 1).coerceAtLeast(1)} → #${readyState.nextRequiredCheckpoint}"
                                }
                                else -> "Fill every cell to finish."
                            }
                        }
                        readyState != null && currentBoard.coveredCellCount == 0 -> {
                            "Touch Clue #1 to start drawing your continuous path"
                        }
                        readyState != null && readyState.nextRequiredCheckpoint <= currentBoard.maxCheckpointNumber -> {
                            val prevClue = (readyState.nextRequiredCheckpoint - 1).coerceAtLeast(1)
                            "Connecting Clue #$prevClue → #${readyState.nextRequiredCheckpoint} (${currentBoard.coveredCellCount}/${currentBoard.totalRequiredCells} cells filled)"
                        }
                        else -> null
                    }

                    if (portraitGuidance != null) {
                        Spacer(modifier = Modifier.height(verticalSpacing))
                        val isErr = rejectionMessage != null
                        val isWarn = readyState != null && readyState.nextRequiredCheckpoint > currentBoard.maxCheckpointNumber && currentBoard.coveredCellCount < currentBoard.totalRequiredCells
                        val isSucc = readyState?.isCompleted == true
                        val pBg = when {
                            isErr -> Color(0x33FF5252)
                            isWarn -> Color(0x33FFB703)
                            isSucc -> Color(0x33FFC247)
                            else -> Color(0x2221D4FD)
                        }
                        val pBorder = when {
                            isErr -> Color(0x99FF5252)
                            isWarn -> Color(0x99FFB703)
                            isSucc -> AccentGold.copy(alpha = 0.8f)
                            else -> GameElectricCyan.copy(alpha = 0.5f)
                        }
                        val pColor = when {
                            isErr -> Color(0xFFFF8A80)
                            isWarn -> Color(0xFFFFD166)
                            isSucc -> AccentGold
                            else -> GameElectricCyan
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(pBg)
                                .border(1.dp, pBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .semantics {
                                    liveRegion = LiveRegionMode.Polite
                                    contentDescription = portraitGuidance
                                }
                        ) {
                            Text(
                                text = portraitGuidance,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = pColor,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(verticalSpacing))
                    }

                    // Master Reference Panel 05: Bottom Action Toolbar (Hint, Shuffle, Undo)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = if (isCompact) 6.dp else 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Hint Button (Yellow bulb, red badge 3)
                        val hintCount = readyState?.levelHintState?.freeHintsRemaining
                            ?: readyState?.remainingHints
                            ?: 3
                        ZynpathMasterActionBadgeButton(
                            iconPainter = painterResource(id = R.drawable.ic_action_lightbulb),
                            label = "Hint",
                            badgeCount = hintCount,
                            iconTint = Color(0xFFFFD54F),
                            onClick = { viewModel?.requestHint() }
                        )

                        // 2. Shuffle / Reset Button (Cyan crossing arrows, red badge 2)
                        ZynpathMasterActionBadgeButton(
                            iconPainter = painterResource(id = R.drawable.ic_action_shuffle_arrows),
                            label = "Shuffle",
                            badgeCount = 2,
                            iconTint = Color(0xFF00E5FF),
                            onClick = { showResetDialog = true }
                        )

                        // 3. Undo Button (Cyan curved arrow, red badge 2)
                        ZynpathMasterActionBadgeButton(
                            iconPainter = painterResource(id = R.drawable.ic_action_undo_arrow),
                            label = "Undo",
                            badgeCount = 2,
                            iconTint = Color(0xFF00E5FF),
                            onClick = { viewModel?.onUndoClicked() }
                        )
                    }
                }
            }
        }
    }
}

    // Reset Confirmation Dialog
    if (showResetDialog) {
        ZynpathConfirmationDialog(
            title = "Reset Puzzle",
            message = "Clear your current path? The puzzle board will return to checkpoint #1. Historical best times are preserved.",
            confirmButtonText = "Reset",
            isDestructive = true,
            onConfirm = {
                viewModel?.onResetClicked()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }

    // Recovery Guidance Dialog (Prompt 14 Section 15 & 16)
    if (readyState?.showRecoveryDialog == true && readyState.activeHint?.type == HintType.RECOVERY_REQUIRED) {
        val steps = readyState.activeHint.stepsToRetract
        val rollbackPos = readyState.activeHint.rollbackPosition
        ZynpathConfirmationDialog(
            title = "Dead End Detected",
            message = readyState.activeHint.explanation.ifBlank {
                "The current path cannot reach a solution. Retract $steps ${if (steps == 1) "step" else "steps"} to return to a solvable branch."
            },
            confirmButtonText = "Retract $steps ${if (steps == 1) "Step" else "Steps"}",
            isDestructive = false,
            onConfirm = {
                if (rollbackPos != null) {
                    viewModel?.onConfirmRollback(rollbackPos)
                } else {
                    viewModel?.dismissRecoveryDialog()
                }
            },
            onDismiss = {
                viewModel?.dismissRecoveryDialog()
            }
        )
    }

    // Hint Limit Reached Dialog (Prompt 14 Section 29, Prompt 29 Section 21)
    if (readyState?.showLimitReachedDialog == true) {
        val context = LocalContext.current
        var currentContext = context
        while (currentContext is ContextWrapper && currentContext !is Activity) {
            currentContext = currentContext.baseContext
        }
        val activity = currentContext as? Activity

        val isAdReady = readyState.isRewardedAdAvailable && !readyState.isUnlimitedHints
        val isAdLoading = readyState.isAdRewardLoading

        AlertDialog(
            onDismissRequest = { viewModel?.dismissLimitReachedDialog() },
            title = {
                Text(
                    text = "No Hints Remaining",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestMint
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You have used both free hints for Level ${readyState.levelId}.\n\n" +
                                "Undo and Reset remain completely free and unlimited to help you solve the puzzle!",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    if (!readyState.isUnlimitedHints) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Optional: Watch a short video to earn +1 Hint for Level ${readyState.levelId}.",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!isAdReady && !isAdLoading && !readyState.rewardedAdError.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ad Status: ${readyState.rewardedAdError}",
                                fontSize = 11.sp,
                                color = Color(0xFFFF8A80)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isAdReady && activity != null) {
                        ZynpathPrimaryButton(
                            text = if (isAdLoading) "Loading Ad..." else "Watch Ad — Get 1 Hint",
                            onClick = { viewModel?.watchRewardedAdForHint(activity) },
                            enabled = !isAdLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (isAdLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = ForestMint,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Loading video ad...",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    } else {
                        ZynpathPrimaryButton(
                            text = "Retry Loading Ad",
                            onClick = { viewModel?.retryLoadingRewardedAd() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Video ad not ready yet. Tap retry or try again in a moment.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    ZynpathSecondaryButton(
                        text = "Keep Playing",
                        onClick = { viewModel?.dismissLimitReachedDialog() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = BackgroundCard,
            shape = ZynpathShapes.large
        )
    }

    // Pause Dialog (Prompt 15 Section 17)
    if (showPauseDialog || readyState?.gameState?.isPaused == true) {
        GameplayPauseDialog(
            worldId = worldId,
            levelId = levelId,
            timeFormatted = formattedTime,
            coveredCount = currentBoard.coveredCellCount,
            totalRequired = currentBoard.totalRequiredCells,
            checkpointsVisited = readyState?.gameState?.visitedCheckpointCount ?: 0,
            maxCheckpoints = currentBoard.maxCheckpointNumber,
            onResume = {
                viewModel?.onResumeGame()
                showPauseDialog = false
            },
            onRestart = {
                showPauseDialog = false
                showResetDialog = true
            },
            onReturnToLevels = {
                viewModel?.onNavigatedAway()
                showPauseDialog = false
                onBackClick()
            }
        )
    }

    // Rules / Info Dialog
    if (showRulesDialog) {
        ZynpathConfirmationDialog(
            title = "Zynpath Rules",
            message = "1. Begin at checkpoint #1.\n" +
                    "2. Move between orthogonally adjacent cells (no diagonals).\n" +
                    "3. Visit checkpoints in strictly ascending order (1 -> 2 -> ... -> N).\n" +
                    "4. Walls cannot be crossed.\n" +
                    "5. Victory requires covering 100% of cells AND ending at the final checkpoint.",
            confirmButtonText = "Got it",
            onConfirm = { showRulesDialog = false },
            onDismiss = { showRulesDialog = false }
        )
    }

    // Catalog Status / Next Level Error Dialog (Prompt 15 Section 22)
    if (nextLevelStatusMessage != null) {
        ZynpathConfirmationDialog(
            title = "Next Level Status",
            message = nextLevelStatusMessage ?: "",
            confirmButtonText = "Return to Levels",
            onConfirm = {
                nextLevelStatusMessage = null
                onBackClick()
            },
            onDismiss = {
                nextLevelStatusMessage = null
            }
        )
    }

    // World Completion Celebration Dialog (Prompt 41 Section 11-13)
    if (readyState?.worldCelebration != null) {
        WorldCompletionCelebrationDialog(
            world = readyState.worldCelebration,
            isReducedMotion = readyState.isReducedMotion,
            onDismiss = {
                viewModel?.dismissWorldCelebration()
            }
        )
    }

    // Validated Solo Victory Screen (Prompt 07/24)
    if (readyState?.isCompleted == true && readyState.worldCelebration == null) {
        val earnedStars = com.zynpath.game.core.puzzle.model.StarRatingPolicy.calculateStars(
            hintCount = readyState.completionResult?.hintCount ?: 0,
            undoResetCount = readyState.completionResult?.undoResetCount ?: (readyState.undoCount + readyState.resetCount)
        )
        val context = LocalContext.current
        val activity = context as? Activity
        SoloVictoryScreen(
            worldId = worldId,
            levelId = levelId,
            timeFormatted = formattedTime,
            elapsedTimeMs = readyState.elapsedTimeMs,
            moves = readyState.gameState.moveCount,
            earnedStars = earnedStars,
            coinsEarned = readyState.coinsEarned,
            isWorldRewardBonusEligible = readyState.isWorldRewardBonusEligible,
            onWatchWorldAdBonus = {
                if (activity != null) {
                    viewModel?.watchAdForWorldBonus(activity)
                }
            },
            personalBestFormatted = readyState.personalBestTimeMs?.let { formatTime(it) },
            isNewPersonalBest = readyState.isNewPersonalBest,
            isReducedMotion = readyState.isReducedMotion,
            isSfxEnabled = readyState.isSfxEnabled,
            isHapticsEnabled = readyState.isHapticsEnabled,
            isLoadingNext = isResolvingNextLevel,
            nextLevelStatusMessage = nextLevelStatusMessage,
            onNextLevel = {
                if (activity != null && !readyState.isWorldRewardBonusEligible && viewModel?.interstitialAdManager?.isAdReadyToShow() == true) {
                    viewModel.interstitialAdManager.showInterstitial(activity)
                }
                coroutineScope.launch {
                    isResolvingNextLevel = true
                    try {
                        if (viewModel != null) {
                            when (val resolution = viewModel.resolveNextLevel()) {
                                is NextLevelResolution.Available -> {
                                    if (onNextLevelClick != null) {
                                        onNextLevelClick(resolution.worldId, resolution.levelId)
                                    } else {
                                        onBackClick()
                                    }
                                }
                                is NextLevelResolution.WorldComplete -> {
                                    if (onNextWorldClick != null) {
                                        onNextWorldClick(resolution.nextWorldId)
                                    } else if (onNextLevelClick != null) {
                                        val firstLevel = com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorld(resolution.nextWorldId).startLevel
                                        onNextLevelClick(resolution.nextWorldId, firstLevel)
                                    } else {
                                        onBackClick()
                                    }
                                }
                                is NextLevelResolution.Locked -> {
                                    nextLevelStatusMessage = "Level ${resolution.levelId} is locked by progression."
                                }
                                is NextLevelResolution.Unavailable -> {
                                    nextLevelStatusMessage = resolution.message
                                }
                                is NextLevelResolution.CatalogCompleted -> {
                                    if (onJourneyCompleteClick != null) {
                                        onJourneyCompleteClick()
                                    } else {
                                        nextLevelStatusMessage = "Congratulations! You have completed all 300 levels in the Zynpath catalog!"
                                    }
                                }
                            }
                        } else {
                            val res = com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver.resolve(worldId, levelId)
                            when (val dest = res.destination) {
                                is com.zynpath.game.core.puzzle.model.ProgressionDestination.NextLevel -> {
                                    if (onNextLevelClick != null) {
                                        onNextLevelClick(dest.worldId, dest.levelId)
                                    } else {
                                        onBackClick()
                                    }
                                }
                                is com.zynpath.game.core.puzzle.model.ProgressionDestination.NextWorldEntry -> {
                                    if (onNextWorldClick != null) {
                                        onNextWorldClick(dest.worldId)
                                    } else if (onNextLevelClick != null) {
                                        val firstLevel = com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorld(dest.worldId).startLevel
                                        onNextLevelClick(dest.worldId, firstLevel)
                                    } else {
                                        onBackClick()
                                    }
                                }
                                is com.zynpath.game.core.puzzle.model.ProgressionDestination.JourneyComplete -> {
                                    if (onJourneyCompleteClick != null) {
                                        onJourneyCompleteClick()
                                    } else {
                                        nextLevelStatusMessage = "Congratulations! You have completed all 300 levels in the Zynpath catalog!"
                                    }
                                }
                            }
                        }
                    } finally {
                        isResolvingNextLevel = false
                    }
                }
            },
            onNextWorld = {
                if (activity != null && !readyState.isWorldRewardBonusEligible && viewModel?.interstitialAdManager?.isAdReadyToShow() == true) {
                    viewModel.interstitialAdManager.showInterstitial(activity)
                }
                coroutineScope.launch {
                    isResolvingNextLevel = true
                    try {
                        val destinationRes = viewModel?.resolveProgressionDestination()
                            ?: com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver.resolve(worldId, levelId)
                        val targetWorldId = destinationRes.nextWorldId
                        if (targetWorldId != null) {
                            val isUnlocked = viewModel?.isWorldUnlocked(targetWorldId) ?: true
                            if (isUnlocked) {
                                if (onNextWorldClick != null) {
                                    onNextWorldClick(targetWorldId)
                                } else if (onNextLevelClick != null) {
                                    val firstLevel = com.zynpath.game.core.puzzle.model.WorldConfiguration.getWorld(targetWorldId).startLevel
                                    onNextLevelClick(targetWorldId, firstLevel)
                                } else {
                                    onBackClick()
                                }
                            } else {
                                nextLevelStatusMessage = "World $targetWorldId is locked by progression rules."
                            }
                        } else {
                            if (onJourneyCompleteClick != null) {
                                onJourneyCompleteClick()
                            } else {
                                nextLevelStatusMessage = "You have conquered all worlds in the journey!"
                            }
                        }
                    } finally {
                        isResolvingNextLevel = false
                    }
                }
            },
            onJourneyComplete = {
                if (onJourneyCompleteClick != null) {
                    onJourneyCompleteClick()
                } else if (onHomeClick != null) {
                    onHomeClick()
                } else {
                    onBackClick()
                }
            },
            onReplay = {
                viewModel?.onReplayLevel()
            },
            onHome = onHomeClick ?: onBackClick,
            onDismissStatusMessage = { nextLevelStatusMessage = null }
        )
    }
}

/**
 * Visual trail representing consecutive number-to-number segments of the puzzle path (Prompt 27 Task 5).
 * Clearly depicts:
 * - Completed clue connections (solid cyan/gold link with checkmark).
 * - Active target clue and currently extending segment (pulsing cyan link and glow).
 * - Future unreached clues (calm dark royal blue plate).
 */
@Composable
private fun NumberConnectionTrail(
    maxCheckpointNumber: Int,
    visitedCheckpointCount: Int,
    nextRequiredCheckpoint: Int,
    isCompleted: Boolean,
    isReducedMotion: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (maxCheckpointNumber < 2) return

    val infiniteTransition = rememberInfiniteTransition(label = "TrailTransition")
    val pulseAlphaState = if (isReducedMotion) {
        remember { mutableFloatStateOf(0.70f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "trailPulse"
        )
    }
    val pulseAlpha = pulseAlphaState.value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x6608132B))
            .border(1.dp, GameRoyalBlue.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .semantics {
                contentDescription = if (isCompleted) {
                    "All $maxCheckpointNumber clues successfully connected in continuous path."
                } else {
                    "Connecting clue ${nextRequiredCheckpoint - 1} to clue $nextRequiredCheckpoint of $maxCheckpointNumber."
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (clue in 1..maxCheckpointNumber) {
                val isClueVisited = isCompleted || (clue < nextRequiredCheckpoint)
                val isNextTarget = !isCompleted && (clue == nextRequiredCheckpoint)

                // Clue Node Pill
                val nodeBackground = when {
                    isCompleted -> AccentGold
                    clue == 1 -> ForestMint
                    isClueVisited -> GameElectricCyan
                    isNextTarget -> Color(0xFF132A55)
                    else -> Color(0xFF0D1830)
                }
                val nodeBorderColor = when {
                    isCompleted -> AccentGold
                    isNextTarget -> GameElectricCyan.copy(alpha = pulseAlpha)
                    isClueVisited -> Color.White.copy(alpha = 0.85f)
                    else -> Color(0x444361EE)
                }
                val nodeTextColor = when {
                    isCompleted || clue == 1 || isClueVisited -> Color(0xFF070E22)
                    isNextTarget -> GameElectricCyan
                    else -> GameSecondaryText
                }

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(nodeBackground)
                        .border(
                            width = if (isNextTarget) 1.5.dp else 1.dp,
                            color = nodeBorderColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$clue",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = nodeTextColor
                    )
                }

                // Connecting Link to next clue
                if (clue < maxCheckpointNumber) {
                    val isSegmentCompleted = isCompleted || (clue + 1 < nextRequiredCheckpoint)
                    val isSegmentActive = !isCompleted && (clue + 1 == nextRequiredCheckpoint)

                    Row(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .widthIn(min = 12.dp, max = 32.dp)
                            .padding(horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (isSegmentActive) 2.5.dp else 1.5.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(
                                    when {
                                        isSegmentCompleted -> GameElectricCyan
                                        isSegmentActive -> GameElectricCyan.copy(alpha = pulseAlpha)
                                        else -> Color(0x334361EE)
                                    }
                                )
                        )
                        if (isSegmentCompleted) {
                            Text(
                                text = "✓",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameElectricCyan,
                                modifier = Modifier.padding(horizontal = 1.dp)
                            )
                        } else if (isSegmentActive) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(GameElectricCyan.copy(alpha = pulseAlpha))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    accessibilityLabel: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics {
            contentDescription = accessibilityLabel ?: "$label: $value"
        }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            maxLines = 1
        )
    }
}

/**
 * Polished gameplay control button with minimum 48dp touch targets and clear accessibility semantics.
 */
@Composable
private fun GameplayActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    highlight: Boolean = false,
    contentDescription: String? = null
) {
    val borderColor = when {
        !enabled -> Color(0x1AFFFFFF)
        highlight -> GameGoldHighlight.copy(alpha = 0.85f)
        else -> GameElectricCyan.copy(alpha = 0.45f)
    }
    val backgroundBrush = when {
        !enabled -> Brush.verticalGradient(
            colors = listOf(Color(0x200D1630), Color(0x300D1630))
        )
        highlight -> Brush.verticalGradient(
            colors = listOf(Color(0x40FFC247), Color(0x20E09500), Color(0x3516254A))
        )
        else -> Brush.verticalGradient(
            colors = listOf(Color(0x35162A5A), Color(0x250F1D3E))
        )
    }
    val contentTint = when {
        !enabled -> Color(0x608EA5C8)
        highlight -> GameGoldHighlight
        else -> GameElectricCyan
    }

    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundBrush)
            .border(
                width = if (highlight) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .padding(horizontal = 6.dp, vertical = 10.dp)
            .semantics {
                contentDescription?.let { this.contentDescription = it }
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = GameElectricCyan
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = label,
                fontSize = if (label.length > 15) 10.sp else if (label.length > 10) 11.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) GameWhite else Color(0x608EA5C8),
                maxLines = 1
            )
        }
    }
}

/**
 * Prompt 15 Section 17: Pause Overlay Dialog providing Resume, Restart attempt, and Return to Level Selection.
 */
@Composable
private fun GameplayPauseDialog(
    worldId: Int,
    levelId: Int,
    timeFormatted: String,
    coveredCount: Int,
    totalRequired: Int,
    checkpointsVisited: Int,
    maxCheckpoints: Int,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onReturnToLevels: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Game Paused",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GameElectricCyan,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "World $worldId • Level $levelId",
                    fontSize = 13.sp,
                    color = GameSecondaryText
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x66091122))
                        .border(1.dp, GameElectricCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TIME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = timeFormatted, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GameWhite)
                    }
                    Box(modifier = Modifier.size(1.dp, 28.dp).background(Color(0x3321D4FD)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "COVERAGE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$coveredCount / $totalRequired", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GameElectricCyan)
                    }
                    Box(modifier = Modifier.size(1.dp, 28.dp).background(Color(0x3321D4FD)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "CHECKPOINTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$checkpointsVisited / $maxCheckpoints", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GameGoldHighlight)
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZynpathPrimaryButton(
                    text = "Resume",
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onRestart,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) {
                        Text(text = "Restart Attempt", color = GameSecondaryText, fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(
                        onClick = onReturnToLevels,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) {
                        Text(text = "Return to Levels", color = GameSecondaryText, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color(0xF20D1630)
    )
}

// Old LevelCompletionDialog superseded by comprehensive SoloVictoryScreen (Prompt 07/24)

/**
 * Prompt 41 Section 11-13:
 * Dedicated first-time World Completion celebration dialog.
 * Recognizes canonical world mastery and ensures celebration does not replay on every navigation.
 */
@Composable
private fun WorldCompletionCelebrationDialog(
    world: com.zynpath.game.core.puzzle.model.WorldDefinition,
    isReducedMotion: Boolean,
    onDismiss: () -> Unit
) {
    val celebrationProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        if (!isReducedMotion) {
            celebrationProgress.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        } else {
            celebrationProgress.snapTo(1f)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Festive celebration particle canvas
                if (!isReducedMotion && celebrationProgress.value > 0f) {
                    Box(modifier = Modifier.fillMaxWidth().height(48.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val p = celebrationProgress.value
                            val colors = listOf(AccentGold, ForestMint, PathCyanGlow, Color(world.accentColorHex))
                            val particleCount = 28
                            for (i in 0 until particleCount) {
                                val angle = (i.toFloat() / particleCount) * Math.PI.toFloat() * 2f
                                val dist = (36.dp.toPx() + (i % 4) * 16.dp.toPx()) * p
                                val cx = size.width / 2f + kotlin.math.cos(angle) * dist
                                val cy = size.height / 2f + kotlin.math.sin(angle) * dist * 0.5f
                                val alpha = (1f - p).coerceIn(0f, 1f)
                                drawCircle(
                                    color = colors[i % colors.size].copy(alpha = alpha),
                                    radius = 3.dp.toPx(),
                                    center = Offset(cx, cy)
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AccentGold.copy(alpha = 0.2f))
                        .border(2.dp, AccentGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "World Completion Trophy",
                        tint = AccentGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "World ${world.worldId} Complete!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = world.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Congratulations! You conquered all ${world.totalLevels} levels in ${world.name} (Levels ${world.startLevel}–${world.endLevel}).",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                StatusBadge(
                    text = "★ World Milestone Achieved ★",
                    color = ForestMint
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ForestMint),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Continue Journey", fontWeight = FontWeight.Bold, color = BackgroundDark)
            }
        },
        containerColor = BackgroundCard,
        shape = RoundedCornerShape(20.dp)
    )
}

private fun formatTime(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun GameplayShellPreview() {
    GameplayShellScreen(worldId = 1, levelId = 1, onBackClick = {})
}
