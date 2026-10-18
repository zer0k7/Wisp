package app.wisp.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Core color tokens for the Wisp design system (2027 look).
 * Default dark theme: "Midnight Ink".
 */
data class WispColorTokens(
    val background: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val outline: Color,
    val text: Color,
    val textMuted: Color,
    val accentA: Color,
    val accentB: Color,
    val hot: Color,
    val success: Color,
    val warning: Color,
    val danger: Color
)

val MidnightInkTokens = WispColorTokens(
    background = Color(0xFF0A0A10),
    surface = Color(0xFF13131B),
    surfaceHigh = Color(0xFF1C1C27),
    outline = Color(0xFF2B2B3A),
    text = Color(0xFFF5F5FA),
    textMuted = Color(0xFF9B9BB0),
    accentA = Color(0xFF8B6CFF),
    accentB = Color(0xFF22E5C4),
    hot = Color(0xFFFF5C8A),
    success = Color(0xFF3DDC97),
    warning = Color(0xFFFFB020),
    danger = Color(0xFFFF5A5F)
)

val PaperGlassTokens = WispColorTokens(
    background = Color(0xFFF4F3F8),
    surface = Color(0xFFFFFFFF),
    surfaceHigh = Color(0xFFECEAF3),
    outline = Color(0xFFD9D6E4),
    text = Color(0xFF14141C),
    textMuted = Color(0xFF6B6980),
    accentA = Color(0xFF6D4DF0),
    accentB = Color(0xFF0FBF9F),
    hot = Color(0xFFE8336B),
    success = Color(0xFF1FAE72),
    warning = Color(0xFFD98E00),
    danger = Color(0xFFD93A40)
)
