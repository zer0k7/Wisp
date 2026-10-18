package app.wisp.core.designsystem.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TrendingDown
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
        icon = Icons.Rounded.TrendingDown,
        contentDescription = "Navigate to Price Tracker space"
    ),
    SEARCH(
        title = "Search",
        icon = Icons.Rounded.Search,
        contentDescription = "Navigate to Search space"
    ),
    YOU(
        title = "You",
        icon = Icons.Rounded.Person,
        contentDescription = "Navigate to Profile and Studio space"
    );

    companion object {
        val spaces: List<NavSpace> = entries
    }
}
