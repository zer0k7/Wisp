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
            icon = "books",
            accentColorHex = "#22E5C4"
        )
        repository.saveBoard(board)

        val retrieved = repository.getBoard("board_books")
        assertNotNull(retrieved)
        assertEquals("Books to Read", retrieved?.name)
        assertEquals("books", retrieved?.icon)
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

    @Test
    fun testFindItemByUrl() = runTest(testDispatcher) {
        val item = WishItemEntity(
            id = "url_test",
            boardId = "b1",
            title = "Sony XM5",
            url = "https://amazon.in/dp/sony-xm5"
        )
        repository.saveItem(item)

        val found = repository.findItemByUrl("https://amazon.in/dp/sony-xm5")
        assertNotNull(found)
        assertEquals("url_test", found?.id)

        val notFound = repository.findItemByUrl("https://nonexistent.com")
        assertNull(notFound)
    }

    @Test
    fun testUpdateBoardViewAndColumns() = runTest(testDispatcher) {
        val board = BoardEntity(id = "b_view", name = "Fashion", viewType = "grid", gridColumns = 2)
        repository.saveBoard(board)

        repository.updateBoardView("b_view", "canvas", 3)
        val updated = repository.getBoard("b_view")
        assertEquals("canvas", updated?.viewType)
        assertEquals(3, updated?.gridColumns)
    }

    @Test
    fun testUpdateCanvasTransform() = runTest(testDispatcher) {
        val item = WishItemEntity(id = "item_canvas", boardId = "b1", title = "Poster")
        repository.saveItem(item)

        repository.updateItemCanvasTransform("item_canvas", 120f, 240f, 1.5f, 15f)
        val updated = repository.getItem("item_canvas")
        assertEquals(120f, updated?.canvasX ?: 0f, 0.01f)
        assertEquals(240f, updated?.canvasY ?: 0f, 0.01f)
        assertEquals(1.5f, updated?.canvasScale ?: 0f, 0.01f)
        assertEquals(15f, updated?.canvasRotation ?: 0f, 0.01f)
    }

    @Test
    fun testBatchDeleteAndBatchMove() = runTest(testDispatcher) {
        val i1 = WishItemEntity(id = "batch_1", boardId = "b1", title = "Item 1")
        val i2 = WishItemEntity(id = "batch_2", boardId = "b1", title = "Item 2")
        val i3 = WishItemEntity(id = "batch_3", boardId = "b1", title = "Item 3")
        repository.saveItem(i1)
        repository.saveItem(i2)
        repository.saveItem(i3)

        repository.moveItemsToBoard(listOf("batch_1", "batch_2"), "b2")
        assertEquals("b2", repository.getItem("batch_1")?.boardId)
        assertEquals("b2", repository.getItem("batch_2")?.boardId)
        assertEquals("b1", repository.getItem("batch_3")?.boardId)

        repository.deleteItems(listOf("batch_1", "batch_3"))
        assertNull(repository.getItem("batch_1"))
        assertNotNull(repository.getItem("batch_2"))
        assertNull(repository.getItem("batch_3"))
    }
}
