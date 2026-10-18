package app.wisp.core.designsystem

import app.wisp.core.designsystem.theme.MidnightInkTokens
import app.wisp.core.designsystem.theme.PaperGlassTokens
import app.wisp.core.designsystem.theme.ThemeSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeSpecTest {
    @Test
    fun testDefaultThemeIsMidnightInk() {
        val defaultTheme = ThemeSpec.Default
        assertEquals("Midnight Ink", defaultTheme.name)
        assertTrue(defaultTheme.isDark)
        assertEquals(MidnightInkTokens, defaultTheme.colors)
    }

    @Test
    fun testPaperGlassThemeIsLight() {
        val paperGlass = ThemeSpec.PaperGlass
        assertEquals("Paper Glass", paperGlass.name)
        assertTrue(!paperGlass.isDark)
        assertEquals(PaperGlassTokens, paperGlass.colors)
    }
}
