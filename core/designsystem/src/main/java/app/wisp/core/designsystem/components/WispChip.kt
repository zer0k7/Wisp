package app.wisp.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.theme.WispTheme

/**
 * Filter and tag pill chip with spring physics.
 */
@Composable
fun WispChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = WispTheme.colors
    val shapes = WispTheme.shapes
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    val backgroundModifier = if (selected) {
        Modifier.background(WispTheme.brandGradient)
    } else {
        Modifier
            .background(colors.surfaceHigh)
            .border(width = spacing.stroke, color = colors.outline, shape = shapes.pill)
    }

    val textColor = if (selected) colors.background else colors.text

    Box(
        modifier = modifier
            .springPress(onClick = onClick)
            .clip(shapes.pill)
            .then(backgroundModifier)
            .semantics {
                role = Role.Checkbox
                contentDescription = "$label, ${if (selected) "selected" else "not selected"}"
            }
            .padding(horizontal = spacing.medium, vertical = spacing.small),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(spacing.extraSmall))
            }
            Text(
                text = label,
                style = typography.labelSmall,
                color = textColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
