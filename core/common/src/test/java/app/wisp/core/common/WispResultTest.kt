package app.wisp.core.common

import app.wisp.core.common.result.WispResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WispResultTest {
    @Test
    fun testWispResultSuccess() {
        val result: WispResult<String> = WispResult.Success("item")
        assertTrue(result is WispResult.Success)
        assertEquals("item", (result as WispResult.Success).data)
    }

    @Test
    fun testWispResultError() {
        val exception = IllegalStateException("failure")
        val result: WispResult<String> = WispResult.Error(exception)
        assertTrue(result is WispResult.Error)
        assertEquals("failure", (result as WispResult.Error).throwable.message)
    }
}
