package app.wisp.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import app.wisp.core.database.model.PlaceholderEntity

@Dao
interface PlaceholderDao {
    @Query("SELECT * FROM placeholders")
    suspend fun getAll(): List<PlaceholderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(placeholder: PlaceholderEntity)
}

@Database(
    entities = [PlaceholderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WispDatabase : RoomDatabase() {
    abstract fun placeholderDao(): PlaceholderDao
}
