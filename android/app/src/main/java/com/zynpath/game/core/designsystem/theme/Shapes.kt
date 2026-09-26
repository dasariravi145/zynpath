package com.zynpath.game.core.designsystem.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Zynpath rounded corner shape tokens.
 */
object ZynpathShapes {
    val none: CornerBasedShape = RoundedCornerShape(0.dp)
    val extraSmall: CornerBasedShape = RoundedCornerShape(4.dp)
    val small: CornerBasedShape = RoundedCornerShape(8.dp)
    val medium: CornerBasedShape = RoundedCornerShape(12.dp)
    val large: CornerBasedShape = RoundedCornerShape(16.dp)
    val extraLarge: CornerBasedShape = RoundedCornerShape(24.dp)
    val boardShape: CornerBasedShape = RoundedCornerShape(20.dp)
    val cardShape: CornerBasedShape = RoundedCornerShape(16.dp)
    val buttonShape: CornerBasedShape = RoundedCornerShape(16.dp)
    val pill: CornerBasedShape = RoundedCornerShape(999.dp)
}
