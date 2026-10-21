package app.wisp.core.designsystem.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The 5 primary spaces in Wisp navigation.
 */
enum class NavSpace(
    val title: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    BOARDS(
        title = "Boards",
        icon = Icons.Rounded.Dashboard,
        contentDescription = "Navigate to Boards space"
    ),
    INBOX(
        title = "Inbox",
        icon = Icons.Rounded.Inbox,
        contentDescription = "Navigate to Inbox space"
    ),
    TRACKER(
        title = "Tracker",
        icon = Icons.AutoMirrored.Rounded.TrendingDown,
        contentDescription = "Navigate to Price Tracker space"
    ),
    SEARCH(
        title = "Search",
        icon = Icons.Rounded.Search,
        contentDescription = "Navigate to Search space"
    ),
    YOU(
        title = "Settings",
        icon = Icons.Rounded.Settings,
        contentDescription = "Navigate to Settings"
    );

    companion object {
        val spaces: List<NavSpace> = entries
    }
}
