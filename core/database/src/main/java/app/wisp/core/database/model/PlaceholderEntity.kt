package app.wisp.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placeholders")
data class PlaceholderEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)
