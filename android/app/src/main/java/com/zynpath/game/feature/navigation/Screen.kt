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
    data object FriendDuel : Screen("friend_duel?targetId={targetId}") {
        fun createRoute(targetId: String? = null): String =
            if (!targetId.isNullOrBlank()) "friend_duel?targetId=$targetId" else "friend_duel"
    }
    data object MiniLeague : Screen("mini_league?roomCode={roomCode}") {
        fun createRoute(roomCode: String? = null): String =
            if (!roomCode.isNullOrBlank()) "mini_league?roomCode=$roomCode" else "mini_league"
    }
    data object FriendsArena : Screen("friends_arena")
    data object FriendsArenaRoom : Screen("friends_arena_room?roomId={roomId}") {
        fun createRoute(roomId: String? = null): String =
            if (!roomId.isNullOrBlank()) "friends_arena_room?roomId=$roomId" else "friends_arena_room"
    }
    data object FriendsArenaJoin : Screen("friends_arena_join?code={code}") {
        fun createRoute(initialCode: String? = null): String =
            if (!initialCode.isNullOrBlank()) "friends_arena_join?code=$initialCode" else "friends_arena_join"
    }
    data object FriendsArenaFacebook : Screen("friends_arena_facebook?roomId={roomId}") {
        fun createRoute(roomId: String? = null): String =
            if (!roomId.isNullOrBlank()) "friends_arena_facebook?roomId=$roomId" else "friends_arena_facebook"
    }
    data object FriendsArenaGameplay : Screen("friends_arena_gameplay?roomId={roomId}&matchId={matchId}") {
        fun createRoute(roomId: String? = null, matchId: String? = null): String {
            val params = mutableListOf<String>()
            if (!roomId.isNullOrBlank()) params.add("roomId=$roomId")
            if (!matchId.isNullOrBlank()) params.add("matchId=$matchId")
            return if (params.isNotEmpty()) "friends_arena_gameplay?${params.joinToString("&")}" else "friends_arena_gameplay"
        }
    }
    data object FriendsArenaResults : Screen("friends_arena_results?roomId={roomId}&matchId={matchId}") {
        fun createRoute(roomId: String? = null, matchId: String? = null): String {
            val params = mutableListOf<String>()
            if (!roomId.isNullOrBlank()) params.add("roomId=$roomId")
            if (!matchId.isNullOrBlank()) params.add("matchId=$matchId")
            return if (params.isNotEmpty()) "friends_arena_results?${params.joinToString("&")}" else "friends_arena_results"
        }
    }
    data object Friends : Screen("friends?invitationId={invitationId}") {
        fun createRoute(invitationId: String? = null): String =
            if (!invitationId.isNullOrBlank()) "friends?invitationId=$invitationId" else "friends"
    }
    data object Profile : Screen("profile")
    data object Achievements : Screen("achievements")
    data object Premium : Screen("premium")
    data object Settings : Screen("settings")
    data object SignIn : Screen("sign_in")
    data object MatchHistory : Screen("match_history")
    data object MatchDetails : Screen("match_details/{matchId}") {
        fun createRoute(matchId: String): String = "match_details/$matchId"
    }
    data object Leaderboard : Screen("leaderboard")
    data object DailyLeaderboard : Screen("daily_leaderboard?dateKey={dateKey}") {
        fun createRoute(dateKey: String? = null): String =
            if (!dateKey.isNullOrBlank()) "daily_leaderboard?dateKey=$dateKey" else "daily_leaderboard"
    }

    data object PremiumPacks : Screen("premium_packs")
    data object PackLevels : Screen("pack_levels/{packId}") {
        fun createRoute(packId: String): String = "pack_levels/$packId"
    }
    data object PackGameplay : Screen("pack_gameplay/{packId}/{levelIndex}") {
        fun createRoute(packId: String, levelIndex: Int): String = "pack_gameplay/$packId/$levelIndex"
    }

    data object Cosmetics : Screen("cosmetics")
    data object Statistics : Screen("statistics")
    data object Notifications : Screen("notifications")
    data object Privacy : Screen("privacy")
}
