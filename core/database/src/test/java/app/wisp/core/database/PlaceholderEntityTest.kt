package app.wisp.core.database

import app.wisp.core.database.model.PlaceholderEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceholderEntityTest {
    @Test
    fun testEntityCreation() {
        val entity = PlaceholderEntity(id = "1", title = "Test Entity")
        assertEquals("1", entity.id)
        assertEquals("Test Entity", entity.title)
    }
}
