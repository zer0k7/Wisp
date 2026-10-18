package app.wisp.core.designsystem.navigation

/**
 * Supported navigation styles in Wisp.
 * Configurable live in Studio.
 */
enum class NavStyleType(
    val id: String,
    val displayName: String,
    val description: String
) {
    ORB_DOCK(
        id = "ORB_DOCK",
        displayName = "Orb Dock",
        description = "56dp glowing floating orb snapping to edges with arc fan-out"
    ),
    EDGE_HANDLE(
        id = "EDGE_HANDLE",
        displayName = "Edge Handle",
        description = "Slim pull-out strip docked to the screen edge"
    ),
    FLOATING_PILL(
        id = "FLOATING_PILL",
        displayName = "Floating Pill",
        description = "Collapsed floating pill that expands into spaces"
    ),
    GESTURE_ONLY(
        id = "GESTURE_ONLY",
        displayName = "Gesture Only",
        description = "Minimalist fullscreen UI driven by edge swipes and pull-down"
    );

    companion object {
        fun fromId(id: String): NavStyleType = entries.firstOrNull { it.id == id } ?: ORB_DOCK
    }
}
