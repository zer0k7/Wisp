package app.wisp.core.designsystem.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun EdgeHandleNav(
    currentSpace: NavSpace,
    onSpaceSelected: (NavSpace) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val haptic = LocalHapticFeedback.current

    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Slim Pull-out Handle
        if (!isExpanded) {
            Box(
                modifier = Modifier
                    .padding(end = spacing.extraSmall)
                    .width(8.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                    .background(WispTheme.brandGradient)
                    .springPress(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isExpanded = true
                    })
                    .semantics {
                        role = Role.Button
                        contentDescription = "Expand navigation spaces"
                    }
            )
        }

        // Expanded Frosted Vertical Strip
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(dampingRatio = 0.75f)
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = spring(dampingRatio = 0.75f)
            ) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(end = spacing.small)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surfaceHigh.copy(alpha = 0.92f))
                    .border(spacing.stroke, colors.outline, RoundedCornerShape(20.dp))
                    .padding(vertical = spacing.medium, horizontal = spacing.small)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NavSpace.spaces.forEach { space ->
                        val isSelected = space == currentSpace
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) WispTheme.brandGradient else Color.Transparent)
                                .springPress(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSpaceSelected(space)
                                    isExpanded = false
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = space.icon,
                                contentDescription = space.contentDescription,
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
