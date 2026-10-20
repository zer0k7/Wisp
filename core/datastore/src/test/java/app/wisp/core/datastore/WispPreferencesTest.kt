package app.wisp.core.datastore

import org.junit.Assert.assertEquals
import org.junit.Test

class WispPreferencesTest {
    @Test
    fun testPreferenceKeys() {
        assertEquals("theme_spec_json", WispPreferenceKeys.THEME_SPEC.name)
        assertEquals("nav_style_id", WispPreferenceKeys.NAV_STYLE.name)
        assertEquals("onboarding_completed", WispPreferenceKeys.ONBOARDING_COMPLETED.name)
    }
}
