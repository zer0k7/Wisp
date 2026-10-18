package app.wisp.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity
import app.wisp.core.database.repository.RoomWishlistRepository
import app.wisp.core.database.repository.WishlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RoomWishlistRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var database: WispDatabase
    private lateinit var repository: WishlistRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WispDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RoomWishlistRepository(
            boardDao = database.boardDao(),
            wishItemDao = database.wishItemDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testSeedInitialDataCreatesStarterBoardsAndItems() = runTest(testDispatcher) {
        val initialBoards = repository.allBoards.first()
        assertEquals(0, initialBoards.size)

        repository.seedInitialDataIfEmpty()

        val seededBoards = repository.allBoards.first()
        assertEquals(3, seededBoards.size)

        val seededItems = repository.allItems.first()
        assertEquals(3, seededItems.size)
    }

    @Test
    fun testInsertAndGetBoard() = runTest(testDispatcher) {
        val board = BoardEntity(
            id = "board_books",
            name = "Books to Read",
            icon = "📚",
            accentColorHex = "#22E5C4"
        )
        repository.saveBoard(board)

        val retrieved = repository.getBoard("board_books")
        assertNotNull(retrieved)
        assertEquals("Books to Read", retrieved?.name)
        assertEquals("📚", retrieved?.icon)
    }

    @Test
    fun testInsertAndGetWishItem() = runTest(testDispatcher) {
        val item = WishItemEntity(
            id = "item_kindle",
            boardId = "board_books",
            title = "Kindle Paperwhite",
            price = 14999.0,
            currency = "₹"
        )
        repository.saveItem(item)

        val retrieved = repository.getItem("item_kindle")
        assertNotNull(retrieved)
        assertEquals("Kindle Paperwhite", retrieved?.title)
        assertEquals(14999.0, retrieved?.price ?: 0.0, 0.001)

        repository.deleteItem("item_kindle")
        assertNull(repository.getItem("item_kindle"))
    }

    @Test
    fun testPriceDropsQuery() = runTest(testDispatcher) {
        val itemOnSale = WishItemEntity(
            id = "sale_item",
            boardId = "b1",
            title = "Headphones",
            price = 8000.0,
            originalPrice = 10000.0
        )
        val regularItem = WishItemEntity(
            id = "regular_item",
            boardId = "b1",
            title = "Mouse",
            price = 2000.0,
            originalPrice = null
        )
        repository.saveItem(itemOnSale)
        repository.saveItem(regularItem)

        val drops = repository.priceDrops.first()
        assertEquals(1, drops.size)
        assertEquals("sale_item", drops[0].id)
    }

    @Test
    fun testUpdateItemStatusAndMoveBoard() = runTest(testDispatcher) {
        val item = WishItemEntity(
            id = "item_move",
            boardId = "b1",
            title = "Travel Backpack",
            status = "wanted"
        )
        repository.saveItem(item)

        repository.updateItemStatus("item_move", "bought")
        val updated = repository.getItem("item_move")
        assertEquals("bought", updated?.status)

        repository.moveItemToBoard("item_move", "b2")
        val moved = repository.getItem("item_move")
        assertEquals("b2", moved?.boardId)
    }
}
