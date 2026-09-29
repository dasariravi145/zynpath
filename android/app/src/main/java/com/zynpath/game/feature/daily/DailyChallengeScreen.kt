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
import com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton
import com.zynpath.game.core.designsystem.components.ZynpathMasterSegmentedTabs
import com.zynpath.game.core.designsystem.components.ZynpathPillStyle
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
import androidx.compose.material.icons.filled.HelpOutline
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
 * Handles presentation for ready state: switches between Daily Challenge Hub Overview
 * and the active interactive gameplay board.
 */
@Composable
private fun DailyChallengeReadyScreen(
    state: DailyChallengeUiState.Ready,
    viewModel: DailyChallengeViewModel,
    soundFeedback: SoundFeedbackManager,
    onBackClick: () -> Unit,
    onNavigateToLeaderboard: (String) -> Unit
) {
    // When there is an active path in progress and puzzle is not completed, default to active play
    var isPlaying by rememberSaveable {
        mutableStateOf(state.gameState.currentPath.size > 1 && !state.gameState.isCompleted)
    }

    if (isPlaying) {
        DailyChallengeActiveBoardView(
            state = state,
            viewModel = viewModel,
            soundFeedback = soundFeedback,
            onBackToOverview = { isPlaying = false },
            onNavigateToLeaderboard = onNavigateToLeaderboard
        )
    } else {
        DailyChallengeHubView(
            state = state,
            viewModel = viewModel,
            onStartPlay = { isPlaying = true },
            onReplay = {
                viewModel.onReplayChallenge()
                isPlaying = true
            },
            onBackClick = onBackClick,
            onRulesClick = { viewModel.toggleRulesDialog(true) },
            onNavigateToLeaderboard = { onNavigateToLeaderboard(state.challenge.dateKey) }
        )
    }

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
}

/**
 * 1. HERO & HUB VIEW:
 * Displays Daily Challenge hero header, challenge card, streak/rewards, and leaderboard action.
 */
@Composable
private fun DailyChallengeHubView(
    state: DailyChallengeUiState.Ready,
    viewModel: DailyChallengeViewModel,
    onStartPlay: () -> Unit,
    onReplay: () -> Unit,
    onBackClick: () -> Unit,
    onRulesClick: () -> Unit,
    onNavigateToLeaderboard: () -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RefNavyDark)
            .statusBarsPadding()
    ) {
        // Master Reference Panel 07 Top Bar: Currency, Stars, Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF131D33))
                    .border(1.dp, Color(0xFF24324E), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZynpathMasterCurrencyPill(
                    iconRes = R.drawable.ic_game_coin,
                    amount = java.text.NumberFormat.getIntegerInstance().format(state.coinBalance),
                    iconTint = RefGoldPrimary
                )
                ZynpathMasterCurrencyPill(
                    iconRes = R.drawable.ic_game_star,
                    amount = state.totalStars.toString(),
                    iconTint = Color(0xFF00E5FF)
                )
                IconButton(
                    onClick = onRulesClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131D33))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Title: REWARDS
            Text(
                text = "REWARDS",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            // Segmented Navigation: Daily (active gold pill), Missions, Achievements
            ZynpathMasterSegmentedTabs(
                tabs = listOf("Daily", "Missions", "Achievements"),
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
                modifier = Modifier.fillMaxWidth()
            )

            if (selectedTabIndex == 0) {
                // DAILY TAB (Panel 07)
                // 1. Daily Rewards Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(RefNavySurface)
                        .border(1.dp, Color(0xFF1E3A8A), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_rewards_gift),
                            contentDescription = null,
                            modifier = Modifier.size(46.dp)
                        )
                        Column {
                            Text(
                                text = "Daily Rewards",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Play daily and earn amazing rewards!",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // 2. 5-Day Streak Progress Row (Day 1..Day 5)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val days = listOf(
                        Triple("Day 1", "20", state.isDailyLoginClaimed),
                        Triple("Day 2", "20", false),
                        Triple("Day 3", "20", false),
                        Triple("Day 4", "20", false),
                        Triple("Day 5", "Gift", false)
                    )

                    days.forEachIndexed { index, (dayLabel, rewardVal, isChecked) ->
                        val isCurrentDay = index == 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrentDay) Color(0xFF1E3A8A) else RefNavySurface)
                                .border(
                                    width = if (isCurrentDay) 1.5.dp else 1.dp,
                                    color = if (isCurrentDay) RefGoldPrimary else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrentDay) RefGoldPrimary else Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (isChecked) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                } else if (index == 2) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = RefGoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else if (index == 4) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_game_coin),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = rewardVal,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // 3. Hero Reward Presentation: Glowing Treasure Chest
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_rewards_chest),
                        contentDescription = "Reward Chest",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(160.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "DAILY LOGIN REWARD",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RefGoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (state.isDailyLoginClaimed) "Claimed +20 Coins!" else "+20 Coins",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                // 4. Primary CTA: Big Green "Claim" Pill Button
                ZynpathMasterPillButton(
                    text = if (state.isDailyLoginClaimed) "CLAIMED ✓" else "Claim",
                    onClick = {
                        viewModel.claimDailyLoginReward()
                    },
                    style = ZynpathPillStyle.GREEN,
                    enabled = !state.isDailyLoginClaimed,
                    height = 54.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.isDailyLoginClaimed && !state.isDailyLoginAdBonusClaimed) {
                    val context = LocalContext.current
                    val activity = context as? Activity
                    Spacer(modifier = Modifier.height(4.dp))
                    ZynpathMasterPillButton(
                        text = "WATCH AD FOR +20 BONUS",
                        onClick = {
                            if (activity != null) {
                                viewModel.claimDailyLoginAdBonus(activity)
                            }
                        },
                        style = ZynpathPillStyle.GOLD,
                        height = 46.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 5. Daily Challenge Gameplay Launch Section
                ZynpathMasterPillButton(
                    text = if (state.gameState.isCompleted) "REPLAY TODAY'S QUEST" else "PLAY TODAY'S QUEST",
                    onClick = if (state.gameState.isCompleted) onReplay else onStartPlay,
                    style = ZynpathPillStyle.GOLD,
                    height = 50.dp,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            } else {
                // Missions & Achievements View
                DailyProgressRewardsSection(state = state)
            }

            // Global Standings Card
            GamePanel(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GLOBAL STANDINGS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (state.isLeaderboardEligible) {
                                "Your solve time is eligible for ranking!"
                            } else {
                                "Compete with solvers worldwide"
                            },
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    GameSecondaryButton(
                        text = "LEADERBOARD",
                        onClick = onNavigateToLeaderboard,
                        fillMaxWidth = false,
                        height = 42.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Hero Header with title, actual date, status badge, and lightweight Compose number-path artwork.
 */
@Composable
private fun DailyHeroSection(
    challenge: DailyChallengeDefinition,
    availability: DailyChallengeAvailability,
    isCompleted: Boolean,
    isReducedMotion: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Status Badge Pill
        val statusText = when {
            isCompleted || availability == DailyChallengeAvailability.COMPLETED -> "COMPLETED TODAY"
            availability == DailyChallengeAvailability.IN_PROGRESS -> "IN PROGRESS"
            availability == DailyChallengeAvailability.AVAILABLE -> "TODAY'S QUEST AVAILABLE"
            else -> availability.name.replace('_', ' ')
        }

        val statusColor = when {
            isCompleted || availability == DailyChallengeAvailability.COMPLETED -> GameGoldHighlight
            availability == DailyChallengeAvailability.IN_PROGRESS -> GameElectricCyan
            else -> ForestMint
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .border(1.dp, statusColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "DAILY CHALLENGE",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = GameGoldHighlight,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Date: ${challenge.dateKey} • ${challenge.difficultyTier}",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Lightweight Compose Number-Path Illustration
        DailyPathIllustration(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .padding(horizontal = 8.dp),
            isReducedMotion = isReducedMotion
        )
    }
}

/**
 * Original number-path visual using lightweight Compose graphics.
 */
@Composable
private fun DailyPathIllustration(
    modifier: Modifier = Modifier,
    isReducedMotion: Boolean = false
) {
    val pulseAnim = remember { Animatable(0f) }
    LaunchedEffect(isReducedMotion) {
        if (!isReducedMotion) {
            pulseAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xD90A132C))
            .border(1.dp, Color(0x551E3A8A), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val p1 = Offset(w * 0.12f, h * 0.55f)
            val p2 = Offset(w * 0.38f, h * 0.38f)
            val p3 = Offset(w * 0.64f, h * 0.62f)
            val p4 = Offset(w * 0.88f, h * 0.40f)

            val path = Path().apply {
                moveTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                lineTo(p4.x, p4.y)
            }

            val pulse = if (isReducedMotion) 0.5f else pulseAnim.value

            // Soft cyan outer glow
            drawPath(
                path = path,
                color = PathCyanGlow.copy(alpha = 0.22f + 0.15f * pulse),
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Electric cyan core
            drawPath(
                path = path,
                color = GameElectricCyan,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw Checkpoint Nodes
            drawCircle(color = ForestMint, radius = 11.dp.toPx(), center = p1)
            drawCircle(color = Color(0xFF064E3B), radius = 9.dp.toPx(), center = p1)

            drawCircle(color = GameElectricCyan, radius = 10.dp.toPx(), center = p2)
            drawCircle(color = Color(0xFF0C2A4A), radius = 8.dp.toPx(), center = p2)

            drawCircle(color = GameElectricCyan, radius = 10.dp.toPx(), center = p3)
            drawCircle(color = Color(0xFF0C2A4A), radius = 8.dp.toPx(), center = p3)

            // Terminal Gold Checkpoint
            drawCircle(color = AccentGold.copy(alpha = 0.3f + 0.2f * pulse), radius = 15.dp.toPx(), center = p4)
            drawCircle(color = AccentGold, radius = 12.dp.toPx(), center = p4)
            drawCircle(color = Color(0xFF78350F), radius = 9.dp.toPx(), center = p4)
        }
    }
}

/**
 * 2. CHALLENGE CARD:
 * Displays today's puzzle status and appropriate action button.
 */
@Composable
private fun DailyChallengeStatusCard(
    state: DailyChallengeUiState.Ready,
    onStartPlay: () -> Unit,
    onReplay: () -> Unit
) {
    GamePanel(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TODAY'S PUZZLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${state.challenge.gridDimensions.rows}×${state.challenge.gridDimensions.columns} Grid • ${state.challenge.difficultyTier}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                StatusBadge(
                    text = if (state.isOnlineAttempt) "VERIFIED" else "LOCAL",
                    color = if (state.isOnlineAttempt) ForestMint else Color(0xFFFFB74D)
                )
            }

            when {
                state.gameState.isCompleted || state.availability == DailyChallengeAvailability.COMPLETED -> {
                    // Completed State
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0B1736))
                            .border(1.dp, Color(0xFF263966), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TIME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Text(formatTime(state.elapsedTimeMs), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Box(modifier = Modifier.size(1.dp, 24.dp).background(Color(0xFF253760)))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MOVES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Text("${state.moveCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Box(modifier = Modifier.size(1.dp, 24.dp).background(Color(0xFF253760)))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("RESULT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Text("SOLVED", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForestMint)
                            }
                        }
                    }

                    GameSecondaryButton(
                        text = "PLAY AGAIN (REPLAY)",
                        onClick = onReplay,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                state.availability == DailyChallengeAvailability.IN_PROGRESS || state.gameState.currentPath.size > 1 -> {
                    // In-progress State
                    Text(
                        text = "Active session: ${state.gameState.coveredCellCount} of ${state.gameState.totalRequiredCells} cells covered.",
                        fontSize = 13.sp,
                        color = GameElectricCyan
                    )

                    GamePrimaryButton(
                        text = "CONTINUE CHALLENGE",
                        onClick = onStartPlay,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    // Available State
                    Text(
                        text = "Connect all numbered checkpoints in ascending order and achieve 100% board coverage to maintain your streak!",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    GamePrimaryButton(
                        text = "PLAY CHALLENGE",
                        onClick = onStartPlay,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * 3. PROGRESS & REWARDS SECTION:
 * Displays current streak, max streak, and best time.
 */
@Composable
private fun DailyProgressRewardsSection(state: DailyChallengeUiState.Ready) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "PROGRESS & STREAK",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.2.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GameRewardBadge(
                type = RewardType.STAR,
                title = "STREAK",
                value = "${state.currentStreak} Days",
                tier = RewardTier.GOLD,
                modifier = Modifier.weight(1f)
            )

            GameRewardBadge(
                type = RewardType.CROWN,
                title = "BEST STREAK",
                value = "${state.maxStreak} Days",
                tier = RewardTier.DIAMOND,
                modifier = Modifier.weight(1f)
            )

            state.personalBestTimeMs?.let { bestMs ->
                GameRewardBadge(
                    type = RewardType.TROPHY,
                    title = "BEST TIME",
                    value = formatTime(bestMs),
                    tier = RewardTier.GOLD,
                    modifier = Modifier.weight(1f)
                )
            }
        }
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
        MoveRejectionReason.PREMATURE_FINAL_CHECKPOINT -> "Cover all cells before final checkpoint"
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
                        Icon(Icons.Default.HelpOutline, contentDescription = "Rules", tint = TextSecondary)
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
        val context = LocalContext.current
        val activity = context as? Activity
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
            isDailyChallengeAdBonusClaimed = state.isDailyChallengeAdBonusClaimed,
            onWatchAdBonus = {
                if (activity != null) {
                    viewModel.claimDailyChallengeAdBonus(activity)
                }
            },
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
    isDailyChallengeAdBonusClaimed: Boolean,
    onWatchAdBonus: () -> Unit,
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
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0A132C))
                .border(1.2.dp, AccentGold.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Trophy Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AccentGold.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_game_trophy),
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "DAILY CHALLENGE SOLVED!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = GameGoldHighlight,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${challenge.dateKey} • ${challenge.difficultyTier}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                // Stats row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F1A36))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TIME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text(formattedSolveTime, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Box(modifier = Modifier.size(1.dp, 24.dp).background(Color(0xFF253760)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("STREAK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Text("$currentStreak Days", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                        }
                    }
                }

                // Economy payout badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E3A8A).copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_game_coin),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+15 COINS EARNED!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RefGoldPrimary
                    )
                }

                if (!isDailyChallengeAdBonusClaimed) {
                    ZynpathMasterPillButton(
                        text = "WATCH AD FOR +15 BONUS",
                        onClick = onWatchAdBonus,
                        style = ZynpathPillStyle.GOLD,
                        height = 42.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                GamePrimaryButton(
                    text = "VIEW LEADERBOARD",
                    onClick = onLeaderboardClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameSecondaryButton(
                        text = "REPLAY",
                        onClick = onReplay,
                        modifier = Modifier.weight(1f)
                    )
                    GameSecondaryButton(
                        text = "DONE",
                        onClick = onHome,
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
