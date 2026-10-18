package app.wisp.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class WispSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val orbSize: Dp = 56.dp
)

data class WispShapes(
    val card: Shape = RoundedCornerShape(28.dp),
    val pill: Shape = RoundedCornerShape(999.dp),
    val sheet: Shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    val chip: Shape = RoundedCornerShape(12.dp)
)

/**
 * Full theme specification for Wisp theme engine.
 */
data class ThemeSpec(
    val name: String,
    val isDark: Boolean,
    val colors: WispColorTokens,
    val spacing: WispSpacing = WispSpacing(),
    val shapes: WispShapes = WispShapes(),
    val gradientAngle: Float = 135f,
    val glassIntensity: Float = 0.85f,
    val motionIntensity: Float = 1.0f
) {
    companion object {
        val Default = ThemeSpec(
            name = "Midnight Ink",
            isDark = true,
            colors = MidnightInkTokens
        )

        val PaperGlass = ThemeSpec(
            name = "Paper Glass",
            isDark = false,
            colors = PaperGlassTokens
        )
    }
}
