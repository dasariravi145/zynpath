package com.zynpath.game.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zynpath.game.feature.gameplay.GameplayShellScreen
import com.zynpath.game.feature.home.HomeScreen
import com.zynpath.game.feature.level.LevelSelectionScreen
import com.zynpath.game.feature.onboarding.OnboardingScreen
import com.zynpath.game.feature.placeholder.DevStateScreen
import com.zynpath.game.feature.premium.PremiumScreen
import com.zynpath.game.feature.settings.SettingsScreen
import com.zynpath.game.feature.splash.SplashScreen
import com.zynpath.game.feature.tutorial.TutorialScreen
import com.zynpath.game.feature.world.WorldSelectionScreen

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

        composable(Screen.Tutorial.route) {
            TutorialScreen(
                onFinishTutorial = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSoloPlay = { navController.navigate(Screen.WorldSelection.route) },
                onNavigateToLevels = { navController.navigate(Screen.WorldSelection.route) },
                onNavigateToTutorial = { navController.navigate(Screen.Tutorial.route) },
                onNavigateToQuickDuel = { navController.navigate(Screen.QuickDuel.route) },
                onNavigateToFriendDuel = { navController.navigate(Screen.FriendDuel.route) },
                onNavigateToMiniLeague = { navController.navigate(Screen.MiniLeague.route) },
                onNavigateToDailyChallenge = { navController.navigate(Screen.DailyChallenge.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.WorldSelection.route) {
            WorldSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onSelectWorld = { worldId ->
                    navController.navigate(Screen.LevelSelection.createRoute(worldId))
                }
            )
        }

        composable(
            route = Screen.LevelSelection.route,
            arguments = listOf(
                navArgument("worldId") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val worldId = backStackEntry.arguments?.getInt("worldId") ?: 1
            LevelSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onSelectLevel = { wId, levelNum ->
                    navController.navigate(Screen.Gameplay.createRoute(wId, levelNum))
                }
            )
        }

        composable(
            route = Screen.Gameplay.route,
            arguments = listOf(
                navArgument("worldId") {
                    type = NavType.IntType
                    defaultValue = 1
                },
                navArgument("levelId") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val worldId = backStackEntry.arguments?.getInt("worldId") ?: 1
            val levelId = backStackEntry.arguments?.getInt("levelId") ?: 1
            GameplayShellScreen(
                worldId = worldId,
                levelId = levelId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Premium.route) {
            PremiumScreen(
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

        composable(Screen.Friends.route) {
            DevStateScreen(
                featureTitle = "Friends & Invites",
                scheduledPhase = "PHASE 7 • PROMPTS 33–40",
                description = "Manage puzzle friends, send room duel invites, and view asynchronous rival solve times.",
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
    }
}
