package com.zynpath.game.feature.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Tutorial : Screen("tutorial")
    data object Home : Screen("home")
    data object WorldSelection : Screen("world_selection")

    data object LevelSelection : Screen("level_selection/{worldId}") {
        fun createRoute(worldId: Int = 1): String = "level_selection/$worldId"
    }

    data object Gameplay : Screen("gameplay/{worldId}/{levelId}") {
        fun createRoute(worldId: Int = 1, levelId: Int = 1): String = "gameplay/$worldId/$levelId"
    }

    data object DailyChallenge : Screen("daily_challenge")
    data object QuickDuel : Screen("quick_duel")
    data object FriendDuel : Screen("friend_duel")
    data object MiniLeague : Screen("mini_league")
    data object Friends : Screen("friends")
    data object Profile : Screen("profile")
    data object Premium : Screen("premium")
    data object Settings : Screen("settings")
}
