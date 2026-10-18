package app.wisp.core.designsystem.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext

val LocalWispThemeSpec = staticCompositionLocalOf { ThemeSpec.Default }
val LocalWispColors = staticCompositionLocalOf { MidnightInkTokens }
val LocalWispSpacing = staticCompositionLocalOf { WispSpacing() }
val LocalWispShapes = staticCompositionLocalOf { WispShapes() }
val LocalWispTypography = staticCompositionLocalOf { WispTypography() }

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

    val typography: WispTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalWispTypography.current

    val brandGradient: Brush
        @Composable
        @ReadOnlyComposable
        get() = Brush.linearGradient(
            colors = listOf(colors.accentA, colors.accentB)
        )
}

@Composable
fun WispTheme(
    themeSpec: ThemeSpec = ThemeSpec.Default,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Resolve colors: if DYNAMIC preset is requested on API 31+, extract from Material You
    val effectiveColors = if (themeSpec.preset == ThemePreset.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicScheme = if (themeSpec.isDark) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
        themeSpec.colors.copy(
            background = dynamicScheme.background,
            surface = dynamicScheme.surface,
            surfaceHigh = dynamicScheme.surfaceVariant,
            outline = dynamicScheme.outline,
            text = dynamicScheme.onBackground,
            textMuted = dynamicScheme.onSurfaceVariant,
            accentA = dynamicScheme.primary,
            accentB = dynamicScheme.secondary
        )
    } else {
        themeSpec.colors
    }

    val materialColorScheme = if (themeSpec.isDark) {
        darkColorScheme(
            background = effectiveColors.background,
            surface = effectiveColors.surface,
            surfaceVariant = effectiveColors.surfaceHigh,
            onBackground = effectiveColors.text,
            onSurface = effectiveColors.text,
            primary = effectiveColors.accentA,
            secondary = effectiveColors.accentB,
            outline = effectiveColors.outline
        )
    } else {
        lightColorScheme(
            background = effectiveColors.background,
            surface = effectiveColors.surface,
            surfaceVariant = effectiveColors.surfaceHigh,
            onBackground = effectiveColors.text,
            onSurface = effectiveColors.text,
            primary = effectiveColors.accentA,
            secondary = effectiveColors.accentB,
            outline = effectiveColors.outline
        )
    }

    val resolvedSpec = themeSpec.copy(colors = effectiveColors)

    CompositionLocalProvider(
        LocalWispThemeSpec provides resolvedSpec,
        LocalWispColors provides effectiveColors,
        LocalWispSpacing provides resolvedSpec.spacing,
        LocalWispShapes provides resolvedSpec.shapes,
        LocalWispTypography provides WispTypography()
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            content = content
        )
    }
}
