package app.wisp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import app.wisp.core.designsystem.theme.WispTheme

data class BoardIconOption(
    val key: String,
    val label: String,
    val icon: ImageVector
)

val boardIconOptions: List<BoardIconOption> = listOf(
    BoardIconOption("folder", "General", Icons.Rounded.Folder),
    BoardIconOption("devices", "Tech", Icons.Rounded.Devices),
    BoardIconOption("style", "Clothing", Icons.Rounded.Checkroom),
    BoardIconOption("gifts", "Gifts", Icons.Rounded.CardGiftcard),
    BoardIconOption("books", "Books", Icons.Rounded.MenuBook),
    BoardIconOption("home", "Home", Icons.Rounded.Home),
    BoardIconOption("travel", "Travel", Icons.Rounded.Flight)
)

fun boardIconFor(key: String): ImageVector = boardIconOptions.firstOrNull { it.key == key }?.icon
    ?: Icons.Rounded.Folder

@Composable
fun BoardIcon(key: String, contentDescription: String?, modifier: Modifier = Modifier) {
    Icon(
        imageVector = boardIconFor(key),
        contentDescription = contentDescription,
        tint = WispTheme.colors.accentB,
        modifier = modifier
    )
}
