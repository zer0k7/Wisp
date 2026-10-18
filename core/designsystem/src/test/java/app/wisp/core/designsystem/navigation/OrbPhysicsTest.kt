package app.wisp.core.designsystem.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrbPhysicsTest {

    @Test
    fun testSnappingEdgeLeftAndRight() {
        val screenWidth = 1080f
        assertEquals(DockEdge.LEFT, OrbPhysics.calculateSnappingEdge(200f, screenWidth))
        assertEquals(DockEdge.LEFT, OrbPhysics.calculateSnappingEdge(539f, screenWidth))
        assertEquals(DockEdge.RIGHT, OrbPhysics.calculateSnappingEdge(541f, screenWidth))
        assertEquals(DockEdge.RIGHT, OrbPhysics.calculateSnappingEdge(900f, screenWidth))
    }

    @Test
    fun testCalculateTargetX() {
        val screenWidth = 1000f
        val orbSize = 150f
        val margin = 50f

        val leftTarget = OrbPhysics.calculateTargetX(DockEdge.LEFT, screenWidth, orbSize, margin)
        assertEquals(50f, leftTarget, 0.001f)

        val rightTarget = OrbPhysics.calculateTargetX(DockEdge.RIGHT, screenWidth, orbSize, margin)
        assertEquals(800f, rightTarget, 0.001f)
    }

    @Test
    fun testConstrainYWithinBounds() {
        val minY = 100f
        val maxY = 2000f
        val orbSize = 150f

        // Below minimum
        assertEquals(100f, OrbPhysics.constrainY(50f, minY, maxY, orbSize), 0.001f)

        // Inside bounds
        assertEquals(500f, OrbPhysics.constrainY(500f, minY, maxY, orbSize), 0.001f)

        // Above maximum (maxY - orbSize = 1850f)
        assertEquals(1850f, OrbPhysics.constrainY(1900f, minY, maxY, orbSize), 0.001f)
    }

    @Test
    fun testCalculateArcPositionsRightEdge() {
        val positions = OrbPhysics.calculateArcPositions(DockEdge.RIGHT, radius = 200f)
        assertEquals(5, positions.size)

        // When docked on the right, all fan items should fan out to the left (offsetX <= 0)
        positions.forEach { pos ->
            assertTrue("offsetX should be <= 0 when docked on right, was ${pos.offsetX}", pos.offsetX <= 0f)
            assertTrue("offsetY should be <= 0 (upwards), was ${pos.offsetY}", pos.offsetY <= 0f)
        }
    }

    @Test
    fun testCalculateArcPositionsLeftEdge() {
        val positions = OrbPhysics.calculateArcPositions(DockEdge.LEFT, radius = 200f)
        assertEquals(5, positions.size)

        // When docked on the left, all fan items should fan out to the right (offsetX >= 0)
        positions.forEach { pos ->
            assertTrue("offsetX should be >= 0 when docked on left, was ${pos.offsetX}", pos.offsetX >= 0f)
            assertTrue("offsetY should be <= 0 (upwards), was ${pos.offsetY}", pos.offsetY <= 0f)
        }
    }
}
