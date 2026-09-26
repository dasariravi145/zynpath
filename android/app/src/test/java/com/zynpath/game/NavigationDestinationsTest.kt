package com.zynpath.game

import com.zynpath.game.feature.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class NavigationDestinationsTest {

    @Test
    fun screenRoutes_areUniqueAndNonEmpty() {
        val screens = listOf(
            Screen.Splash,
            Screen.Onboarding,
            Screen.Tutorial,
            Screen.Home,
            Screen.WorldSelection,
            Screen.LevelSelection,
            Screen.Gameplay,
            Screen.DailyChallenge,
            Screen.QuickDuel,
            Screen.FriendDuel,
            Screen.MiniLeague,
            Screen.Friends,
            Screen.Profile,
            Screen.Premium,
            Screen.Settings
        )

        val routes = screens.map { it.route }
        assertEquals(routes.size, routes.distinct().size)
        routes.forEach { route ->
            assertNotEquals("", route.trim())
        }
    }

    @Test
    fun parameterizedRoutes_formatCorrectly() {
        assertEquals("level_selection/1", Screen.LevelSelection.createRoute(1))
        assertEquals("level_selection/3", Screen.LevelSelection.createRoute(3))

        assertEquals("gameplay/1/1", Screen.Gameplay.createRoute(1, 1))
        assertEquals("gameplay/4/125", Screen.Gameplay.createRoute(4, 125))
    }
}
