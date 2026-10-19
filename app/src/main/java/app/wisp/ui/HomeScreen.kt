package app.wisp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity
import app.wisp.core.designsystem.components.WispButton
import app.wisp.core.designsystem.components.WispCard
import app.wisp.core.designsystem.components.WispChip
import app.wisp.core.designsystem.components.WispSheet
import app.wisp.ui.board.BoardDetailView
import app.wisp.core.designsystem.components.WispTextField
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.navigation.NavSpace
import app.wisp.core.designsystem.navigation.NavStyleType
import app.wisp.core.designsystem.navigation.WispScaffold
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.WispTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    boards: List<BoardEntity> = emptyList(),
    items: List<WishItemEntity> = emptyList(),
    priceDrops: List<WishItemEntity> = emptyList(),
    selectedBoard: BoardEntity? = null,
    onOpenBoard: (BoardEntity) -> Unit = {},
    onCloseBoard: () -> Unit = {},
    onCreateBoard: (String, String) -> Unit = { _, _ -> },
    onDeleteBoard: (String) -> Unit = {},
    onAddWishItem: (title: String, url: String, price: Double, boardId: String?) -> Unit = { _, _, _, _ -> },
    onDeleteItem: (String) -> Unit = {},
    selectedItem: WishItemEntity? = null,
    onOpenItem: (WishItemEntity) -> Unit = {},
    onCloseItem: () -> Unit = {},
    onUpdateItemStatus: (itemId: String, status: String) -> Unit = { _, _ -> },
    onMoveItemToBoard: (itemId: String, targetBoardId: String) -> Unit = { _, _ -> },
    onUpdateItem: (WishItemEntity) -> Unit = {},
    onSaveClipboardLink: (String) -> Unit = {},
    currentPreset: ThemePreset = ThemePreset.MIDNIGHT_INK,
    onPresetSelected: (ThemePreset) -> Unit = {},
    currentSpace: NavSpace = NavSpace.BOARDS,
    onSpaceSelected: (NavSpace) -> Unit = {},
    navStyle: NavStyleType = NavStyleType.ORB_DOCK,
    onNavStyleSelected: (NavStyleType) -> Unit = {},
    initialXRatio: Float = 0.90f,
    initialYRatio: Float = 0.85f,
    onOrbPositionChanged: ((Float, Float) -> Unit)? = null,
    quickAddTriggered: Boolean = false,
    onDismissQuickAdd: () -> Unit = {},
    onTriggerQuickAdd: () -> Unit = {},
    selectedItemIds: Set<String> = emptySet(),
    onToggleSelectItem: (String) -> Unit = {},
    onClearSelection: () -> Unit = {},
    onSelectAllItems: (List<String>) -> Unit = {},
    onDeleteSelectedItems: () -> Unit = {},
    onMoveSelectedItems: (String) -> Unit = {},
    onUpdateBoardView: (boardId: String, viewType: String, columns: Int) -> Unit = { _, _, _ -> },
    onUpdateItemCanvas: (itemId: String, x: Float, y: Float, scale: Float, rotation: Float) -> Unit = { _, _, _, _, _ -> }
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography
    val haptic = LocalHapticFeedback.current

    var quickAddTitle by remember { mutableStateOf("") }
    var quickAddUrl by remember { mutableStateOf("") }
    var quickAddPrice by remember { mutableStateOf("") }
    var selectedTargetBoardId by remember { mutableStateOf(boards.firstOrNull()?.id ?: "") }

    var isNewBoardSheetOpen by remember { mutableStateOf(false) }
    var newBoardName by remember { mutableStateOf("") }
    var newBoardIcon by remember { mutableStateOf("✨") }

    var internalSelectedSpace by remember { mutableStateOf(currentSpace) }
    var internalNavStyle by remember { mutableStateOf(navStyle) }
    var internalQuickAdd by remember { mutableStateOf(quickAddTriggered) }

    val effectiveSpace = if (currentSpace != NavSpace.BOARDS) currentSpace else internalSelectedSpace
    val effectiveNavStyle = if (navStyle != NavStyleType.ORB_DOCK) navStyle else internalNavStyle
    val effectiveQuickAdd = quickAddTriggered || internalQuickAdd

    WispScaffold(
        currentSpace = effectiveSpace,
        onSpaceSelected = { space ->
            internalSelectedSpace = space
            onSpaceSelected(space)
        },
        navStyle = effectiveNavStyle,
        onNavStyleSelected = { style ->
            internalNavStyle = style
            onNavStyleSelected(style)
        },
        onQuickAdd = {
            internalQuickAdd = true
            onTriggerQuickAdd()
        },
        onThemePresetSelected = onPresetSelected,
        initialXRatio = initialXRatio,
        initialYRatio = initialYRatio,
        onOrbPositionChanged = onOrbPositionChanged,
        modifier = modifier
    ) { space ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            if (selectedItem != null) {
                WishItemDetailView(
                    item = selectedItem,
                    boards = boards,
                    onBack = onCloseItem,
                    onStatusChange = { status -> onUpdateItemStatus(selectedItem.id, status) },
                    onMoveToBoard = { targetId -> onMoveItemToBoard(selectedItem.id, targetId) },
                    onDelete = {
                        onDeleteItem(selectedItem.id)
                        onCloseItem()
                    },
                    onUpdate = onUpdateItem
                )
            } else {
                when (space) {
                    NavSpace.BOARDS -> {
                        if (selectedBoard == null) {
                            RealBoardsGrid(
                                boards = boards,
                                items = items,
                                onOpenBoard = onOpenBoard,
                                onOpenCreateBoard = { isNewBoardSheetOpen = true }
                            )
                        } else {
                            BoardDetailView(
                                board = selectedBoard,
                                items = items.filter { it.boardId == selectedBoard.id },
                                allBoards = boards,
                                onBack = onCloseBoard,
                                onOpenItem = onOpenItem,
                                onDeleteItem = onDeleteItem,
                                onDeleteBoard = {
                                    onDeleteBoard(selectedBoard.id)
                                },
                                onAddItem = {
                                    selectedTargetBoardId = selectedBoard.id
                                    internalQuickAdd = true
                                },
                                onUpdateBoardView = { viewType, cols ->
                                    onUpdateBoardView(selectedBoard.id, viewType, cols)
                                },
                                selectedItemIds = selectedItemIds,
                                onToggleSelectItem = onToggleSelectItem,
                                onClearSelection = onClearSelection,
                                onSelectAllItems = onSelectAllItems,
                                onDeleteSelectedItems = onDeleteSelectedItems,
                                onMoveSelectedItems = onMoveSelectedItems,
                                onUpdateItemCanvas = onUpdateItemCanvas
                            )
                        }
                    }
                    NavSpace.INBOX -> RealInboxSpace(
                        items = items,
                        onOpenItem = onOpenItem,
                        onDeleteItem = onDeleteItem,
                        onQuickAdd = { internalQuickAdd = true },
                        onSaveClipboardLink = onSaveClipboardLink
                    )
                    NavSpace.TRACKER -> RealTrackerSpace(
                        priceDrops = priceDrops,
                        onOpenItem = onOpenItem,
                        onDeleteItem = onDeleteItem
                    )
                    NavSpace.SEARCH -> RealSearchSpace(
                        items = items,
                        onOpenItem = onOpenItem,
                        onDeleteItem = onDeleteItem
                    )
                    NavSpace.YOU -> RealStudioSettingsSpace(
                        currentNavStyle = effectiveNavStyle,
                        onNavStyleSelected = { style ->
                            internalNavStyle = style
                            onNavStyleSelected(style)
                        },
                        currentPreset = currentPreset,
                        onPresetSelected = onPresetSelected,
                        boardsCount = boards.size,
                        itemsCount = items.size
                    )
                }
            }

            // Quick Add Bottom Sheet
            if (effectiveQuickAdd) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    WispSheet(
                        modifier = Modifier.fillMaxWidth(),
                        glass = true,
                        showHandle = true
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.medium),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Quick Add to Wishlist",
                                style = typography.headlineMedium,
                                color = colors.text
                            )
                            WispTextField(
                                value = quickAddTitle,
                                onValueChange = { quickAddTitle = it },
                                placeholder = "Item name (e.g. Wireless Headphones)..."
                            )
                            WispTextField(
                                value = quickAddUrl,
                                onValueChange = { quickAddUrl = it },
                                placeholder = "Paste link (Amazon, Flipkart, etc.)..."
                            )
                            WispTextField(
                                value = quickAddPrice,
                                onValueChange = { quickAddPrice = it },
                                placeholder = "Price (e.g. 2499)..."
                            )

                            // Select Board Chips
                            if (boards.isNotEmpty()) {
                                Text(
                                    text = "Save to Board:",
                                    style = typography.labelLarge,
                                    color = colors.textMuted
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                                ) {
                                    val currentBoardChoice = selectedTargetBoardId.ifBlank { boards.first().id }
                                    boards.forEach { b ->
                                        WispChip(
                                            label = "${b.icon} ${b.name}",
                                            selected = b.id == currentBoardChoice,
                                            onClick = { selectedTargetBoardId = b.id }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                WispButton(
                                    text = "Cancel",
                                    onClick = {
                                        internalQuickAdd = false
                                        onDismissQuickAdd()
                                    }
                                )
                                Spacer(modifier = Modifier.width(spacing.small))
                                WispButton(
                                    text = "Save Item",
                                    onClick = {
                                        val priceVal = quickAddPrice.toDoubleOrNull() ?: 0.0
                                        val targetBoard = selectedTargetBoardId.ifBlank { boards.firstOrNull()?.id }
                                        onAddWishItem(quickAddTitle, quickAddUrl, priceVal, targetBoard)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        quickAddTitle = ""
                                        quickAddUrl = ""
                                        quickAddPrice = ""
                                        internalQuickAdd = false
                                        onDismissQuickAdd()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Create New Board Sheet
            if (isNewBoardSheetOpen) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    WispSheet(
                        modifier = Modifier.fillMaxWidth(),
                        glass = true,
                        showHandle = true
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.medium),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Create New Board",
                                style = typography.headlineMedium,
                                color = colors.text
                            )
                            WispTextField(
                                value = newBoardName,
                                onValueChange = { newBoardName = it },
                                placeholder = "Board name (e.g. Dream Travel, Sneaker Grail)..."
                            )

                            Text(
                                text = "Choose Icon:",
                                style = typography.labelLarge,
                                color = colors.textMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.small)
                            ) {
                                listOf("✨", "💻", "👟", "🎁", "📚", "🏠", "✈️").forEach { icon ->
                                    WispChip(
                                        label = icon,
                                        selected = newBoardIcon == icon,
                                        onClick = { newBoardIcon = icon }
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                WispButton(
                                    text = "Cancel",
                                    onClick = { isNewBoardSheetOpen = false }
                                )
                                Spacer(modifier = Modifier.width(spacing.small))
                                WispButton(
                                    text = "Create Board",
                                    onClick = {
                                        if (newBoardName.isNotBlank()) {
                                            onCreateBoard(newBoardName, newBoardIcon)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            newBoardName = ""
                                            isNewBoardSheetOpen = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RealBoardsGrid(
    boards: List<BoardEntity>,
    items: List<WishItemEntity>,
    onOpenBoard: (BoardEntity) -> Unit,
    onOpenCreateBoard: () -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes
    val typography = WispTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your boards",
                    style = typography.displayLarge,
                    color = colors.text
                )
                Text(
                    text = "${boards.size} boards • ${items.size} saved wishes",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(shapes.pill)
                    .background(colors.surfaceHigh)
                    .springPress(onClick = onOpenCreateBoard)
                    .padding(horizontal = spacing.medium, vertical = spacing.small)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = colors.accentB,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "New Board",
                        style = typography.labelSmall,
                        color = colors.accentB
                    )
                }
            }
        }

        // 2-Column Grid of Board Cards
        val chunkedBoards = boards.chunked(2)
        chunkedBoards.forEach { rowBoards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                rowBoards.forEach { board ->
                    val boardItems = items.filter { it.boardId == board.id }
                    WispCard(
                        modifier = Modifier
                            .weight(1f)
                            .springPress(onClick = { onOpenBoard(board) }),
                        glass = true
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = board.icon,
                                    style = typography.headlineLarge
                                )
                                Text(
                                    text = "${boardItems.size} items",
                                    style = typography.labelSmall,
                                    color = colors.accentB
                                )
                            }
                            Text(
                                text = board.name,
                                style = typography.titleMedium,
                                color = colors.text
                            )
                            if (boardItems.isNotEmpty()) {
                                Text(
                                    text = boardItems.first().title,
                                    style = typography.labelSmall,
                                    color = colors.textMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                if (rowBoards.size == 1) {
                    // Dashed "+ New Board" Card
                    DashedNewBoardCard(
                        modifier = Modifier.weight(1f),
                        onClick = onOpenCreateBoard
                    )
                }
            }
        }

        // If even number of boards, show "+ New Board" tile at the end
        if (boards.size % 2 == 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                DashedNewBoardCard(
                    modifier = Modifier.weight(1f),
                    onClick = onOpenCreateBoard
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
    }
}

@Composable
private fun DashedNewBoardCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes
    val typography = WispTheme.typography

    Box(
        modifier = modifier
            .clip(shapes.card)
            .background(colors.surfaceHigh.copy(alpha = 0.5f))
            .border(
                width = spacing.stroke,
                color = colors.outline,
                shape = shapes.card
            )
            .springPress(onClick = onClick)
            .padding(spacing.large),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Create new board",
                    tint = colors.accentB,
                    modifier = Modifier.size(spacing.large)
                )
            }
            Text(
                text = "New Board",
                style = typography.titleMedium,
                color = colors.accentB
            )
        }
    }
}

@Composable
private fun WishItemDetailView(
    item: WishItemEntity,
    boards: List<BoardEntity>,
    onBack: () -> Unit,
    onStatusChange: (String) -> Unit,
    onMoveToBoard: (String) -> Unit,
    onDelete: () -> Unit,
    onUpdate: (WishItemEntity) -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes
    val typography = WispTheme.typography
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    var isMoveSheetOpen by remember { mutableStateOf(false) }
    var notesText by remember(item.id) { mutableStateOf(item.notes ?: "") }
    var isEditingNotes by remember { mutableStateOf(false) }

    val currentBoard = boards.find { it.id == item.boardId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // Top Navigation Bar
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
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceHigh)
                        .springPress(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.text
                    )
                }
                Column {
                    Text(
                        text = currentBoard?.let { "${it.icon} ${it.name}" } ?: "Wish Item",
                        style = typography.labelSmall,
                        color = colors.textMuted
                    )
                    Text(
                        text = "Item Details",
                        style = typography.headlineMedium,
                        color = colors.text
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceHigh)
                    .springPress(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete item",
                    tint = colors.danger
                )
            }
        }

        // Hero Card with Glow & Title
        WispCard(
            modifier = Modifier.fillMaxWidth(),
            glass = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val storeName = item.store
                    if (!storeName.isNullOrBlank()) {
                        WispChip(
                            label = storeName,
                            selected = true,
                            onClick = {}
                        )
                    } else {
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                    }

                    val statusColor = when (item.status.lowercase()) {
                        "bought" -> colors.success
                        "reserved" -> colors.warning
                        "gifted" -> colors.hot
                        else -> colors.accentA
                    }
                    Text(
                        text = item.status.uppercase(),
                        style = typography.labelSmall,
                        color = statusColor
                    )
                }

                Text(
                    text = item.title,
                    style = typography.headlineLarge,
                    color = colors.text
                )

                if (item.price > 0) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Text(
                            text = "${item.currency}${item.price.toInt()}",
                            style = typography.displayLarge,
                            color = colors.text
                        )

                        val origPrice = item.originalPrice
                        if (origPrice != null && origPrice > item.price) {
                            Text(
                                text = "${item.currency}${origPrice.toInt()}",
                                style = typography.bodySmall(colors.textMuted)
                            )
                            val savings = origPrice - item.price
                            Text(
                                text = "Save ${item.currency}${savings.toInt()}",
                                style = typography.labelSmall,
                                color = colors.hot
                            )
                        }
                    }
                }
            }
        }

        // Status Control (Wanted, Reserved, Bought, Gifted)
        WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "Status",
                    style = typography.titleMedium,
                    color = colors.text
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    val statuses = listOf(
                        "wanted" to "Wanted",
                        "reserved" to "Reserved",
                        "bought" to "Bought",
                        "gifted" to "Gifted"
                    )
                    statuses.forEach { (key, label) ->
                        val isSelected = item.status.equals(key, ignoreCase = true)
                        WispChip(
                            label = label,
                            selected = isSelected,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onStatusChange(key)
                            }
                        )
                    }
                }
            }
        }

        // Move to Board
        WispCard(
            modifier = Modifier.fillMaxWidth(),
            glass = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Board",
                        style = typography.labelSmall,
                        color = colors.textMuted
                    )
                    Text(
                        text = currentBoard?.let { "${it.icon} ${it.name}" } ?: "Unassigned",
                        style = typography.titleMedium,
                        color = colors.text
                    )
                }
                WispButton(
                    text = "Move Board",
                    onClick = { isMoveSheetOpen = true }
                )
            }
        }

        // Product Link
        val itemUrl = item.url
        if (!itemUrl.isNullOrBlank()) {
            WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Product Link",
                            style = typography.labelSmall,
                            color = colors.textMuted
                        )
                        Text(
                            text = itemUrl,
                            style = typography.bodyMedium,
                            color = colors.accentA,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.small))
                    WispButton(
                        text = "Visit",
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(itemUrl))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                }
            }
        }

        // Notes & Details
        WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notes & Reminders",
                        style = typography.titleMedium,
                        color = colors.text
                    )
                    if (!isEditingNotes) {
                        WispChip(
                            label = if (notesText.isBlank()) "+ Add Note" else "Edit",
                            selected = false,
                            onClick = { isEditingNotes = true }
                        )
                    }
                }

                if (isEditingNotes) {
                    WispTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        placeholder = "e.g. Size M, wait for Black Friday sale..."
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        WispButton(
                            text = "Save Note",
                            onClick = {
                                onUpdate(item.copy(notes = notesText.trim()))
                                isEditingNotes = false
                            }
                        )
                    }
                } else if (notesText.isNotBlank()) {
                    Text(
                        text = notesText,
                        style = typography.bodyMedium,
                        color = colors.textMuted
                    )
                } else {
                    Text(
                        text = "No notes added yet.",
                        style = typography.bodyMedium,
                        color = colors.textMuted.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
    }

    // Move to Board Sheet
    if (isMoveSheetOpen) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            WispSheet(
                modifier = Modifier.fillMaxWidth(),
                glass = true,
                showHandle = true
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Move to Board",
                        style = typography.headlineMedium,
                        color = colors.text
                    )
                    Text(
                        text = "Select a destination board for this item:",
                        style = typography.bodyMedium,
                        color = colors.textMuted
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                        boards.forEach { board ->
                            val isCurrent = board.id == item.boardId
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shapes.card)
                                    .background(if (isCurrent) colors.accentA.copy(alpha = 0.2f) else colors.surfaceHigh)
                                    .springPress(onClick = {
                                        onMoveToBoard(board.id)
                                        isMoveSheetOpen = false
                                    })
                                    .padding(spacing.medium)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${board.icon} ${board.name}",
                                        style = typography.titleMedium,
                                        color = if (isCurrent) colors.accentA else colors.text
                                    )
                                    if (isCurrent) {
                                        Text(
                                            text = "Current",
                                            style = typography.labelSmall,
                                            color = colors.accentA
                                        )
                                    }
                                }
                            }
                        }
                    }
                    WispButton(
                        text = "Close",
                        onClick = { isMoveSheetOpen = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun WishItemCard(
    item: WishItemEntity,
    onClick: () -> Unit = {},
    onDelete: () -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    WispCard(
        modifier = Modifier
            .fillMaxWidth()
            .springPress(onClick = onClick),
        glass = true
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        Text(
                            text = item.title,
                            style = typography.titleMedium,
                            color = colors.text,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (!item.status.equals("wanted", ignoreCase = true)) {
                            val statusColor = when (item.status.lowercase()) {
                                "bought" -> colors.success
                                "reserved" -> colors.warning
                                "gifted" -> colors.hot
                                else -> colors.accentA
                            }
                            Text(
                                text = "· ${item.status}",
                                style = typography.labelSmall,
                                color = statusColor
                            )
                        }
                    }
                    val storeName = item.store
                    if (!storeName.isNullOrBlank()) {
                        Text(
                            text = storeName,
                            style = typography.labelSmall,
                            color = colors.accentB
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .springPress(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete item",
                        tint = colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.price > 0) {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                        Text(
                            text = "${item.currency}${item.price.toInt()}",
                            style = typography.headlineMedium,
                            color = colors.text
                        )
                        val origPrice = item.originalPrice
                        if (origPrice != null && origPrice > item.price) {
                            Text(
                                text = "${item.currency}${origPrice.toInt()}",
                                style = typography.bodySmall(colors.textMuted)
                            )
                        }
                    }
                }

                if (!item.url.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.OpenInNew,
                            contentDescription = null,
                            tint = colors.accentA,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Link Saved",
                            style = typography.labelSmall,
                            color = colors.accentA
                        )
                    }
                }
            }
        }
    }
}

private fun app.wisp.core.designsystem.theme.WispTypography.bodySmall(color: Color): androidx.compose.ui.text.TextStyle {
    return this.labelSmall.copy(color = color)
}

@Composable
private fun RealInboxSpace(
    items: List<WishItemEntity>,
    onOpenItem: (WishItemEntity) -> Unit,
    onDeleteItem: (String) -> Unit,
    onQuickAdd: () -> Unit,
    onSaveClipboardLink: (String) -> Unit = {}
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography
    val clipboardManager = LocalClipboardManager.current
    var clipboardUrl by remember {
        mutableStateOf(
            clipboardManager.getText()?.text?.trim()?.let { text ->
                if (text.startsWith("http://") || text.startsWith("https://")) text else null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Text(
            text = "Inbox",
            style = typography.displayLarge,
            color = colors.text
        )
        Text(
            text = "Saved links and wishes waiting to be categorized",
            style = typography.bodyMedium,
            color = colors.textMuted
        )

        if (clipboardUrl != null) {
            val detectedUrl = clipboardUrl!!
            WispCard(
                modifier = Modifier.fillMaxWidth(),
                glass = true
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.accentA.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsActive,
                                contentDescription = "Clipboard link detected",
                                tint = colors.accentA,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Save link from clipboard?",
                                style = typography.titleMedium,
                                color = colors.text
                            )
                            Text(
                                text = detectedUrl,
                                style = typography.labelSmall,
                                color = colors.textMuted,
                                maxLines = 1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(spacing.small))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        WispButton(
                            text = "Save",
                            onClick = {
                                onSaveClipboardLink(detectedUrl)
                                clipboardUrl = null
                            }
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceHigh)
                                .springPress(onClick = { clipboardUrl = null }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Dismiss clipboard suggestion",
                                tint = colors.textMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        WispButton(
            text = "+ Quick Add Wish",
            onClick = onQuickAdd,
            modifier = Modifier.fillMaxWidth()
        )

        if (items.isEmpty()) {
            WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
                Text(
                    text = "Inbox is empty! Tap the + button to save your first link.",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            }
        } else {
            items.forEach { item ->
                WishItemCard(
                    item = item,
                    onClick = { onOpenItem(item) },
                    onDelete = { onDeleteItem(item.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
    }
}

@Composable
private fun RealTrackerSpace(
    priceDrops: List<WishItemEntity>,
    onOpenItem: (WishItemEntity) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Price Tracker",
                    style = typography.displayLarge,
                    color = colors.text
                )
                Text(
                    text = "Real-time drops on your saved wishes",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            }
            Icon(
                imageVector = Icons.Rounded.NotificationsActive,
                contentDescription = null,
                tint = colors.hot,
                modifier = Modifier.size(spacing.extraLarge)
            )
        }

        if (priceDrops.isEmpty()) {
            WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
                Text(
                    text = "No price drops detected yet. Wisp checks prices offline in the background.",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            }
        } else {
            priceDrops.forEach { item ->
                val dropAmount = (item.originalPrice ?: 0.0) - item.price
                WispCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .springPress(onClick = { onOpenItem(item) }),
                    glass = true
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.title,
                                style = typography.titleMedium,
                                color = colors.text,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "-${item.currency}${dropAmount.toInt()}",
                                style = typography.labelLarge,
                                color = colors.hot
                            )
                        }
                        Text(
                            text = "Now ${item.currency}${item.price.toInt()} (was ${item.currency}${item.originalPrice?.toInt()})",
                            style = typography.bodyMedium,
                            color = colors.success
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
    }
}

@Composable
private fun RealSearchSpace(
    items: List<WishItemEntity>,
    onOpenItem: (WishItemEntity) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    var searchQuery by remember { mutableStateOf("") }
    val filtered = if (searchQuery.isBlank()) {
        items
    } else {
        items.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                (it.store?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Text(
            text = "Search",
            style = typography.displayLarge,
            color = colors.text
        )
        WispTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Search saved items, stores, products...",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = colors.textMuted
                )
            }
        )

        Text(
            text = "Results (${filtered.size})",
            style = typography.labelLarge,
            color = colors.accentB
        )

        filtered.forEach { item ->
            WishItemCard(
                item = item,
                onClick = { onOpenItem(item) },
                onDelete = { onDeleteItem(item.id) }
            )
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
    }
}

@Composable
private fun RealStudioSettingsSpace(
    currentNavStyle: NavStyleType,
    onNavStyleSelected: (NavStyleType) -> Unit,
    currentPreset: ThemePreset,
    onPresetSelected: (ThemePreset) -> Unit,
    boardsCount: Int,
    itemsCount: Int
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Text(
            text = "Studio & Settings",
            style = typography.displayLarge,
            color = colors.text
        )

        // Navigation Style Selector
        WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "Navigation Style",
                    style = typography.headlineMedium,
                    color = colors.accentB
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    NavStyleType.entries.forEach { style ->
                        WispChip(
                            label = style.displayName,
                            selected = style == currentNavStyle,
                            onClick = { onNavStyleSelected(style) }
                        )
                    }
                }
            }
        }

        // Theme Presets Selector
        WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "Theme Presets",
                    style = typography.headlineMedium,
                    color = colors.accentA
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    ThemePreset.entries.forEach { preset ->
                        WispChip(
                            label = preset.displayName,
                            selected = preset == currentPreset,
                            onClick = { onPresetSelected(preset) }
                        )
                    }
                }
            }
        }

        // Offline & Database Status
        WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = colors.success,
                        modifier = Modifier.size(spacing.large)
                    )
                    Text(
                        text = "100% Offline & Private Room DB",
                        style = typography.titleMedium,
                        color = colors.success
                    )
                }
                Text(
                    text = "Database holds $boardsCount boards and $itemsCount wishes on this device. 0 analytics, 0 trackers.",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
    }
}
