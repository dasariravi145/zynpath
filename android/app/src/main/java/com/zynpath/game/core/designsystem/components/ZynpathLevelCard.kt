package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary

enum class LevelState {
    LOCKED,
    UNLOCKED,
    COMPLETED
}

data class LevelCardData(
    val levelNumber: Int,
    val state: LevelState,
    val stars: Int = 0,
    val isCurrent: Boolean = false,
    val bestTimeSeconds: Int? = null,
    val isAvailable: Boolean = true,
    val difficultyBand: String? = null,
    val chapterTitle: String? = null,
    val isMilestone: Boolean = false,
    val milestoneTitle: String? = null,
    val difficultyCategory: String? = null,
    val localLevelNumber: Int = com.zynpath.game.core.puzzle.model.WorldConfiguration.toLocalLevel(levelNumber)
)

@Composable
fun ZynpathLevelCard(
    level: LevelCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val isLocked = level.state == LevelState.LOCKED
    val isCompleted = level.state == LevelState.COMPLETED

    val bestTimeFormatted = level.bestTimeSeconds?.takeIf { it > 0 }?.let { sec ->
        val m = sec / 60
        val s = sec % 60
        if (m > 0) String.format("%d:%02d", m, s) else "${s}s"
    }

    val accessibilityDesc = when {
        isLocked -> "Level ${level.localLevelNumber}, Locked"
        isCompleted -> "Level ${level.localLevelNumber}, Completed, ${level.stars} stars" +
            (if (bestTimeFormatted != null) ", Best time $bestTimeFormatted" else "")
        level.isCurrent -> "Level ${level.localLevelNumber}, Current objective, Available to play"
        else -> "Level ${level.localLevelNumber}, Available to play"
    }

    Box(
        modifier = modifier
            .size(76.dp)
            .clip(shape)
            .background(
                when {
                    isLocked -> BackgroundCard.copy(alpha = 0.4f)
                    !level.isAvailable -> BackgroundCard.copy(alpha = 0.3f)
                    level.isCurrent -> ForestMint
                    isCompleted -> BackgroundElevated
                    else -> BackgroundCard
                }
            )
            .then(
                when {
                    isCompleted -> Modifier.border(1.dp, AccentGold.copy(alpha = 0.6f), shape)
                    level.isCurrent && level.isAvailable -> Modifier.border(2.dp, ForestMint, shape)
                    !isLocked -> Modifier.border(1.dp, BackgroundCard, shape)
                    else -> Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(4.dp)
            .semantics { contentDescription = accessibilityDesc },
        contentAlignment = Alignment.Center
    ) {
        if (isLocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${level.localLevelNumber}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (level.isCurrent && level.isAvailable) BackgroundDark else TextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                if (!level.isAvailable && !isCompleted) {
                    Text(
                        text = "PENDING",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                } else if (isCompleted) {
                    // Stars row
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..3) {
                            val isStarEarned = i <= level.stars
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isStarEarned) AccentGold else TextMuted.copy(alpha = 0.3f),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }

                    if (bestTimeFormatted != null) {
                        Text(
                            text = bestTimeFormatted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                } else {
                    // Ready / Unlocked state indicator
                    Text(
                        text = if (level.isCurrent) "NEXT" else "PLAY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (level.isCurrent) BackgroundDark else ForestMint
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun ZynpathLevelCardPreview() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ZynpathLevelCard(
            level = LevelCardData(1, LevelState.COMPLETED, stars = 3, bestTimeSeconds = 42),
            onClick = {}
        )
        ZynpathLevelCard(
            level = LevelCardData(2, LevelState.UNLOCKED, isCurrent = true),
            onClick = {}
        )
        ZynpathLevelCard(
            level = LevelCardData(3, LevelState.LOCKED),
            onClick = {}
        )
    }
}
