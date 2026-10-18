package app.wisp.ui

import app.wisp.core.datastore.NavigationPreferencesRepository
import app.wisp.core.designsystem.navigation.NavSpace
import app.wisp.core.designsystem.navigation.NavStyleType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeNavigationPreferencesRepository : NavigationPreferencesRepository {
        val navStyleFlow = MutableStateFlow("ORB_DOCK")
        val orbPositionRatioFlow = MutableStateFlow(0.90f to 0.85f)

        override val navStyle: Flow<String> = navStyleFlow
        override val orbPositionRatio: Flow<Pair<Float, Float>> = orbPositionRatioFlow

        override suspend fun setNavStyle(style: String) {
            navStyleFlow.value = style
        }

        override suspend fun setOrbPositionRatio(xRatio: Float, yRatio: Float) {
            orbPositionRatioFlow.value = xRatio to yRatio
        }

        override suspend fun resetNavigationPreferences() {
            navStyleFlow.value = "ORB_DOCK"
            orbPositionRatioFlow.value = 0.90f to 0.85f
        }
    }

    private lateinit var fakeRepo: FakeNavigationPreferencesRepository
    private lateinit var viewModel: NavigationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeNavigationPreferencesRepository()
        viewModel = NavigationViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialSpaceIsBoards() {
        assertEquals(NavSpace.BOARDS, viewModel.currentSpace.value)
    }

    @Test
    fun testSelectSpaceUpdatesCurrentSpace() {
        viewModel.selectSpace(NavSpace.TRACKER)
        assertEquals(NavSpace.TRACKER, viewModel.currentSpace.value)

        viewModel.selectSpace(NavSpace.YOU)
        assertEquals(NavSpace.YOU, viewModel.currentSpace.value)
    }

    @Test
    fun testQuickAddTriggerAndConsume() {
        assertFalse(viewModel.quickAddTriggered.value)

        viewModel.triggerQuickAdd()
        assertTrue(viewModel.quickAddTriggered.value)

        viewModel.consumeQuickAdd()
        assertFalse(viewModel.quickAddTriggered.value)
    }

    @Test
    fun testSetNavStyleUpdatesRepository() = runTest(testDispatcher) {
        viewModel.setNavStyle(NavStyleType.FLOATING_PILL)
        advanceUntilIdle()
        assertEquals("FLOATING_PILL", fakeRepo.navStyleFlow.value)
    }

    @Test
    fun testSetOrbPositionRatioUpdatesRepository() = runTest(testDispatcher) {
        viewModel.setOrbPositionRatio(0.12f, 0.45f)
        advanceUntilIdle()
        assertEquals(0.12f to 0.45f, fakeRepo.orbPositionRatioFlow.value)
    }
}
