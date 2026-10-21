package app.wisp.core.designsystem.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.theme.WispTheme
import kotlin.math.roundToInt

@Composable
fun OrbFanItem(
    space: NavSpace,
    isSelected: Boolean,
    targetOffsetX: Float,
    targetOffsetY: Float,
    isOpen: Boolean,
    animationDelayMs: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = WispTheme.colors
    val typography = WispTheme.typography
    val spacing = WispTheme.spacing

    val progress by animateFloatAsState(
        targetValue = if (isOpen) 1.0f else 0.0f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = 350f
        ),
        label = "OrbFanItem_${space.name}"
    )

    if (progress <= 0.01f && !isOpen) return

    val currentOffsetX = (targetOffsetX * progress).roundToInt()
    val currentOffsetY = (targetOffsetY * progress).roundToInt()

    val shapes = WispTheme.shapes

    Row(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(currentOffsetX, currentOffsetY) }
            .scale(progress)
            .alpha(progress)
            .springPress(onClick = onClick)
            .clip(shapes.pill)
            .then(
                if (isSelected) {
                    Modifier.background(WispTheme.brandGradient)
                } else {
                    Modifier
                        .background(colors.surfaceHigh.copy(alpha = 0.94f))
                        .border(spacing.stroke, colors.outline, shapes.pill)
                }
            )
            .semantics {
                role = Role.Button
                contentDescription = space.contentDescription
            }
            .padding(horizontal = spacing.medium, vertical = spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = space.icon,
            contentDescription = null,
            tint = if (isSelected) colors.background else colors.text,
            modifier = Modifier.size(spacing.large)
        )
        Text(
            text = space.title,
            style = typography.labelLarge,
            color = if (isSelected) colors.background else colors.text,
            modifier = Modifier.padding(start = spacing.small)
        )
    }
}
