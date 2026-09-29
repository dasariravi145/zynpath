package com.zynpath.game.feature.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.zynpath.game.core.analytics.model.CompletionTrendItem
import com.zynpath.game.core.designsystem.theme.AccentPurple
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import kotlin.math.max

/**
 * Accessible Compose-based completion trend chart.
 * Distinguishes first-time level completions from replays.
 * Implements Prompt 30 Sections 16, 17, 50 & 51.
 */
@Composable
fun CompletionTrendChart(
    trends: List<CompletionTrendItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val totalFirstTime = trends.sumOf { it.firstTimeCompletions.toInt() }
            val totalReplays = trends.sumOf { it.replayCompletions.toInt() }
            val totalSessions = trends.sumOf { it.totalCompletions.toInt() }

            Text(
                text = "Completion Trends",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Recorded activity: $totalSessions total ($totalFirstTime first-time, $totalReplays replays)",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (trends.isEmpty() || totalSessions == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recorded completions in this period.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            } else {
                val maxCount = max(1, trends.maxOf { it.totalCompletions })
                val recentTrends = trends.takeLast(7)

                // Accessible overall description
                val summaryDescription = "Completion trend chart showing $totalSessions completed puzzles across ${recentTrends.size} days."

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundElevated)
                        .padding(12.dp)
                        .semantics { contentDescription = summaryDescription }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        recentTrends.forEach { item ->
                            val heightFraction = (item.totalCompletions.toFloat() / maxCount).coerceIn(0.05f, 1.0f)
                            val dayDesc = "${item.dateKey}: ${item.totalCompletions} puzzles completed (${item.firstTimeCompletions} first-time, ${item.replayCompletions} replays)"

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .semantics { contentDescription = dayDesc }
                            ) {
                                Text(
                                    text = item.totalCompletions.toString(),
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (item.firstTimeCompletions > 0) ForestMint else AccentPurple)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                val label = if (item.dateKey.length >= 5) item.dateKey.takeLast(5) else item.dateKey
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(ForestMint)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "First-Time Solves", fontSize = 11.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(AccentPurple)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Replays", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
