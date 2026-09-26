package com.zynpath.game.feature.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object WorldSelection : Screen("world_selection")
    data object LevelSelection : Screen("level_selection")
    data object Gameplay : Screen("gameplay")
    data object DailyChallenge : Screen("daily_challenge")
    data object QuickDuel : Screen("quick_duel")
    data object FriendDuel : Screen("friend_duel")
    data object MiniLeague : Screen("mini_league")
    data object Friends : Screen("friends")
    data object Profile : Screen("profile")
    data object Premium : Screen("premium")
    data object Settings : Screen("settings")
}
