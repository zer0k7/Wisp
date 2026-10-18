package app.wisp.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class NavigationPreferencesRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private fun createRepository(): NavigationPreferencesRepository {
        val testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tempFolder.newFile("test_nav_prefs_${System.nanoTime()}.preferences_pb") }
        )
        return DataStoreNavigationPreferencesRepository(testDataStore)
    }

    @Test
    fun testDefaultNavStyleIsOrbDock() = runTest(testDispatcher) {
        val repository = createRepository()
        val style = repository.navStyle.first()
        assertEquals("ORB_DOCK", style)
    }

    @Test
    fun testDefaultOrbPositionRatio() = runTest(testDispatcher) {
        val repository = createRepository()
        val (x, y) = repository.orbPositionRatio.first()
        assertEquals(0.90f, x, 0.001f)
        assertEquals(0.85f, y, 0.001f)
    }

    @Test
    fun testSetNavStylePersists() = runTest(testDispatcher) {
        val repository = createRepository()
        repository.setNavStyle("FLOATING_PILL")
        assertEquals("FLOATING_PILL", repository.navStyle.first())
    }

    @Test
    fun testSetOrbPositionRatioCoercesRange() = runTest(testDispatcher) {
        val repository = createRepository()
        repository.setOrbPositionRatio(1.5f, -0.5f)
        val (x, y) = repository.orbPositionRatio.first()
        assertEquals(1.0f, x, 0.001f)
        assertEquals(0.0f, y, 0.001f)
    }

    @Test
    fun testResetNavigationPreferencesRestoresDefaults() = runTest(testDispatcher) {
        val repository = createRepository()
        repository.setNavStyle("EDGE_HANDLE")
        repository.setOrbPositionRatio(0.1f, 0.2f)

        repository.resetNavigationPreferences()
        assertEquals("ORB_DOCK", repository.navStyle.first())
        val (x, y) = repository.orbPositionRatio.first()
        assertEquals(0.90f, x, 0.001f)
        assertEquals(0.85f, y, 0.001f)
    }
}
