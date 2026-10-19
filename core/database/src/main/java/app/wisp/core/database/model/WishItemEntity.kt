package app.wisp.core.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wish_items",
    indices = [Index(value = ["boardId"])]
)
data class WishItemEntity(
    @PrimaryKey
    val id: String,
    val boardId: String,
    val title: String,
    val imageUrl: String? = null,
    val price: Double = 0.0,
    val originalPrice: Double? = null,
    val currency: String = "₹",
    val url: String? = null,
    val store: String? = null,
    val notes: String? = null,
    val tags: String = "",
    val priority: Int = 3,
    val status: String = "wanted", // wanted, reserved, bought, gifted
    val variants: String? = null,
    val dominantColorHex: String? = null,
    val canvasX: Float = 0f,
    val canvasY: Float = 0f,
    val canvasScale: Float = 1f,
    val canvasRotation: Float = 0f,
    val createdAt: Long = System.currentTimeMillis()
)
