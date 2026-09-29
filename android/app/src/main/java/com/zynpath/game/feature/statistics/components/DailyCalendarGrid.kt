package com.zynpath.game.feature.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.zynpath.game.core.analytics.model.DailyCalendarEntry
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary

/**
 * Calendar-style history for recent Daily Challenges.
 * Distinguishes local provisional and server-verified entries.
 * Implements Prompt 30 Sections 24, 25, 26, 27 & 51.
 */
@Composable
fun DailyCalendarGrid(
    calendarEntries: List<DailyCalendarEntry>,
    currentStreak: Int,
    maxStreak: Int,
    totalParticipations: Int,
    verifiedCompletions: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Challenge Calendar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Participations: $totalParticipations ($verifiedCompletions verified)",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = AccentGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$currentStreak d (Best: $maxStreak)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (calendarEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Daily Challenge activity recorded yet.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            } else {
                val displayEntries = calendarEntries.takeLast(14)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundElevated)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        displayEntries.forEach { entry ->
                            val dayLabel = if (entry.dateKey.length >= 2) entry.dateKey.takeLast(2) else entry.dateKey
                            val cellDesc = when {
                                entry.isServerVerified -> "${entry.dateKey}: Server-verified solve"
                                entry.isCompleted -> "${entry.dateKey}: Local provisional solve"
                                entry.isParticipated -> "${entry.dateKey}: Attempted"
                                else -> "${entry.dateKey}: Not played"
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.semantics { contentDescription = cellDesc }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                entry.isServerVerified -> ForestMint
                                                entry.isCompleted -> AccentGold
                                                entry.isParticipated -> BackgroundCard
                                                else -> BackgroundCard.copy(alpha = 0.4f)
                                            }
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = when {
                                                entry.isServerVerified -> ForestMint
                                                entry.isCompleted -> AccentGold
                                                else -> TextMuted.copy(alpha = 0.3f)
                                            },
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (entry.isCompleted || entry.isServerVerified) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = BackgroundElevated,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = dayLabel,
                                    fontSize = 9.sp,
                                    color = if (entry.isCompleted) TextPrimary else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ForestMint)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Verified", fontSize = 10.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AccentGold)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Provisional", fontSize = 10.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BackgroundCard)
                                .border(1.dp, TextMuted.copy(alpha = 0.3f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Missed", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
