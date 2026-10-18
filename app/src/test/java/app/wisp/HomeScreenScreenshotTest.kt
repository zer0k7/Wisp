package app.wisp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.wisp.core.database.model.BoardEntity
import app.wisp.core.database.model.WishItemEntity
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

    private val sampleBoards = listOf(
        BoardEntity(
            id = "board_tech",
            name = "Tech Setup",
            icon = "💻",
            accentColorHex = "#8B6CFF"
        ),
        BoardEntity(
            id = "board_wardrobe",
            name = "Wardrobe & Style",
            icon = "👟",
            accentColorHex = "#22E5C4"
        ),
        BoardEntity(
            id = "board_gifts",
            name = "Gift Ideas",
            icon = "🎁",
            accentColorHex = "#FF5C8A"
        )
    )

    private val sampleItems = listOf(
        WishItemEntity(
            id = "item_headphones",
            boardId = "board_tech",
            title = "Sony WH-1000XM5 Headphones",
            price = 22490.0,
            originalPrice = 26990.0,
            currency = "₹",
            url = "https://amazon.in",
            store = "Amazon"
        ),
        WishItemEntity(
            id = "item_keyboard",
            boardId = "board_tech",
            title = "NuPhy Air75 V2 Keyboard",
            price = 11999.0,
            originalPrice = 13999.0,
            currency = "₹",
            url = "https://amazon.in",
            store = "Amazon"
        ),
        WishItemEntity(
            id = "item_sneakers",
            boardId = "board_wardrobe",
            title = "New Balance 9060",
            price = 15999.0,
            currency = "₹",
            store = "New Balance"
        )
    )

    @Test
    fun captureHomeScreen_MidnightInk() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                HomeScreen(
                    boards = sampleBoards,
                    items = sampleItems,
                    priceDrops = sampleItems.filter { it.originalPrice != null && it.price < (it.originalPrice ?: 0.0) },
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
                    boards = sampleBoards,
                    items = sampleItems,
                    priceDrops = sampleItems.filter { it.originalPrice != null && it.price < (it.originalPrice ?: 0.0) },
                    currentPreset = ThemeSpec.PaperGlass.preset,
                    currentSpace = NavSpace.BOARDS,
                    navStyle = NavStyleType.ORB_DOCK
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureHomeScreen_BoardDetail() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                HomeScreen(
                    boards = sampleBoards,
                    items = sampleItems,
                    selectedBoard = sampleBoards.first(),
                    currentPreset = ThemeSpec.MidnightInk.preset,
                    currentSpace = NavSpace.BOARDS,
                    navStyle = NavStyleType.ORB_DOCK
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun captureHomeScreen_ItemDetail() {
        composeTestRule.setContent {
            WispTheme(themeSpec = ThemeSpec.MidnightInk) {
                HomeScreen(
                    boards = sampleBoards,
                    items = sampleItems,
                    selectedItem = sampleItems.first(),
                    currentPreset = ThemeSpec.MidnightInk.preset,
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
                    boards = sampleBoards,
                    items = sampleItems,
                    priceDrops = sampleItems.filter { it.originalPrice != null && it.price < (it.originalPrice ?: 0.0) },
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
                    boards = sampleBoards,
                    items = sampleItems,
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
                    boards = sampleBoards,
                    items = sampleItems,
                    currentPreset = ThemeSpec.MidnightInk.preset,
                    currentSpace = NavSpace.SEARCH,
                    navStyle = NavStyleType.FLOATING_PILL
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
