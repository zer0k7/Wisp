package app.wisp.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalWispThemeSpec = staticCompositionLocalOf { ThemeSpec.Default }
val LocalWispColors = staticCompositionLocalOf { MidnightInkTokens }
val LocalWispSpacing = staticCompositionLocalOf { WispSpacing() }
val LocalWispShapes = staticCompositionLocalOf { WispShapes() }

object WispTheme {
    val colors: WispColorTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalWispColors.current

    val spec: ThemeSpec
        @Composable
        @ReadOnlyComposable
        get() = LocalWispThemeSpec.current

    val spacing: WispSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalWispSpacing.current

    val shapes: WispShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalWispShapes.current
}

@Composable
fun WispTheme(
    themeSpec: ThemeSpec = ThemeSpec.Default,
    content: @Composable () -> Unit
) {
    val materialColorScheme = if (themeSpec.isDark) {
        darkColorScheme(
            background = themeSpec.colors.background,
            surface = themeSpec.colors.surface,
            onBackground = themeSpec.colors.text,
            onSurface = themeSpec.colors.text,
            primary = themeSpec.colors.accentA,
            secondary = themeSpec.colors.accentB
        )
    } else {
        lightColorScheme(
            background = themeSpec.colors.background,
            surface = themeSpec.colors.surface,
            onBackground = themeSpec.colors.text,
            onSurface = themeSpec.colors.text,
            primary = themeSpec.colors.accentA,
            secondary = themeSpec.colors.accentB
        )
    }

    CompositionLocalProvider(
        LocalWispThemeSpec provides themeSpec,
        LocalWispColors provides themeSpec.colors,
        LocalWispSpacing provides themeSpec.spacing,
        LocalWispShapes provides themeSpec.shapes
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}
