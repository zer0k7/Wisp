package app.wisp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity
import app.wisp.core.database.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BoardsViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            wishlistRepository.seedInitialDataIfEmpty()
        }
    }

    val boards: StateFlow<List<BoardEntity>> = wishlistRepository.allBoards
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val allItems: StateFlow<List<WishItemEntity>> = wishlistRepository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val priceDrops: StateFlow<List<WishItemEntity>> = wishlistRepository.priceDrops
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _selectedBoard = MutableStateFlow<BoardEntity?>(null)
    val selectedBoard: StateFlow<BoardEntity?> = _selectedBoard.asStateFlow()

    private val _selectedItem = MutableStateFlow<WishItemEntity?>(null)
    val selectedItem: StateFlow<WishItemEntity?> = _selectedItem.asStateFlow()

    private val _isCreateBoardOpen = MutableStateFlow(false)
    val isCreateBoardOpen: StateFlow<Boolean> = _isCreateBoardOpen.asStateFlow()

    fun openBoard(board: BoardEntity) {
        _selectedBoard.value = board
    }

    fun closeBoard() {
        _selectedBoard.value = null
    }

    fun openItem(item: WishItemEntity) {
        _selectedItem.value = item
    }

    fun closeItem() {
        _selectedItem.value = null
    }

    fun updateItemStatus(itemId: String, status: String) {
        viewModelScope.launch {
            wishlistRepository.updateItemStatus(itemId, status)
            if (_selectedItem.value?.id == itemId) {
                _selectedItem.value = _selectedItem.value?.copy(status = status)
            }
        }
    }

    fun moveItemToBoard(itemId: String, targetBoardId: String) {
        viewModelScope.launch {
            wishlistRepository.moveItemToBoard(itemId, targetBoardId)
            if (_selectedItem.value?.id == itemId) {
                _selectedItem.value = _selectedItem.value?.copy(boardId = targetBoardId)
            }
        }
    }

    fun updateWishItem(item: WishItemEntity) {
        viewModelScope.launch {
            wishlistRepository.saveItem(item)
            if (_selectedItem.value?.id == item.id) {
                _selectedItem.value = item
            }
        }
    }

    fun setCreateBoardOpen(open: Boolean) {
        _isCreateBoardOpen.value = open
    }

    fun createBoard(name: String, icon: String, accentHex: String = "#8B6CFF") {
        if (name.isBlank()) return
        viewModelScope.launch {
            val board = BoardEntity(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                icon = icon.ifBlank { "✨" },
                accentColorHex = accentHex
            )
            wishlistRepository.saveBoard(board)
            _isCreateBoardOpen.value = false
        }
    }

    fun addWishItem(
        title: String,
        url: String? = null,
        price: Double = 0.0,
        boardId: String? = null
    ) {
        if (title.isBlank() && url.isNullOrBlank()) return
        viewModelScope.launch {
            val effectiveBoardId = boardId
                ?: _selectedBoard.value?.id
                ?: boards.value.firstOrNull()?.id
                ?: "board_default"

            val displayTitle = if (title.isNotBlank()) {
                title.trim()
            } else {
                url?.substringAfter("://")?.substringBefore("/")?.replace("www.", "") ?: "New Wish"
            }

            val item = WishItemEntity(
                id = UUID.randomUUID().toString(),
                boardId = effectiveBoardId,
                title = displayTitle,
                url = url?.trim()?.ifBlank { null },
                price = price,
                store = extractStore(url)
            )
            wishlistRepository.saveItem(item)
        }
    }

    suspend fun checkDuplicate(url: String): WishItemEntity? {
        if (url.isBlank()) return null
        return wishlistRepository.findItemByUrl(url.trim())
    }

    fun captureUrl(
        url: String,
        targetBoardId: String? = null,
        onComplete: ((WishItemEntity) -> Unit)? = null
    ) {
        if (url.isBlank()) return
        viewModelScope.launch {
            val metadata = app.wisp.core.common.metadata.LinkMetadataExtractor.extract(url)
            val effectiveBoardId = targetBoardId
                ?: _selectedBoard.value?.id
                ?: boards.value.firstOrNull()?.id
                ?: "board_default"

            val item = WishItemEntity(
                id = UUID.randomUUID().toString(),
                boardId = effectiveBoardId,
                title = metadata.title,
                url = metadata.canonicalUrl ?: url.trim(),
                price = metadata.price ?: 0.0,
                currency = metadata.currency,
                store = metadata.store,
                imageUrl = metadata.imageUrl,
                notes = metadata.description
            )
            wishlistRepository.saveItem(item)
            onComplete?.invoke(item)
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            wishlistRepository.deleteItem(id)
            if (_selectedItem.value?.id == id) {
                _selectedItem.value = null
            }
        }
    }

    fun deleteBoard(id: String) {
        viewModelScope.launch {
            wishlistRepository.deleteBoard(id)
            if (_selectedBoard.value?.id == id) {
                _selectedBoard.value = null
            }
        }
    }

    private fun extractStore(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return app.wisp.core.common.metadata.LinkMetadataExtractor.extractStore(url)
    }
}
