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

val boardIconOptions: List<Pair<String, ImageVector>> = listOf(
    "folder" to Icons.Rounded.Folder,
    "devices" to Icons.Rounded.Devices,
    "style" to Icons.Rounded.Checkroom,
    "gifts" to Icons.Rounded.CardGiftcard,
    "books" to Icons.Rounded.MenuBook,
    "home" to Icons.Rounded.Home,
    "travel" to Icons.Rounded.Flight
)

fun boardIconFor(key: String): ImageVector = boardIconOptions.firstOrNull { it.first == key }?.second
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
