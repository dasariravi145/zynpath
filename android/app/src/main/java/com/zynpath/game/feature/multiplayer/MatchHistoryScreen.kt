package com.zynpath.game.feature.multiplayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.CharcoalNavy
import com.zynpath.game.core.designsystem.theme.DeepIndigo
import com.zynpath.game.core.designsystem.theme.EmeraldGreen
import com.zynpath.game.core.designsystem.theme.GunmetalCard
import com.zynpath.game.core.designsystem.theme.MutedSlate
import com.zynpath.game.core.designsystem.theme.OffWhiteText
import com.zynpath.game.core.designsystem.theme.PathNeonCyan
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.MatchHistoryItem
import com.zynpath.game.core.multiplayer.model.MatchParticipantSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Competitive Match History Screen.
 *
 * Implements Prompt 24 Sections 7, 10, 11, 12, 13, 15, 51:
 * - Tabbed filtering by game mode: All, Quick Duel, Friend Duel, Mini League.
 * - Newest finalized matches listed first with verified outcomes.
 * - Bounded pagination with load-more support.
 * - Navigation to Match Details for authoritative match breakdowns.
 */
@Composable
fun MatchHistoryScreen(
    onBackClick: () -> Unit,
    onMatchClick: (String) -> Unit,
    viewModel: CompetitiveViewModel = hiltViewModel()
) {
    val state by viewModel.historyState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (state.items.isEmpty()) {
            viewModel.loadHistory(reset = true)
        }
    }

    Scaffold(
        topBar = {
            HistoryTopBar(
                onBackClick = onBackClick,
                onRefreshClick = { viewModel.loadHistory(reset = true) },
                isRefreshing = state.isRefreshing
            )
        },
        containerColor = DeepIndigo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mode Filter Tabs
            HistoryModeTabs(
                selectedMode = state.selectedMode,
                onSelectMode = { viewModel.selectHistoryMode(it) }
            )

            // Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    state.isLoading && state.items.isEmpty() -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PathNeonCyan
                        )
                    }
                    state.errorMessage != null && state.items.isEmpty() -> {
                        HistoryErrorState(
                            message = state.errorMessage ?: "Failed to load history",
                            onRetry = { viewModel.loadHistory(reset = true) }
                        )
                    }
                    state.items.isEmpty() -> {
                        HistoryEmptyState(selectedMode = state.selectedMode)
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }

                            items(state.items, key = { it.matchId }) { item ->
                                MatchHistoryCard(
                                    item = item,
                                    onClick = { onMatchClick(item.matchId) }
                                )
                            }

                            if (state.hasMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (state.isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = PathNeonCyan,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            OutlinedButton(
                                                onClick = { viewModel.loadNextHistoryPage() },
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = PathNeonCyan
                                                ),
                                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                                    brush = androidx.compose.ui.graphics.SolidColor(PathNeonCyan.copy(alpha = 0.5f))
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Load Earlier Matches", fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryTopBar(
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    isRefreshing: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CharcoalNavy)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = OffWhiteText
            )
        }
        Text(
            text = "Match History",
            color = OffWhiteText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRefreshClick, enabled = !isRefreshing) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PathNeonCyan,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MutedSlate
                )
            }
        }
    }
}

@Composable
private fun HistoryModeTabs(
    selectedMode: GameMode?,
    onSelectMode: (GameMode?) -> Unit
) {
    val modes = listOf<Pair<String, GameMode?>>(
        "All Matches" to null,
        "Quick Duel" to GameMode.QUICK_DUEL,
        "Friend Duel" to GameMode.FRIEND_DUEL,
        "Mini League" to GameMode.MINI_LEAGUE
    )

    val selectedIndex = modes.indexOfFirst { it.second == selectedMode }.coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = CharcoalNavy,
        contentColor = PathNeonCyan,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = PathNeonCyan
                )
            }
        }
    ) {
        modes.forEachIndexed { index, (label, mode) ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onSelectMode(mode) },
                text = {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedIndex == index) PathNeonCyan else MutedSlate
                    )
                }
            )
        }
    }
}

@Composable
private fun MatchHistoryCard(
    item: MatchHistoryItem,
    onClick: () -> Unit
) {
    val myResult = item.myResult
    val isWin = myResult?.isWinner == true || myResult?.finishOrder == 1
    val isTied = myResult?.outcomeStatus.equals("TIED", ignoreCase = true)

    val badgeColor = when {
        isWin -> EmeraldGreen
        isTied -> AccentGold
        item.gameMode == GameMode.MINI_LEAGUE && (myResult?.finishOrder ?: 99) in 2..3 -> AccentGold
        else -> Color(0xFFEF5350)
    }

    val outcomeText = when {
        item.gameMode == GameMode.MINI_LEAGUE -> {
            when (myResult?.finishOrder) {
                1 -> "1st Place"
                2 -> "2nd Place"
                3 -> "3rd Place"
                null -> if (myResult?.completed == true) "Finished" else "DNF"
                else -> "${myResult.finishOrder}th Place"
            }
        }
        isWin -> "VICTORY"
        isTied -> "TIED"
        myResult?.outcomeStatus?.contains("FORFEIT", ignoreCase = true) == true -> "FORFEIT"
        myResult?.outcomeStatus?.contains("ABANDON", ignoreCase = true) == true -> "ABANDONED"
        else -> "DEFEAT"
    }

    val modeTitle = when (item.gameMode) {
        GameMode.QUICK_DUEL -> "Quick Duel 1v1"
        GameMode.FRIEND_DUEL -> "Friend Duel 1v1"
        GameMode.MINI_LEAGUE -> "Mini League (${item.participantCount} Players)"
        GameMode.FRIENDS_ARENA -> "Friends Arena (${item.participantCount} Players)"
    }

    val dateFormatted = try {
        val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(if (item.endedAt > 0) item.endedAt else item.startedAt))
    } catch (_: Exception) {
        ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mode Tag
                    Box(
                        modifier = Modifier
                            .background(CharcoalNavy, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = modeTitle,
                            color = when (item.gameMode) {
                                GameMode.QUICK_DUEL -> PathNeonCyan
                                GameMode.FRIEND_DUEL -> AccentGold
                                GameMode.MINI_LEAGUE -> EmeraldGreen
                                GameMode.FRIENDS_ARENA -> PathNeonCyan
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Outcome Tag
                    Box(
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = outcomeText,
                            color = badgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date
                Text(
                    text = dateFormatted,
                    color = MutedSlate,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Puzzle & Timing info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Puzzle: ${item.puzzleFingerprint.take(6).ifEmpty { item.puzzleId.take(6) }} (${item.gridRows}x${item.gridCols})",
                        color = OffWhiteText.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )

                    myResult?.solveTimeMs?.let { ms ->
                        if (ms > 0 && myResult.completed) {
                            Text(
                                text = " • ${String.format(Locale.US, "%.1fs", ms / 1000.0)}",
                                color = PathNeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Opponents summary
                val otherParticipants = item.participants.filter { it.playerId != myResult?.playerId }
                if (otherParticipants.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val opponentsText = "vs ${otherParticipants.joinToString(", ") { it.displayName }}"
                    Text(
                        text = opponentsText,
                        color = MutedSlate,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = MutedSlate,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun HistoryEmptyState(selectedMode: GameMode?) {
    val modeName = when (selectedMode) {
        GameMode.QUICK_DUEL -> "Quick Duel"
        GameMode.FRIEND_DUEL -> "Friend Duel"
        GameMode.MINI_LEAGUE -> "Mini League"
        GameMode.FRIENDS_ARENA -> "Friends Arena"
        null -> "multiplayer"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(CharcoalNavy, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MutedSlate,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No $modeName matches yet",
                color = OffWhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Complete matches against other players to build your verified competitive history and leaderboard rank.",
                color = MutedSlate,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HistoryErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = Color(0xFFEF5350),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
        }
    }
}
