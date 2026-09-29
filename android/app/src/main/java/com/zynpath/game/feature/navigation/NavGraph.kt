package com.zynpath.game.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zynpath.game.feature.achievement.AchievementsScreen
import com.zynpath.game.feature.daily.DailyChallengeScreen
import com.zynpath.game.feature.daily.DailyLeaderboardScreen
import com.zynpath.game.feature.gameplay.GameplayShellScreen
import com.zynpath.game.feature.home.HomeScreen
import com.zynpath.game.feature.level.LevelSelectionScreen
import com.zynpath.game.feature.onboarding.OnboardingScreen
import com.zynpath.game.feature.placeholder.DevStateScreen
import com.zynpath.game.feature.cosmetics.CosmeticsScreen
import com.zynpath.game.feature.premium.PremiumScreen
import com.zynpath.game.feature.profile.ProfileScreen
import com.zynpath.game.feature.settings.SettingsScreen
import com.zynpath.game.feature.splash.SplashScreen
import com.zynpath.game.feature.statistics.StatisticsScreen
import com.zynpath.game.feature.tutorial.TutorialScreen
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.feature.auth.SignInScreen
import com.zynpath.game.feature.friends.FriendsScreen
import com.zynpath.game.feature.multiplayer.FriendDuelScreen
import com.zynpath.game.feature.multiplayer.FriendsArenaEntryScreen
import com.zynpath.game.feature.multiplayer.FriendsArenaFacebookScreen
import com.zynpath.game.feature.multiplayer.FriendsArenaGameplayScreen
import com.zynpath.game.feature.multiplayer.FriendsArenaJoinScreen
import com.zynpath.game.feature.multiplayer.FriendsArenaResultsScreen
import com.zynpath.game.feature.multiplayer.FriendsArenaRoomScreen
import com.zynpath.game.feature.multiplayer.MiniLeagueScreen
import com.zynpath.game.feature.multiplayer.MultiplayerHubScreen
import com.zynpath.game.feature.multiplayer.QuickDuelScreen
import com.zynpath.game.feature.multiplayer.MatchHistoryScreen
import com.zynpath.game.feature.multiplayer.MatchDetailsScreen
import com.zynpath.game.feature.multiplayer.LeaderboardScreen
import com.zynpath.game.feature.premium.packs.PackGameplayScreen
import com.zynpath.game.feature.premium.packs.PackLevelsScreen
import com.zynpath.game.feature.premium.packs.PremiumPacksScreen
import com.zynpath.game.feature.world.WorldSelectionScreen
import androidx.navigation.navDeepLink

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
                onNavigateToLogin = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
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

        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        // Root destination exit
                    }
                },
                onContinueAsGuest = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onSignInSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToPrivacy = {
                    navController.navigate(Screen.Privacy.route)
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToTutorial = {
                    navController.navigate(Screen.Tutorial.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Tutorial.route) {
            TutorialScreen(
                onFinishTutorial = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Tutorial.route) { inclusive = true }
                        }
                    }
                },
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Tutorial.route) { inclusive = true }
                        }
                    }
                },
                onStartSoloLevel1 = {
                    navController.navigate(Screen.Gameplay.createRoute(worldId = 1, levelId = 1)) {
                        popUpTo(Screen.Tutorial.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSoloPlay = { navController.navigate(Screen.WorldSelection.route) },
                onNavigateToLevels = { navController.navigate(Screen.WorldSelection.route) },
                onNavigateToTutorial = { navController.navigate(Screen.Tutorial.route) },
                onNavigateToQuickDuel = { navController.navigate(Screen.QuickDuel.route) },
                onNavigateToFriendDuel = { navController.navigate(Screen.FriendDuel.createRoute()) },
                onNavigateToMiniLeague = { navController.navigate(Screen.MiniLeague.createRoute()) },
                onNavigateToDailyChallenge = { navController.navigate(Screen.DailyChallenge.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToFriends = { navController.navigate(Screen.Friends.createRoute()) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToFriendsArena = { navController.navigate(Screen.FriendsArena.route) },
                onNavigateToPlayLevel = { worldId, levelId ->
                    navController.navigate(Screen.Gameplay.createRoute(worldId, levelId))
                },
                onNavigateToResume = { worldId, levelId ->
                    navController.navigate(Screen.Gameplay.createRoute(worldId, levelId))
                }
            )
        }

        composable(Screen.WorldSelection.route) {
            WorldSelectionScreen(
                onBackClick = { navController.popBackStack() },
                onSelectWorld = { worldId ->
                    navController.navigate(Screen.LevelSelection.createRoute(worldId))
                },
                onNavigateToPremiumPacks = {
                    navController.navigate(Screen.PremiumPacks.route)
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
                },
                onNavigateToTutorial = {
                    navController.navigate(Screen.Tutorial.route)
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
            val viewModel: com.zynpath.game.feature.gameplay.GameplayViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            GameplayShellScreen(
                worldId = worldId,
                levelId = levelId,
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel,
                onNextLevelClick = { nextWorldId, nextLevelId ->
                    navController.navigate(Screen.Gameplay.createRoute(nextWorldId, nextLevelId)) {
                        popUpTo(Screen.Gameplay.route) { inclusive = true }
                    }
                },
                onNextWorldClick = { nextWorldId ->
                    navController.navigate(Screen.LevelSelection.createRoute(nextWorldId)) {
                        popUpTo(Screen.Gameplay.route) { inclusive = true }
                    }
                },
                onJourneyCompleteClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToCosmetics = { navController.navigate(Screen.Cosmetics.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToTutorial = { navController.navigate(Screen.Tutorial.route) }
            )
        }

        composable(
            route = Screen.Privacy.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "zynpath://privacy" },
                navDeepLink { uriPattern = "https://zynpath.com/privacy" }
            )
        ) {
            com.zynpath.game.feature.settings.PrivacyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Premium.route) {
            PremiumScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToAuth = { navController.navigate(Screen.SignIn.route) },
                onNavigateToPacks = { navController.navigate(Screen.PremiumPacks.route) }
            )
        }

        composable(
            route = Screen.DailyChallenge.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "zynpath://daily" },
                navDeepLink { uriPattern = "https://zynpath.com/daily" },
                navDeepLink { uriPattern = "zynpath://invite?action=daily" }
            )
        ) {
            DailyChallengeScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToLeaderboard = { dateKey ->
                    navController.navigate(Screen.DailyLeaderboard.createRoute(dateKey))
                }
            )
        }

        composable(Screen.QuickDuel.route) {
            QuickDuelScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) }
            )
        }

        composable(
            route = Screen.FriendDuel.route,
            arguments = listOf(
                navArgument("targetId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "zynpath://friend_duel?targetId={targetId}" },
                navDeepLink { uriPattern = "zynpath://friend_duel" },
                navDeepLink { uriPattern = "https://zynpath.com/friend_duel?targetId={targetId}" },
                navDeepLink { uriPattern = "https://zynpath.com/friend_duel" }
            )
        ) { backStackEntry ->
            val targetId = backStackEntry.arguments?.getString("targetId")
            FriendDuelScreen(
                initialTargetPublicZynpathId = targetId,
                onBackClick = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToFriends = { navController.navigate(Screen.Friends.route) }
            )
        }

        composable(
            route = Screen.MiniLeague.route,
            arguments = listOf(
                navArgument("roomCode") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "zynpath://minileague?code={roomCode}" },
                navDeepLink { uriPattern = "zynpath://minileague" },
                navDeepLink { uriPattern = "https://zynpath.com/minileague?code={roomCode}" },
                navDeepLink { uriPattern = "https://zynpath.com/minileague" }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode")
            MiniLeagueScreen(
                initialRoomCode = roomCode,
                onBackClick = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToFriends = { navController.navigate(Screen.Friends.route) }
            )
        }

        composable(Screen.FriendsArena.route) {
            FriendsArenaEntryScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToCreateRoom = { navController.navigate(Screen.FriendsArenaRoom.createRoute()) },
                onNavigateToJoinWithCode = { navController.navigate(Screen.FriendsArenaJoin.createRoute()) },
                onNavigateToFacebookFriends = { navController.navigate(Screen.FriendsArenaFacebook.createRoute()) }
            )
        }

        composable(
            route = Screen.FriendsArenaRoom.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            FriendsArenaRoomScreen(
                roomId = roomId,
                onBackClick = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToFacebookFriends = { id ->
                    navController.navigate(Screen.FriendsArenaFacebook.createRoute(id))
                },
                onNavigateToGameplay = { rId, mId ->
                    navController.navigate(Screen.FriendsArenaGameplay.createRoute(rId, mId)) {
                        popUpTo(Screen.FriendsArenaRoom.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.FriendsArenaJoin.route,
            arguments = listOf(
                navArgument("code") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "zynpath://arena?code={code}" },
                navDeepLink { uriPattern = "https://zynpath.com/arena?code={code}" },
                navDeepLink { uriPattern = "zynpath://arena_join?code={code}" },
                navDeepLink { uriPattern = "https://zynpath.com/arena_join?code={code}" },
                navDeepLink { uriPattern = "zynpath://arena_join" },
                navDeepLink { uriPattern = "https://zynpath.com/arena_join" }
            )
        ) { backStackEntry ->
            val initialCode = backStackEntry.arguments?.getString("code")
            FriendsArenaJoinScreen(
                initialCode = initialCode,
                onBackClick = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onJoinSuccess = { roomId ->
                    navController.navigate(Screen.FriendsArenaRoom.createRoute(roomId)) {
                        popUpTo(Screen.FriendsArenaJoin.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.FriendsArenaFacebook.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            FriendsArenaFacebookScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToRoomLobby = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.FriendsArenaGameplay.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("matchId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            val matchId = backStackEntry.arguments?.getString("matchId")
            FriendsArenaGameplayScreen(
                roomId = roomId,
                matchId = matchId,
                onBackClick = { navController.popBackStack() },
                onNavigateToResults = { finishedMatchId ->
                    if (finishedMatchId.isNotBlank() || !roomId.isNullOrBlank()) {
                        navController.navigate(
                            Screen.FriendsArenaResults.createRoute(
                                roomId = roomId,
                                matchId = finishedMatchId.ifBlank { null }
                            )
                        ) {
                            popUpTo(Screen.FriendsArenaGameplay.route) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = Screen.FriendsArenaResults.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("matchId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val resultRoomId = backStackEntry.arguments?.getString("roomId")
            val resultMatchId = backStackEntry.arguments?.getString("matchId")
            FriendsArenaResultsScreen(
                matchId = resultMatchId,
                roomId = resultRoomId,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToRoom = { targetRoomId ->
                    navController.navigate(Screen.FriendsArenaRoom.createRoute(targetRoomId)) {
                        popUpTo(Screen.FriendsArenaResults.route) { inclusive = true }
                    }
                },
                onNavigateToRematchGameplay = { targetRoomId, newMatchId ->
                    navController.navigate(Screen.FriendsArenaGameplay.createRoute(targetRoomId, newMatchId)) {
                        popUpTo(Screen.FriendsArenaResults.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Friends.route,
            arguments = listOf(
                navArgument("invitationId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "zynpath://invite?id={invitationId}" },
                navDeepLink { uriPattern = "https://zynpath.com/invite?id={invitationId}" },
                navDeepLink { uriPattern = "zynpath://friends?invitationId={invitationId}" },
                navDeepLink { uriPattern = "zynpath://friends" },
                navDeepLink { uriPattern = "https://zynpath.com/friends" }
            )
        ) { backStackEntry ->
            val invitationId = backStackEntry.arguments?.getString("invitationId")
            FriendsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                initialInvitationId = invitationId,
                onNavigateToFriendDuel = { targetId ->
                    navController.navigate(Screen.FriendDuel.createRoute(targetId))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToMatchHistory = { navController.navigate(Screen.MatchHistory.route) },
                onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) },
                onNavigateToCosmetics = { navController.navigate(Screen.Cosmetics.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) }
            )
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    }
                },
                onContinueAsGuest = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    }
                },
                onSignInSuccess = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) }
            )
        }

        composable(Screen.MatchHistory.route) {
            MatchHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onMatchClick = { matchId ->
                    navController.navigate(Screen.MatchDetails.createRoute(matchId))
                }
            )
        }

        composable(
            route = Screen.MatchDetails.route,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "zynpath://match/{matchId}" },
                navDeepLink { uriPattern = "https://zynpath.com/match/{matchId}" }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchDetailsScreen(
                matchId = matchId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DailyLeaderboard.route,
            arguments = listOf(
                navArgument("dateKey") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            DailyLeaderboardScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.PremiumPacks.route) {
            PremiumPacksScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPackLevels = { packId ->
                    navController.navigate(Screen.PackLevels.createRoute(packId))
                },
                onNavigateToPremiumPaywall = {
                    navController.navigate(Screen.Premium.route)
                }
            )
        }

        composable(
            route = Screen.PackLevels.route,
            arguments = listOf(navArgument("packId") { type = NavType.StringType })
        ) {
            PackLevelsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGameplay = { packId, levelIndex ->
                    navController.navigate(Screen.PackGameplay.createRoute(packId, levelIndex))
                },
                onNavigateToPremiumPaywall = {
                    navController.navigate(Screen.Premium.route)
                }
            )
        }

        composable(
            route = Screen.PackGameplay.route,
            arguments = listOf(
                navArgument("packId") { type = NavType.StringType },
                navArgument("levelIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val packId = backStackEntry.arguments?.getString("packId") ?: ""
            val levelIndex = backStackEntry.arguments?.getInt("levelIndex") ?: 1
            PackGameplayScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNextLevel = { nextPackId, nextLevelIndex ->
                    navController.navigate(Screen.PackGameplay.createRoute(nextPackId, nextLevelIndex)) {
                        popUpTo(Screen.PackGameplay.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Cosmetics.route) {
            CosmeticsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToUpgrade = { navController.navigate(Screen.Premium.route) }
            )
        }

        composable(
            route = Screen.Notifications.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "zynpath://notifications" },
                navDeepLink { uriPattern = "https://zynpath.com/notifications" }
            )
        ) {
            com.zynpath.game.feature.notification.NotificationsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDestination = { dest ->
                    when {
                        dest.startsWith("friends") -> navController.navigate(Screen.Friends.route)
                        dest.startsWith("friend_duel") -> {
                            val targetId = dest.removePrefix("friend_duel/").takeIf { it.isNotBlank() && it != "friend_duel" }
                            navController.navigate(Screen.FriendDuel.createRoute(targetId))
                        }
                        dest.startsWith("mini_league") -> {
                            val roomCode = dest.removePrefix("mini_league/").takeIf { it.isNotBlank() && it != "mini_league" }
                            navController.navigate(Screen.MiniLeague.createRoute(roomCode))
                        }
                        dest.startsWith("match_details") -> {
                            val matchId = dest.removePrefix("match_details/").ifBlank { "" }
                            if (matchId.isNotBlank()) {
                                navController.navigate(Screen.MatchDetails.createRoute(matchId))
                            } else {
                                navController.navigate(Screen.MatchHistory.route)
                            }
                        }
                        dest.startsWith("daily") -> navController.navigate(Screen.DailyChallenge.route)
                        else -> navController.navigate(dest)
                    }
                }
            )
        }
    }
}
