package app.wisp.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import app.wisp.core.designsystem.theme.WispTheme

/**
 * Reusable gradient action button with spring physics.
 */
@Composable
fun WispButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    gradient: Brush? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = WispTheme.colors
    val shapes = WispTheme.shapes
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    val effectiveGradient = gradient ?: WispTheme.brandGradient

    val buttonAlpha = if (enabled) 1.0f else 0.45f

    val contentColor = Color.White

    Box(
        modifier = modifier
            .alpha(buttonAlpha)
            .springPress(enabled = enabled && !loading, onClick = onClick)
            .clip(shapes.pill)
            .background(effectiveGradient)
            .semantics {
                role = Role.Button
                contentDescription = text
            }
            .padding(horizontal = spacing.large, vertical = spacing.medium),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(spacing.medium),
                    color = contentColor,
                    strokeWidth = spacing.strokeThick
                )
            } else if (leadingIcon != null) {
                leadingIcon()
            }
            Text(
                text = text,
                style = typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
