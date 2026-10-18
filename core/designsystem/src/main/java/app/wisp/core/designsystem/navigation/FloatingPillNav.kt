package app.wisp.core.designsystem.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.theme.WispTheme

@Composable
fun FloatingPillNav(
    currentSpace: NavSpace,
    onSpaceSelected: (NavSpace) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = WispTheme.colors
    val shapes = WispTheme.shapes
    val spacing = WispTheme.spacing
    val haptic = LocalHapticFeedback.current

    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = spacing.large)
                .animateContentSize(animationSpec = spring(dampingRatio = 0.75f))
                .clip(shapes.pill)
                .background(colors.surfaceHigh.copy(alpha = 0.94f))
                .border(spacing.stroke, colors.outline, shapes.pill)
                .padding(horizontal = spacing.medium, vertical = spacing.small)
        ) {
            if (!isExpanded) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .springPress(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isExpanded = true
                        })
                        .semantics {
                            role = Role.Button
                            contentDescription = "Expand Floating Pill Navigation. Current: ${currentSpace.title}"
                        }
                ) {
                    Icon(
                        imageVector = currentSpace.icon,
                        contentDescription = null,
                        tint = colors.accentB,
                        modifier = Modifier.size(spacing.large)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(spacing.medium)
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavSpace.spaces.forEach { space ->
                        val isSelected = space == currentSpace
                        val bgModifier = if (isSelected) {
                            Modifier.background(WispTheme.brandGradient)
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .then(bgModifier)
                                .springPress(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSpaceSelected(space)
                                    isExpanded = false
                                })
                                .semantics {
                                    role = Role.Button
                                    contentDescription = space.contentDescription
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = space.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else colors.text,
                                modifier = Modifier.size(spacing.large)
                            )
                        }
                    }
                }
            }
        }
    }
}
