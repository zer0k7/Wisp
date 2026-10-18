package app.wisp.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.wisp.core.designsystem.components.WispButton
import app.wisp.core.designsystem.components.WispCard
import app.wisp.core.designsystem.components.WispChip
import app.wisp.core.designsystem.components.WispSheet
import app.wisp.core.designsystem.components.WispTextField
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
class ThemeComponentsScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Composable
    private fun ComponentShowcase() {
        val colors = WispTheme.colors
        val spacing = WispTheme.spacing
        val typography = WispTheme.typography

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(spacing.medium)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Text(
                    text = "${WispTheme.spec.name} Components",
                    style = typography.headlineLarge,
                    color = colors.text
                )

                // WispCard - Normal & Glass
                WispCard(modifier = Modifier.fillMaxWidth(), glass = false) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                        Text(
                            text = "Squircle SurfaceHigh Card",
                            style = typography.titleMedium,
                            color = colors.text
                        )
                        Text(
                            text = "Card content adhering strictly to design system tokens.",
                            style = typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                }

                // WispChips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    WispChip(label = "Selected Chip", selected = true, onClick = {})
                    WispChip(label = "Unselected Chip", selected = false, onClick = {})
                }

                // WispTextField
                WispTextField(
                    value = "Wishlist Item Title",
                    onValueChange = {},
                    placeholder = "Enter item name..."
                )

                // WispButtons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    WispButton(
                        text = "Gradient Action",
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                    WispButton(
                        text = "Loading",
                        loading = true,
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }

                // WispSheet
                WispSheet(
                    modifier = Modifier.fillMaxWidth(),
                    glass = true,
                    showHandle = true
                ) {
                    Text(
                        text = "20dp Rounded Sheet Container",
                        style = typography.bodyLarge,
                        color = colors.text
                    )
                    Spacer(modifier = Modifier.height(spacing.small))
                    Text(
                        text = "Glass surface with blur effect and drag handle",
                        style = typography.bodyMedium,
                        color = colors.textMuted
                    )
                }
            }
        }
    }

    @Test
    fun captureComponents_MidnightInk() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                ComponentShowcase()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureComponents_PaperGlass() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.PaperGlass) {
                ComponentShowcase()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureComponents_Amoled() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.Amoled) {
                ComponentShowcase()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureComponents_Sunset() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.Sunset) {
                ComponentShowcase()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureComponents_Sakura() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.Sakura) {
                ComponentShowcase()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureComponents_Ocean() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.Ocean) {
                ComponentShowcase()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureComponents_Terminal() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.Terminal) {
                ComponentShowcase()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureComponents_Mono() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.Mono) {
                ComponentShowcase()
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
