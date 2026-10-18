package app.wisp.core.designsystem.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.theme.WispTheme

@Composable
fun GestureOnlyNav(
    modifier: Modifier = Modifier
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing

    // Subtle edge swipe indicator bar at bottom
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Gesture navigation mode active. Swipe horizontally to switch spaces, pull down for command bar."
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = spacing.small)
                .width(48.dp)
                .height(3.dp)
                .alpha(0.35f)
                .clip(RoundedCornerShape(999.dp))
                .background(colors.outline)
        )
    }
}
