package com.zynpath.game.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.zynpath.game.core.designsystem.components.GameScreenBackground

/**
 * Simplified Player Profile Screen container.
 *
 * Implements Complete Profile Simplification:
 * - Delegates directly to [ProfileModal]
 * - Preserves existing navigation parameters for backward compatibility
 * - Dismissing the modal pops the navigation back stack
 */
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToMatchHistory: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToCosmetics: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    GameScreenBackground(modifier = modifier.fillMaxSize()) {
        ProfileModal(
            onDismiss = onBackClick,
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel
        )
    }
}
