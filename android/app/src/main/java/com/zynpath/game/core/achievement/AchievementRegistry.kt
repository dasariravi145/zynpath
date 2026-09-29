package com.zynpath.game.core.achievement

/**
 * Authoritative registry of all game achievements.
 *
 * Implements Prompt 17 Section 18 & 20:
 * - Single source of truth for achievement IDs and rules
 * - Only verified milestones based on actual game data
 */
object AchievementRegistry {

    val SOLO_FIRST_STEP = AchievementDefinition(
        id = "solo_first_step",
        title = "First Step",
        description = "Complete your first Solo level.",
        category = AchievementCategory.SOLO,
        targetValue = 1,
        iconName = "flag"
    )

    val SOLO_APPRENTICE = AchievementDefinition(
        id = "solo_apprentice",
        title = "Pathfinder Apprentice",
        description = "Complete 5 distinct Solo levels.",
        category = AchievementCategory.SOLO,
        targetValue = 5,
        iconName = "explore"
    )

    val SOLO_JOURNEYMAN = AchievementDefinition(
        id = "solo_journeyman",
        title = "Seasoned Traveler",
        description = "Complete 15 distinct Solo levels.",
        category = AchievementCategory.SOLO,
        targetValue = 15,
        iconName = "map"
    )

    val SOLO_HALF_CENTURY = AchievementDefinition(
        id = "solo_half_century",
        title = "Half Century",
        description = "Complete 50 unique Solo levels.",
        category = AchievementCategory.SOLO,
        targetValue = 50,
        iconName = "stars"
    )

    val SOLO_CENTURY = AchievementDefinition(
        id = "solo_century",
        title = "Centurion",
        description = "Complete 100 unique Solo levels.",
        category = AchievementCategory.SOLO,
        targetValue = 100,
        iconName = "looks_one"
    )

    val SOLO_DOUBLE_CENTURY = AchievementDefinition(
        id = "solo_double_century",
        title = "Path Veteran",
        description = "Complete 200 unique Solo levels.",
        category = AchievementCategory.SOLO,
        targetValue = 200,
        iconName = "workspace_premium"
    )

    val SOLO_CAMPAIGN_MASTER = AchievementDefinition(
        id = "solo_campaign_master",
        title = "Campaign Legend",
        description = "Complete all 300 unique Solo levels.",
        category = AchievementCategory.SOLO,
        targetValue = 300,
        iconName = "emoji_events"
    )

    val WORLD_ONE_PIONEER = AchievementDefinition(
        id = "world_one_pioneer",
        title = "World 1 Pioneer",
        description = "Complete all 20 levels in World 1 (Learn the Path).",
        category = AchievementCategory.WORLD,
        targetValue = 20,
        iconName = "landscape"
    )

    val WORLD_TWO_EXPLORER = AchievementDefinition(
        id = "world_two_explorer",
        title = "World 2 Explorer",
        description = "Complete all 30 levels in World 2 (Longer Connections).",
        category = AchievementCategory.WORLD,
        targetValue = 30,
        iconName = "explore"
    )

    val WORLD_THREE_WALL_BREAKER = AchievementDefinition(
        id = "world_three_wall_breaker",
        title = "Wall Breaker",
        description = "Complete all 50 levels in World 3 (Wall Challenge).",
        category = AchievementCategory.WORLD,
        targetValue = 50,
        iconName = "shield"
    )

    val WORLD_FOUR_NAVIGATOR = AchievementDefinition(
        id = "world_four_navigator",
        title = "Route Navigator",
        description = "Complete all 50 levels in World 4 (Complex Routes).",
        category = AchievementCategory.WORLD,
        targetValue = 50,
        iconName = "navigation"
    )

    val WORLD_FIVE_MASTERMIND = AchievementDefinition(
        id = "world_five_mastermind",
        title = "Logic Mastermind",
        description = "Complete all 50 levels in World 5 (Advanced Logic).",
        category = AchievementCategory.WORLD,
        targetValue = 50,
        iconName = "psychology"
    )

    val WORLD_SIX_GRANDMASTER = AchievementDefinition(
        id = "world_six_grandmaster",
        title = "Grandmaster of the Path",
        description = "Complete all 100 levels in World 6 (Expert Path).",
        category = AchievementCategory.WORLD,
        targetValue = 100,
        iconName = "military_tech"
    )

    val PURE_INTELLECT = AchievementDefinition(
        id = "pure_intellect",
        title = "Pure Intellect",
        description = "Solve any Solo level without using any hints.",
        category = AchievementCategory.MASTERY,
        targetValue = 1,
        iconName = "psychology"
    )

    val SPEED_DEMON = AchievementDefinition(
        id = "speed_demon",
        title = "Swift Footwork",
        description = "Solve any Solo level in under 30 seconds.",
        category = AchievementCategory.MASTERY,
        targetValue = 1,
        iconName = "bolt"
    )

    val DAILY_FIRST_DAWN = AchievementDefinition(
        id = "daily_first_dawn",
        title = "Day One",
        description = "Complete your first Daily Challenge.",
        category = AchievementCategory.DAILY,
        targetValue = 1,
        iconName = "wb_sunny"
    )

    val DAILY_THREE_STREAK = AchievementDefinition(
        id = "daily_three_streak",
        title = "Three-Day Rhythm",
        description = "Maintain a 3-day Daily Challenge streak.",
        category = AchievementCategory.STREAK,
        targetValue = 3,
        iconName = "local_fire_department"
    )

    val DAILY_SEVEN_STREAK = AchievementDefinition(
        id = "daily_seven_streak",
        title = "Weekly Devotion",
        description = "Maintain a 7-day Daily Challenge streak.",
        category = AchievementCategory.STREAK,
        targetValue = 7,
        iconName = "emoji_events"
    )

    val DAILY_SERVER_VALIDATED = AchievementDefinition(
        id = "daily_server_validated",
        title = "Official Pathfinder",
        description = "Complete your first server-validated Daily Challenge.",
        category = AchievementCategory.DAILY,
        targetValue = 1,
        iconName = "verified"
    )

    val DAILY_LEADERBOARD_RANKED = AchievementDefinition(
        id = "daily_leaderboard_ranked",
        title = "On the Board",
        description = "Earn an eligible ranking on the official Daily Leaderboard.",
        category = AchievementCategory.DAILY,
        targetValue = 1,
        iconName = "leaderboard"
    )

    // Competitive Achievements (Prompt 24 Section 38 & 39)
    val COMPETITIVE_QUICK_DUEL_COMPLETE = AchievementDefinition(
        id = "comp_quick_duel_complete",
        title = "First Duelist",
        description = "Complete your first verified Quick Duel.",
        category = AchievementCategory.COMPETITIVE,
        targetValue = 1,
        iconName = "sports_kabaddi"
    )

    val COMPETITIVE_QUICK_DUEL_WIN = AchievementDefinition(
        id = "comp_quick_duel_win",
        title = "Duel Victor",
        description = "Win your first verified Quick Duel.",
        category = AchievementCategory.COMPETITIVE,
        targetValue = 1,
        iconName = "military_tech"
    )

    val COMPETITIVE_FRIEND_DUEL_COMPLETE = AchievementDefinition(
        id = "comp_friend_duel_complete",
        title = "Friendly Rivalry",
        description = "Complete your first Friend Duel.",
        category = AchievementCategory.COMPETITIVE,
        targetValue = 1,
        iconName = "group"
    )

    val COMPETITIVE_MINI_LEAGUE_PARTICIPATION = AchievementDefinition(
        id = "comp_mini_league_participation",
        title = "League Contender",
        description = "Participate in your first Mini League.",
        category = AchievementCategory.COMPETITIVE,
        targetValue = 1,
        iconName = "groups"
    )

    val COMPETITIVE_MINI_LEAGUE_WIN = AchievementDefinition(
        id = "comp_mini_league_win",
        title = "League Champion",
        description = "Take 1st place in a verified Mini League.",
        category = AchievementCategory.COMPETITIVE,
        targetValue = 1,
        iconName = "workspace_premium"
    )

    val ALL_ACHIEVEMENTS: List<AchievementDefinition> = listOf(
        SOLO_FIRST_STEP,
        SOLO_APPRENTICE,
        SOLO_JOURNEYMAN,
        SOLO_HALF_CENTURY,
        SOLO_CENTURY,
        SOLO_DOUBLE_CENTURY,
        SOLO_CAMPAIGN_MASTER,
        WORLD_ONE_PIONEER,
        WORLD_TWO_EXPLORER,
        WORLD_THREE_WALL_BREAKER,
        WORLD_FOUR_NAVIGATOR,
        WORLD_FIVE_MASTERMIND,
        WORLD_SIX_GRANDMASTER,
        PURE_INTELLECT,
        SPEED_DEMON,
        DAILY_FIRST_DAWN,
        DAILY_THREE_STREAK,
        DAILY_SEVEN_STREAK,
        DAILY_SERVER_VALIDATED,
        DAILY_LEADERBOARD_RANKED,
        COMPETITIVE_QUICK_DUEL_COMPLETE,
        COMPETITIVE_QUICK_DUEL_WIN,
        COMPETITIVE_FRIEND_DUEL_COMPLETE,
        COMPETITIVE_MINI_LEAGUE_PARTICIPATION,
        COMPETITIVE_MINI_LEAGUE_WIN
    )

    fun getById(id: String): AchievementDefinition? {
        return ALL_ACHIEVEMENTS.firstOrNull { it.id == id }
    }
}
