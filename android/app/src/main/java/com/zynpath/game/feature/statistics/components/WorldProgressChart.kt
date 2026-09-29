package com.zynpath.game.feature.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.analytics.model.WorldProgressItem
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary

/**
 * Accessible visual progress summary for canonical Worlds 1–6 (300 levels).
 * Does not rely on color alone; provides full textual and numeric data.
 * Implements Prompt 30 Sections 11, 12, 13 & 51.
 */
@Composable
fun WorldProgressChart(
    worlds: List<WorldProgressItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Solo World Progression",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Canonical 300 free levels across 6 worlds",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            worlds.forEach { item ->
                WorldProgressRow(item = item)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun WorldProgressRow(
    item: WorldProgressItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundElevated)
            .padding(12.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = item.accessibleDescription
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "World ${item.worldId}: ${item.worldName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Levels ${item.levelRangeStart}–${item.levelRangeEnd}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            Text(
                text = "${item.completedLevels}/${item.totalLevels} (${(item.completionPercentage * 100).toInt()}%)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.isCompleted) ForestMint else PathCyanGlow
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { item.completionPercentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (item.isCompleted) ForestMint else PathCyanGlow,
            trackColor = BackgroundCard
        )

        if (item.fastestSolveTimeMs != null) {
            Spacer(modifier = Modifier.height(6.dp))
            val seconds = item.fastestSolveTimeMs / 1000.0
            Text(
                text = "Best solve: ${"%.1f".format(seconds)}s",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}
