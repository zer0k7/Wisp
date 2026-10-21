package app.wisp.core.designsystem.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.delay
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

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            delay(animationDelayMs.toLong())
            progress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.75f,
                    stiffness = 350f
                )
            )
        } else {
            progress.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = 400f
                )
            )
        }
    }

    val animationProgress = progress.value
    if (animationProgress <= 0.01f && !isOpen) return

    val currentOffsetX = (targetOffsetX * animationProgress).roundToInt()
    val currentOffsetY = (targetOffsetY * animationProgress).roundToInt()

    val shapes = WispTheme.shapes
    val actionSize = spacing.orbSize * 0.78f

    Row(
        modifier = modifier
            .offset { IntOffset(currentOffsetX, currentOffsetY) }
            .scale(animationProgress)
            .alpha(animationProgress)
            .springPress(onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = space.contentDescription
            }
            .padding(spacing.extraSmall),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(actionSize)
                .clip(shapes.pill)
                .then(
                    if (isSelected) {
                        Modifier.background(WispTheme.brandGradient)
                    } else {
                        Modifier
                            .background(colors.surfaceHigh.copy(alpha = 0.96f))
                            .border(spacing.strokeThick, colors.outline, shapes.pill)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = space.icon,
                contentDescription = null,
                tint = if (isSelected) colors.background else colors.accentB,
                modifier = Modifier.size(spacing.large)
            )
        }
        Text(
            text = space.title,
            style = typography.labelLarge,
            color = colors.text,
            maxLines = 1,
            modifier = Modifier
                .clip(shapes.pill)
                .background(colors.surface.copy(alpha = 0.92f))
                .border(spacing.stroke, colors.outline, shapes.pill)
                .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
        )
    }
}
