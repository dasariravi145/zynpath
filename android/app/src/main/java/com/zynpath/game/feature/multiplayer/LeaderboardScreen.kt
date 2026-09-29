package com.zynpath.game.feature.multiplayer

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.zynpath.game.core.multiplayer.model.LeaderboardCategory
import com.zynpath.game.core.multiplayer.model.LeaderboardEntry
import com.zynpath.game.core.multiplayer.model.LeaderboardPeriod

/**
 * Server-Authoritative Leaderboard Screen.
 *
 * Implements Prompt 24 Sections 23-34, 51:
 * - Real, finalized rankings without synthetic placeholder players.
 * - Categories: Quick Duel Victories, Mini League 1st Places, Total Completions.
 * - Time Periods: All Time, This Month, This Week.
 * - Player rank banner highlighting current player's standing.
 * - Deterministic tie handling and bounded pagination.
 */
@Composable
fun LeaderboardScreen(
    onBackClick: () -> Unit,
    viewModel: CompetitiveViewModel = hiltViewModel()
) {
    val state by viewModel.leaderboardState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (state.entries.isEmpty()) {
            viewModel.loadLeaderboard(reset = true)
        }
    }

    Scaffold(
        topBar = {
            LeaderboardTopBar(
                onBackClick = onBackClick,
                onRefreshClick = { viewModel.loadLeaderboard(reset = true) },
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
            // Category Tabs
            LeaderboardCategoryTabs(
                selectedCategory = state.selectedCategory,
                onSelectCategory = { viewModel.selectLeaderboardCategory(it) }
            )

            // Period Filter Chips
            LeaderboardPeriodChips(
                selectedPeriod = state.selectedPeriod,
                onSelectPeriod = { viewModel.selectLeaderboardPeriod(it) }
            )

            // Authenticated Player Rank Banner
            PlayerRankBanner(
                myRank = state.myRank,
                myMetricValue = state.myMetricValue,
                category = state.selectedCategory
            )

            // Leaderboard Entries
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    state.isLoading && state.entries.isEmpty() -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PathNeonCyan
                        )
                    }
                    state.errorMessage != null && state.entries.isEmpty() -> {
                        LeaderboardErrorState(
                            message = state.errorMessage ?: "Failed to load leaderboard",
                            onRetry = { viewModel.loadLeaderboard(reset = true) }
                        )
                    }
                    state.entries.isEmpty() -> {
                        LeaderboardEmptyState(category = state.selectedCategory)
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }

                            items(state.entries, key = { "${it.rank}_${it.publicZynpathId}" }) { entry ->
                                LeaderboardRow(entry = entry)
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
                                                onClick = { viewModel.loadNextLeaderboardPage() },
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = PathNeonCyan
                                                ),
                                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                                    brush = androidx.compose.ui.graphics.SolidColor(PathNeonCyan.copy(alpha = 0.5f))
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Load More Ranks", fontSize = 13.sp)
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
private fun LeaderboardTopBar(
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
            text = "Leaderboard",
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
private fun LeaderboardCategoryTabs(
    selectedCategory: LeaderboardCategory,
    onSelectCategory: (LeaderboardCategory) -> Unit
) {
    val categories = LeaderboardCategory.values()
    val selectedIndex = categories.indexOf(selectedCategory).coerceAtLeast(0)

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
        categories.forEachIndexed { index, cat ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onSelectCategory(cat) },
                text = {
                    Text(
                        text = cat.title,
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
private fun LeaderboardPeriodChips(
    selectedPeriod: LeaderboardPeriod,
    onSelectPeriod: (LeaderboardPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepIndigo)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LeaderboardPeriod.values().forEach { period ->
            val isSelected = period == selectedPeriod
            FilterChip(
                selected = isSelected,
                onClick = { onSelectPeriod(period) },
                label = { Text(period.displayName, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PathNeonCyan.copy(alpha = 0.2f),
                    selectedLabelColor = PathNeonCyan,
                    containerColor = CharcoalNavy,
                    labelColor = MutedSlate
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = CharcoalNavy,
                    selectedBorderColor = PathNeonCyan.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
private fun PlayerRankBanner(
    myRank: Int?,
    myMetricValue: Long?,
    category: LeaderboardCategory
) {
    val metricUnit = when (category) {
        LeaderboardCategory.QUICK_DUEL_WINS -> "Wins"
        LeaderboardCategory.MINI_LEAGUE_WINS -> "1st Places"
        LeaderboardCategory.TOTAL_COMPLETIONS -> "Solves"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(CharcoalNavy, RoundedCornerShape(12.dp))
            .border(1.dp, PathNeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MilitaryTech,
                    contentDescription = null,
                    tint = if (myRank != null) AccentGold else MutedSlate,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (myRank != null) "Your Rank: #$myRank" else "Unranked",
                        color = OffWhiteText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (myRank != null && myMetricValue != null) "$myMetricValue $metricUnit" else "Complete matches to rank",
                        color = if (myRank != null) PathNeonCyan else MutedSlate,
                        fontSize = 12.sp
                    )
                }
            }

            if (myRank != null && myRank <= 10) {
                Box(
                    modifier = Modifier
                        .background(AccentGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "TOP 10",
                        color = AccentGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    val rankColor = when (entry.rank) {
        1 -> AccentGold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MutedSlate
    }

    val isTopThree = entry.rank in 1..3

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number or Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (isTopThree) rankColor.copy(alpha = 0.2f) else CharcoalNavy,
                        CircleShape
                    )
                    .border(1.dp, if (isTopThree) rankColor else CharcoalNavy, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${entry.rank}",
                    color = if (isTopThree) rankColor else OffWhiteText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Player Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.displayName,
                    color = OffWhiteText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.publicZynpathId,
                    color = MutedSlate,
                    fontSize = 11.sp
                )
            }

            // Metric Value
            Box(
                modifier = Modifier
                    .background(CharcoalNavy, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = entry.formattedValue,
                    color = PathNeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LeaderboardEmptyState(category: LeaderboardCategory) {
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
                    imageVector = Icons.Default.Leaderboard,
                    contentDescription = null,
                    tint = MutedSlate,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No ranked players yet",
                color = OffWhiteText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Be the first to complete verified ${category.title.lowercase()} to claim the top spot on the server leaderboard!",
                color = MutedSlate,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LeaderboardErrorState(
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
