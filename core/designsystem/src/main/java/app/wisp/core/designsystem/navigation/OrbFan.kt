package app.wisp.core.designsystem.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.wisp.core.designsystem.theme.WispTheme

@Composable
fun OrbFan(
    isOpen: Boolean,
    currentSpace: NavSpace,
    dockEdge: DockEdge,
    onSpaceSelected: (NavSpace) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes

    AnimatedVisibility(
        visible = isOpen,
        modifier = modifier,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Column(
            modifier = Modifier
                .background(colors.surface, shapes.card)
                .border(spacing.stroke, colors.outline, shapes.card)
                .padding(spacing.small),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {
            NavSpace.spaces.forEachIndexed { index, space ->
                OrbFanItem(
                    space = space,
                    isSelected = space == currentSpace,
                    targetOffsetX = 0f,
                    targetOffsetY = 0f,
                    isOpen = isOpen,
                    animationDelayMs = index * 25,
                    onClick = { onSpaceSelected(space) }
                )
            }
        }
    }
}
