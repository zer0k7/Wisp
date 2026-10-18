package app.wisp.core.designsystem.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.wisp.core.designsystem.theme.ThemeSpec
import app.wisp.core.designsystem.theme.WispTheme
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
class OrbDockScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun captureOrbDock_MidnightInk() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WispTheme.colors.background)
                ) {
                    OrbDock(
                        currentSpace = NavSpace.BOARDS,
                        onSpaceSelected = {},
                        onQuickAdd = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureEdgeHandle_MidnightInk() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WispTheme.colors.background)
                ) {
                    EdgeHandleNav(
                        currentSpace = NavSpace.BOARDS,
                        onSpaceSelected = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureFloatingPill_MidnightInk() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WispTheme.colors.background)
                ) {
                    FloatingPillNav(
                        currentSpace = NavSpace.BOARDS,
                        onSpaceSelected = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
