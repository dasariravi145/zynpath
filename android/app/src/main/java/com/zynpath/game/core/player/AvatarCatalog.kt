package com.zynpath.game.core.player

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.AccentPurple
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow

/**
 * Built-in avatar definition for local profile customization.
 *
 * Implements Prompt 17 Section 12:
 * - Local vector/drawable options
 * - No network image requirements
 * - No camera or photo permissions
 */
data class AvatarOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val primaryColor: Color
)

object AvatarCatalog {
    val DEFAULT_AVATAR_ID = "avatar_compass"

    val AVATARS: List<AvatarOption> = listOf(
        AvatarOption(
            id = "avatar_compass",
            title = "Pathfinder",
            subtitle = "Guiding the path through every number",
            icon = Icons.Default.Explore,
            primaryColor = ForestMint
        ),
        AvatarOption(
            id = "avatar_grid",
            title = "Grid Master",
            subtitle = "Spatial reasoning and board control",
            icon = Icons.Default.GridView,
            primaryColor = PathCyanGlow
        ),
        AvatarOption(
            id = "avatar_zenith",
            title = "Zenith",
            subtitle = "Calm, focused, and steady solutions",
            icon = Icons.Default.SelfImprovement,
            primaryColor = ForestMint
        ),
        AvatarOption(
            id = "avatar_trophy",
            title = "Champion",
            subtitle = "Pure mastery across all worlds",
            icon = Icons.Default.EmojiEvents,
            primaryColor = AccentGold
        ),
        AvatarOption(
            id = "avatar_bolt",
            title = "Speedster",
            subtitle = "Swift moves and record solve times",
            icon = Icons.Default.Bolt,
            primaryColor = AccentPurple
        ),
        AvatarOption(
            id = "avatar_cube",
            title = "Geometric",
            subtitle = "Mathematical logic and geometric paths",
            icon = Icons.Default.Category,
            primaryColor = PathCyanGlow
        )
    )

    fun getAvatar(id: String): AvatarOption {
        return AVATARS.firstOrNull { it.id == id } ?: AVATARS.first()
    }
}
