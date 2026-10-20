package app.wisp.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface OnboardingPreferencesRepository {
    val isCompleted: Flow<Boolean>

    suspend fun complete()
}

class DataStoreOnboardingPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : OnboardingPreferencesRepository {
    override val isCompleted: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[WispPreferenceKeys.ONBOARDING_COMPLETED] ?: false }

    override suspend fun complete() {
        dataStore.edit { preferences ->
            preferences[WispPreferenceKeys.ONBOARDING_COMPLETED] = true
        }
    }
}
