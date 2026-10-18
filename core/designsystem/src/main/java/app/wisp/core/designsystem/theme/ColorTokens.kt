package app.wisp.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Core color tokens for the Wisp design system (2027 look).
 * All screens take colors exclusively from these tokens.
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

// Preset 1: Dark "Midnight Ink" (Default)
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

// Preset 2: Light "Paper Glass"
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

// Preset 3: AMOLED Pure Black
val AmoledTokens = WispColorTokens(
    background = Color(0xFF000000),
    surface = Color(0xFF0A0A0A),
    surfaceHigh = Color(0xFF141414),
    outline = Color(0xFF242424),
    text = Color(0xFFFFFFFF),
    textMuted = Color(0xFF888888),
    accentA = Color(0xFF8B6CFF),
    accentB = Color(0xFF22E5C4),
    hot = Color(0xFFFF5C8A),
    success = Color(0xFF3DDC97),
    warning = Color(0xFFFFB020),
    danger = Color(0xFFFF5A5F)
)

// Preset 4: Sunset
val SunsetTokens = WispColorTokens(
    background = Color(0xFF0E0A08),
    surface = Color(0xFF17120E),
    surfaceHigh = Color(0xFF241B16),
    outline = Color(0xFF3A2B22),
    text = Color(0xFFFFF6F0),
    textMuted = Color(0xFFA69389),
    accentA = Color(0xFFFF7A59),
    accentB = Color(0xFFFFC145),
    hot = Color(0xFFFF5C8A),
    success = Color(0xFF3DDC97),
    warning = Color(0xFFFFB020),
    danger = Color(0xFFFF5A5F)
)

// Preset 5: Sakura
val SakuraTokens = WispColorTokens(
    background = Color(0xFF0F0A0D),
    surface = Color(0xFF1A1218),
    surfaceHigh = Color(0xFF261A23),
    outline = Color(0xFF3D2938),
    text = Color(0xFFFFF0F5),
    textMuted = Color(0xFFA8919E),
    accentA = Color(0xFFFF8FB8),
    accentB = Color(0xFFFFD1E3),
    hot = Color(0xFFFF5C8A),
    success = Color(0xFF3DDC97),
    warning = Color(0xFFFFB020),
    danger = Color(0xFFFF5A5F)
)

// Preset 6: Ocean
val OceanTokens = WispColorTokens(
    background = Color(0xFF070C12),
    surface = Color(0xFF0E1622),
    surfaceHigh = Color(0xFF162334),
    outline = Color(0xFF22364E),
    text = Color(0xFFF0F7FF),
    textMuted = Color(0xFF899FB7),
    accentA = Color(0xFF3A8DFF),
    accentB = Color(0xFF3DE0FF),
    hot = Color(0xFFFF5C8A),
    success = Color(0xFF3DDC97),
    warning = Color(0xFFFFB020),
    danger = Color(0xFFFF5A5F)
)

// Preset 7: Terminal
val TerminalTokens = WispColorTokens(
    background = Color(0xFF050D08),
    surface = Color(0xFF0A170F),
    surfaceHigh = Color(0xFF102418),
    outline = Color(0xFF183B26),
    text = Color(0xFFE8FFEF),
    textMuted = Color(0xFF7FA88B),
    accentA = Color(0xFF39FF88),
    accentB = Color(0xFF0BBF5C),
    hot = Color(0xFFFF5C8A),
    success = Color(0xFF3DDC97),
    warning = Color(0xFFFFB020),
    danger = Color(0xFFFF5A5F)
)

// Preset 8: Mono
val MonoTokens = WispColorTokens(
    background = Color(0xFF0C0C0C),
    surface = Color(0xFF161616),
    surfaceHigh = Color(0xFF222222),
    outline = Color(0xFF333333),
    text = Color(0xFFFFFFFF),
    textMuted = Color(0xFF9E9E9E),
    accentA = Color(0xFFFFFFFF),
    accentB = Color(0xFF888888),
    hot = Color(0xFFFF5C8A),
    success = Color(0xFF3DDC97),
    warning = Color(0xFFFFB020),
    danger = Color(0xFFFF5A5F)
)
