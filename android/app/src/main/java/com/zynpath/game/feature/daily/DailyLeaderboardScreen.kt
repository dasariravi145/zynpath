package com.zynpath.game.feature.daily

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.StatusBadge
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.components.ZynpathSecondaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.puzzle.daily.DailyLeaderboardEntry
import com.zynpath.game.core.puzzle.daily.DailyLeaderboardResponse
import com.zynpath.game.core.puzzle.daily.DailyVerificationStatus

/**
 * Production Daily Leaderboard Screen.
 *
 * Implements Prompt 25 Sections 36-40, 44 & 52:
 * - Specific official challenge date ranking.
 * - Server-authoritative timing with deterministic tie handling.
 * - Exposes only public Zynpath ID, display name, public avatar, and validated solve time.
 * - Distinguishes player's own position where eligible.
 * - Zero fake leaderboard entries.
 */
@Composable
fun DailyLeaderboardScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DailyLeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            DailyLeaderboardTopBar(
                onBackClick = onBackClick,
                onRefreshClick = { viewModel.refresh() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Date Navigation Bar
            val currentDateKey = when (val state = uiState) {
                is DailyLeaderboardUiState.Loading -> state.dateKey
                is DailyLeaderboardUiState.Empty -> state.dateKey
                is DailyLeaderboardUiState.Error -> state.dateKey
                is DailyLeaderboardUiState.Success -> state.dateKey
            }

            DateNavigationBar(
                dateKey = currentDateKey,
                isToday = viewModel.isToday,
                onPreviousDay = { viewModel.navigateDate(-1) },
                onNextDay = { viewModel.navigateDate(1) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState) {
                is DailyLeaderboardUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ForestMint)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading verified rankings...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                is DailyLeaderboardUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Failed to Load Leaderboard",
                                color = Color(0xFFFF5252),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = TextMuted,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            ZynpathSecondaryButton(
                                text = "Retry",
                                onClick = { viewModel.loadLeaderboard() }
                            )
                        }
                    }
                }

                is DailyLeaderboardUiState.Empty -> {
                    DailyLeaderboardEmptyView(
                        dateKey = state.dateKey,
                        onBackClick = onBackClick
                    )
                }

                is DailyLeaderboardUiState.Success -> {
                    DailyLeaderboardContentView(
                        response = state.response,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyLeaderboardTopBar(
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Daily Leaderboard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Official Server-Verified Rankings",
                    fontSize = 12.sp,
                    color = ForestMint,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        IconButton(onClick = onRefreshClick, modifier = Modifier.size(44.dp)) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh Leaderboard",
                tint = TextSecondary
            )
        }
    }
}

@Composable
private fun DateNavigationBar(
    dateKey: String,
    isToday: Boolean,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundElevated)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous Day",
                tint = TextPrimary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = ForestMint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isToday) "$dateKey (Today)" else dateKey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        IconButton(
            onClick = onNextDay,
            enabled = !isToday,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next Day",
                tint = if (!isToday) TextPrimary else TextMuted.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun DailyLeaderboardContentView(
    response: DailyLeaderboardResponse,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Player's Own Position Card (if present)
        item {
            if (response.playerEntry != null) {
                PlayerStandingBanner(entry = response.playerEntry)
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                NonParticipantNotice()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Section header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GLOBAL RANKINGS (${response.totalEntries})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Standard Competition Ranking",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        // Ranked Entries
        items(
            items = response.entries,
            key = { "${it.rank}_${it.publicZynpathId}" }
        ) { entry ->
            DailyLeaderboardEntryRow(entry = entry)
        }
    }
}

@Composable
private fun PlayerStandingBanner(
    entry: DailyLeaderboardEntry,
    modifier: Modifier = Modifier
) {
    val solveSeconds = entry.solveTimeMs / 1000.0
    val formattedSolveTime = String.format("%02d:%05.2f", (entry.solveTimeMs / 60000), solveSeconds % 60.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ForestMint.copy(alpha = 0.12f))
            .border(1.dp, ForestMint.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RankBadge(rank = entry.rank, isMe = true)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "YOUR RESULT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestMint,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = ForestMint,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = entry.displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = entry.publicZynpathId,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formattedSolveTime,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentGold
                )
                Text(
                    text = "Server Authoritative",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun NonParticipantNotice(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundElevated)
            .border(1.dp, TextMuted.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = PathCyanGlow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Complete today's challenge online to earn an official server-verified rank on this leaderboard!",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun DailyLeaderboardEntryRow(
    entry: DailyLeaderboardEntry,
    modifier: Modifier = Modifier
) {
    val solveSeconds = entry.solveTimeMs / 1000.0
    val formattedSolveTime = String.format("%02d:%05.2f", (entry.solveTimeMs / 60000), solveSeconds % 60.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundCard)
            .border(
                width = if (entry.rank == 1) 1.dp else 0.5.dp,
                color = if (entry.rank == 1) AccentGold.copy(alpha = 0.5f) else TextMuted.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RankBadge(rank = entry.rank, isMe = false)

                Spacer(modifier = Modifier.width(12.dp))

                // Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BackgroundElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.displayName.take(1).uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = entry.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = entry.publicZynpathId,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formattedSolveTime,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (entry.rank == 1) AccentGold else ForestMint
                )
                Text(
                    text = entry.verificationStatus.displayLabel,
                    fontSize = 10.sp,
                    color = ForestMint.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun RankBadge(
    rank: Int,
    isMe: Boolean,
    modifier: Modifier = Modifier
) {
    val (bg, textCol) = when (rank) {
        1 -> AccentGold.copy(alpha = 0.25f) to AccentGold
        2 -> Color(0xFFC0C0C0).copy(alpha = 0.25f) to Color(0xFFE0E0E0)
        3 -> Color(0xFFCD7F32).copy(alpha = 0.25f) to Color(0xFFFFB74D)
        else -> if (isMe) ForestMint.copy(alpha = 0.3f) to ForestMint else BackgroundElevated to TextSecondary
    }

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.dp, textCol.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (rank == 1) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Rank 1",
                tint = textCol,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(
                text = "#$rank",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textCol
            )
        }
    }
}

@Composable
private fun DailyLeaderboardEmptyView(
    dateKey: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BackgroundElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Verified Entries Yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No players have submitted a server-validated completion for $dateKey yet. Complete the challenge online to claim the #1 spot!",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            ZynpathPrimaryButton(
                text = "Back to Daily Challenge",
                onClick = onBackClick
            )
        }
    }
}
