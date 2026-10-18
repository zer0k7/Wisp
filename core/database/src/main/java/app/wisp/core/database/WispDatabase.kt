package app.wisp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.wisp.core.database.dao.BoardDao
import app.wisp.core.database.dao.WishItemDao
import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity

@Database(
    entities = [
        BoardEntity::class,
        WishItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WispDatabase : RoomDatabase() {
    abstract fun boardDao(): BoardDao
    abstract fun wishItemDao(): WishItemDao
}
