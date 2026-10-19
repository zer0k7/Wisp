package app.wisp.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.wisp.core.database.model.WishItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishItemDao {

    @Query("SELECT * FROM wish_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<WishItemEntity>>

    @Query("SELECT * FROM wish_items WHERE boardId = :boardId ORDER BY priority DESC, createdAt DESC")
    fun getItemsByBoard(boardId: String): Flow<List<WishItemEntity>>

    @Query("SELECT * FROM wish_items ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentItems(limit: Int = 4): Flow<List<WishItemEntity>>

    @Query("SELECT * FROM wish_items WHERE originalPrice IS NOT NULL AND price < originalPrice ORDER BY (originalPrice - price) DESC")
    fun getTrackedPriceDrops(): Flow<List<WishItemEntity>>

    @Query("SELECT * FROM wish_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): WishItemEntity?

    @Query("SELECT * FROM wish_items WHERE url = :url LIMIT 1")
    suspend fun getItemByUrl(url: String): WishItemEntity?

    @Query("SELECT COUNT(*) FROM wish_items WHERE boardId = :boardId")
    fun getItemCountForBoard(boardId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WishItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<WishItemEntity>)

    @Update
    suspend fun updateItem(item: WishItemEntity)

    @Delete
    suspend fun deleteItem(item: WishItemEntity)

    @Query("DELETE FROM wish_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("UPDATE wish_items SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE wish_items SET boardId = :targetBoardId WHERE id = :itemId")
    suspend fun moveItemToBoard(itemId: String, targetBoardId: String)

    @Query("DELETE FROM wish_items WHERE id IN (:ids)")
    suspend fun deleteItemsByIds(ids: List<String>)

    @Query("UPDATE wish_items SET boardId = :targetBoardId WHERE id IN (:ids)")
    suspend fun moveItemsToBoard(ids: List<String>, targetBoardId: String)

    @Query("UPDATE wish_items SET canvasX = :x, canvasY = :y, canvasScale = :scale, canvasRotation = :rotation WHERE id = :id")
    suspend fun updateItemCanvasTransform(id: String, x: Float, y: Float, scale: Float, rotation: Float)
}
