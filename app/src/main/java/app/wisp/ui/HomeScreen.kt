package app.wisp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.wisp.R
import app.wisp.core.designsystem.theme.WispTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val shapes = WispTheme.shapes

    val brandGradient = Brush.linearGradient(
        colors = listOf(colors.accentA, colors.accentB)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(spacing.large)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            // Top Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = colors.text,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                // Status pill
                Box(
                    modifier = Modifier
                        .clip(shapes.pill)
                        .background(colors.surfaceHigh)
                        .border(
                            width = spacing.extraSmall,
                            brush = brandGradient,
                            shape = shapes.pill
                        )
                        .padding(horizontal = spacing.medium, vertical = spacing.small)
                        .semantics {
                            contentDescription = "Build status verified"
                        }
                ) {
                    Text(
                        text = "2027 Look",
                        color = colors.accentB,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = stringResource(id = R.string.wisp_tagline),
                color = colors.textMuted,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            // Squircle Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shapes.card)
                    .background(colors.surfaceHigh)
                    .border(
                        width = spacing.extraSmall,
                        color = colors.outline,
                        shape = shapes.card
                    )
                    .padding(spacing.large)
                    .semantics {
                        contentDescription = "Wisp pipeline verification card"
                    }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(shapes.pill)
                            .background(brandGradient)
                            .padding(horizontal = spacing.medium, vertical = spacing.small)
                    ) {
                        Text(
                            text = stringResource(id = R.string.hello_world_status),
                            color = colors.background,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.small))

                    Text(
                        text = "Kotlin + Compose + Material 3 Expressive",
                        color = colors.text,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = stringResource(id = R.string.ci_status_desc),
                        color = colors.textMuted,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Floating Orb Dock Placeholder Preview
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacing.medium)
                .size(spacing.orbSize)
                .clip(shapes.pill)
                .background(brandGradient)
                .semantics {
                    contentDescription = "Orb Dock Navigation"
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.large)
                    .clip(shapes.pill)
                    .background(colors.surface)
            )
        }
    }
}
