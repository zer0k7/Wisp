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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import app.wisp.core.designsystem.components.WispButton
import app.wisp.core.designsystem.components.WispCard
import app.wisp.core.designsystem.components.WispChip
import app.wisp.core.designsystem.components.WispSheet
import app.wisp.core.designsystem.components.WispTextField
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.WispTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    currentPreset: ThemePreset = ThemePreset.MIDNIGHT_INK,
    onPresetSelected: (ThemePreset) -> Unit = {}
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes
    val typography = WispTheme.typography
    val themeName = WispTheme.spec.name

    var textInput by remember { mutableStateOf("") }
    var buttonTapCount by remember { mutableIntStateOf(0) }
    var isButtonLoading by remember { mutableStateOf(false) }
    var showSheetPreview by remember { mutableStateOf(false) }
    var showOrbInfo by remember { mutableStateOf(false) }
    var stateMode by remember { mutableStateOf("content") } // "content", "empty", "loading", "error"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            // Header: App Title & Preset Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Wisp",
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

            // Theme Preset Selector Chips
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
                        label = preset.displayName,
                        selected = currentPreset == preset,
                        onClick = { onPresetSelected(preset) }
                    )
                }
            }

            // State Selector (Content, Empty, Loading, Error)
            Text(
                text = "State Demonstrations",
                style = typography.titleMedium,
                color = colors.text
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                listOf("content", "empty", "loading", "error").forEach { mode ->
                    WispChip(
                        label = mode.replaceFirstChar { it.uppercase() },
                        selected = stateMode == mode,
                        onClick = { stateMode = mode },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // State Card Demonstration
            when (stateMode) {
                "content" -> {
                    WispCard(
                        modifier = Modifier.fillMaxWidth(),
                        glass = true
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                            Text(
                                text = "Frosted Glass Squircle Card",
                                style = typography.headlineMedium,
                                color = colors.text
                            )
                            Text(
                                text = "Crisp, readable typography with a translucent frosted surface and high-contrast outline. All colors and radii are dynamically fed from WispTheme.",
                                style = typography.bodyMedium,
                                color = colors.textMuted
                            )
                        }
                    }
                }
                "empty" -> {
                    WispCard(
                        modifier = Modifier.fillMaxWidth(),
                        glass = false
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.small)
                        ) {
                            Text(
                                text = "No Wishlist Items Yet",
                                style = typography.headlineMedium,
                                color = colors.text
                            )
                            Text(
                                text = "Save items from anywhere or create a board to get started.",
                                style = typography.bodyMedium,
                                color = colors.textMuted
                            )
                        }
                    }
                }
                "loading" -> {
                    WispCard(
                        modifier = Modifier.fillMaxWidth(),
                        glass = false
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.small)
                        ) {
                            WispButton(
                                text = "Loading State",
                                loading = true,
                                onClick = {}
                            )
                            Text(
                                text = "Fetching board and wish details...",
                                style = typography.bodyMedium,
                                color = colors.textMuted
                            )
                        }
                    }
                }
                "error" -> {
                    WispCard(
                        modifier = Modifier.fillMaxWidth(),
                        glass = false
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(spacing.small)
                        ) {
                            Text(
                                text = "Unable to Load Wishlist",
                                style = typography.headlineMedium,
                                color = colors.danger
                            )
                            Text(
                                text = "An error occurred while reading local data. Tap retry to reload.",
                                style = typography.bodyMedium,
                                color = colors.textMuted
                            )
                            WispButton(
                                text = "Retry",
                                onClick = { stateMode = "content" }
                            )
                        }
                    }
                }
            }

            // Interactive Text Field
            Text(
                text = "Interactive Styled Input",
                style = typography.titleMedium,
                color = colors.text
            )
            WispTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = "Type item title or paste link..."
            )

            // Gradient Action Button & Loading Toggle
            Text(
                text = "Spring Action Button",
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
                    "Tap the button to feel tactile haptics and spring physics (damping: 0.75)."
                } else {
                    "Success! Tapped $buttonTapCount time(s) with spring animation."
                },
                style = typography.bodyMedium,
                color = if (buttonTapCount > 0) colors.success else colors.textMuted
            )

            // Bottom Sheet Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Squircle Bottom Sheet",
                    style = typography.titleMedium,
                    color = colors.text
                )
                WispButton(
                    text = if (showSheetPreview) "Hide Sheet" else "Preview Sheet",
                    onClick = { showSheetPreview = !showSheetPreview }
                )
            }
            if (showSheetPreview) {
                WispSheet(
                    modifier = Modifier.fillMaxWidth(),
                    glass = true,
                    showHandle = true
                ) {
                    Text(
                        text = "Wisp 20dp Squircle Sheet",
                        style = typography.titleMedium,
                        color = colors.text
                    )
                    Spacer(modifier = Modifier.height(spacing.small))
                    Text(
                        text = "Standard container for modal actions and quick capture.",
                        style = typography.bodyMedium,
                        color = colors.textMuted
                    )
                }
            }

            // Orb Info Banner (toggled by clicking the Orb in bottom right)
            if (showOrbInfo) {
                WispCard(
                    modifier = Modifier.fillMaxWidth(),
                    glass = true
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                        Text(
                            text = "Orb Dock Navigation (F2 Preview)",
                            style = typography.headlineMedium,
                            color = colors.accentB
                        )
                        Text(
                            text = "This glowing orb is Wisp's primary navigation in the thumb zone. In F2, tapping it fans out Boards, Inbox, Tracker, Search, and You spaces, and dragging snaps it to any screen edge.",
                            style = typography.bodyMedium,
                            color = colors.text
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.extraLarge))
        }

        // Orb Dock Preview Interactive Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacing.medium)
                .size(spacing.orbSize)
                .springPress(onClick = { showOrbInfo = !showOrbInfo })
                .clip(shapes.pill)
                .background(WispTheme.brandGradient)
                .border(
                    width = spacing.strokeThick,
                    color = colors.surfaceHigh,
                    shape = shapes.pill
                )
                .semantics {
                    contentDescription = "Orb Dock Navigation Preview. Tap for details."
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = colors.background,
                modifier = Modifier.size(spacing.large)
            )
        }
    }
}
