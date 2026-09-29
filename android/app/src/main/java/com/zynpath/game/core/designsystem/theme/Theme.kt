package com.zynpath.game.core.designsystem.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * CompositionLocal providing the currently equipped or active path effect identifier.
 * Defaults to "path_solid_glow" (Free Default).
 */
val LocalPathEffectId = staticCompositionLocalOf { "path_solid_glow" }

/**
 * Authoritative Zynpath Jetpack Compose Theme.
 *
 * Implements Prompt 28 Sections 13, 14, 15, 16 & 18:
 * - Dynamically derives Material 3 color schemes from the active [ZynpathColorPalette].
 * - Configures window status bar and navigation bar colors to eliminate visual flashes.
 * - Provides [LocalZynpathPalette] and [LocalPathEffectId] across the entire composition tree.
 */
@Composable
fun ZynpathTheme(
    palette: ZynpathColorPalette = LocalZynpathPalette.current,
    pathEffectId: String = "path_solid_glow",
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = palette.backgroundDark.toArgb()
                window.navigationBarColor = palette.backgroundDark.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    val dynamicColorScheme = darkColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.textPrimary,
        secondary = palette.secondary,
        onSecondary = palette.backgroundDark,
        secondaryContainer = palette.backgroundCard,
        onSecondaryContainer = palette.textPrimary,
        tertiary = palette.tertiary,
        background = palette.backgroundDark,
        onBackground = palette.textPrimary,
        surface = palette.backgroundSurface,
        onSurface = palette.textPrimary,
        surfaceVariant = palette.backgroundCard,
        onSurfaceVariant = palette.textSecondary
    )

    CompositionLocalProvider(
        LocalZynpathPalette provides palette,
        LocalPathEffectId provides pathEffectId
    ) {
        MaterialTheme(
            colorScheme = dynamicColorScheme,
            typography = Typography,
            content = content
        )
    }
}
