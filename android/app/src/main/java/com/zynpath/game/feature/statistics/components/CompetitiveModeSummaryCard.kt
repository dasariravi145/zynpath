package com.zynpath.game.feature.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.zynpath.game.core.analytics.model.CompetitiveModeSummary
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary

/**
 * Summary card for 1v1 competitive game modes (Quick Duel & Friend Duel).
 * Displays authoritative wins, losses, ties, win rate, and sample size N.
 * Implements Prompt 30 Sections 28, 29, 30, 32, 33, 34 & 51.
 */
@Composable
fun CompetitiveModeSummaryCard(
    modeTitle: String,
    summary: CompetitiveModeSummary,
    isFriendDuel: Boolean = false,
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
                    text = modeTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "N = ${summary.matchesPlayed}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PathCyanGlow,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BackgroundElevated)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (summary.matchesPlayed == 0) {
                BoxStateEmpty(message = "No matches recorded for this mode yet.")
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill(
                        label = "Wins",
                        value = summary.wins.toString(),
                        valueColor = ForestMint,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "Losses",
                        value = summary.losses.toString(),
                        valueColor = TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "Ties",
                        value = summary.ties.toString(),
                        valueColor = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    val decisiveMatches = summary.matchesPlayed - summary.ties
                    StatPill(
                        label = "Win Rate",
                        value = if (decisiveMatches > 0) {
                            "${(summary.winRate * 100).toInt()}%"
                        } else {
                            "—"
                        },
                        valueColor = PathCyanGlow,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Validated Solves: ${summary.validatedCompletions}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    val decisiveMatches = summary.matchesPlayed - summary.ties
                    Text(
                        text = if (decisiveMatches > 0) "Decisive: $decisiveMatches" else "0 decisive",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            if (isFriendDuel) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Private matches are strictly isolated from Quick Duel leaderboards.",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun BoxStateEmpty(message: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundElevated),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 12.sp,
            color = TextMuted
        )
    }
}
