package app.wisp.core.designsystem.navigation

import kotlin.math.cos
import kotlin.math.sin

enum class DockEdge {
    LEFT,
    RIGHT
}

data class FanItemPosition(
    val space: NavSpace,
    val offsetX: Float,
    val offsetY: Float,
    val angleDegrees: Float
)

object OrbPhysics {

    /**
     * Determines whether the orb should snap to the left or right edge.
     */
    fun calculateSnappingEdge(currentX: Float, screenWidth: Float): DockEdge {
        return if (currentX < screenWidth / 2f) {
            DockEdge.LEFT
        } else {
            DockEdge.RIGHT
        }
    }

    /**
     * Calculates the target X position when snapped to an edge with margin.
     */
    fun calculateTargetX(edge: DockEdge, screenWidth: Float, orbSize: Float, margin: Float): Float {
        return when (edge) {
            DockEdge.LEFT -> margin
            DockEdge.RIGHT -> (screenWidth - orbSize - margin).coerceAtLeast(margin)
        }
    }

    /**
     * Constrains Y position within safe vertical bounds.
     */
    fun constrainY(currentY: Float, minY: Float, maxY: Float, orbSize: Float): Float {
        val effectiveMax = (maxY - orbSize).coerceAtLeast(minY)
        return currentY.coerceIn(minY, effectiveMax)
    }

    /**
     * Computes the (offsetX, offsetY) positions for the 5 spaces in an arc around the orb center.
     * When docked on the right, angles fan outwards to the left (from -85° to -175°).
     * When docked on the left, angles fan outwards to the right (from -95° to -5°).
     *
     * @param dockEdge Whether the orb is currently on the LEFT or RIGHT edge.
     * @param radius Distance from the orb center in pixels.
     * @param spaces The list of spaces to place along the arc.
     */
    fun calculateArcPositions(
        dockEdge: DockEdge,
        radius: Float,
        spaces: List<NavSpace> = NavSpace.spaces
    ): List<FanItemPosition> {
        if (spaces.isEmpty()) return emptyList()
        val count = spaces.size

        val (startAngle, endAngle) = when (dockEdge) {
            DockEdge.RIGHT -> -90.0 to -180.0
            DockEdge.LEFT -> -90.0 to 0.0
        }

        val step = if (count > 1) (endAngle - startAngle) / (count - 1) else 0.0

        return spaces.mapIndexed { index, space ->
            val angleDeg = startAngle + step * index
            val angleRad = Math.toRadians(angleDeg)
            val offsetX = (radius * cos(angleRad)).toFloat()
            val offsetY = (radius * sin(angleRad)).toFloat()

            FanItemPosition(
                space = space,
                offsetX = offsetX,
                offsetY = offsetY,
                angleDegrees = angleDeg.toFloat()
            )
        }
    }
}
