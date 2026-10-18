package app.wisp.core.database.repository

import app.wisp.core.database.dao.BoardDao
import app.wisp.core.database.dao.WishItemDao
import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface WishlistRepository {
    val allBoards: Flow<List<BoardEntity>>
    val allItems: Flow<List<WishItemEntity>>
    val priceDrops: Flow<List<WishItemEntity>>

    fun getItemsForBoard(boardId: String): Flow<List<WishItemEntity>>
    fun getItemCountForBoard(boardId: String): Flow<Int>

    suspend fun getBoard(id: String): BoardEntity?
    suspend fun saveBoard(board: BoardEntity)
    suspend fun deleteBoard(id: String)

    suspend fun getItem(id: String): WishItemEntity?
    suspend fun saveItem(item: WishItemEntity)
    suspend fun deleteItem(id: String)
    suspend fun updateItemStatus(id: String, status: String)
    suspend fun moveItemToBoard(itemId: String, targetBoardId: String)
    suspend fun findItemByUrl(url: String): WishItemEntity?

    suspend fun seedInitialDataIfEmpty()
}

class RoomWishlistRepository(
    private val boardDao: BoardDao,
    private val wishItemDao: WishItemDao
) : WishlistRepository {

    override val allBoards: Flow<List<BoardEntity>> = boardDao.getAllBoards()
    override val allItems: Flow<List<WishItemEntity>> = wishItemDao.getAllItems()
    override val priceDrops: Flow<List<WishItemEntity>> = wishItemDao.getTrackedPriceDrops()

    override fun getItemsForBoard(boardId: String): Flow<List<WishItemEntity>> =
        wishItemDao.getItemsByBoard(boardId)

    override fun getItemCountForBoard(boardId: String): Flow<Int> =
        wishItemDao.getItemCountForBoard(boardId)

    override suspend fun getBoard(id: String): BoardEntity? =
        boardDao.getBoardById(id)

    override suspend fun saveBoard(board: BoardEntity) {
        boardDao.insertBoard(board)
    }

    override suspend fun deleteBoard(id: String) {
        boardDao.deleteBoardById(id)
    }

    override suspend fun getItem(id: String): WishItemEntity? =
        wishItemDao.getItemById(id)

    override suspend fun saveItem(item: WishItemEntity) {
        wishItemDao.insertItem(item)
    }

    override suspend fun deleteItem(id: String) {
        wishItemDao.deleteItemById(id)
    }

    override suspend fun updateItemStatus(id: String, status: String) {
        wishItemDao.updateStatus(id, status)
    }

    override suspend fun moveItemToBoard(itemId: String, targetBoardId: String) {
        wishItemDao.moveItemToBoard(itemId, targetBoardId)
    }

    override suspend fun findItemByUrl(url: String): WishItemEntity? =
        wishItemDao.getItemByUrl(url)

    override suspend fun seedInitialDataIfEmpty() {
        val currentBoards = boardDao.getAllBoards().first()
        if (currentBoards.isEmpty()) {
            val starterBoards = listOf(
                BoardEntity(
                    id = "board_tech",
                    name = "Tech Setup",
                    icon = "💻",
                    accentColorHex = "#8B6CFF",
                    sortOrder = 0
                ),
                BoardEntity(
                    id = "board_wardrobe",
                    name = "Wardrobe & Style",
                    icon = "👟",
                    accentColorHex = "#22E5C4",
                    sortOrder = 1
                ),
                BoardEntity(
                    id = "board_gifts",
                    name = "Gift Ideas",
                    icon = "🎁",
                    accentColorHex = "#FF5C8A",
                    sortOrder = 2
                )
            )
            boardDao.insertBoards(starterBoards)

            val starterItems = listOf(
                WishItemEntity(
                    id = "item_headphones",
                    boardId = "board_tech",
                    title = "Sony WH-1000XM5 Wireless Noise Canceling Headphones",
                    price = 22490.0,
                    originalPrice = 26990.0,
                    currency = "₹",
                    url = "https://amazon.in/dp/B09XS7JWHH",
                    store = "Amazon",
                    priority = 5,
                    status = "wanted",
                    notes = "Wait for Diwali or Great Indian Festival sale"
                ),
                WishItemEntity(
                    id = "item_keyboard",
                    boardId = "board_tech",
                    title = "NuPhy Air75 V2 Low-Profile Wireless Mechanical Keyboard",
                    price = 11999.0,
                    originalPrice = 13999.0,
                    currency = "₹",
                    url = "https://amazon.in",
                    store = "Amazon",
                    priority = 4,
                    status = "wanted"
                ),
                WishItemEntity(
                    id = "item_sneakers",
                    boardId = "board_wardrobe",
                    title = "New Balance 9060 'Rain Cloud'",
                    price = 15999.0,
                    currency = "₹",
                    url = "https://newbalance.co.in",
                    store = "New Balance",
                    priority = 3,
                    status = "wanted"
                )
            )
            wishItemDao.insertItems(starterItems)
        }
    }
}
