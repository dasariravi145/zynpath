package com.zynpath.game.feature.gameplay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.core.designsystem.feedback.rememberSoundFeedbackManager
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import kotlinx.coroutines.delay

/**
 * Zynpath Simplified Solo Victory Celebration Modal.
 *
 * Implements Complete Victory Screen Simplification:
 * - Replaces the full-screen results page with a compact modal popup over the completed puzzle.
 * - Completed puzzle remains visible behind a dark translucent scrim (#CC040C1E).
 * - Instant next-level transition with zero scrolling required.
 * - Displays ONLY essential results:
 *   1. Trophy icon and "VICTORY!" heading
 *   2. "Level X Completed"
 *   3. Authoritative star rating (1–3 stars)
 *   4. Coins earned (+100 Coins for genuine first clear, omitted on replay)
 *   5. Large dominant NEXT LEVEL action
 *   6. Small REPLAY and HOME secondary actions
 * - Strict idempotency and reward protection.
 */
@Composable
fun SoloVictoryScreen(
    worldId: Int,
    levelId: Int,
    timeFormatted: String = "",
    elapsedTimeMs: Long = 0L,
    moves: Int = 0,
    earnedStars: Int = 3,
    personalBestFormatted: String? = null,
    isNewPersonalBest: Boolean = false,
    isReducedMotion: Boolean = false,
    isSfxEnabled: Boolean = true,
    isHapticsEnabled: Boolean = true,
    isLoadingNext: Boolean = false,
    nextLevelStatusMessage: String? = null,
    totalCells: Int = 36,
    coveredCells: Int = totalCells,
    isPathValidated: Boolean = true,
    coinsEarned: Int = 0,
    currentWalletBalance: Int = 0,
    levelCoinsAwarded: Int = 0,
    worldCoinsAwarded: Int = 0,
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
            totalCells = totalCells,
            coveredCells = coveredCells,
            isPathValidated = isPathValidated,
            coinsEarned = coinsEarned,
            currentWalletBalance = currentWalletBalance,
            levelCoinsAwarded = levelCoinsAwarded,
            worldCoinsAwarded = worldCoinsAwarded,
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
 * Compact Victory Content Card over gameplay scrim.
 */
@Composable
fun SoloVictoryContent(
    worldId: Int,
    levelId: Int,
    timeFormatted: String = "",
    elapsedTimeMs: Long = 0L,
    moves: Int = 0,
    earnedStars: Int = 3,
    personalBestFormatted: String? = null,
    isNewPersonalBest: Boolean = false,
    isReducedMotion: Boolean = false,
    isSfxEnabled: Boolean = true,
    isHapticsEnabled: Boolean = true,
    isLoadingNext: Boolean = false,
    nextLevelStatusMessage: String? = null,
    totalCells: Int = 36,
    coveredCells: Int = totalCells,
    isPathValidated: Boolean = true,
    coinsEarned: Int = 0,
    currentWalletBalance: Int = 0,
    levelCoinsAwarded: Int = 0,
    worldCoinsAwarded: Int = 0,
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

    // Trigger celebratory audio and haptics upon entrance
    LaunchedEffect(Unit) {
        if (isSfxEnabled) {
            soundManager.playPuzzleCompleted()
        }
        if (isHapticsEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Play coin collection sound when coins are awarded
    LaunchedEffect(coinsEarned) {
        if (coinsEarned > 0 && isSfxEnabled) {
            delay(400)
            soundManager.playCoinCollected()
        }
    }

    // Modal Background: Translucent Scrim keeping the completed puzzle visible
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC040C1E)),
        contentAlignment = Alignment.Center
    ) {
        // Modal Card
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 380.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF80A1635),
                            GameMidnightBlue
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GameGoldHighlight.copy(alpha = 0.85f),
                            GameElectricCyan.copy(alpha = 0.5f),
                            Color(0x1102449B)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .glossyTopHighlight(cornerRadius = 24.dp, highlightAlpha = 0.16f)
                .padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Trophy Icon & Victory Heading
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GameGoldHighlight.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Victory Trophy",
                        tint = GameGoldHighlight,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "VICTORY!",
                    style = GameTypography.screenHeading.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ),
                    color = GameGoldHighlight
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Level $levelId Completed",
                    style = GameTypography.labelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = GameSecondaryText
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Sequential Star Reveal (1–3 Stars)
                val star1Scale = remember { Animatable(if (isReducedMotion || earnedStars < 1) 1f else 0f) }
                val star2Scale = remember { Animatable(if (isReducedMotion || earnedStars < 2) 1f else 0f) }
                val star3Scale = remember { Animatable(if (isReducedMotion || earnedStars < 3) 1f else 0f) }

                LaunchedEffect(isReducedMotion, earnedStars) {
                    if (!isReducedMotion) {
                        if (earnedStars >= 1) {
                            delay(150)
                            if (isHapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            star1Scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow)
                            )
                        }
                        if (earnedStars >= 2) {
                            delay(100)
                            if (isHapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            star2Scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow)
                            )
                        }
                        if (earnedStars >= 3) {
                            delay(100)
                            if (isHapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            star3Scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.semantics {
                        contentDescription = "$earnedStars of 3 Stars Earned"
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (earnedStars >= 1) GameGoldHighlight else Color(0xFF15223E),
                        modifier = Modifier
                            .size(34.dp)
                            .graphicsLayer {
                                scaleX = if (earnedStars >= 1) star1Scale.value else 1f
                                scaleY = if (earnedStars >= 1) star1Scale.value else 1f
                            }
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (earnedStars >= 2) GameGoldHighlight else Color(0xFF15223E),
                        modifier = Modifier
                            .size(34.dp)
                            .graphicsLayer {
                                scaleX = if (earnedStars >= 2) star2Scale.value else 1f
                                scaleY = if (earnedStars >= 2) star2Scale.value else 1f
                            }
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (earnedStars >= 3) GameGoldHighlight else Color(0xFF15223E),
                        modifier = Modifier
                            .size(34.dp)
                            .graphicsLayer {
                                scaleX = if (earnedStars >= 3) star3Scale.value else 1f
                                scaleY = if (earnedStars >= 3) star3Scale.value else 1f
                            }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (earnedStars) {
                        3 -> "★ ★ ★  3 Stars Earned"
                        2 -> "★ ★  2 Stars Earned"
                        else -> "★  1 Star Earned"
                    },
                    style = GameTypography.labelMedium.copy(
                        fontSize = 12.sp,
                        color = GameGold,
                        fontWeight = FontWeight.Medium
                    )
                )

                // 3. Compact Coins-Earned Information (Only When Applicable)
                if (coinsEarned > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F1E36))
                            .border(1.dp, GameGoldHighlight.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_game_coin),
                                contentDescription = "Coins",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "+$coinsEarned Coins",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GameGoldHighlight
                            )
                        }
                    }
                }

                // Status Notice (if any)
                if (nextLevelStatusMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = nextLevelStatusMessage,
                        style = GameTypography.secondaryInfo.copy(
                            fontSize = 11.sp,
                            color = GameElectricCyan,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                // 4. Large Dominant Action: NEXT LEVEL Button
                Spacer(modifier = Modifier.height(18.dp))

                val isGrandFinale = levelId == 300
                val primaryButtonText = if (isGrandFinale) "COMPLETE JOURNEY" else "NEXT LEVEL"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFB300),
                                    GameGoldHighlight,
                                    Color(0xFFFFB300)
                                )
                            )
                        )
                        .border(1.2.dp, Color(0xFFFFF3D0), RoundedCornerShape(16.dp))
                        .glossyTopHighlight(cornerRadius = 16.dp, highlightAlpha = 0.22f)
                        .clickable(enabled = !isLoadingNext) {
                            if (isGrandFinale && onJourneyComplete != null) {
                                onJourneyComplete()
                            } else {
                                onNextLevel()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoadingNext) {
                        CircularProgressIndicator(
                            color = Color(0xFF0F1A36),
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = primaryButtonText,
                            color = Color(0xFF0A1428),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // 5. Two Small Secondary Actions: REPLAY and HOME
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // REPLAY Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0C1935))
                            .border(1.dp, Color(0x3321D4FD), RoundedCornerShape(14.dp))
                            .glossyTopHighlight(cornerRadius = 14.dp, highlightAlpha = 0.14f)
                            .clickable(onClick = onReplay),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Replay",
                                tint = GameElectricCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "REPLAY",
                                color = GameWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // HOME Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0C1935))
                            .border(1.dp, Color(0x3321D4FD), RoundedCornerShape(14.dp))
                            .glossyTopHighlight(cornerRadius = 14.dp, highlightAlpha = 0.14f)
                            .clickable(onClick = onHome),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Home",
                                tint = GameSecondaryText,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "HOME",
                                color = GameWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
