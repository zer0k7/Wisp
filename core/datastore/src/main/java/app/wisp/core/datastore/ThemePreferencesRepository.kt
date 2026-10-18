package app.wisp.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.ThemeSpec
import app.wisp.core.designsystem.theme.ThemeSpecJsonAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

interface ThemePreferencesRepository {
    val themeSpec: Flow<ThemeSpec>
    suspend fun setThemeSpec(spec: ThemeSpec)
    suspend fun setThemePreset(preset: ThemePreset)
    suspend fun resetToDefault()
}

class DataStoreThemePreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : ThemePreferencesRepository {

    override val themeSpec: Flow<ThemeSpec> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val json = preferences[WispPreferenceKeys.THEME_SPEC]
            if (json.isNullOrBlank()) {
                ThemeSpec.Default
            } else {
                ThemeSpecJsonAdapter.fromJson(json)
            }
        }

    override suspend fun setThemeSpec(spec: ThemeSpec) {
        val json = ThemeSpecJsonAdapter.toJson(spec)
        dataStore.edit { preferences ->
            preferences[WispPreferenceKeys.THEME_SPEC] = json
        }
    }

    override suspend fun setThemePreset(preset: ThemePreset) {
        val spec = ThemeSpec.forPreset(preset)
        setThemeSpec(spec)
    }

    override suspend fun resetToDefault() {
        dataStore.edit { preferences ->
            preferences.remove(WispPreferenceKeys.THEME_SPEC)
        }
    }
}
