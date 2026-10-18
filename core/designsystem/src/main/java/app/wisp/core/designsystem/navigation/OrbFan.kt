package app.wisp.core.designsystem.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun OrbFan(
    isOpen: Boolean,
    currentSpace: NavSpace,
    dockEdge: DockEdge,
    onSpaceSelected: (NavSpace) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val arcRadiusPx = with(density) { 110.dp.toPx() }

    val arcPositions = remember(dockEdge, arcRadiusPx) {
        OrbPhysics.calculateArcPositions(
            dockEdge = dockEdge,
            radius = arcRadiusPx,
            spaces = NavSpace.spaces
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        arcPositions.forEachIndexed { index, itemPos ->
            OrbFanItem(
                space = itemPos.space,
                isSelected = itemPos.space == currentSpace,
                targetOffsetX = itemPos.offsetX,
                targetOffsetY = itemPos.offsetY,
                isOpen = isOpen,
                animationDelayMs = index * 25,
                onClick = { onSpaceSelected(itemPos.space) }
            )
        }
    }
}
