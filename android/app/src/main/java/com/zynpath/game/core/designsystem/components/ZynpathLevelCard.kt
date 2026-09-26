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
    val bestTimeSeconds: Int? = null
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

    Box(
        modifier = modifier
            .size(72.dp)
            .clip(shape)
            .background(
                when {
                    isLocked -> BackgroundCard.copy(alpha = 0.4f)
                    level.isCurrent -> ForestMint
                    isCompleted -> BackgroundElevated
                    else -> BackgroundCard
                }
            )
            .then(
                if (isCompleted) Modifier.border(1.dp, AccentGold.copy(alpha = 0.5f), shape)
                else if (level.isCurrent) Modifier.border(2.dp, ForestMint, shape)
                else Modifier
            )
            .clickable(enabled = !isLocked) { onClick() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked level ${level.levelNumber}",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${level.levelNumber}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (level.isCurrent) BackgroundDark else TextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

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
                            tint = when {
                                level.isCurrent && isStarEarned -> BackgroundDark
                                isStarEarned -> AccentGold
                                level.isCurrent -> BackgroundDark.copy(alpha = 0.3f)
                                else -> TextMuted.copy(alpha = 0.3f)
                            },
                            modifier = Modifier.size(11.dp)
                        )
                    }
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
            level = LevelCardData(1, LevelState.COMPLETED, stars = 3),
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
