package app.wisp.core.designsystem.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.theme.WispTheme

/**
 * Reusable squircle card with optional 2027 glass blur effect.
 * Glass surfaces utilize RenderEffect blur on API 31+ with a translucent fallback below.
 */
@Composable
fun WispCard(
    modifier: Modifier = Modifier,
    glass: Boolean = false,
    borderWidth: Dp? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = WispTheme.colors
    val shapes = WispTheme.shapes
    val spacing = WispTheme.spacing
    val effectiveBorderWidth = borderWidth ?: spacing.stroke

    val backgroundColor = if (glass) {
        colors.surfaceHigh.copy(alpha = 0.75f)
    } else {
        colors.surfaceHigh
    }

    val glassModifier = if (glass && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.graphicsLayer {
            val blurRadius = 25f
            renderEffect = RenderEffect.createBlurEffect(
                blurRadius,
                blurRadius,
                Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        }
    } else {
        Modifier
    }

    val pressModifier = if (onClick != null) {
        Modifier.springPress(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(pressModifier)
            .then(glassModifier)
            .clip(shapes.card)
            .background(backgroundColor)
            .border(
                width = effectiveBorderWidth,
                color = colors.outline,
                shape = shapes.card
            )
            .padding(spacing.medium),
        content = content
    )
}
