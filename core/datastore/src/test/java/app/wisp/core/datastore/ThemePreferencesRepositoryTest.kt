package app.wisp.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.ThemeSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class ThemePreferencesRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private fun createRepository(): ThemePreferencesRepository {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tempFolder.newFile("test_prefs_${System.nanoTime()}.preferences_pb") }
        )
        return DataStoreThemePreferencesRepository(testDataStore)
    }

    @Test
    fun testDefaultThemeSpecWhenEmpty() = runTest(testDispatcher) {
        val repository = createRepository()
        val spec = repository.themeSpec.first()
        assertEquals(ThemeSpec.Default, spec)
    }

    @Test
    fun testSetThemePresetPersistsCorrectly() = runTest(testDispatcher) {
        val repository = createRepository()
        repository.setThemePreset(ThemePreset.SUNSET)

        val spec = repository.themeSpec.first()
        assertEquals(ThemePreset.SUNSET, spec.preset)
        assertEquals("Sunset", spec.name)
    }

    @Test
    fun testResetToDefaultClearsCustomPreset() = runTest(testDispatcher) {
        val repository = createRepository()
        repository.setThemePreset(ThemePreset.TERMINAL)
        assertEquals(ThemePreset.TERMINAL, repository.themeSpec.first().preset)

        repository.resetToDefault()
        assertEquals(ThemeSpec.Default, repository.themeSpec.first())
    }
}
