package app.wisp.core.designsystem.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.theme.WispTheme

/**
 * Reusable bottom sheet container with 20dp top rounded corners and optional glass effect.
 */
@Composable
fun WispSheet(
    modifier: Modifier = Modifier,
    glass: Boolean = true,
    showHandle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = WispTheme.colors
    val shapes = WispTheme.shapes
    val spacing = WispTheme.spacing

    val backgroundColor = if (glass) {
        colors.surfaceHigh.copy(alpha = 0.88f)
    } else {
        colors.surfaceHigh
    }

    val glassModifier = if (glass && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.graphicsLayer {
            val blurRadius = 30f
            renderEffect = RenderEffect.createBlurEffect(
                blurRadius,
                blurRadius,
                Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(glassModifier)
            .clip(shapes.sheet)
            .background(backgroundColor)
            .border(
                width = spacing.stroke,
                color = colors.outline,
                shape = shapes.sheet
            )
            .padding(spacing.large)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showHandle) {
                // Drag handle pill
                Box(
                    modifier = Modifier
                        .size(width = spacing.extraLarge + spacing.extraSmall, height = spacing.extraSmall)
                        .clip(shapes.pill)
                        .background(colors.outline)
                )
                Spacer(modifier = Modifier.height(spacing.medium))
            }
            content()
        }
    }
}
