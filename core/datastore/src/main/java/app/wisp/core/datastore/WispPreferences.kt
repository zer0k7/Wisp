package app.wisp.core.datastore

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object WispPreferenceKeys {
    val THEME_SPEC = stringPreferencesKey("theme_spec_json")
    val NAV_STYLE = stringPreferencesKey("nav_style_id")
    val ORB_POSITION_X_RATIO = floatPreferencesKey("orb_position_x_ratio")
    val ORB_POSITION_Y_RATIO = floatPreferencesKey("orb_position_y_ratio")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
}
