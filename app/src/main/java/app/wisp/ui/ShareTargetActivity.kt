package app.wisp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.wisp.core.common.metadata.LinkMetadataExtractor
import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity
import app.wisp.core.designsystem.components.WispButton
import app.wisp.core.designsystem.components.WispChip
import app.wisp.core.designsystem.components.WispSheet
import app.wisp.core.designsystem.components.WispTextField
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.theme.WispTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareTargetActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    private val boardsViewModel: BoardsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedText = extractSharedText(intent)
        val sharedUri = extractSharedUri(intent)

        setContent {
            val themeSpec by themeViewModel.themeSpec.collectAsStateWithLifecycle()
            val boards by boardsViewModel.boards.collectAsStateWithLifecycle()

            WispTheme(themeSpec = themeSpec) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ShareTargetContent(
                        initialText = sharedText,
                        initialImageUri = sharedUri?.toString(),
                        boards = boards,
                        onCheckDuplicate = { url -> boardsViewModel.checkDuplicate(url) },
                        onSave = { title, url, price, boardId, imageUrl ->
                            boardsViewModel.addWishItem(
                                title = title,
                                url = url,
                                price = price,
                                boardId = boardId
                            )
                            finish()
                        },
                        onDismiss = { finish() }
                    )
                }
            }
        }
    }

    private fun extractSharedText(intent: Intent): String {
        return when (intent.action) {
            Intent.ACTION_SEND -> intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
            else -> ""
        }
    }

    private fun extractSharedUri(intent: Intent): Uri? {
        return when (intent.action) {
            Intent.ACTION_SEND -> {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
            }
            else -> null
        }
    }
}

@Composable
fun ShareTargetContent(
    initialText: String,
    initialImageUri: String? = null,
    boards: List<BoardEntity>,
    onCheckDuplicate: suspend (String) -> WishItemEntity? = { null },
    onSave: (title: String, url: String, price: Double, boardId: String, imageUrl: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    val urlRegex = Regex("""https?://[^\s]+""")
    val extractedUrl = remember(initialText) {
        urlRegex.find(initialText)?.value ?: if (initialText.startsWith("http")) initialText else ""
    }

    var title by remember { mutableStateOf(initialText.replace(extractedUrl, "").trim().ifBlank { "New Wish" }) }
    var priceText by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf(LinkMetadataExtractor.extractStore(extractedUrl) ?: "") }
    var selectedBoardId by remember(boards) { mutableStateOf(boards.firstOrNull()?.id ?: "") }
    var duplicateItem by remember { mutableStateOf<WishItemEntity?>(null) }
    var isFetchingMetadata by remember { mutableStateOf(extractedUrl.isNotBlank()) }

    LaunchedEffect(extractedUrl) {
        if (extractedUrl.isNotBlank()) {
            duplicateItem = onCheckDuplicate(extractedUrl)
            val metadata = LinkMetadataExtractor.extract(extractedUrl)
            if (metadata.title.isNotBlank()) {
                title = metadata.title
            }
            val metaPrice = metadata.price
            if (metaPrice != null && metaPrice > 0) {
                priceText = metaPrice.toInt().toString()
            }
            val metaStore = metadata.store
            if (!metaStore.isNullOrBlank()) {
                storeName = metaStore
            }
            isFetchingMetadata = false
        }
    }

    WispSheet(
        modifier = Modifier.fillMaxWidth(),
        glass = true,
        showHandle = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(WispTheme.brandGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Link,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Save to Wisp",
                            style = typography.titleMedium,
                            color = colors.text
                        )
                        if (isFetchingMetadata) {
                            Text(
                                text = "Reading page info...",
                                style = typography.labelSmall,
                                color = colors.accentB
                            )
                        } else if (storeName.isNotBlank()) {
                            Text(
                                text = "From $storeName",
                                style = typography.labelSmall,
                                color = colors.accentB
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceHigh)
                        .springPress(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Duplicate warning badge
            val duplicate = duplicateItem
            if (duplicate != null) {
                val duplicateBoard = boards.find { it.id == duplicate.boardId }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(WispTheme.shapes.pill)
                        .background(colors.warning.copy(alpha = 0.15f))
                        .padding(horizontal = spacing.medium, vertical = spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = colors.warning,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Already in '${duplicateBoard?.name ?: "Wishlist"}'",
                        style = typography.labelSmall,
                        color = colors.warning
                    )
                }
            }

            // Title Field
            WispTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Wish title..."
            )

            // Price Field
            WispTextField(
                value = priceText,
                onValueChange = { priceText = it },
                placeholder = "Price (e.g. 1999)..."
            )

            // Board Picker Chips
            if (boards.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                    Text(
                        text = "Choose Board:",
                        style = typography.labelSmall,
                        color = colors.textMuted
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        boards.take(4).forEach { board ->
                            WispChip(
                                label = "${board.icon} ${board.name}",
                                selected = selectedBoardId == board.id,
                                onClick = { selectedBoardId = board.id }
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                WispButton(
                    text = "Cancel",
                    onClick = onDismiss
                )
                Spacer(modifier = Modifier.width(spacing.small))
                WispButton(
                    text = "Save Wish",
                    onClick = {
                        val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
                        val targetId = selectedBoardId.ifBlank { boards.firstOrNull()?.id ?: "board_default" }
                        onSave(title, extractedUrl, parsedPrice, targetId, initialImageUri)
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))
        }
    }
}
