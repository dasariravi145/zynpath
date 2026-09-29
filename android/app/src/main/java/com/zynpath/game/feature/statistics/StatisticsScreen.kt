package com.zynpath.game.feature.statistics

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.analytics.model.PackProgressSummary
import com.zynpath.game.core.analytics.model.PersonalAnalyticsReport
import com.zynpath.game.core.analytics.model.PersonalMilestone
import com.zynpath.game.core.analytics.model.PremiumPackAnalytics
import com.zynpath.game.core.designsystem.components.ZynpathTopBar
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.AccentPurple
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.feature.statistics.components.CompetitiveModeSummaryCard
import com.zynpath.game.feature.statistics.components.CompletionTrendChart
import com.zynpath.game.feature.statistics.components.DailyCalendarGrid
import com.zynpath.game.feature.statistics.components.MiniLeagueSummaryCard
import com.zynpath.game.feature.statistics.components.PremiumAnalyticsPreviewCard
import com.zynpath.game.feature.statistics.components.WorldProgressChart

/**
 * Dedicated Personal Statistics & Progression Insights screen.
 * Displays authoritative gameplay metrics with accessible visualizations.
 * Implements Prompt 30 Sections 37, 38, 39, 50, 51, 52 & 53.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBackClick: () -> Unit,
    onNavigateToUpgrade: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            ZynpathTopBar(
                title = "Personal Statistics",
                subtitle = "Progression Insights & Analytics",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh analytics",
                            tint = PathCyanGlow
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is StatisticsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PathCyanGlow)
                    }
                }

                is StatisticsUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }

                is StatisticsUiState.Success -> {
                    StatisticsContent(
                        report = state.report,
                        selectedTab = state.selectedTab,
                        timeFilter = state.timeFilter,
                        onTabSelected = { viewModel.selectTab(it) },
                        onTimeFilterSelected = { viewModel.selectTimeFilter(it) },
                        onUpgradeClick = onNavigateToUpgrade
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsContent(
    report: PersonalAnalyticsReport,
    selectedTab: StatisticsTab,
    timeFilter: TimeFilter,
    onTabSelected: (StatisticsTab) -> Unit,
    onTimeFilterSelected: (TimeFilter) -> Unit,
    onUpgradeClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = BackgroundCard,
            contentColor = PathCyanGlow,
            edgePadding = 16.dp
        ) {
            StatisticsTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == tab) PathCyanGlow else TextSecondary
                        )
                    }
                )
            }
        }

        // Time filter selector for trend-sensitive tabs
        if (selectedTab == StatisticsTab.PREMIUM_INSIGHTS && report.isAdvancedAnalyticsUnlocked) {
            TimeFilterSelector(
                selectedFilter = timeFilter,
                onFilterSelected = onTimeFilterSelected,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                StatisticsTab.OVERVIEW -> {
                    item { OverviewSection(report = report) }
                }

                StatisticsTab.SOLO -> {
                    item { SoloSection(report = report) }
                }

                StatisticsTab.DAILY -> {
                    item { DailySection(report = report) }
                }

                StatisticsTab.COMPETITIVE -> {
                    item { CompetitiveSection(report = report) }
                }

                StatisticsTab.PREMIUM_INSIGHTS -> {
                    item {
                        PremiumInsightsSection(
                            report = report,
                            onUpgradeClick = onUpgradeClick
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun OverviewSection(report: PersonalAnalyticsReport) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Overview Summary Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OverviewMetricCard(
                title = "Solo Solved",
                value = "${report.soloProgression.completedLevels}/${report.soloProgression.totalLevels}",
                subtitle = "${(report.soloProgression.completionPercentage * 100).toInt()}% of 300 free levels",
                icon = Icons.Default.Extension,
                iconTint = PathCyanGlow,
                modifier = Modifier.weight(1f)
            )
            OverviewMetricCard(
                title = "Daily Streak",
                value = "${report.dailyAnalytics.currentStreak} Days",
                subtitle = "Best: ${report.dailyAnalytics.maxStreak} days",
                icon = Icons.Default.LocalFireDepartment,
                iconTint = AccentGold,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OverviewMetricCard(
                title = "Quick Duel",
                value = "${report.competitiveSummary.quickDuel.wins}W - ${report.competitiveSummary.quickDuel.losses}L",
                subtitle = if (report.competitiveSummary.quickDuel.matchesPlayed - report.competitiveSummary.quickDuel.ties > 0) {
                    "${(report.competitiveSummary.quickDuel.winRate * 100).toInt()}% win rate (N=${report.competitiveSummary.quickDuel.matchesPlayed})"
                } else {
                    "N=${report.competitiveSummary.quickDuel.matchesPlayed} matches"
                },
                icon = Icons.Default.MilitaryTech,
                iconTint = ForestMint,
                modifier = Modifier.weight(1f)
            )
            OverviewMetricCard(
                title = "Active World",
                value = "World ${report.soloProgression.currentWorldId}",
                subtitle = "${report.soloProgression.completedWorldsCount} completed",
                icon = Icons.Default.Speed,
                iconTint = AccentPurple,
                modifier = Modifier.weight(1f)
            )
        }

        // Milestones
        PersonalMilestonesCard(milestones = report.milestones)
    }
}

@Composable
private fun SoloSection(report: PersonalAnalyticsReport) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // World Progress Chart
        WorldProgressChart(worlds = report.soloProgression.worlds)

        // Time Metrics Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Solo Solve Times",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        label = "Fastest Solve",
                        value = if (report.soloProgression.fastestSolveTimeMs != null) {
                            "%.1fs".format(report.soloProgression.fastestSolveTimeMs!! / 1000.0)
                        } else {
                            "—"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "Average Solve",
                        value = if (report.soloProgression.averageSolveTimeMs != null) {
                            "%.1fs".format(report.soloProgression.averageSolveTimeMs!! / 1000.0)
                        } else {
                            "—"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Untimed legacy completions are excluded from average calculations rather than counted as zero.",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        // Separate Premium Solo Pack Analytics
        if (report.premiumPackAnalytics.totalInstalledPuzzles > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Premium Solo Packs",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Packs: ${report.premiumPackAnalytics.completedPacksCount}/${report.premiumPackAnalytics.totalInstalledPacks} completed • ${report.premiumPackAnalytics.completedPuzzles}/${report.premiumPackAnalytics.totalInstalledPuzzles} puzzles",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    report.premiumPackAnalytics.packs.forEach { pack ->
                        PremiumPackRow(pack = pack)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumPackRow(pack: PackProgressSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundElevated)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pack.packTitle,
            fontSize = 13.sp,
            color = TextPrimary
        )
        Text(
            text = "${pack.completedPuzzles}/${pack.totalPuzzles} (${(pack.completionPercentage * 100).toInt()}%)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (pack.isCompleted) ForestMint else AccentGold
        )
    }
}

@Composable
private fun DailySection(report: PersonalAnalyticsReport) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DailyCalendarGrid(
            calendarEntries = report.dailyAnalytics.calendarEntries,
            currentStreak = report.dailyAnalytics.currentStreak,
            maxStreak = report.dailyAnalytics.maxStreak,
            totalParticipations = report.dailyAnalytics.totalParticipations,
            verifiedCompletions = report.dailyAnalytics.serverVerifiedCompletions
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Daily Challenge Timings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        label = "Fastest Verified",
                        value = if (report.dailyAnalytics.fastestSolveTimeMs != null) {
                            "%.1fs".format(report.dailyAnalytics.fastestSolveTimeMs!! / 1000.0)
                        } else {
                            "—"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        label = "Average Solve",
                        value = if (report.dailyAnalytics.averageSolveTimeMs != null) {
                            "%.1fs".format(report.dailyAnalytics.averageSolveTimeMs!! / 1000.0)
                        } else {
                            "—"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Only server-validated Daily Challenge runs are eligible for global leaderboards and official timing ranks.",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun CompetitiveSection(report: PersonalAnalyticsReport) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CompetitiveModeSummaryCard(
            modeTitle = "Quick Duel (1v1)",
            summary = report.competitiveSummary.quickDuel
        )

        CompetitiveModeSummaryCard(
            modeTitle = "Friend Duel (Private)",
            summary = report.competitiveSummary.friendDuel,
            isFriendDuel = true
        )

        MiniLeagueSummaryCard(
            summary = report.competitiveSummary.miniLeague
        )
    }
}

@Composable
private fun PremiumInsightsSection(
    report: PersonalAnalyticsReport,
    onUpgradeClick: () -> Unit
) {
    if (!report.isAdvancedAnalyticsUnlocked) {
        PremiumAnalyticsPreviewCard(onUpgradeClick = onUpgradeClick)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CompletionTrendChart(trends = report.completionTrends)

            if (report.timeImprovements.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Same-Puzzle Time Improvements",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Compared strictly across identical puzzle versions",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        report.timeImprovements.take(5).forEach { insight ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundElevated)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Level ${insight.levelId} (v${insight.puzzleVersion})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    val firstSec = insight.firstSolveTimeMs / 1000.0
                                    val bestSec = insight.bestSolveTimeMs / 1000.0
                                    Text(
                                        text = "${"%.1f".format(firstSec)}s → ${"%.1f".format(bestSec)}s",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                val improvementPct = (insight.improvementPercentage ?: 0.0).toInt()
                                Text(
                                    text = "-${"%.1f".format(insight.improvementDeltaMs / 1000.0)}s ($improvementPct%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestMint
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundElevated)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun PersonalMilestonesCard(
    milestones: List<PersonalMilestone>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val unlockedCount = milestones.count { it.isUnlocked }
            Text(
                text = "Personal Milestones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "$unlockedCount of ${milestones.size} achieved from gameplay records",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            milestones.forEach { milestone ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundElevated)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (milestone.isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (milestone.isUnlocked) ForestMint else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = milestone.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (milestone.isUnlocked) TextPrimary else TextMuted
                        )
                        Text(
                            text = milestone.description,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun TimeFilterSelector(
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimeFilter.entries.forEach { filter ->
            val isSelected = selectedFilter == filter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) PathCyanGlow else BackgroundElevated)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = filter.title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) BackgroundDark else TextSecondary
                )
            }
        }
    }
}
