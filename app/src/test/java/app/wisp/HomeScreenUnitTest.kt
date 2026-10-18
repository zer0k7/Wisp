package app.wisp

import app.wisp.core.designsystem.theme.MidnightInkTokens
import org.junit.Assert.assertNotNull
import org.junit.Test

class HomeScreenUnitTest {
    @Test
    fun testMidnightInkPaletteLoaded() {
        assertNotNull(MidnightInkTokens.background)
        assertNotNull(MidnightInkTokens.accentA)
        assertNotNull(MidnightInkTokens.accentB)
    }
}
