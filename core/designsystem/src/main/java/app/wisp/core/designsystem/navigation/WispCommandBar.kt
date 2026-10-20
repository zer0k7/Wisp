package app.wisp.core.designsystem.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.components.WispCard
import app.wisp.core.designsystem.components.WispChip
import app.wisp.core.designsystem.components.WispTextField
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.WispTheme

@Composable
fun WispCommandBar(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onQuickAdd: () -> Unit,
    onSpaceSelected: (NavSpace) -> Unit,
    onNavStyleSelected: (NavStyleType) -> Unit,
    onThemePresetSelected: (ThemePreset) -> Unit,
    currentNavStyle: NavStyleType,
    modifier: Modifier = Modifier
) {
    val colors = WispTheme.colors
    val shapes = WispTheme.shapes
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography

    var query by remember { mutableStateOf("") }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(dampingRatio = 0.75f)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(dampingRatio = 0.75f)
        ) + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background.copy(alpha = 0.75f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium, vertical = spacing.extraLarge)
                    .clip(shapes.card)
                    .background(colors.surfaceHigh.copy(alpha = 0.95f))
                    .border(spacing.strokeThick, colors.outline, shapes.card)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Intercept clicks inside card
                    )
                    .padding(spacing.medium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Menu",
                            style = typography.headlineLarge,
                            color = colors.text
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(shapes.pill)
                                .background(colors.surface)
                                .springPress(onClick = onDismiss)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Close command bar"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                tint = colors.textMuted,
                                modifier = Modifier.size(spacing.medium)
                            )
                        }
                    }

                    // Quick Search input
                    WispTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = "Type a command, search items, or paste URL...",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = colors.textMuted
                            )
                        }
                    )

                    // Quick Action: Add Item
                    WispCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .springPress(onClick = {
                                onDismiss()
                                onQuickAdd()
                            }),
                        glass = true
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.small)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(shapes.pill)
                                    .background(WispTheme.brandGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = "Add item",
                                    style = typography.titleMedium,
                                    color = colors.text
                                )
                                Text(
                                    text = "Save product, paste link, or upload photo",
                                    style = typography.bodyMedium,
                                    color = colors.textMuted
                                )
                            }
                        }
                    }

                    // Go to a section
                    Text(
                        text = "Go to",
                        style = typography.labelLarge,
                        color = colors.accentB
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        NavSpace.spaces.forEach { space ->
                            WispChip(
                                label = space.title,
                                selected = false,
                                onClick = {
                                    onDismiss()
                                    onSpaceSelected(space)
                                }
                            )
                        }
                    }

                    // Switch Nav Style
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TouchApp,
                            contentDescription = null,
                            tint = colors.accentA,
                            modifier = Modifier.size(spacing.medium)
                        )
                        Text(
                            text = "Navigation Style",
                            style = typography.labelLarge,
                            color = colors.accentA
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        NavStyleType.entries.forEach { style ->
                            WispChip(
                                label = style.displayName,
                                selected = style == currentNavStyle,
                                onClick = {
                                    onNavStyleSelected(style)
                                }
                            )
                        }
                    }

                    // Switch Theme Preset
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = null,
                            tint = colors.hot,
                            modifier = Modifier.size(spacing.medium)
                        )
                        Text(
                            text = "Theme",
                            style = typography.labelLarge,
                            color = colors.hot
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        listOf(
                            ThemePreset.MIDNIGHT_INK,
                            ThemePreset.PAPER_GLASS,
                            ThemePreset.AMOLED,
                            ThemePreset.SUNSET,
                            ThemePreset.OCEAN
                        ).forEach { preset ->
                            WispChip(
                                label = preset.displayName,
                                selected = false,
                                onClick = {
                                    onThemePresetSelected(preset)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
