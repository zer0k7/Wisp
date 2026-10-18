package app.wisp.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.json.JSONObject

enum class ThemePreset(val displayName: String) {
    MIDNIGHT_INK("Midnight Ink"),
    PAPER_GLASS("Paper Glass"),
    AMOLED("AMOLED"),
    SUNSET("Sunset"),
    SAKURA("Sakura"),
    OCEAN("Ocean"),
    TERMINAL("Terminal"),
    MONO("Mono"),
    DYNAMIC("Material You Dynamic")
}

enum class SpacingDensity {
    COMPACT,
    COMFORTABLE,
    RELAXED
}

enum class IconStyle {
    ROUNDED,
    SHARP,
    OUTLINE
}

enum class FontPair(val displayName: String) {
    DEFAULT("Space Grotesk / Inter"),
    EDITORIAL("Serif Display / Sans"),
    TECHNICAL("Monospace / Sans")
}

data class WispSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val orbSize: Dp = 56.dp,
    val stroke: Dp = 1.dp,
    val strokeThick: Dp = 2.dp
) {
    companion object {
        fun fromDensity(density: SpacingDensity): WispSpacing = when (density) {
            SpacingDensity.COMPACT -> WispSpacing(
                extraSmall = 2.dp,
                small = 6.dp,
                medium = 12.dp,
                large = 18.dp,
                extraLarge = 24.dp,
                orbSize = 48.dp,
                stroke = 1.dp,
                strokeThick = 2.dp
            )
            SpacingDensity.COMFORTABLE -> WispSpacing(
                extraSmall = 4.dp,
                small = 8.dp,
                medium = 16.dp,
                large = 24.dp,
                extraLarge = 32.dp,
                orbSize = 56.dp,
                stroke = 1.dp,
                strokeThick = 2.dp
            )
            SpacingDensity.RELAXED -> WispSpacing(
                extraSmall = 6.dp,
                small = 12.dp,
                medium = 20.dp,
                large = 28.dp,
                extraLarge = 40.dp,
                orbSize = 64.dp,
                stroke = 1.dp,
                strokeThick = 2.dp
            )
        }
    }
}

data class WispShapes(
    val cardRadius: Dp = 28.dp,
    val pillRadius: Dp = 999.dp,
    val sheetRadius: Dp = 20.dp,
    val chipRadius: Dp = 12.dp
) {
    val card: Shape get() = RoundedCornerShape(cardRadius)
    val pill: Shape get() = RoundedCornerShape(pillRadius)
    val sheet: Shape get() = RoundedCornerShape(topStart = sheetRadius, topEnd = sheetRadius)
    val chip: Shape get() = RoundedCornerShape(chipRadius)
}

/**
 * Full theme specification for Wisp theme engine.
 */
data class ThemeSpec(
    val preset: ThemePreset = ThemePreset.MIDNIGHT_INK,
    val name: String = "Midnight Ink",
    val isDark: Boolean = true,
    val colors: WispColorTokens = MidnightInkTokens,
    val gradientAngle: Float = 135f,
    val cornerRadiusScale: Float = 28f, // dp
    val pillRadius: Float = 999f,
    val sheetRadius: Float = 20f,
    val fontPair: FontPair = FontPair.DEFAULT,
    val density: SpacingDensity = SpacingDensity.COMFORTABLE,
    val glassIntensity: Float = 0.85f,
    val motionIntensity: Float = 1.0f,
    val iconStyle: IconStyle = IconStyle.ROUNDED
) {
    val spacing: WispSpacing get() = WispSpacing.fromDensity(density)
    val shapes: WispShapes get() = WispShapes(
        cardRadius = cornerRadiusScale.dp,
        pillRadius = pillRadius.dp,
        sheetRadius = sheetRadius.dp
    )

    companion object {
        val MidnightInk = ThemeSpec(
            preset = ThemePreset.MIDNIGHT_INK,
            name = "Midnight Ink",
            isDark = true,
            colors = MidnightInkTokens
        )

        val PaperGlass = ThemeSpec(
            preset = ThemePreset.PAPER_GLASS,
            name = "Paper Glass",
            isDark = false,
            colors = PaperGlassTokens
        )

        val Amoled = ThemeSpec(
            preset = ThemePreset.AMOLED,
            name = "AMOLED",
            isDark = true,
            colors = AmoledTokens
        )

        val Sunset = ThemeSpec(
            preset = ThemePreset.SUNSET,
            name = "Sunset",
            isDark = true,
            colors = SunsetTokens
        )

        val Sakura = ThemeSpec(
            preset = ThemePreset.SAKURA,
            name = "Sakura",
            isDark = true,
            colors = SakuraTokens
        )

        val Ocean = ThemeSpec(
            preset = ThemePreset.OCEAN,
            name = "Ocean",
            isDark = true,
            colors = OceanTokens
        )

        val Terminal = ThemeSpec(
            preset = ThemePreset.TERMINAL,
            name = "Terminal",
            isDark = true,
            colors = TerminalTokens
        )

        val Mono = ThemeSpec(
            preset = ThemePreset.MONO,
            name = "Mono",
            isDark = true,
            colors = MonoTokens
        )

        val Default = MidnightInk

        val AllPresets = listOf(
            MidnightInk,
            PaperGlass,
            Amoled,
            Sunset,
            Sakura,
            Ocean,
            Terminal,
            Mono
        )

        fun forPreset(preset: ThemePreset): ThemeSpec = when (preset) {
            ThemePreset.MIDNIGHT_INK -> MidnightInk
            ThemePreset.PAPER_GLASS -> PaperGlass
            ThemePreset.AMOLED -> Amoled
            ThemePreset.SUNSET -> Sunset
            ThemePreset.SAKURA -> Sakura
            ThemePreset.OCEAN -> Ocean
            ThemePreset.TERMINAL -> Terminal
            ThemePreset.MONO -> Mono
            ThemePreset.DYNAMIC -> MidnightInk // Dynamic overrides colors dynamically
        }
    }
}

/**
 * Clean JSON serializer and deserializer for ThemeSpec persistence and sharing.
 */
object ThemeSpecJsonAdapter {

    fun toJson(spec: ThemeSpec): String {
        val root = JSONObject()
        root.put("preset", spec.preset.name)
        root.put("name", spec.name)
        root.put("isDark", spec.isDark)
        root.put("gradientAngle", spec.gradientAngle.toDouble())
        root.put("cornerRadiusScale", spec.cornerRadiusScale.toDouble())
        root.put("fontPair", spec.fontPair.name)
        root.put("density", spec.density.name)
        root.put("glassIntensity", spec.glassIntensity.toDouble())
        root.put("motionIntensity", spec.motionIntensity.toDouble())
        root.put("iconStyle", spec.iconStyle.name)

        val colorsObj = JSONObject()
        colorsObj.put("background", colorToHex(spec.colors.background))
        colorsObj.put("surface", colorToHex(spec.colors.surface))
        colorsObj.put("surfaceHigh", colorToHex(spec.colors.surfaceHigh))
        colorsObj.put("outline", colorToHex(spec.colors.outline))
        colorsObj.put("text", colorToHex(spec.colors.text))
        colorsObj.put("textMuted", colorToHex(spec.colors.textMuted))
        colorsObj.put("accentA", colorToHex(spec.colors.accentA))
        colorsObj.put("accentB", colorToHex(spec.colors.accentB))
        colorsObj.put("hot", colorToHex(spec.colors.hot))
        colorsObj.put("success", colorToHex(spec.colors.success))
        colorsObj.put("warning", colorToHex(spec.colors.warning))
        colorsObj.put("danger", colorToHex(spec.colors.danger))
        root.put("colors", colorsObj)

        return root.toString()
    }

    fun fromJson(jsonStr: String): ThemeSpec {
        return try {
            val root = JSONObject(jsonStr)
            val presetName = root.optString("preset", ThemePreset.MIDNIGHT_INK.name)
            val preset = runCatching { ThemePreset.valueOf(presetName) }.getOrDefault(ThemePreset.MIDNIGHT_INK)
            val base = ThemeSpec.forPreset(preset)

            val colorsObj = root.optJSONObject("colors")
            val colors = if (colorsObj != null) {
                WispColorTokens(
                    background = parseColor(colorsObj.optString("background"), base.colors.background),
                    surface = parseColor(colorsObj.optString("surface"), base.colors.surface),
                    surfaceHigh = parseColor(colorsObj.optString("surfaceHigh"), base.colors.surfaceHigh),
                    outline = parseColor(colorsObj.optString("outline"), base.colors.outline),
                    text = parseColor(colorsObj.optString("text"), base.colors.text),
                    textMuted = parseColor(colorsObj.optString("textMuted"), base.colors.textMuted),
                    accentA = parseColor(colorsObj.optString("accentA"), base.colors.accentA),
                    accentB = parseColor(colorsObj.optString("accentB"), base.colors.accentB),
                    hot = parseColor(colorsObj.optString("hot"), base.colors.hot),
                    success = parseColor(colorsObj.optString("success"), base.colors.success),
                    warning = parseColor(colorsObj.optString("warning"), base.colors.warning),
                    danger = parseColor(colorsObj.optString("danger"), base.colors.danger)
                )
            } else {
                base.colors
            }

            val fontPairName = root.optString("fontPair", base.fontPair.name)
            val fontPair = runCatching { FontPair.valueOf(fontPairName) }.getOrDefault(FontPair.DEFAULT)

            val densityName = root.optString("density", base.density.name)
            val density = runCatching { SpacingDensity.valueOf(densityName) }.getOrDefault(SpacingDensity.COMFORTABLE)

            val iconStyleName = root.optString("iconStyle", base.iconStyle.name)
            val iconStyle = runCatching { IconStyle.valueOf(iconStyleName) }.getOrDefault(IconStyle.ROUNDED)

            ThemeSpec(
                preset = preset,
                name = root.optString("name", base.name),
                isDark = root.optBoolean("isDark", base.isDark),
                colors = colors,
                gradientAngle = root.optDouble("gradientAngle", base.gradientAngle.toDouble()).toFloat(),
                cornerRadiusScale = root.optDouble("cornerRadiusScale", base.cornerRadiusScale.toDouble()).toFloat(),
                fontPair = fontPair,
                density = density,
                glassIntensity = root.optDouble("glassIntensity", base.glassIntensity.toDouble()).toFloat(),
                motionIntensity = root.optDouble("motionIntensity", base.motionIntensity.toDouble()).toFloat(),
                iconStyle = iconStyle
            )
        } catch (_: Exception) {
            ThemeSpec.Default
        }
    }

    private fun colorToHex(color: Color): String {
        val argb = (color.value.toLong() shr 32).toInt()
        return String.format("#%08X", argb)
    }

    private fun parseColor(hex: String, fallback: Color): Color {
        return try {
            if (hex.isBlank()) return fallback
            val cleaned = hex.removePrefix("#")
            val colorLong = cleaned.toLong(16)
            if (cleaned.length <= 6) {
                Color((0xFF000000 or colorLong).toInt())
            } else {
                Color(colorLong.toInt())
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
