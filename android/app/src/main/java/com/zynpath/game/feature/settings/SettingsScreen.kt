package com.zynpath.game.feature.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Settings Screen entrypoint.
 *
 * Implements Prompt 49 — ZYNPATH SETTINGS COMPLETE SIMPLIFICATION:
 * - Replaces the legacy full-page 1282-line settings scroll view.
 * - Delegates directly to [SettingsModal].
 * - Dismissing pops the back stack back to the underlying screen.
 */
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCosmetics: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onNavigateToTutorial: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsModal(
        onDismiss = onBackClick,
        onNavigateToTutorial = onNavigateToTutorial,
        viewModel = viewModel
    )
}
