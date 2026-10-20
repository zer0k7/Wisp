package app.wisp.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String = "folder",
    val coverStyle: String = "mosaic", // mosaic, single, color
    val accentColorHex: String = "#8B6CFF",
    val isPrivate: Boolean = false,
    val sortOrder: Int = 0,
    val viewType: String = "grid", // grid, canvas, list, deck
    val gridColumns: Int = 2, // 1 to 4
    val createdAt: Long = System.currentTimeMillis()
)
