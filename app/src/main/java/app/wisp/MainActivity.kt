package app.wisp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.wisp.core.designsystem.theme.WispTheme
import app.wisp.ui.BoardsViewModel
import app.wisp.ui.HomeScreen
import app.wisp.ui.NavigationViewModel
import app.wisp.ui.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by viewModels()
    private val boardsViewModel: BoardsViewModel by viewModels()
    private val onboardingViewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSpec by themeViewModel.themeSpec.collectAsStateWithLifecycle()
            val currentSpace by navigationViewModel.currentSpace.collectAsStateWithLifecycle()
            val navStyle by navigationViewModel.navStyle.collectAsStateWithLifecycle()
            val orbPosition by navigationViewModel.orbPositionRatio.collectAsStateWithLifecycle()
            val quickAddTriggered by navigationViewModel.quickAddTriggered.collectAsStateWithLifecycle()

            val boards by boardsViewModel.boards.collectAsStateWithLifecycle()
            val items by boardsViewModel.allItems.collectAsStateWithLifecycle()
            val priceDrops by boardsViewModel.priceDrops.collectAsStateWithLifecycle()
            val selectedBoard by boardsViewModel.selectedBoard.collectAsStateWithLifecycle()
            val selectedItem by boardsViewModel.selectedItem.collectAsStateWithLifecycle()
            val selectedItemIds by boardsViewModel.selectedItemIds.collectAsStateWithLifecycle()
            val onboardingCompleted by onboardingViewModel.isCompleted.collectAsStateWithLifecycle()

            WispTheme(themeSpec = themeSpec) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WispTheme.colors.background
                ) {
                    if (!onboardingCompleted) {
                        OnboardingScreen(onComplete = onboardingViewModel::complete)
                    } else HomeScreen(
                        boards = boards,
                        items = items,
                        priceDrops = priceDrops,
                        selectedBoard = selectedBoard,
                        onOpenBoard = { board -> boardsViewModel.openBoard(board) },
                        onCloseBoard = { boardsViewModel.closeBoard() },
                        onCreateBoard = { name, icon -> boardsViewModel.createBoard(name, icon) },
                        onDeleteBoard = { boardId -> boardsViewModel.deleteBoard(boardId) },
                        onAddWishItem = { title, url, price, boardId ->
                            boardsViewModel.addWishItem(title, url, price, boardId)
                        },
                        onDeleteItem = { itemId -> boardsViewModel.deleteItem(itemId) },
                        selectedItem = selectedItem,
                        onOpenItem = { item -> boardsViewModel.openItem(item) },
                        onCloseItem = { boardsViewModel.closeItem() },
                        onUpdateItemStatus = { itemId, status ->
                            boardsViewModel.updateItemStatus(itemId, status)
                        },
                        onMoveItemToBoard = { itemId, targetBoardId ->
                            boardsViewModel.moveItemToBoard(itemId, targetBoardId)
                        },
                        onUpdateItem = { item -> boardsViewModel.updateWishItem(item) },
                        onSaveClipboardLink = { url -> boardsViewModel.captureUrl(url) },
                        currentPreset = themeSpec.preset,
                        onPresetSelected = { preset -> themeViewModel.selectPreset(preset) },
                        currentSpace = currentSpace,
                        onSpaceSelected = { space -> navigationViewModel.selectSpace(space) },
                        navStyle = navStyle,
                        onNavStyleSelected = { style -> navigationViewModel.setNavStyle(style) },
                        initialXRatio = orbPosition.first,
                        initialYRatio = orbPosition.second,
                        onOrbPositionChanged = { xRatio, yRatio ->
                            navigationViewModel.setOrbPositionRatio(xRatio, yRatio)
                        },
                        quickAddTriggered = quickAddTriggered,
                        onDismissQuickAdd = { navigationViewModel.consumeQuickAdd() },
                        onTriggerQuickAdd = { navigationViewModel.triggerQuickAdd() },
                        selectedItemIds = selectedItemIds,
                        onToggleSelectItem = { id -> boardsViewModel.toggleSelectItem(id) },
                        onClearSelection = { boardsViewModel.clearSelection() },
                        onSelectAllItems = { ids -> boardsViewModel.selectAllItems(ids) },
                        onDeleteSelectedItems = { boardsViewModel.deleteSelectedItems() },
                        onMoveSelectedItems = { targetId -> boardsViewModel.moveSelectedItems(targetId) },
                        onUpdateBoardView = { boardId, viewType, cols ->
                            boardsViewModel.updateBoardView(boardId, viewType, cols)
                        },
                        onUpdateItemCanvas = { id, x, y, s, r ->
                            boardsViewModel.updateItemCanvas(id, x, y, s, r)
                        }
                    )
                }
            }
        }
    }
}
