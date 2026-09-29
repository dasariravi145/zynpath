package com.zynpath.game.feature.gameplay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameRewardBadge
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.components.RewardTier
import com.zynpath.game.core.designsystem.components.RewardType
import com.zynpath.game.core.designsystem.feedback.rememberSoundFeedbackManager
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.designsystem.theme.ChapterTheme
import com.zynpath.game.core.designsystem.theme.ChapterThemes
import kotlinx.coroutines.delay
import com.zynpath.game.core.puzzle.model.WorldConfiguration

/**
 * Premium Solo Victory & Level Completion Screen.
 *
 * Implements Prompt 07/24:
 * - Deep navy & royal blue fantasy game environment.
 * - Large glowing VICTORY heading and Level Completed subtitle.
 * - Original Zynpath completed Number Path illustration with cyan energy and gold terminal node.
 * - Sequential animated star reveal with gold glow and spring physics.
 * - Glassmorphic Game HUD stats panel (Time, Personal Best, Moves, 100% Coverage).
 * - Authoritative reward badges (Stars, Next Level unlocked, New Record).
 * - Tactile gold NEXT LEVEL button, REPLAY, and HOME actions.
 * - Sound feedback, haptics, and reduced-motion compliance.
 * - Strict reward idempotency (renders completion results without independently granting items).
 */
@Composable
fun SoloVictoryScreen(
    worldId: Int,
    levelId: Int,
    timeFormatted: String,
    elapsedTimeMs: Long,
    moves: Int,
    earnedStars: Int,
    personalBestFormatted: String? = null,
    isNewPersonalBest: Boolean = false,
    isReducedMotion: Boolean = false,
    isSfxEnabled: Boolean = true,
    isHapticsEnabled: Boolean = true,
    isLoadingNext: Boolean = false,
    nextLevelStatusMessage: String? = null,
    coinsEarned: Int = 0,
    isWorldRewardBonusEligible: Boolean = false,
    onWatchWorldAdBonus: (() -> Unit)? = null,
    onNextLevel: () -> Unit,
    onNextWorld: (() -> Unit)? = null,
    onJourneyComplete: (() -> Unit)? = null,
    onReplay: () -> Unit,
    onHome: () -> Unit,
    onDismissStatusMessage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onHome,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        SoloVictoryContent(
            worldId = worldId,
            levelId = levelId,
            timeFormatted = timeFormatted,
            elapsedTimeMs = elapsedTimeMs,
            moves = moves,
            earnedStars = earnedStars,
            personalBestFormatted = personalBestFormatted,
            isNewPersonalBest = isNewPersonalBest,
            isReducedMotion = isReducedMotion,
            isSfxEnabled = isSfxEnabled,
            isHapticsEnabled = isHapticsEnabled,
            isLoadingNext = isLoadingNext,
            nextLevelStatusMessage = nextLevelStatusMessage,
            coinsEarned = coinsEarned,
            isWorldRewardBonusEligible = isWorldRewardBonusEligible,
            onWatchWorldAdBonus = onWatchWorldAdBonus,
            onNextLevel = onNextLevel,
            onNextWorld = onNextWorld,
            onJourneyComplete = onJourneyComplete,
            onReplay = onReplay,
            onHome = onHome,
            onDismissStatusMessage = onDismissStatusMessage,
            modifier = modifier
        )
    }
}

/**
 * Core content of the Solo Victory screen, isolated for preview and independent testing.
 */
@Composable
fun SoloVictoryContent(
    worldId: Int,
    levelId: Int,
    timeFormatted: String,
    elapsedTimeMs: Long,
    moves: Int,
    earnedStars: Int,
    personalBestFormatted: String? = null,
    isNewPersonalBest: Boolean = false,
    isReducedMotion: Boolean = false,
    isSfxEnabled: Boolean = true,
    isHapticsEnabled: Boolean = true,
    isLoadingNext: Boolean = false,
    nextLevelStatusMessage: String? = null,
    coinsEarned: Int = 0,
    isWorldRewardBonusEligible: Boolean = false,
    onWatchWorldAdBonus: (() -> Unit)? = null,
    onNextLevel: () -> Unit,
    onNextWorld: (() -> Unit)? = null,
    onJourneyComplete: (() -> Unit)? = null,
    onReplay: () -> Unit,
    onHome: () -> Unit,
    onDismissStatusMessage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val soundManager = rememberSoundFeedbackManager(isSfxEnabled = isSfxEnabled)

    // Trigger celebratory sound and haptics exactly once upon entrance (Task 10)
    LaunchedEffect(Unit) {
        if (isSfxEnabled) {
            soundManager.playPuzzleCompleted()
        }
        if (isHapticsEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Celebration entrance progress (Task 9)
    val entranceProgress = remember { Animatable(if (isReducedMotion) 1f else 0f) }
    LaunchedEffect(isReducedMotion) {
        if (!isReducedMotion) {
            entranceProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
            )
        } else {
            entranceProgress.snapTo(1f)
        }
    }

    // Sequential Star Reveal Animations (Task 3)
    val star1Scale = remember { Animatable(if (isReducedMotion || earnedStars < 1) 1f else 0f) }
    val star2Scale = remember { Animatable(if (isReducedMotion || earnedStars < 2) 1f else 0f) }
    val star3Scale = remember { Animatable(if (isReducedMotion || earnedStars < 3) 1f else 0f) }

    LaunchedEffect(isReducedMotion, earnedStars) {
        if (!isReducedMotion) {
            if (earnedStars >= 1) {
                delay(200)
                if (isHapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                star1Scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.52f, stiffness = Spring.StiffnessMediumLow)
                )
            }
            if (earnedStars >= 2) {
                delay(120)
                if (isHapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                star2Scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.52f, stiffness = Spring.StiffnessMediumLow)
                )
            }
            if (earnedStars >= 3) {
                delay(120)
                if (isHapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                star3Scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.52f, stiffness = Spring.StiffnessMediumLow)
                )
            }
        }
    }

    val chapterTheme = remember(levelId) { ChapterThemes.getThemeForLevel(levelId) }
    val isMilestoneLevel = levelId in setOf(25, 50, 100, 150, 200, 250, 300)
    val isGrandFinale = levelId == 300

    GameScreenBackground(modifier = modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                // Background celebration confetti / particles
                if (!isReducedMotion) {
                    VictoryParticleCanvas(
                        progress = entranceProgress.value,
                        chapterTheme = chapterTheme,
                        isMilestone = isMilestoneLevel,
                        isFinale = isGrandFinale,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 520.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // TOP SECTION (Task 2 & Prompt 30 Tasks 10, 11, 12)
                    TopVictoryHeader(
                        worldId = worldId,
                        levelId = levelId,
                        chapterTheme = chapterTheme,
                        entranceProgress = entranceProgress.value,
                        isReducedMotion = isReducedMotion
                    )

                    // COMPLETED NUMBER PATH ARTWORK (Task 5)
                    CompletedPathArtwork(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .padding(horizontal = 8.dp),
                        isReducedMotion = isReducedMotion
                    )

                    // STAR REVEAL PRESENTATION (Task 3)
                    StarRevealRow(
                        earnedStars = earnedStars,
                        star1Scale = star1Scale.value,
                        star2Scale = star2Scale.value,
                        star3Scale = star3Scale.value
                    )

                    // PERSONAL BEST BADGE (if applicable)
                    if (isNewPersonalBest) {
                        NewPersonalBestBanner(
                            timeFormatted = timeFormatted,
                            isReducedMotion = isReducedMotion
                        )
                    }

                    // GAME STATS PANEL (Task 2)
                    VictoryStatsCard(
                        timeFormatted = timeFormatted,
                        personalBestFormatted = personalBestFormatted,
                        moves = moves,
                        isNewPersonalBest = isNewPersonalBest
                    )

                    // REWARD BADGES (Task 4)
                    VictoryRewardsSection(
                        levelId = levelId,
                        earnedStars = earnedStars,
                        isNewPersonalBest = isNewPersonalBest,
                        timeFormatted = timeFormatted,
                        coinsEarned = coinsEarned
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // LOWER SECTION — ACTION BUTTONS (Task 2, 6, 7, 8)
                    VictoryActionControls(
                        worldId = worldId,
                        levelId = levelId,
                        isLoadingNext = isLoadingNext,
                        isWorldRewardBonusEligible = isWorldRewardBonusEligible,
                        onWatchWorldAdBonus = onWatchWorldAdBonus,
                        onNextLevel = onNextLevel,
                        onNextWorld = onNextWorld,
                        onJourneyComplete = onJourneyComplete,
                        onReplay = onReplay,
                        onHome = onHome
                    )

                    // Status / Alert message when next level resolution is locked or completed
                    if (nextLevelStatusMessage != null) {
                        NextLevelAlertCard(
                            message = nextLevelStatusMessage,
                            onDismiss = onDismissStatusMessage
                        )
                    }

                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars).heightIn(min = 24.dp))
                }
            }
        }
    }
}

/**
 * Top celebration header displaying victory emblem, glowing title, and level subtitle.
 */
@Composable
private fun TopVictoryHeader(
    worldId: Int,
    levelId: Int,
    destinationResolution: com.zynpath.game.core.puzzle.model.NextDestinationResolution = remember(worldId, levelId) {
        com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver.resolve(worldId, levelId)
    },
    chapterTheme: ChapterTheme = ChapterThemes.getThemeForLevel(levelId),
    entranceProgress: Float,
    isReducedMotion: Boolean
) {
    val isGrandFinale = levelId == 300
    val isWorldComplete = destinationResolution.completionState == com.zynpath.game.core.puzzle.model.LevelCompletionState.WORLD_COMPLETED
    val isMilestone = levelId in setOf(25, 50, 100, 150, 200, 250, 300)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                val scale = if (isReducedMotion) 1f else 0.82f + 0.18f * entranceProgress
                scaleX = scale
                scaleY = scale
                alpha = if (isReducedMotion) 1f else entranceProgress
            }
            .semantics(mergeDescendants = true) {
                liveRegion = LiveRegionMode.Assertive
                contentDescription = if (isGrandFinale) {
                    "Grand Finale! All 300 levels conquered in Zynpath!"
                } else if (isWorldComplete) {
                    "World $worldId Completed! All levels conquered!"
                } else if (isMilestone) {
                    "Chapter Milestone! Level $levelId completed in ${chapterTheme.title}"
                } else {
                    "Victory! Level $levelId completed in World $worldId"
                }
            }
    ) {
        // Glowing Emblem (Crown for Finale/Centuries, Trophy for Milestones)
        Box(
            modifier = Modifier
                .size(if (isGrandFinale) 78.dp else 68.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (isGrandFinale) chapterTheme.milestoneGlow else GameGoldHighlight.copy(alpha = 0.35f),
                            GameGold.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = if (isGrandFinale) 2.dp else 1.dp,
                    brush = chapterTheme.milestoneBadgeBrush,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (isGrandFinale || levelId in setOf(100, 200)) {
                        R.drawable.ic_game_crown
                    } else {
                        R.drawable.ic_game_trophy
                    }
                ),
                contentDescription = null,
                tint = if (isGrandFinale) GameGoldHighlight else AccentGold,
                modifier = Modifier.size(if (isGrandFinale) 48.dp else 42.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isGrandFinale) "GRAND FINALE!" else "VICTORY!",
            fontSize = if (isGrandFinale) 34.sp else 32.sp,
            fontWeight = FontWeight.Black,
            color = GameGoldHighlight,
            letterSpacing = 3.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = if (isGrandFinale) {
                "ALL 300 NUMBER PATHS MASTERED"
            } else if (isWorldComplete) {
                "WORLD $worldId MASTERED"
            } else if (isMilestone) {
                "${chapterTheme.title.uppercase()} • MASTERED"
            } else {
                "PUZZLE COMPLETED"
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = chapterTheme.primaryAccent,
            letterSpacing = 2.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isMilestone) chapterTheme.celebrationBannerBrush else Brush.horizontalGradient(listOf(GameMidnightBlue.copy(alpha = 0.85f), GameMidnightBlue.copy(alpha = 0.85f)))
                )
                .border(
                    1.dp,
                    if (isMilestone) chapterTheme.milestoneGlow else Color(0xFF263966),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = when {
                    levelId == 300 -> "GRAND PATHMASTER • 100% JOURNEY CONQUERED!"
                    isWorldComplete -> "WORLD $worldId COMPLETED • ALL LEVELS MASTERED"
                    levelId == 25 -> "CHAPTER 1 MILESTONE • FIRST STEPS MASTERED"
                    levelId == 50 -> "CHAPTER 2 MILESTONE • SMART TURNS MASTERED"
                    levelId == 100 -> "CHAPTER 3 MILESTONE • PATH EXPLORER MASTERED"
                    levelId == 150 -> "CHAPTER 4 MILESTONE • STRATEGIC PATHS MASTERED"
                    levelId == 200 -> "CHAPTER 5 MILESTONE • EXPERT JOURNEY MASTERED"
                    levelId == 250 -> "CHAPTER 6 MILESTONE • MASTER TRAILS MASTERED"
                    else -> WorldConfiguration.formatLevelTitle(worldId, levelId).uppercase()
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isMilestone || isWorldComplete) GameGoldHighlight else TextSecondary,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Original Zynpath celebratory illustration of a completed Number Path.
 * Displays numbered nodes connected by glowing cyan energy and a gold terminal checkpoint.
 */
@Composable
private fun CompletedPathArtwork(
    modifier: Modifier = Modifier,
    isReducedMotion: Boolean = false
) {
    val pulseAnim = remember { Animatable(0f) }
    LaunchedEffect(isReducedMotion) {
        if (!isReducedMotion) {
            pulseAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xD90A132C))
            .border(1.dp, Color(0x661D4ED8), RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 4 Key checkpoint coordinates across the width in a celebratory gentle wave
            val p1 = Offset(w * 0.12f, h * 0.58f)
            val p2 = Offset(w * 0.38f, h * 0.40f)
            val p3 = Offset(w * 0.64f, h * 0.60f)
            val p4 = Offset(w * 0.88f, h * 0.42f)

            val path = Path().apply {
                moveTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                lineTo(p4.x, p4.y)
            }

            val pulse = if (isReducedMotion) 0.5f else pulseAnim.value

            // 1. Soft Outer Cyan Glow
            drawPath(
                path = path,
                color = PathCyanGlow.copy(alpha = 0.25f + 0.15f * pulse),
                style = Stroke(
                    width = 12.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 2. Bright Primary Cyan Core Path
            drawPath(
                path = path,
                color = GameElectricCyan,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 3. Draw Checkpoint Nodes
            // Node 1: Start (Mint)
            drawCheckpointNode(
                center = p1,
                radius = 14.dp.toPx(),
                fillColor = Color(0xFF064E3B),
                borderColor = ForestMint,
                glowColor = ForestMint.copy(alpha = 0.4f),
                label = "1"
            )

            // Node 2: Intermediate (Cyan)
            drawCheckpointNode(
                center = p2,
                radius = 13.dp.toPx(),
                fillColor = Color(0xFF0C2A4A),
                borderColor = PathCyanGlow,
                glowColor = GameElectricCyan.copy(alpha = 0.35f),
                label = "2"
            )

            // Node 3: Intermediate (Cyan)
            drawCheckpointNode(
                center = p3,
                radius = 13.dp.toPx(),
                fillColor = Color(0xFF0C2A4A),
                borderColor = PathCyanGlow,
                glowColor = GameElectricCyan.copy(alpha = 0.35f),
                label = "3"
            )

            // Node 4: Final Golden Terminal Node (Gold double halo)
            drawCircle(
                color = AccentGold.copy(alpha = 0.3f + 0.2f * pulse),
                radius = 19.dp.toPx(),
                center = p4
            )
            drawCheckpointNode(
                center = p4,
                radius = 15.dp.toPx(),
                fillColor = Color(0xFF78350F),
                borderColor = AccentGold,
                glowColor = GameGoldHighlight.copy(alpha = 0.5f),
                label = "★"
            )
        }
    }
}

private fun DrawScope.drawCheckpointNode(
    center: Offset,
    radius: Float,
    fillColor: Color,
    borderColor: Color,
    glowColor: Color,
    label: String
) {
    // Outer halo
    drawCircle(
        color = glowColor,
        radius = radius + 3.dp.toPx(),
        center = center
    )
    // Body fill
    drawCircle(
        color = fillColor,
        radius = radius,
        center = center
    )
    // Border stroke
    drawCircle(
        color = borderColor,
        radius = radius,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
}

/**
 * Sequential Star Reveal Row.
 * Earned stars sequentially spring into view with radiant gold glows.
 */
@Composable
private fun StarRevealRow(
    earnedStars: Int,
    star1Scale: Float,
    star2Scale: Float,
    star3Scale: Float
) {
    val starScales = listOf(star1Scale, star2Scale, star3Scale)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$earnedStars of 3 stars earned"
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until 3) {
                val isEarned = index < earnedStars
                val scale = if (isEarned) starScales[index] else 1f
                val isCenterStar = (index == 1)
                val starSize = if (isCenterStar) 58.dp else 48.dp
                val iconSize = if (isCenterStar) 34.dp else 26.dp

                Box(
                    modifier = Modifier
                        .size(starSize)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(CircleShape)
                        .background(
                            if (isEarned) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        GameGoldHighlight.copy(alpha = 0.40f),
                                        Color(0xFF281C06),
                                        Color(0xFF0F182F)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF16203B),
                                        Color(0xFF0A0F1F)
                                    )
                                )
                            }
                        )
                        .border(
                            width = if (isEarned) 1.8.dp else 1.dp,
                            color = if (isEarned) AccentGold else Color(0xFF243252),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_game_star),
                        contentDescription = null,
                        tint = if (isEarned) GameGoldHighlight else Color(0xFF3B4E73),
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = when (earnedStars) {
                3 -> "PERFECT CLEAR!"
                2 -> "GREAT JOB!"
                1 -> "LEVEL SOLVED!"
                else -> "COMPLETED!"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (earnedStars == 3) GameGoldHighlight else TextPrimary,
            letterSpacing = 1.sp
        )
    }
}

/**
 * Personal Best celebration banner when a new fastest record is set.
 */
@Composable
private fun NewPersonalBestBanner(
    timeFormatted: String,
    isReducedMotion: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF78350F).copy(alpha = 0.45f),
                        Color(0xFFB45309).copy(alpha = 0.55f),
                        Color(0xFF78350F).copy(alpha = 0.45f)
                    )
                )
            )
            .border(1.2.dp, AccentGold, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_game_crown),
                contentDescription = null,
                tint = GameGoldHighlight,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "★ NEW RECORD: $timeFormatted ★",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = GameGoldHighlight,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Game HUD Stats Card displaying Time, Personal Best, Moves, and Coverage.
 */
@Composable
private fun VictoryStatsCard(
    timeFormatted: String,
    personalBestFormatted: String?,
    moves: Int,
    isNewPersonalBest: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GameMidnightBlue.copy(alpha = 0.88f))
            .border(1.dp, Color(0xFF1E3A6E), RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                label = "TIME",
                value = timeFormatted,
                valueColor = TextPrimary
            )

            StatDivider()

            StatItem(
                label = "BEST",
                value = personalBestFormatted ?: timeFormatted,
                valueColor = if (isNewPersonalBest) AccentGold else TextPrimary
            )

            StatDivider()

            StatItem(
                label = "MOVES",
                value = "$moves",
                valueColor = TextPrimary
            )

            StatDivider()

            StatItem(
                label = "COVERAGE",
                value = "100%",
                valueColor = GameElectricCyan
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics {
            contentDescription = "$label: $value"
        }
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .size(1.dp, 28.dp)
            .background(Color(0xFF253760))
    )
}

/**
 * Authoritative reward badges earned during completion.
 * Displays earned stars, level progression milestone, and record achievements.
 */
@Composable
private fun VictoryRewardsSection(
    levelId: Int,
    earnedStars: Int,
    isNewPersonalBest: Boolean,
    timeFormatted: String,
    coinsEarned: Int = 0
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "REWARDS & UNLOCKS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val badgeItems = buildList {
            add(
                Triple(
                    RewardType.STAR,
                    "STARS" to "+$earnedStars",
                    RewardTier.GOLD
                )
            )
            if (coinsEarned > 0) {
                add(
                    Triple(
                        RewardType.COIN,
                        "COINS" to "+$coinsEarned",
                        RewardTier.GOLD
                    )
                )
            }
            add(
                Triple(
                    RewardType.TROPHY,
                    (when (levelId) {
                        20 -> "WORLD 1 COMPLETE"
                        50 -> "WORLD 2 COMPLETE"
                        100 -> "WORLD 3 COMPLETE"
                        150 -> "WORLD 4 COMPLETE"
                        200 -> "WORLD 5 COMPLETE"
                        300 -> "CAMPAIGN CONQUERED"
                        else -> "PROGRESS"
                    }) to (when (levelId) {
                        20 -> "World 1 Mastered!"
                        50 -> "World 2 Mastered!"
                        100 -> "Path Explorer!"
                        150 -> "Strategist!"
                        200 -> "Expert!"
                        300 -> "Pathmaster 300"
                        else -> "Level ${levelId + 1}"
                    }),
                    if (levelId in setOf(20, 50, 100, 150, 200, 300)) RewardTier.GOLD else RewardTier.DIAMOND
                )
            )
            if (isNewPersonalBest) {
                add(
                    Triple(
                        RewardType.CROWN,
                        "RECORD" to "Personal Best",
                        RewardTier.GOLD
                    )
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            badgeItems.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    rowItems.forEach { (type, textPair, tier) ->
                        GameRewardBadge(
                            type = type,
                            title = textPair.first,
                            value = textPair.second,
                            tier = tier,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Bottom action controls: Gold NEXT LEVEL / NEXT WORLD / JOURNEY COMPLETE button, REPLAY, and HOME.
 */
@Composable
private fun VictoryActionControls(
    worldId: Int,
    levelId: Int,
    destinationResolution: com.zynpath.game.core.puzzle.model.NextDestinationResolution = remember(worldId, levelId) {
        com.zynpath.game.core.puzzle.model.ProgressionDestinationResolver.resolve(worldId, levelId)
    },
    isLoadingNext: Boolean,
    isWorldRewardBonusEligible: Boolean = false,
    onWatchWorldAdBonus: (() -> Unit)? = null,
    onNextLevel: () -> Unit,
    onNextWorld: (() -> Unit)? = null,
    onJourneyComplete: (() -> Unit)? = null,
    onReplay: () -> Unit,
    onHome: () -> Unit
) {
    var isActionDispatched by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Optional Rewarded Ad Bonus for World Completion (+25 coins)
        if (isWorldRewardBonusEligible && onWatchWorldAdBonus != null) {
            com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                text = "▶  WATCH AD & CLAIM +25 COINS",
                onClick = onWatchWorldAdBonus,
                style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.GOLD,
                height = 48.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Primary Action: NEXT LEVEL, NEXT WORLD, or JOURNEY COMPLETE (Prompt 07/24 & Section 2)
        GamePrimaryButton(
            text = if (isLoadingNext) "Loading..." else destinationResolution.buttonLabel,
            onClick = {
                if (!isActionDispatched && !isLoadingNext) {
                    isActionDispatched = true
                    when (destinationResolution.destination) {
                        is com.zynpath.game.core.puzzle.model.ProgressionDestination.NextLevel -> {
                            onNextLevel()
                        }
                        is com.zynpath.game.core.puzzle.model.ProgressionDestination.NextWorldEntry -> {
                            if (onNextWorld != null) {
                                onNextWorld()
                            } else {
                                onNextLevel()
                            }
                        }
                        is com.zynpath.game.core.puzzle.model.ProgressionDestination.JourneyComplete -> {
                            if (onJourneyComplete != null) {
                                onJourneyComplete()
                            } else {
                                onHome()
                            }
                        }
                    }
                }
            },
            enabled = !isLoadingNext,
            isLoading = isLoadingNext,
            modifier = Modifier.fillMaxWidth()
        )

        // Secondary Actions Row: REPLAY and HOME
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GameSecondaryButton(
                text = "REPLAY",
                onClick = onReplay,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = GameElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            GameSecondaryButton(
                text = "HOME",
                onClick = onHome,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Status message alert card for edge cases like locked progression or catalog completion.
 */
@Composable
private fun NextLevelAlertCard(
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            TextButton(onClick = onDismiss) {
                Text(
                    text = "DISMISS",
                    fontWeight = FontWeight.Bold,
                    color = GameElectricCyan,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Ambient celebratory confetti particle canvas with chapter-specific sparkle palette.
 */
@Composable
private fun VictoryParticleCanvas(
    progress: Float,
    chapterTheme: ChapterTheme = ChapterThemes.CHAPTER_1,
    isMilestone: Boolean = false,
    isFinale: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (progress <= 0f) return@Canvas
        val colors = chapterTheme.celebrationSparkles
        val particleCount = when {
            isFinale -> 48
            isMilestone -> 36
            else -> 24
        }
        val cx = size.width / 2f
        val cy = size.height * 0.28f

        for (i in 0 until particleCount) {
            val angle = (i.toFloat() / particleCount) * (Math.PI * 2).toFloat()
            val speed = 60.dp.toPx() + (i % 6) * 20.dp.toPx()
            val distance = speed * progress
            val px = cx + kotlin.math.cos(angle) * distance
            val py = cy + kotlin.math.sin(angle) * distance * 0.65f + (progress * progress * 35.dp.toPx())
            val alpha = (1f - progress * 0.85f).coerceIn(0f, 1f)

            drawCircle(
                color = colors[i % colors.size].copy(alpha = alpha),
                radius = if (i % 2 == 0) 3.dp.toPx() else 1.8.dp.toPx(),
                center = Offset(px, py)
            )
        }
    }
}

@Preview(name = "Solo Victory Screen - 3 Stars", showBackground = true, backgroundColor = 0xFF070B19)
@Composable
private fun SoloVictoryContentPreview() {
    SoloVictoryContent(
        worldId = 1,
        levelId = 1,
        timeFormatted = "0:42",
        elapsedTimeMs = 42000L,
        moves = 16,
        earnedStars = 3,
        personalBestFormatted = "0:42",
        isNewPersonalBest = true,
        onNextLevel = {},
        onReplay = {},
        onHome = {}
    )
}
