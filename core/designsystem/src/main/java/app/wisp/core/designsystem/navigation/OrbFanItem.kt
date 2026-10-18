package app.wisp.core.designsystem.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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

    val itemSize = 48.dp

    Column(
        modifier = modifier
            .offset { IntOffset(currentOffsetX, currentOffsetY) }
            .scale(progress)
            .alpha(progress)
            .semantics {
                role = Role.Button
                contentDescription = space.contentDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(itemSize)
                .springPress(onClick = onClick)
                .clip(CircleShape)
                .background(
                    if (isSelected) WispTheme.brandGradient else colors.surfaceHigh.copy(alpha = 0.90f)
                )
                .border(
                    width = if (isSelected) spacing.strokeThick else spacing.stroke,
                    color = if (isSelected) colors.accentB else colors.outline,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = space.icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else colors.text,
                modifier = Modifier.size(spacing.large)
            )
        }

        Spacer(modifier = Modifier.height(spacing.extraSmall))

        Text(
            text = space.title,
            style = typography.labelSmall,
            color = if (isSelected) colors.accentB else colors.textMuted
        )
    }
}
