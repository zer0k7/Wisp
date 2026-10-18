package app.wisp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.wisp.core.designsystem.navigation.NavSpace
import app.wisp.core.designsystem.navigation.NavStyleType
import app.wisp.core.designsystem.theme.ThemeSpec
import app.wisp.core.designsystem.theme.WispTheme
import app.wisp.ui.HomeScreen
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
class HomeScreenScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun captureHomeScreen_MidnightInk() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                HomeScreen(
                    currentPreset = ThemeSpec.MidnightInk.preset,
                    currentSpace = NavSpace.BOARDS,
                    navStyle = NavStyleType.ORB_DOCK
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureHomeScreen_PaperGlass() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.PaperGlass) {
                HomeScreen(
                    currentPreset = ThemeSpec.PaperGlass.preset,
                    currentSpace = NavSpace.BOARDS,
                    navStyle = NavStyleType.ORB_DOCK
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureHomeScreen_TrackerSpace() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                HomeScreen(
                    currentPreset = ThemeSpec.MidnightInk.preset,
                    currentSpace = NavSpace.TRACKER,
                    navStyle = NavStyleType.ORB_DOCK
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureHomeScreen_EdgeHandleNav() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                HomeScreen(
                    currentPreset = ThemeSpec.MidnightInk.preset,
                    currentSpace = NavSpace.YOU,
                    navStyle = NavStyleType.EDGE_HANDLE
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureHomeScreen_FloatingPillNav() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                HomeScreen(
                    currentPreset = ThemeSpec.MidnightInk.preset,
                    currentSpace = NavSpace.SEARCH,
                    navStyle = NavStyleType.FLOATING_PILL
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
