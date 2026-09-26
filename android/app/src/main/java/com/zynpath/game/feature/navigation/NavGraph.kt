package com.zynpath.game.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zynpath.game.feature.home.HomeScreen
import com.zynpath.game.feature.onboarding.OnboardingScreen
import com.zynpath.game.feature.placeholder.DevStateScreen
import com.zynpath.game.feature.settings.SettingsScreen
import com.zynpath.game.feature.splash.SplashScreen

@Composable
fun ZynpathNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSoloPlay = { navController.navigate(Screen.Gameplay.route) },
                onNavigateToLevels = { navController.navigate(Screen.LevelSelection.route) },
                onNavigateToQuickDuel = { navController.navigate(Screen.QuickDuel.route) },
                onNavigateToFriendDuel = { navController.navigate(Screen.FriendDuel.route) },
                onNavigateToMiniLeague = { navController.navigate(Screen.MiniLeague.route) },
                onNavigateToDailyChallenge = { navController.navigate(Screen.DailyChallenge.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Gameplay.route) {
            DevStateScreen(
                featureTitle = "Solo Gameplay",
                scheduledPhase = "PHASE 2 • PROMPTS 06–12",
                description = "The pure Kotlin continuous-path puzzle engine, movement validators, and hardware-accelerated drag Canvas will be implemented in Phase 2.",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.LevelSelection.route) {
            DevStateScreen(
                featureTitle = "Level Select",
                scheduledPhase = "PHASE 4 • PROMPTS 19–23",
                description = "Worlds 1 to 6 progression maps, 300 base levels, star ratings, and unlock gates will be implemented in Phase 4.",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.DailyChallenge.route) {
            DevStateScreen(
                featureTitle = "Daily Challenge",
                scheduledPhase = "PHASE 4 • PROMPT 23",
                description = "Globally synchronized daily puzzle with offline local solve and optional online leaderboard ranking.",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.QuickDuel.route) {
            DevStateScreen(
                featureTitle = "Quick Duel (1v1)",
                scheduledPhase = "PHASE 7 • PROMPTS 33–40",
                description = "Real-time online matchmaking with identical puzzle seeds, live progress indicators, and authoritative server validation.",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.FriendDuel.route) {
            DevStateScreen(
                featureTitle = "Friend Duel",
                scheduledPhase = "PHASE 7 • PROMPTS 33–40",
                description = "Direct private challenge using room codes and deep-link invites without requiring Facebook friend permissions.",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.MiniLeague.route) {
            DevStateScreen(
                featureTitle = "Mini League",
                scheduledPhase = "PHASE 7 • PROMPTS 33–40",
                description = "Multi-round competitive tournament for 2–5 players with cumulative point standings and round-by-round rankings.",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            DevStateScreen(
                featureTitle = "Player Profile",
                scheduledPhase = "PHASE 5 • PROMPTS 24–27",
                description = "Guest-first profile management, Zynpath Tag customization, and optional account linking (Google/Facebook).",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Premium.route) {
            DevStateScreen(
                featureTitle = "Zynpath Premium",
                scheduledPhase = "PHASE 8 • PROMPTS 41–44",
                description = "Google Play Billing integration for Monthly (₹99) and 6-Month (₹499) ad-free subscriptions and bonus puzzle packs.",
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
