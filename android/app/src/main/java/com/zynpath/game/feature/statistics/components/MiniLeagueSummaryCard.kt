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
import com.zynpath.game.core.analytics.model.MiniLeagueSummary
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary

/**
 * Summary card for Mini League multi-player tournament records.
 * Adheres strictly to multi-player finish positions; never forces into 1v1 win/loss model.
 * Implements Prompt 30 Sections 28, 31, 34 & 51.
 */
@Composable
fun MiniLeagueSummaryCard(
    summary: MiniLeagueSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = summary.accessibleDescription
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mini League (4-Player)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "N = ${summary.participations}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BackgroundElevated)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (summary.participations == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Mini League tournaments played yet.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill(
                        label = "1st Place",
                        value = summary.firstPlaceFinishes.toString(),
                        valueColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "Top 3 Podium",
                        value = summary.topThreeFinishes.toString(),
                        valueColor = PathCyanGlow,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "Podium Rate",
                        value = "${(summary.podiumRate * 100).toInt()}%",
                        valueColor = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "Avg Finish",
                        value = if (summary.participations > 0) "%.1f".format(summary.averageFinishPosition) else "—",
                        valueColor = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Validated Solves: ${summary.validatedCompletions} across ${summary.participations} events",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Mini League records reflect 4-player standings and are not conflated with 1v1 duels.",
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundElevated)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = TextMuted
        )
    }
}
