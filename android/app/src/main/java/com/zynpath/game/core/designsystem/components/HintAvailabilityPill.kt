package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSuccessGreen
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.puzzle.experience.hint.LevelHintState

/**
 * Reusable pill component indicating hint status for the active level.
 *
 * Implements Prompt 25 Task 8:
 * - Shows free hint balance (e.g. "Free Hints: 2/2").
 * - When free hints are exhausted, displays "Watch Ad for +1 Hint".
 */
@Composable
fun HintAvailabilityPill(
    hintState: LevelHintState,
    onWatchAdClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    if (hintState.freeHintsRemaining > 0) {
        // Free hints available
        Row(
            modifier = modifier
                .background(
                    color = GameSuccessGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = GameSuccessGreen.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Free Hints Available",
                tint = GameSuccessGreen,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Free Hints: ${hintState.freeHintsRemaining}/${hintState.freeHintsTotal}",
                color = GameSuccessGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else if (hintState.rewardedHintsRemaining > 0) {
        // Rewarded credits available
        Row(
            modifier = modifier
                .background(
                    color = GameElectricCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = GameElectricCyan.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Bonus Hints",
                tint = GameElectricCyan,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Bonus Hints: ${hintState.rewardedHintsRemaining}",
                color = GameElectricCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        // Free hints exhausted: Prompt for rewarded ad
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            GameRoyalBlue,
                            GameOrangeAccent.copy(alpha = 0.7f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = GameGold.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(enabled = enabled, onClick = onWatchAdClick)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Watch Rewarded Ad for Hint",
                tint = GameGold,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "Watch Ad for +1 Hint",
                color = GameWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
