package app.wisp.core.designsystem

import androidx.compose.ui.graphics.Color
import app.wisp.core.designsystem.theme.FontPair
import app.wisp.core.designsystem.theme.IconStyle
import app.wisp.core.designsystem.theme.MidnightInkTokens
import app.wisp.core.designsystem.theme.PaperGlassTokens
import app.wisp.core.designsystem.theme.SpacingDensity
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.ThemeSpec
import app.wisp.core.designsystem.theme.ThemeSpecJsonAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
        assertFalse(paperGlass.isDark)
        assertEquals(PaperGlassTokens, paperGlass.colors)
    }

    @Test
    fun testAllPresetsExistAndAreConfigured() {
        assertEquals(8, ThemeSpec.AllPresets.size)
        ThemeSpec.AllPresets.forEach { presetSpec ->
            assertNotNull(presetSpec.name)
            assertNotNull(presetSpec.colors)
            assertTrue(presetSpec.cornerRadiusScale > 0)
        }
    }

    @Test
    fun testJsonAdapterSerializationRoundTrip_AllPresets() {
        ThemeSpec.AllPresets.forEach { originalSpec ->
            val json = ThemeSpecJsonAdapter.toJson(originalSpec)
            assertTrue("JSON should not be blank for ${originalSpec.name}", json.isNotBlank())

            val deserialized = ThemeSpecJsonAdapter.fromJson(json)
            assertEquals(originalSpec.preset, deserialized.preset)
            assertEquals(originalSpec.name, deserialized.name)
            assertEquals(originalSpec.isDark, deserialized.isDark)
            assertEquals(originalSpec.fontPair, deserialized.fontPair)
            assertEquals(originalSpec.density, deserialized.density)
            assertEquals(originalSpec.iconStyle, deserialized.iconStyle)
            assertEquals(originalSpec.colors.background, deserialized.colors.background)
            assertEquals(originalSpec.colors.accentA, deserialized.colors.accentA)
            assertEquals(originalSpec.colors.accentB, deserialized.colors.accentB)
        }
    }

    @Test
    fun testJsonAdapterSerializationWithCustomProperties() {
        val customSpec = ThemeSpec(
            preset = ThemePreset.SUNSET,
            name = "Custom Sunset",
            isDark = true,
            cornerRadiusScale = 16f,
            fontPair = FontPair.EDITORIAL,
            density = SpacingDensity.RELAXED,
            glassIntensity = 0.5f,
            motionIntensity = 0.8f,
            iconStyle = IconStyle.SHARP
        )

        val json = ThemeSpecJsonAdapter.toJson(customSpec)
        val deserialized = ThemeSpecJsonAdapter.fromJson(json)

        assertEquals(ThemePreset.SUNSET, deserialized.preset)
        assertEquals("Custom Sunset", deserialized.name)
        assertEquals(16f, deserialized.cornerRadiusScale, 0.01f)
        assertEquals(FontPair.EDITORIAL, deserialized.fontPair)
        assertEquals(SpacingDensity.RELAXED, deserialized.density)
        assertEquals(0.5f, deserialized.glassIntensity, 0.01f)
        assertEquals(0.8f, deserialized.motionIntensity, 0.01f)
        assertEquals(IconStyle.SHARP, deserialized.iconStyle)
    }

    @Test
    fun testJsonAdapterHandlesInvalidJsonGracefully() {
        val invalidJson = "{ corrupted: json !!! }"
        val result = ThemeSpecJsonAdapter.fromJson(invalidJson)
        assertEquals(ThemeSpec.Default, result)
    }

    @Test
    fun testJsonAdapterHandlesEmptyString() {
        val result = ThemeSpecJsonAdapter.fromJson("")
        assertEquals(ThemeSpec.Default, result)
    }
}
