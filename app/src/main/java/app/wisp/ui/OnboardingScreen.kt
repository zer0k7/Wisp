package app.wisp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLink
import androidx.compose.material.icons.rounded.Dashboard
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
import app.wisp.core.designsystem.components.WispButton
import app.wisp.core.designsystem.components.WispCard
import app.wisp.core.designsystem.theme.WispTheme

private data class OnboardingStep(
    val title: String,
    val body: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val typography = WispTheme.typography
    val steps = remember {
        listOf(
            OnboardingStep(
                title = "Save what you want",
                body = "Add a product with a name, link, price, or photo. Wisp keeps it on this device.",
                icon = Icons.Rounded.AddLink
            ),
            OnboardingStep(
                title = "Keep it organised",
                body = "Use boards for groups such as gifts, clothes, travel, or home ideas.",
                icon = Icons.Rounded.Dashboard
            ),
            OnboardingStep(
                title = "Decide when you are ready",
                body = "See saved items, price changes, and notes in one simple place. No account required.",
                icon = Icons.Rounded.PriceCheck
            )
        )
    }
    var stepIndex by remember { mutableIntStateOf(0) }
    val step = steps[stepIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(spacing.large),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text("Wisp", style = typography.displayLarge, color = colors.text)
            Text("A clear place for the things you want.", style = typography.bodyLarge, color = colors.textMuted)
        }

        WispCard(modifier = Modifier.fillMaxWidth(), glass = true) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Icon(step.icon, contentDescription = null, tint = colors.accentB, modifier = Modifier.size(spacing.extraLarge))
                Text(step.title, style = typography.headlineMedium, color = colors.text)
                Text(step.body, style = typography.bodyLarge, color = colors.textMuted)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.medium)) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                steps.indices.forEach { index ->
                    Spacer(
                        modifier = Modifier
                            .width(if (index == stepIndex) spacing.large else spacing.small)
                            .padding(horizontal = spacing.extraSmall)
                            .height(spacing.extraSmall)
                            .background(if (index == stepIndex) colors.accentB else colors.outline)
                    )
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
                WispButton(
                    text = "Skip",
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
