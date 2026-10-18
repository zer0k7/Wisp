package app.wisp.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.delay

/**
 * Spring-based press animation modifier following the 2027 design principles:
 * Everything moves with physics (springs, dampingRatio 0.75), nothing snaps linearly.
 * Includes tactile haptic feedback and a visual press guarantee for rapid taps.
 */
fun Modifier.springPress(
    targetScale: Float = 0.92f,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    var isVisuallyPressed by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (isPressed && enabled) {
            isVisuallyPressed = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        } else {
            delay(100)
            isVisuallyPressed = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisuallyPressed && enabled) targetScale else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "spring_press_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
}
