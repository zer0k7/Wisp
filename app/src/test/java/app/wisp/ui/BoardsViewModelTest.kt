package app.wisp.ui

import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity
import app.wisp.core.database.repository.WishlistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BoardsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeWishlistRepository : WishlistRepository {
        val boardsList = mutableListOf<BoardEntity>()
        val itemsList = mutableListOf<WishItemEntity>()

        val boardsFlow = MutableStateFlow<List<BoardEntity>>(emptyList())
        val itemsFlow = MutableStateFlow<List<WishItemEntity>>(emptyList())
        val priceDropsFlow = MutableStateFlow<List<WishItemEntity>>(emptyList())

        override val allBoards: Flow<List<BoardEntity>> = boardsFlow
        override val allItems: Flow<List<WishItemEntity>> = itemsFlow
        override val priceDrops: Flow<List<WishItemEntity>> = priceDropsFlow

        override fun getItemsForBoard(boardId: String): Flow<List<WishItemEntity>> =
            flowOf(itemsList.filter { it.boardId == boardId })

        override fun getItemCountForBoard(boardId: String): Flow<Int> =
            flowOf(itemsList.count { it.boardId == boardId })

        override suspend fun getBoard(id: String): BoardEntity? =
            boardsList.find { it.id == id }

        override suspend fun saveBoard(board: BoardEntity) {
            boardsList.removeAll { it.id == board.id }
            boardsList.add(board)
            boardsFlow.value = boardsList.toList()
        }

        override suspend fun deleteBoard(id: String) {
            boardsList.removeAll { it.id == id }
            boardsFlow.value = boardsList.toList()
        }

        override suspend fun getItem(id: String): WishItemEntity? =
            itemsList.find { it.id == id }

        override suspend fun saveItem(item: WishItemEntity) {
            itemsList.removeAll { it.id == item.id }
            itemsList.add(item)
            itemsFlow.value = itemsList.toList()
        }

        override suspend fun deleteItem(id: String) {
            itemsList.removeAll { it.id == id }
            itemsFlow.value = itemsList.toList()
        }

        override suspend fun updateItemStatus(id: String, status: String) {
            val item = itemsList.find { it.id == id }
            if (item != null) {
                saveItem(item.copy(status = status))
            }
        }

        override suspend fun moveItemToBoard(itemId: String, targetBoardId: String) {
            val item = itemsList.find { it.id == itemId }
            if (item != null) {
                saveItem(item.copy(boardId = targetBoardId))
            }
        }

        override suspend fun findItemByUrl(url: String): WishItemEntity? =
            itemsList.find { it.url == url }

        override suspend fun seedInitialDataIfEmpty() {
            if (boardsList.isEmpty()) {
                val seedBoard = BoardEntity(
                    id = "test_board",
                    name = "Tech Setup",
                    icon = "💻"
                )
                saveBoard(seedBoard)
            }
        }
    }

    private lateinit var fakeRepo: FakeWishlistRepository
    private lateinit var viewModel: BoardsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeWishlistRepository()
        viewModel = BoardsViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSeedingTriggeredOnViewModelInit() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertEquals(1, fakeRepo.boardsList.size)
        assertEquals("Tech Setup", fakeRepo.boardsList.first().name)
    }

    @Test
    fun testSelectAndClearBoard() {
        val board = BoardEntity(id = "b1", name = "Travel")
        assertNull(viewModel.selectedBoard.value)

        viewModel.openBoard(board)
        assertEquals("b1", viewModel.selectedBoard.value?.id)

        viewModel.closeBoard()
        assertNull(viewModel.selectedBoard.value)
    }

    @Test
    fun testCreateBoardAddsBoardToRepository() = runTest(testDispatcher) {
        viewModel.createBoard(name = "Books & Comics", icon = "📚")
        advanceUntilIdle()

        val created = fakeRepo.boardsList.find { it.name == "Books & Comics" }
        assertNotNull(created)
        assertEquals("📚", created?.icon)
    }

    @Test
    fun testAddWishItemSavesItem() = runTest(testDispatcher) {
        advanceUntilIdle() // let seed complete
        val targetBoard = fakeRepo.boardsList.first()
        viewModel.openBoard(targetBoard)

        viewModel.addWishItem(
            title = "Kindle Paperwhite",
            url = "https://amazon.com/dp/123",
            price = 139.99,
            boardId = targetBoard.id
        )
        advanceUntilIdle()

        val saved = fakeRepo.itemsList.find { it.title == "Kindle Paperwhite" }
        assertNotNull(saved)
        assertEquals(targetBoard.id, saved?.boardId)
        assertEquals(139.99, saved?.price ?: 0.0, 0.001)
        assertEquals("Amazon", saved?.store)
    }

    @Test
    fun testDeleteItemRemovesItem() = runTest(testDispatcher) {
        val item = WishItemEntity(id = "item_1", boardId = "b1", title = "Desk Mat")
        fakeRepo.saveItem(item)
        advanceUntilIdle()
        assertEquals(1, fakeRepo.itemsList.size)

        viewModel.deleteItem("item_1")
        advanceUntilIdle()
        assertTrue(fakeRepo.itemsList.isEmpty())
    }

    @Test
    fun testDeleteBoardRemovesBoardAndClearsSelected() = runTest(testDispatcher) {
        val board = BoardEntity(id = "b_del", name = "To Delete")
        fakeRepo.saveBoard(board)
        viewModel.openBoard(board)
        advanceUntilIdle()

        assertEquals("b_del", viewModel.selectedBoard.value?.id)

        viewModel.deleteBoard("b_del")
        advanceUntilIdle()

        assertNull(fakeRepo.boardsList.find { it.id == "b_del" })
        assertNull(viewModel.selectedBoard.value)
    }

    @Test
    fun testOpenAndCloseItem() {
        val item = WishItemEntity(id = "item_1", boardId = "b1", title = "Keyboard")
        assertNull(viewModel.selectedItem.value)

        viewModel.openItem(item)
        assertEquals("item_1", viewModel.selectedItem.value?.id)

        viewModel.closeItem()
        assertNull(viewModel.selectedItem.value)
    }

    @Test
    fun testUpdateItemStatus() = runTest(testDispatcher) {
        val item = WishItemEntity(id = "item_1", boardId = "b1", title = "Backpack", status = "wanted")
        fakeRepo.saveItem(item)
        viewModel.openItem(item)

        viewModel.updateItemStatus("item_1", "bought")
        advanceUntilIdle()

        assertEquals("bought", viewModel.selectedItem.value?.status)
        val repoItem = fakeRepo.itemsList.find { it.id == "item_1" }
        assertEquals("bought", repoItem?.status)
    }

    @Test
    fun testMoveItemToBoard() = runTest(testDispatcher) {
        val item = WishItemEntity(id = "item_1", boardId = "b1", title = "Sneakers")
        fakeRepo.saveItem(item)
        viewModel.openItem(item)

        viewModel.moveItemToBoard("item_1", "b2")
        advanceUntilIdle()

        assertEquals("b2", viewModel.selectedItem.value?.boardId)
        val repoItem = fakeRepo.itemsList.find { it.id == "item_1" }
        assertEquals("b2", repoItem?.boardId)
    }

    @Test
    fun testUpdateWishItem() = runTest(testDispatcher) {
        val item = WishItemEntity(id = "item_1", boardId = "b1", title = "Initial Title")
        fakeRepo.saveItem(item)
        viewModel.openItem(item)

        val updated = item.copy(title = "Updated Title", price = 99.9)
        viewModel.updateWishItem(updated)
        advanceUntilIdle()

        assertEquals("Updated Title", viewModel.selectedItem.value?.title)
        assertEquals(99.9, viewModel.selectedItem.value?.price ?: 0.0, 0.001)
    }

    @Test
    fun testDeleteItemClearsSelectedItem() = runTest(testDispatcher) {
        val item = WishItemEntity(id = "item_del", boardId = "b1", title = "To Delete")
        fakeRepo.saveItem(item)
        viewModel.openItem(item)

        viewModel.deleteItem("item_del")
        advanceUntilIdle()

        assertNull(viewModel.selectedItem.value)
    }

    @Test
    fun testCheckDuplicateReturnsItemWhenExists() = runTest(testDispatcher) {
        val item = WishItemEntity(id = "item_dup", boardId = "b1", title = "Duplicate", url = "https://example.com/p1")
        fakeRepo.saveItem(item)

        val result = viewModel.checkDuplicate("https://example.com/p1")
        assertEquals("item_dup", result?.id)

        val notFound = viewModel.checkDuplicate("https://example.com/other")
        assertNull(notFound)
    }

    @Test
    fun testCaptureUrlSavesItem() = runTest(testDispatcher) {
        advanceUntilIdle()
        val defaultBoardId = fakeRepo.boardsList.first().id

        var capturedResult: WishItemEntity? = null
        viewModel.captureUrl("https://example.com/product") {
            capturedResult = it
        }
        advanceUntilIdle()

        val savedItem = fakeRepo.itemsList.find { it.url.contains("example.com") }
        assertNotNull(savedItem)
        assertEquals(defaultBoardId, savedItem?.boardId)
    }
}
