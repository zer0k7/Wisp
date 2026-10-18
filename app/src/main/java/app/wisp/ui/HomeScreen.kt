package app.wisp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.components.WispButton
import app.wisp.core.designsystem.components.WispCard
import app.wisp.core.designsystem.components.WispChip
import app.wisp.core.designsystem.components.WispSheet
import app.wisp.core.designsystem.components.WispTextField
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.navigation.NavSpace
import app.wisp.core.designsystem.navigation.NavStyleType
import app.wisp.core.designsystem.navigation.WispScaffold
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.WispTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    currentPreset: ThemePreset = ThemePreset.MIDNIGHT_INK,
    onPresetSelected: (ThemePreset) -> Unit = {},
    currentSpace: NavSpace = NavSpace.BOARDS,
    onSpaceSelected: (NavSpace) -> Unit = {},
    navStyle: NavStyleType = NavStyleType.ORB_DOCK,
    onNavStyleSelected: (NavStyleType) -> Unit = {},
    initialXRatio: Float = 0.90f,
    initialYRatio: Float = 0.85f,
    onOrbPositionChanged: ((Float, Float) -> Unit)? = null,
    quickAddTriggered: Boolean = false,
    onDismissQuickAdd: () -> Unit = {},
    onTriggerQuickAdd: () -> Unit = {}
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes
    val typography = WispTheme.typography
    val themeName = WispTheme.spec.name

    var quickAddUrl by remember { mutableStateOf("") }
    var internalSelectedSpace by remember { mutableStateOf(currentSpace) }
    var internalNavStyle by remember { mutableStateOf(navStyle) }
    var internalQuickAdd by remember { mutableStateOf(quickAddTriggered) }

    // Keep internal and external states synchronized
    val effectiveSpace = if (currentSpace != NavSpace.BOARDS) currentSpace else internalSelectedSpace
    val effectiveNavStyle = if (navStyle != NavStyleType.ORB_DOCK) navStyle else internalNavStyle
    val effectiveQuickAdd = quickAddTriggered || internalQuickAdd

    WispScaffold(
        currentSpace = effectiveSpace,
        onSpaceSelected = { space ->
            internalSelectedSpace = space
            onSpaceSelected(space)
        },
        navStyle = effectiveNavStyle,
        onNavStyleSelected = { style ->
            internalNavStyle = style
            onNavStyleSelected(style)
        },
        onQuickAdd = {
            internalQuickAdd = true
            onTriggerQuickAdd()
        },
        onThemePresetSelected = onPresetSelected,
        initialXRatio = initialXRatio,
        initialYRatio = initialYRatio,
        onOrbPositionChanged = onOrbPositionChanged,
        modifier = modifier
    ) { space ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            when (space) {
                NavSpace.BOARDS -> BoardsSpaceContent(
                    currentPreset = currentPreset,
                    onPresetSelected = onPresetSelected,
                    themeName = themeName
                )
                NavSpace.INBOX -> InboxSpaceContent(
                    onQuickAdd = { internalQuickAdd = true }
                )
                NavSpace.TRACKER -> TrackerSpaceContent()
                NavSpace.SEARCH -> SearchSpaceContent()
                NavSpace.YOU -> YouSpaceContent(
                    currentNavStyle = effectiveNavStyle,
                    onNavStyleSelected = { style ->
                        internalNavStyle = style
                        onNavStyleSelected(style)
                    },
                    currentPreset = currentPreset,
                    onPresetSelected = onPresetSelected
                )
            }

            // Quick Add Bottom Sheet
            if (effectiveQuickAdd) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    WispSheet(
                        modifier = Modifier.fillMaxWidth(),
                        glass = true,
                        showHandle = true
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.medium),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Quick Add to Wishlist",
                                style = typography.headlineMedium,
                                color = colors.text
                            )
                            Text(
                                text = "Paste a product link from Amazon, Flipkart, or any store.",
                                style = typography.bodyMedium,
                                color = colors.textMuted
                            )
                            WispTextField(
                                value = quickAddUrl,
                                onValueChange = { quickAddUrl = it },
                                placeholder = "https://amazon.in/dp/..."
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                WispButton(
                                    text = "Close",
                                    onClick = {
                                        internalQuickAdd = false
                                        onDismissQuickAdd()
                                    }
                                )
                                Spacer(modifier = Modifier.width(spacing.small))
                                WispButton(
                                    text = "Save Wish",
                                    onClick = {
                                        quickAddUrl = ""
                                        internalQuickAdd = false
                                        onDismissQuickAdd()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardsSpaceContent(
    currentPreset: ThemePreset,
    onPresetSelected: (ThemePreset) -> Unit,
    themeName: String
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes
    val typography = WispTheme.typography

    var buttonTapCount by remember { mutableIntStateOf(0) }
    var isButtonLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your boards",
                    style = typography.displayLarge,
                    color = colors.text
                )
                Text(
                    text = "Visual wishlist organizer",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(shapes.pill)
                    .background(colors.surfaceHigh)
                    .padding(horizontal = spacing.medium, vertical = spacing.small)
                    .semantics {
                        contentDescription = "Active preset is $themeName"
                    }
            ) {
                Text(
                    text = themeName,
                    style = typography.labelSmall,
                    color = colors.accentB
                )
            }
        }

        // Theme Presets Row
        Text(
            text = "Theme Presets",
            style = typography.titleMedium,
            color = colors.text
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            ThemePreset.entries.forEach { preset ->
                WispChip(
                    text = preset.displayName,
                    selected = currentPreset == preset,
                    onClick = { onPresetSelected(preset) }
                )
            }
        }

        // Boards Preview Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            WispCard(
                modifier = Modifier.weight(1f),
                glass = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                    Text(
                        text = "Tech Setup",
                        style = typography.titleMedium,
                        color = colors.text
                    )
                    Text(
                        text = "6 items • 2 price drops",
                        style = typography.bodySmall,
                        color = colors.accentB
                    )
                }
            }

            WispCard(
                modifier = Modifier.weight(1f),
                glass = true
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                    Text(
                        text = "Wardrobe",
                        style = typography.titleMedium,
                        color = colors.text
                    )
                    Text(
                        text = "12 items • Updated today",
                        style = typography.bodySmall,
                        color = colors.accentA
                    )
                }
            }
        }

        // Interactive Spring Button UX
        Text(
            text = "Spring Action Feedback",
            style = typography.titleMedium,
            color = colors.text
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            WispButton(
                text = if (isButtonLoading) "Processing..." else "Tap Me (Count: $buttonTapCount)",
                loading = isButtonLoading,
                onClick = { buttonTapCount++ },
                modifier = Modifier.weight(1.2f)
            )
            WispButton(
                text = if (isButtonLoading) "Stop Load" else "Simulate Load",
                onClick = { isButtonLoading = !isButtonLoading },
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = if (buttonTapCount == 0) {
                "Swipe left/right to switch spaces. Pull down anywhere for command bar."
            } else {
                "Success! Tapped $buttonTapCount time(s) with tactile haptics."
            },
            style = typography.bodyMedium,
            color = if (buttonTapCount > 0) colors.success else colors.textMuted
        )

        Spacer(modifier = Modifier.height(spacing.extraLarge))
    }
}

@Composable
private fun InboxSpaceContent(
    onQuickAdd: () -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Text(
            text = "Inbox",
            style = typography.displayLarge,
            color = colors.text
        )
        Text(
            text = "Unsorted links and rapid captures waiting to be sorted into boards.",
            style = typography.bodyMedium,
            color = colors.textMuted
        )

        WispCard(
            modifier = Modifier.fillMaxWidth(),
            glass = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "No Unorganized Wishes",
                    style = typography.headlineMedium,
                    color = colors.text
                )
                Text(
                    text = "Tap the + button on the Orb Dock or use the Command Bar to quick-capture links.",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
                WispButton(
                    text = "Quick Add Item",
                    onClick = onQuickAdd
                )
            }
        }
    }
}

@Composable
private fun TrackerSpaceContent() {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Price Tracker",
                    style = typography.displayLarge,
                    color = colors.text
                )
                Text(
                    text = "Offline background monitoring for drops",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            }
            Icon(
                imageVector = Icons.Rounded.NotificationsActive,
                contentDescription = null,
                tint = colors.hot,
                modifier = Modifier.size(spacing.extraLarge)
            )
        }

        WispCard(
            modifier = Modifier.fillMaxWidth(),
            glass = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "Hot Price Drop Alert",
                    style = typography.headlineMedium,
                    color = colors.hot
                )
                Text(
                    text = "Sony WH-1000XM5 Noise Canceling Headphones dropped by ₹4,500 (-15%) on Amazon.",
                    style = typography.bodyMedium,
                    color = colors.text
                )
                Text(
                    text = "Target Price: ₹24,990 • Current: ₹22,490",
                    style = typography.labelLarge,
                    color = colors.success
                )
            }
        }
    }
}

@Composable
private fun SearchSpaceContent() {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography
    var searchInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Text(
            text = "Search",
            style = typography.displayLarge,
            color = colors.text
        )
        Text(
            text = "Search wish items, boards, tags, and stores",
            style = typography.bodyMedium,
            color = colors.textMuted
        )

        WispTextField(
            value = searchInput,
            onValueChange = { searchInput = it },
            placeholder = "Search gadgets, outfits, notes...",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = colors.textMuted
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            listOf("All", "Price Drops", "Reserved", "Tech", "Fashion", "Priority 5").forEach { tag ->
                WispChip(
                    text = tag,
                    selected = tag == "All",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun YouSpaceContent(
    currentNavStyle: NavStyleType,
    onNavStyleSelected: (NavStyleType) -> Unit,
    currentPreset: ThemePreset,
    onPresetSelected: (ThemePreset) -> Unit
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Text(
            text = "You & Studio",
            style = typography.displayLarge,
            color = colors.text
        )
        Text(
            text = "Total control over navigation style, themes, and offline privacy.",
            style = typography.bodyMedium,
            color = colors.textMuted
        )

        // Nav Style Settings
        WispCard(
            modifier = Modifier.fillMaxWidth(),
            glass = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "Navigation Style",
                    style = typography.titleLarge,
                    color = colors.accentB
                )
                Text(
                    text = "Switch between Orb Dock, Edge Handle, Floating Pill, or Gesture-only navigation live.",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    NavStyleType.entries.forEach { style ->
                        WispChip(
                            text = style.displayName,
                            selected = style == currentNavStyle,
                            onClick = { onNavStyleSelected(style) }
                        )
                    }
                }
            }
        }

        // Theme Engine Presets
        WispCard(
            modifier = Modifier.fillMaxWidth(),
            glass = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(
                    text = "WispTheme Presets",
                    style = typography.titleLarge,
                    color = colors.accentA
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    ThemePreset.entries.forEach { preset ->
                        WispChip(
                            text = preset.displayName,
                            selected = currentPreset == preset,
                            onClick = { onPresetSelected(preset) }
                        )
                    }
                }
            }
        }

        // Privacy & Offline Info Card
        WispCard(
            modifier = Modifier.fillMaxWidth(),
            glass = true
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = colors.success,
                        modifier = Modifier.size(spacing.large)
                    )
                    Text(
                        text = "100% Offline & Private",
                        style = typography.titleMedium,
                        color = colors.success
                    )
                }
                Text(
                    text = "No trackers, no analytics, no cloud account needed. All wishlist data resides exclusively on your device.",
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraLarge))
    }
}
