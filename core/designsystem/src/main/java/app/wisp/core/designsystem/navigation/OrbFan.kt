package app.wisp.core.designsystem.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
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
    val spacing = WispTheme.spacing
    val density = LocalDensity.current
    val itemHeight = spacing.large + (spacing.extraSmall * 2f)
    val itemStep = itemHeight + spacing.extraSmall
    val diagonalTravel = spacing.large * 2f

    BoxWithConstraints(modifier = modifier) {
        val itemWidth = if (maxWidth > diagonalTravel) {
            maxWidth - diagonalTravel
        } else {
            maxWidth
        }
        val availableWidthPx = with(density) { maxWidth.toPx() }
        val itemWidthPx = with(density) { itemWidth.toPx() }
        val itemStepPx = with(density) { itemStep.toPx() }
        val diagonalTravelPx = with(density) { diagonalTravel.toPx() }

        // The items share one invisible trajectory rather than a containing panel.
        Box {
            NavSpace.spaces.forEachIndexed { index, space ->
                val xOffset = if (dockEdge == DockEdge.RIGHT) {
                    (availableWidthPx - itemWidthPx) -
                        (diagonalTravelPx * index / (NavSpace.spaces.size - 1).coerceAtLeast(1))
                } else {
                    diagonalTravelPx * index / (NavSpace.spaces.size - 1).coerceAtLeast(1)
                }
                OrbFanItem(
                    space = space,
                    isSelected = space == currentSpace,
                    targetOffsetX = xOffset,
                    targetOffsetY = itemStepPx * index,
                    isOpen = isOpen,
                    animationDelayMs = index * 55,
                    onClick = { onSpaceSelected(space) },
                    modifier = Modifier.requiredWidth(itemWidth)
                )
            }
        }
    }
}
