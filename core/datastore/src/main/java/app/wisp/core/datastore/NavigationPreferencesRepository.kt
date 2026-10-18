package app.wisp.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

interface NavigationPreferencesRepository {
    val navStyle: Flow<String>
    val orbPositionRatio: Flow<Pair<Float, Float>>

    suspend fun setNavStyle(style: String)
    suspend fun setOrbPositionRatio(xRatio: Float, yRatio: Float)
    suspend fun resetNavigationPreferences()
}

class DataStoreNavigationPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : NavigationPreferencesRepository {

    override val navStyle: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[WispPreferenceKeys.NAV_STYLE] ?: "ORB_DOCK"
        }

    override val orbPositionRatio: Flow<Pair<Float, Float>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val x = preferences[WispPreferenceKeys.ORB_POSITION_X_RATIO] ?: 0.90f
            val y = preferences[WispPreferenceKeys.ORB_POSITION_Y_RATIO] ?: 0.85f
            x to y
        }

    override suspend fun setNavStyle(style: String) {
        dataStore.edit { preferences ->
            preferences[WispPreferenceKeys.NAV_STYLE] = style
        }
    }

    override suspend fun setOrbPositionRatio(xRatio: Float, yRatio: Float) {
        dataStore.edit { preferences ->
            preferences[WispPreferenceKeys.ORB_POSITION_X_RATIO] = xRatio.coerceIn(0f, 1f)
            preferences[WispPreferenceKeys.ORB_POSITION_Y_RATIO] = yRatio.coerceIn(0f, 1f)
        }
    }

    override suspend fun resetNavigationPreferences() {
        dataStore.edit { preferences ->
            preferences.remove(WispPreferenceKeys.NAV_STYLE)
            preferences.remove(WispPreferenceKeys.ORB_POSITION_X_RATIO)
            preferences.remove(WispPreferenceKeys.ORB_POSITION_Y_RATIO)
        }
    }
}
