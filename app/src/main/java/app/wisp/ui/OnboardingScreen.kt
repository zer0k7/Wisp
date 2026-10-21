package app.wisp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLink
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PriceCheck
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import app.wisp.core.designsystem.components.WispButton
import app.wisp.core.designsystem.components.WispCard
import app.wisp.core.designsystem.components.springPress
import app.wisp.core.designsystem.theme.WispTheme

private data class OnboardingStep(
    val title: String,
    val body: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconDescription: String
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes
    val typography = WispTheme.typography
    val steps = remember {
        listOf(
            OnboardingStep(
                title = "Save an item",
                body = "Add a product with a name, link, price, or photo. It stays on this device.",
                icon = Icons.Rounded.AddLink,
                iconDescription = "Add a wishlist item"
            ),
            OnboardingStep(
                title = "Put it in a board",
                body = "Create simple groups such as Gifts, Clothing, Travel, or Home.",
                icon = Icons.Rounded.Dashboard,
                iconDescription = "Organise items into boards"
            ),
            OnboardingStep(
                title = "Check it later",
                body = "Open an item when you are ready to compare the price, link, and notes.",
                icon = Icons.Rounded.PriceCheck,
                iconDescription = "Review wishlist details"
            )
        )
    }
    var stepIndex by remember { mutableIntStateOf(0) }
    val step = steps[stepIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = spacing.large, vertical = spacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "WISP",
                style = typography.labelSmall,
                color = colors.accentB
            )
            Text(
                text = "Save things you want to buy.",
                style = typography.displayMedium,
                color = colors.text
            )
            Text(
                text = "Keep products, links, and prices together so you can decide later.",
                style = typography.bodyLarge,
                color = colors.textMuted
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.large)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(shapes.chip)
                                .background(colors.surfaceHigh)
                                .border(spacing.stroke, colors.outline, shapes.chip)
                                .padding(spacing.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = step.iconDescription,
                                tint = colors.accentB,
                                modifier = Modifier.size(spacing.extraLarge)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                            Text(
                                text = step.title,
                                style = typography.headlineLarge,
                                color = colors.text
                            )
                        }
                    }
                    Text(
                        text = step.body,
                        style = typography.bodyLarge,
                        color = colors.textMuted
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Private and stored on this device",
                            tint = colors.success,
                            modifier = Modifier.size(spacing.medium)
                        )
                        Text(
                            text = "Private by default · stored on this device",
                            style = typography.bodyMedium,
                            color = colors.textMuted
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {
                        contentDescription = "Onboarding progress: step ${stepIndex + 1} of ${steps.size}"
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                Text(
                    text = "STEP ${stepIndex + 1} OF ${steps.size}",
                    style = typography.labelSmall,
                    color = colors.textMuted
                )
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(spacing.extraSmall)
                                .clip(shapes.pill)
                                .background(
                                    if (index <= stepIndex) colors.accentB else colors.outline
                                )
                        )
                    }
                }
            }

            WispButton(
                text = if (stepIndex == steps.lastIndex) "Start using Wisp" else "Next",
                onClick = {
                    if (stepIndex == steps.lastIndex) onComplete() else stepIndex++
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (stepIndex < steps.lastIndex) {
                Text(
                    text = "Skip",
                    style = typography.labelLarge,
                    color = colors.textMuted,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .springPress(onClick = onComplete)
                        .semantics {
                            role = Role.Button
                            contentDescription = "Skip onboarding"
                        }
                        .padding(horizontal = spacing.medium, vertical = spacing.small)
                )
            }
        }
    }
}
