package app.wisp.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import app.wisp.core.designsystem.theme.WispTheme

/**
 * Reusable squircle card with optional 2027 frosted glass effect.
 * Glass styling provides a translucent surface with crisp text and high-contrast outline.
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

    val borderColor = if (glass) {
        colors.outline.copy(alpha = 0.90f)
    } else {
        colors.outline
    }

    val pressModifier = if (onClick != null) {
        Modifier.springPress(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(pressModifier)
            .clip(shapes.card)
            .background(backgroundColor)
            .border(
                width = effectiveBorderWidth,
                color = borderColor,
                shape = shapes.card
            )
            .padding(spacing.medium),
        content = content
    )
}
