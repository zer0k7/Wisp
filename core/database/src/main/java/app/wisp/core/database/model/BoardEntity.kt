package app.wisp.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String = "✨",
    val coverStyle: String = "mosaic", // mosaic, single, color
    val accentColorHex: String = "#8B6CFF",
    val isPrivate: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
