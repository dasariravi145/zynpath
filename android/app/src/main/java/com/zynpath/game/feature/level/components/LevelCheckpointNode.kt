package com.zynpath.game.feature.level.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.LevelCardData
import com.zynpath.game.core.designsystem.components.LevelState
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

import com.zynpath.game.core.designsystem.theme.ChapterTheme
import com.zynpath.game.core.designsystem.theme.ChapterThemes

/**
 * Premium Level Checkpoint Node on the Adventure World Map.
 *
 * Requirements fulfilled (Prompt 05/24 Task 3, Prompt 30 Tasks 4, 5, 7):
 * - Displays actual level number in bold with non-color-only state cues.
 * - Completed: Chapter-tinted glowing disc with earned stars row beneath.
 * - Current unlocked level: Prominent pulsing beacon with chapter theme highlight and "PLAY" badge.
 * - Chapter milestones: Special milestone icon indicator (trophy/crown) and gold aura.
 * - Locked level: Muted stone/midnight disc with lock icon.
 * - Respects reduced-motion preferences.
 * - Accessible minimum 48dp touch target with descriptive semantics.
 */
@Composable
fun LevelCheckpointNode(
    level: LevelCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isReducedMotion: Boolean = false,
    chapterTheme: ChapterTheme = ChapterThemes.getThemeForLevel(level.levelNumber)
) {
    val isCompleted = level.state == LevelState.COMPLETED
    val isUnlocked = level.state == LevelState.UNLOCKED
    val isLocked = level.state == LevelState.LOCKED
    val isCurrent = level.isCurrent || (isUnlocked && !isCompleted)
    val isMilestone = level.isMilestone || level.levelNumber in setOf(25, 50, 100, 150, 200, 250, 300)
    val isFinale = level.levelNumber == 300

    // Pulse animation for the current active level checkpoint
    val infiniteTransition = rememberInfiniteTransition(label = "CheckpointPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val activeScale = if (isCurrent && !isReducedMotion) pulseScale else 1f
    val activeRingAlpha = if (isCurrent && !isReducedMotion) pulseAlpha else 0f

    val accessibilityDesc = buildString {
        append("Level ${level.localLevelNumber}")
        if (isFinale) {
            append(", Grand Finale")
        } else if (isMilestone) {
            append(", Chapter Milestone ${level.milestoneTitle ?: ""}")
        }
        when {
            isCompleted -> append(", Completed with ${level.stars} stars")
            isCurrent -> append(", Current unlocked challenge, Tap to play")
            isUnlocked -> append(", Unlocked")
            else -> append(", Locked")
        }
    }

    Column(
        modifier = modifier
            .semantics {
                role = Role.Button
                contentDescription = accessibilityDesc
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 34.dp),
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Top Badges Row: Milestone Indicator and/or PLAY Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Milestone Mini Badge
            if (isMilestone && !isCurrent) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isCompleted) GameGold.copy(alpha = 0.25f) else GameMidnightBlue
                        )
                        .border(
                            1.dp,
                            if (isCompleted) GameGold else GameRoyalBlue.copy(alpha = 0.6f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 1.5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(
                                id = if (isFinale || level.levelNumber in setOf(100, 200)) {
                                    R.drawable.ic_game_crown
                                } else {
                                    R.drawable.ic_game_trophy
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (isFinale) "FINALE" else "MILESTONE",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            letterSpacing = 0.6.sp,
                            color = if (isCompleted) GameGoldHighlight else GameSecondaryText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
            }

            // Current Level Mini Badge Pill ("PLAY")
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GameGoldHighlight)
                        .border(1.dp, GameGold, RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = GameDeepNavy,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (isFinale) "FINALE" else "PLAY",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                            color = GameDeepNavy
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Circular Checkpoint Disc Container
        Box(
            modifier = Modifier.size(62.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing Outer Aura for the Current Level
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(activeScale)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = chapterTheme.nodeCurrentRingColor.copy(alpha = activeRingAlpha),
                            shape = CircleShape
                        )
                )
            }

            // Milestone Halo for Completed or Current Milestones
            if (isMilestone && (isCompleted || isCurrent)) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.2.dp,
                            color = chapterTheme.milestoneGlow,
                            shape = CircleShape
                        )
                )
            }

            // Main Disc Surface
            val discModifier = when {
                isCompleted -> {
                    Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF0F3668),
                                    GameDeepNavy
                                )
                            )
                        )
                        .border(
                            width = if (isMilestone) 2.5.dp else 2.dp,
                            brush = if (isMilestone) {
                                Brush.verticalGradient(
                                    listOf(GameGoldHighlight, GameGold, chapterTheme.primaryAccent)
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        GameGoldHighlight,
                                        chapterTheme.nodeCompletedGlow.copy(alpha = 0.7f)
                                    )
                                )
                            },
                            shape = CircleShape
                        )
                }
                isCurrent -> {
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF1E3A75),
                                    GameMidnightBlue
                                )
                            )
                        )
                        .border(
                            width = 2.4.dp,
                            brush = chapterTheme.nodeBorderBrush,
                            shape = CircleShape
                        )
                }
                isUnlocked -> {
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF11254A),
                                    GameDeepNavy
                                )
                            )
                        )
                        .border(
                            width = 1.6.dp,
                            color = chapterTheme.primaryAccent.copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                }
                else -> {
                    // Locked state
                    Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0A1329))
                        .border(
                            width = 1.2.dp,
                            color = GameRoyalBlue.copy(alpha = 0.35f),
                            shape = CircleShape
                        )
                }
            }

            Box(
                modifier = discModifier,
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = GameSecondaryText.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${level.localLevelNumber}",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = GameSecondaryText.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    Text(
                        text = "${level.localLevelNumber}",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Black,
                        fontSize = if (isCurrent) 19.sp else 17.sp,
                        letterSpacing = (-0.5).sp,
                        color = when {
                            isCurrent -> GameGoldHighlight
                            isMilestone && isCompleted -> GameGoldHighlight
                            isCompleted -> GameWhite
                            else -> chapterTheme.primaryAccent
                        }
                    )
                }
            }
        }

        // Stars Row for Completed Levels
        if (isCompleted) {
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val earnedStars = level.stars.coerceIn(0, 3)
                for (s in 1..3) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_game_star),
                        contentDescription = null,
                        modifier = Modifier
                            .size(11.dp)
                            .then(if (s <= earnedStars) Modifier else Modifier.scale(0.85f)),
                        alpha = if (s <= earnedStars) 1f else 0.3f
                    )
                }
            }
        }
    }
}

