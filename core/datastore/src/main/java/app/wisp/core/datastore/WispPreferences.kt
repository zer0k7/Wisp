package app.wisp.core.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object WispPreferenceKeys {
    val THEME_SPEC = stringPreferencesKey("theme_spec_json")
    val NAV_STYLE = stringPreferencesKey("nav_style_id")
}
