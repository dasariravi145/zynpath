package com.zynpath.game.core.designsystem.layout

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window width size classes based on standard Material 3 adaptive design guidelines.
 */
enum class WindowWidthSize {
    COMPACT, // < 600dp (standard portrait phones)
    MEDIUM,  // 600dp - 839dp (foldables unfolded, tablets portrait, landscape phones)
    EXPANDED // >= 840dp (tablets landscape, large foldables, desktop/Chromebooks)
}

/**
 * Window height size classes based on standard Material 3 adaptive design guidelines.
 */
enum class WindowHeightSize {
    COMPACT, // < 480dp (phones in landscape)
    MEDIUM,  // 480dp - 899dp (standard phones in portrait)
    EXPANDED // >= 900dp (tall phones, tablets in portrait)
}

/**
 * Authoritative responsive window information for Zynpath layouts.
 * Adapts UI compositions dynamically across compact phones, standard phones, large phones,
 * tablets, foldables, landscape orientation, and multi-window resizing (Prompt 39).
 */
data class ZynpathWindowInfo(
    val widthSize: WindowWidthSize,
    val heightSize: WindowHeightSize,
    val screenWidthDp: Dp,
    val screenHeightDp: Dp,
    val isLandscape: Boolean,
    val isTablet: Boolean,
    val isCompactPhone: Boolean
) {
    /**
     * True if the layout should use a two-region side-by-side composition
     * (e.g. board on left/center, controls & stats on right panel).
     */
    val useSideBySideLayout: Boolean
        get() = isLandscape || (isTablet && widthSize == WindowWidthSize.EXPANDED)

    /**
     * Recommended maximum content width for portrait tablet layouts to avoid excessive stretching.
     */
    val maxContentWidth: Dp
        get() = when {
            isTablet -> 560.dp
            else -> Dp.Unspecified
        }
}

/**
 * Remembers and calculates authoritative [ZynpathWindowInfo] based on current configuration and window dimensions.
 * Dynamically updates on device rotation, foldable posture shifts, and multi-window resizing.
 */
@Composable
fun rememberZynpathWindowInfo(): ZynpathWindowInfo {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE || screenWidth > screenHeight

    val widthSize = when {
        screenWidth < 600.dp -> WindowWidthSize.COMPACT
        screenWidth < 840.dp -> WindowWidthSize.MEDIUM
        else -> WindowWidthSize.EXPANDED
    }

    val heightSize = when {
        screenHeight < 480.dp -> WindowHeightSize.COMPACT
        screenHeight < 900.dp -> WindowHeightSize.MEDIUM
        else -> WindowHeightSize.EXPANDED
    }

    // A tablet is defined by 600dp+ smallest width (sw600dp)
    val isTablet = minOf(screenWidth, screenHeight) >= 600.dp
    val isCompactPhone = !isTablet && (screenHeight < 640.dp || screenWidth < 360.dp)

    return remember(configuration.screenWidthDp, configuration.screenHeightDp, configuration.orientation) {
        ZynpathWindowInfo(
            widthSize = widthSize,
            heightSize = heightSize,
            screenWidthDp = screenWidth,
            screenHeightDp = screenHeight,
            isLandscape = isLandscape,
            isTablet = isTablet,
            isCompactPhone = isCompactPhone
        )
    }
}
