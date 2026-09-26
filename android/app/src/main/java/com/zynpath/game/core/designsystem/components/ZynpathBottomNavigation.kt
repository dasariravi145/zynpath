package com.zynpath.game.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary

data class ZynpathNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val DefaultNavItems = listOf(
    ZynpathNavItem("home", "Home", Icons.Default.Home),
    ZynpathNavItem("world_selection", "Worlds", Icons.Default.PlayArrow),
    ZynpathNavItem("daily_challenge", "Daily", Icons.Default.Star),
    ZynpathNavItem("settings", "Settings", Icons.Default.Settings)
)

@Composable
fun ZynpathBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    items: List<ZynpathNavItem> = DefaultNavItems
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        containerColor = BackgroundDark,
        contentColor = TextPrimary
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BackgroundDark,
                    selectedTextColor = ForestMint,
                    indicatorColor = ForestMint,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun ZynpathBottomNavigationPreview() {
    ZynpathBottomNavigation(
        currentRoute = "home",
        onNavigate = {}
    )
}
