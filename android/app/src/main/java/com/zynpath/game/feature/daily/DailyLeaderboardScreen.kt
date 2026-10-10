package com.zynpath.game.feature.daily

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.zynpath.game.core.designsystem.components.GameScreenBackground

/**
 * Daily Leaderboard screen route adapter.
 * Renders the compact [LeaderboardModal] popup over a fantasy game background.
 */
@Composable
fun DailyLeaderboardScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DailyLeaderboardViewModel = hiltViewModel()
) {
    GameScreenBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            LeaderboardModal(
                onDismiss = onBackClick,
                viewModel = viewModel
            )
        }
    }
}
