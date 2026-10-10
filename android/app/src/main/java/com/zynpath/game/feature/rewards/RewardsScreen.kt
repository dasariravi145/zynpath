package com.zynpath.game.feature.rewards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Rewards screen destination adapter.
 * Renders the fast [FreeRewardsDialog] modal without full-page overhead.
 * When dismissed, navigates back via [onNavigateBack].
 */
@Composable
fun RewardsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCosmetics: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToDailyChallenge: (() -> Unit)? = null,
    viewModel: RewardsViewModel = hiltViewModel()
) {
    FreeRewardsDialog(
        onDismiss = onNavigateBack,
        onPlayDailyChallenge = onNavigateToDailyChallenge,
        onNavigateToCosmetics = onNavigateToCosmetics,
        modifier = modifier,
        viewModel = viewModel
    )
}
