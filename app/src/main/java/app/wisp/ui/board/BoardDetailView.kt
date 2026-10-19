package app.wisp.ui.board

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DriveFileMove
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity
import app.wisp.core.designsystem.components.WispButton
import app.wisp.core.designsystem.components.WispCard
import app.wisp.core.designsystem.components.WispChip
import app.wisp.core.designsystem.components.WispSheet
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.theme.WispTheme
import coil.compose.AsyncImage
import kotlin.math.roundToInt

enum class BoardViewMode(val id: String, val label: String, val icon: ImageVector) {
    GRID("grid", "Grid", Icons.Rounded.GridView),
    CANVAS("canvas", "Canvas", Icons.Rounded.Palette),
    LIST("list", "List", Icons.Rounded.ViewList),
    DECK("deck", "Deck", Icons.Rounded.Layers);

    companion object {
        fun fromId(id: String): BoardViewMode {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: GRID
        }
    }
}

@Composable
fun BoardDetailView(
    board: BoardEntity,
    items: List<WishItemEntity>,
    allBoards: List<BoardEntity> = emptyList(),
    onBack: () -> Unit,
    onOpenItem: (WishItemEntity) -> Unit,
    onDeleteItem: (String) -> Unit,
    onDeleteBoard: () -> Unit,
    onAddItem: () -> Unit,
    onUpdateBoardView: (viewType: String, columns: Int) -> Unit = { _, _ -> },
    selectedItemIds: Set<String> = emptySet(),
    onToggleSelectItem: (String) -> Unit = {},
    onClearSelection: () -> Unit = {},
    onSelectAllItems: (List<String>) -> Unit = {},
    onDeleteSelectedItems: () -> Unit = {},
    onMoveSelectedItems: (String) -> Unit = {},
    onUpdateItemCanvas: (itemId: String, x: Float, y: Float, scale: Float, rotation: Float) -> Unit = { _, _, _, _, _ -> }
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    val currentMode = remember(board.viewType) { BoardViewMode.fromId(board.viewType) }
    var gridCols by remember(board.gridColumns) { mutableIntStateOf(board.gridColumns.coerceIn(1, 4)) }
    var isMoveSheetOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.medium),
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceHigh)
                            .springPress(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back to boards",
                            tint = colors.text
                        )
                    }
                    Column {
                        Text(
                            text = "${board.icon} ${board.name}",
                            style = typography.headlineMedium,
                            color = colors.text
                        )
                        Text(
                            text = "${items.size} items",
                            style = typography.labelSmall,
                            color = colors.textMuted
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceHigh)
                            .springPress(onClick = onDeleteBoard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete board",
                            tint = colors.danger,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // View Mode Switcher Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoardViewMode.entries.forEach { mode ->
                    val isSelected = mode == currentMode
                    WispChip(
                        label = mode.label,
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                onUpdateBoardView(mode.id, gridCols)
                            }
                        }
                    )
                }

                // If in Grid mode, show column count selector
                if (currentMode == BoardViewMode.GRID) {
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = "Cols:",
                        style = typography.labelSmall,
                        color = colors.textMuted
                    )
                    (1..4).forEach { colCount ->
                        val isColSelected = colCount == gridCols
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isColSelected) colors.accentA else colors.surfaceHigh)
                                .clickable {
                                    gridCols = colCount
                                    onUpdateBoardView(currentMode.id, colCount)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$colCount",
                                style = typography.labelSmall,
                                color = if (isColSelected) Color.White else colors.text,
                                fontWeight = if (isColSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Add item quick button
            WispButton(
                text = "+ Add Wish to ${board.name}",
                onClick = onAddItem,
                modifier = Modifier.fillMaxWidth()
            )

            // Content Area with Morphing Animation
            if (items.isEmpty()) {
                WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(spacing.medium),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Text(
                            text = "No items in this board yet",
                            style = typography.titleMedium,
                            color = colors.text
                        )
                        Text(
                            text = "Add items with the button above or share from any browser.",
                            style = typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                }
            } else {
                AnimatedContent(
                    targetState = currentMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "BoardViewMorph",
                    modifier = Modifier.weight(1f)
                ) { mode ->
                    when (mode) {
                        BoardViewMode.GRID -> MasonryGridView(
                            items = items,
                            columns = gridCols,
                            selectedItemIds = selectedItemIds,
                            onToggleSelect = onToggleSelectItem,
                            onOpenItem = onOpenItem,
                            onDeleteItem = onDeleteItem
                        )
                        BoardViewMode.CANVAS -> CanvasMoodboardView(
                            items = items,
                            onOpenItem = onOpenItem,
                            onUpdateItemCanvas = onUpdateItemCanvas
                        )
                        BoardViewMode.LIST -> CompactListView(
                            items = items,
                            selectedItemIds = selectedItemIds,
                            onToggleSelect = onToggleSelectItem,
                            onOpenItem = onOpenItem,
                            onDeleteItem = onDeleteItem
                        )
                        BoardViewMode.DECK -> CardDeckView(
                            items = items,
                            onOpenItem = onOpenItem,
                            onKeep = { /* kept in board */ },
                            onDiscard = { item -> onDeleteItem(item.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Strip for Multi-Selection
        AnimatedVisibility(
            visible = selectedItemIds.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(spacing.medium)
        ) {
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colors.accentA),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${selectedItemIds.size}",
                                style = typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Selected",
                            style = typography.titleSmall(colors.text)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        // Move to Board
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceHigh)
                                .springPress(onClick = { isMoveSheetOpen = true }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DriveFileMove,
                                contentDescription = "Move selected items",
                                tint = colors.accentB,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Delete
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceHigh)
                                .springPress(onClick = onDeleteSelectedItems),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete selected items",
                                tint = colors.danger,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Clear Selection
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceHigh)
                                .springPress(onClick = onClearSelection),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Clear selection",
                                tint = colors.textMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Destination Board Picker Sheet
        if (isMoveSheetOpen) {
            WispSheet(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                glass = true,
                showHandle = true
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    modifier = Modifier.fillMaxWidth().padding(spacing.medium)
                ) {
                    Text(
                        text = "Move ${selectedItemIds.size} items to:",
                        style = typography.headlineMedium,
                        color = colors.text
                    )
                    allBoards.filter { it.id != board.id }.forEach { targetBoard ->
                        WispCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onMoveSelectedItems(targetBoard.id)
                                    isMoveSheetOpen = false
                                },
                            glass = true
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(spacing.small),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.small)
                            ) {
                                Text(text = targetBoard.icon, style = typography.headlineMedium)
                                Text(
                                    text = targetBoard.name,
                                    style = typography.titleMedium,
                                    color = colors.text
                                )
                            }
                        }
                    }
                    WispButton(
                        text = "Cancel",
                        onClick = { isMoveSheetOpen = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// VIEW 1: Masonry Grid View
// -------------------------------------------------------------
@Composable
fun MasonryGridView(
    items: List<WishItemEntity>,
    columns: Int,
    selectedItemIds: Set<String>,
    onToggleSelect: (String) -> Unit,
    onOpenItem: (WishItemEntity) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val spacing = WispTheme.spacing
    val colors = WispTheme.colors
    val typography = WispTheme.typography

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns.coerceIn(1, 4)),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        verticalItemSpacing = spacing.small,
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { item ->
            val isSelected = selectedItemIds.contains(item.id)
            val isMultiSelectActive = selectedItemIds.isNotEmpty()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(WispTheme.shapes.card)
                    .background(colors.surface)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) colors.accentA else colors.outline,
                        shape = WispTheme.shapes.card
                    )
                    .clickable {
                        if (isMultiSelectActive) {
                            onToggleSelect(item.id)
                        } else {
                            onOpenItem(item)
                        }
                    }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Image or Color Placeholder
                    if (!item.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(if (columns == 1) 1.8f else 1.2f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (columns == 1) 80.dp else 60.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(colors.accentA.copy(alpha = 0.2f), colors.accentB.copy(alpha = 0.2f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.store ?: "Wish",
                                style = typography.labelSmall,
                                color = colors.accentA
                            )
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.small),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        Text(
                            text = item.title,
                            style = if (columns > 2) typography.labelSmall else typography.titleMedium,
                            color = colors.text,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.price > 0) {
                                Text(
                                    text = "${item.currency}${item.price.toInt()}",
                                    style = typography.titleSmall(colors.accentB),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (!item.status.equals("wanted", ignoreCase = true)) {
                                Text(
                                    text = item.status,
                                    style = typography.labelSmall,
                                    color = colors.success
                                )
                            }
                        }
                    }
                }

                // Selection Indicator
                if (isMultiSelectActive || isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(spacing.extraSmall)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) colors.accentA else colors.surface.copy(alpha = 0.8f))
                            .align(Alignment.TopEnd)
                            .clickable { onToggleSelect(item.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// VIEW 2: Freeform Canvas Moodboard View
// -------------------------------------------------------------
@Composable
fun CanvasMoodboardView(
    items: List<WishItemEntity>,
    onOpenItem: (WishItemEntity) -> Unit,
    onUpdateItemCanvas: (itemId: String, x: Float, y: Float, scale: Float, rotation: Float) -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    // Canvas toolbar to auto-organize
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(WispTheme.shapes.card)
                .background(colors.surfaceHigh)
                .border(1.dp, colors.outline, WispTheme.shapes.card)
        ) {
            items.forEachIndexed { index, item ->
                // Initial placement fallback if not yet set on canvas
                val initialX = if (item.canvasX != 0f) item.canvasX else (index % 3 * 100f + 20f)
                val initialY = if (item.canvasY != 0f) item.canvasY else (index / 3 * 120f + 20f)

                var offsetX by remember(item.id) { mutableFloatStateOf(initialX) }
                var offsetY by remember(item.id) { mutableFloatStateOf(initialY) }
                var scale by remember(item.id) { mutableFloatStateOf(if (item.canvasScale > 0f) item.canvasScale else 1f) }
                var rotation by remember(item.id) { mutableFloatStateOf(item.canvasRotation) }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .scale(scale)
                        .rotate(rotation)
                        .size(120.dp, 140.dp)
                        .clip(WispTheme.shapes.card)
                        .background(colors.surface)
                        .border(1.dp, colors.outline, WispTheme.shapes.card)
                        .pointerInput(item.id) {
                            detectTransformGestures { _, pan, zoom, rotateDeg ->
                                offsetX += pan.x
                                offsetY += pan.y
                                scale = (scale * zoom).coerceIn(0.6f, 2.5f)
                                rotation += rotateDeg
                                onUpdateItemCanvas(item.id, offsetX, offsetY, scale, rotation)
                            }
                        }
                        .clickable { onOpenItem(item) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(spacing.small),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!item.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.surfaceHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.store ?: "✨",
                                    style = typography.titleMedium,
                                    color = colors.accentB
                                )
                            }
                        }

                        Text(
                            text = item.title,
                            style = typography.labelSmall,
                            color = colors.text,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.price > 0) {
                            Text(
                                text = "${item.currency}${item.price.toInt()}",
                                style = typography.labelSmall,
                                color = colors.accentA,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Canvas Instructions Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacing.small)
                .clip(WispTheme.shapes.pill)
                .background(colors.surface.copy(alpha = 0.85f))
                .border(1.dp, colors.outline, WispTheme.shapes.pill)
                .padding(horizontal = spacing.medium, vertical = spacing.extraSmall)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = colors.accentA,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Drag, Pinch & Rotate",
                    style = typography.labelSmall,
                    color = colors.textMuted
                )
            }
        }
    }
}

// -------------------------------------------------------------
// VIEW 3: Compact List View
// -------------------------------------------------------------
@Composable
fun CompactListView(
    items: List<WishItemEntity>,
    selectedItemIds: Set<String>,
    onToggleSelect: (String) -> Unit,
    onOpenItem: (WishItemEntity) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val spacing = WispTheme.spacing
    val colors = WispTheme.colors
    val typography = WispTheme.typography

    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { item ->
            val isSelected = selectedItemIds.contains(item.id)

            WispCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenItem(item) },
                glass = true
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        // Checkbox for selection
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) colors.accentA else colors.surfaceHigh)
                                .clickable { onToggleSelect(item.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Thumbnail
                        if (!item.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.surfaceHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.store?.take(2)?.uppercase() ?: "✨",
                                    style = typography.titleSmall(colors.text)
                                )
                            }
                        }

                        // Title & Store
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = typography.titleSmall(colors.text),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val meta = listOfNotNull(item.store, item.status.takeIf { !it.equals("wanted", true) })
                                .joinToString(" · ")
                            if (meta.isNotBlank()) {
                                Text(
                                    text = meta,
                                    style = typography.labelSmall,
                                    color = colors.textMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(spacing.small))

                    // Price & Delete
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        if (item.price > 0) {
                            Text(
                                text = "${item.currency}${item.price.toInt()}",
                                style = typography.titleSmall(colors.accentB),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceHigh)
                                .springPress(onClick = { onDeleteItem(item.id) }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete item",
                                tint = colors.textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// VIEW 4: Card Stack Deck View ("Decide what to keep")
// -------------------------------------------------------------
@Composable
fun CardDeckView(
    items: List<WishItemEntity>,
    onOpenItem: (WishItemEntity) -> Unit,
    onKeep: (WishItemEntity) -> Unit,
    onDiscard: (WishItemEntity) -> Unit
) {
    val spacing = WispTheme.spacing
    val colors = WispTheme.colors
    val typography = WispTheme.typography

    var currentIndex by remember(items) { mutableIntStateOf(0) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }

    if (currentIndex >= items.size) {
        // Celebratory Empty State
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colors.success.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Done",
                    tint = colors.success,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(spacing.medium))
            Text(
                text = "All Reviewed!",
                style = typography.headlineMedium,
                color = colors.text
            )
            Text(
                text = "You've reviewed every item in this deck.",
                style = typography.bodyMedium,
                color = colors.textMuted
            )
            Spacer(modifier = Modifier.height(spacing.medium))
            WispButton(
                text = "Start Over",
                onClick = { currentIndex = 0 }
            )
        }
        return
    }

    val currentItem = items[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Deck progress
        Text(
            text = "Reviewing ${currentIndex + 1} of ${items.size}",
            style = typography.labelSmall,
            color = colors.textMuted
        )

        // Stacked Card Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = spacing.medium),
            contentAlignment = Alignment.Center
        ) {
            // Background Ghost Card
            if (currentIndex + 1 < items.size) {
                val nextItem = items[currentIndex + 1]
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(0.85f)
                        .offset(y = 12.dp)
                        .scale(0.96f)
                        .clip(WispTheme.shapes.card)
                        .background(colors.surfaceHigh.copy(alpha = 0.6f))
                        .border(1.dp, colors.outline, WispTheme.shapes.card)
                )
            }

            // Foreground Active Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .offset { IntOffset(dragOffsetX.roundToInt(), 0) }
                    .rotate(dragOffsetX * 0.04f)
                    .clip(WispTheme.shapes.card)
                    .background(colors.surface)
                    .border(1.dp, colors.outline, WispTheme.shapes.card)
                    .pointerInput(currentIndex) {
                        detectDragGestures(
                            onDragEnd = {
                                if (dragOffsetX > 150f) {
                                    onKeep(currentItem)
                                    currentIndex++
                                } else if (dragOffsetX < -150f) {
                                    onDiscard(currentItem)
                                    currentIndex++
                                }
                                dragOffsetX = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetX += dragAmount.x
                            }
                        )
                    }
                    .clickable { onOpenItem(currentItem) }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (!currentItem.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = currentItem.imageUrl,
                            contentDescription = currentItem.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(
                                    Brush.linearGradient(
                                        listOf(colors.accentA.copy(alpha = 0.25f), colors.accentB.copy(alpha = 0.25f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentItem.store ?: "Wish",
                                style = typography.displayLarge,
                                color = colors.accentA.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        Text(
                            text = currentItem.title,
                            style = typography.titleMedium,
                            color = colors.text,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentItem.price > 0) {
                                Text(
                                    text = "${currentItem.currency}${currentItem.price.toInt()}",
                                    style = typography.headlineMedium,
                                    color = colors.accentB
                                )
                            }
                            val currentStore = currentItem.store
                            if (!currentStore.isNullOrBlank()) {
                                Text(
                                    text = currentStore,
                                    style = typography.labelSmall,
                                    color = colors.textMuted
                                )
                            }
                        }
                    }
                }

                // Keep / Discard Swipe Overlays
                if (dragOffsetX > 50f) {
                    Box(
                        modifier = Modifier
                            .padding(spacing.medium)
                            .clip(WispTheme.shapes.pill)
                            .background(colors.success.copy(alpha = 0.9f))
                            .padding(horizontal = spacing.medium, vertical = spacing.small)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "KEEP",
                            style = typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (dragOffsetX < -50f) {
                    Box(
                        modifier = Modifier
                            .padding(spacing.medium)
                            .clip(WispTheme.shapes.pill)
                            .background(colors.danger.copy(alpha = 0.9f))
                            .padding(horizontal = spacing.medium, vertical = spacing.small)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "DISCARD",
                            style = typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.medium))

        // Deck Decision Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Discard Button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceHigh)
                    .border(1.dp, colors.danger.copy(alpha = 0.4f), CircleShape)
                    .springPress(onClick = {
                        onDiscard(currentItem)
                        currentIndex++
                    }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Discard item",
                    tint = colors.danger,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Reset Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceHigh)
                    .springPress(onClick = { currentIndex = 0 }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Reset deck",
                    tint = colors.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Keep Button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceHigh)
                    .border(1.dp, colors.success.copy(alpha = 0.4f), CircleShape)
                    .springPress(onClick = {
                        onKeep(currentItem)
                        currentIndex++
                    }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = "Keep item",
                    tint = colors.success,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun app.wisp.core.designsystem.theme.WispTypography.titleSmall(color: Color): androidx.compose.ui.text.TextStyle {
    return this.titleMedium.copy(color = color)
}
