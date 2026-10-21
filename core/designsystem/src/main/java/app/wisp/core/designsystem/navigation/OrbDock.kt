package app.wisp.core.designsystem.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.theme.WispTheme
import app.wisp.core.designsystem.components.springPress
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun OrbDock(
    currentSpace: NavSpace,
    onSpaceSelected: (NavSpace) -> Unit,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier,
    initialXRatio: Float = 0.90f,
    initialYRatio: Float = 0.85f,
    onPositionRatioChanged: ((xRatio: Float, yRatio: Float) -> Unit)? = null
) {
    val colors = WispTheme.colors
    val spacing = WispTheme.spacing
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var isFanOpen by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val orbSizePx = with(density) { spacing.orbSize.toPx() }
        val marginPx = with(density) { spacing.medium.toPx() }
        val topMarginPx = with(density) { spacing.extraLarge.toPx() }

        val animOffsetX = remember {
            Animatable(initialXRatio * (screenWidthPx - orbSizePx))
        }
        val animOffsetY = remember {
            Animatable(initialYRatio * (screenHeightPx - orbSizePx))
        }

        var isDragging by remember { mutableStateOf(false) }
        var currentDockEdge by remember {
            mutableStateOf(OrbPhysics.calculateSnappingEdge(animOffsetX.value, screenWidthPx))
        }

        // Keep current edge updated
        LaunchedEffect(animOffsetX.value) {
            currentDockEdge = OrbPhysics.calculateSnappingEdge(animOffsetX.value, screenWidthPx)
        }

        val orbIconRotation by animateFloatAsState(
            targetValue = if (isFanOpen) 135f else 0f,
            animationSpec = spring(dampingRatio = 0.75f),
            label = "OrbIconRotation"
        )

        // Custom accessibility actions for TalkBack
        val accessibilityActions = remember(onSpaceSelected, onQuickAdd) {
            val spaceActions = NavSpace.spaces.map { space ->
                CustomAccessibilityAction(label = "Switch to ${space.title}") {
                    onSpaceSelected(space)
                    isFanOpen = false
                    true
                }
            }
            val quickAddAction = CustomAccessibilityAction(label = "Add item") {
                onQuickAdd()
                true
            }
            spaceActions + quickAddAction
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        animOffsetX.value.roundToInt(),
                        animOffsetY.value.roundToInt()
                    )
                }
                .size(spacing.orbSize),
            contentAlignment = Alignment.Center
        ) {
            // Arc Fan behind/around the orb
            OrbFan(
                isOpen = isFanOpen && !isDragging,
                currentSpace = currentSpace,
                dockEdge = currentDockEdge,
                onSpaceSelected = { space ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSpaceSelected(space)
                    isFanOpen = false
                }
            )

            if (isFanOpen && !isDragging) {
                Box(
                    modifier = Modifier
                        .offset(y = -spacing.extraLarge)
                        .clip(WispTheme.shapes.pill)
                        .background(colors.surfaceHigh)
                        .border(spacing.stroke, colors.outline, WispTheme.shapes.pill)
                        .springPress(onClick = {
                            isFanOpen = false
                            onQuickAdd()
                        })
                        .semantics {
                            role = Role.Button
                            contentDescription = "Add item"
                        }
                        .padding(horizontal = spacing.small, vertical = spacing.extraSmall),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Add item", style = WispTheme.typography.labelSmall, color = colors.text)
                }
            }

            // Outer Soft Glow Aura Canvas
            Canvas(
                modifier = Modifier
                    .size(spacing.orbSize * 1.6f)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.accentA.copy(alpha = 0.40f),
                            colors.accentB.copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    )
                )
            }

            // The Glowing 56dp Orb
            Box(
                modifier = Modifier
                    .size(spacing.orbSize)
                    .clip(CircleShape)
                    .background(WispTheme.brandGradient)
                    .border(
                        width = spacing.strokeThick,
                        color = colors.surfaceHigh.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
                    .semantics {
                        role = Role.Button
                        contentDescription = "Navigation button. Current section: ${currentSpace.title}. " +
                            "Tap to open sections, long press to add an item."
                        this.customActions = accessibilityActions
                    }
                    .pointerInput(screenWidthPx, screenHeightPx, orbSizePx) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                isFanOpen = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    val newX = (animOffsetX.value + dragAmount.x)
                                        .coerceIn(marginPx, screenWidthPx - orbSizePx - marginPx)
                                    val newY = OrbPhysics.constrainY(
                                        currentY = animOffsetY.value + dragAmount.y,
                                        minY = topMarginPx,
                                        maxY = screenHeightPx - marginPx,
                                        orbSize = orbSizePx
                                    )
                                    animOffsetX.snapTo(newX)
                                    animOffsetY.snapTo(newY)
                                }
                            },
                            onDragEnd = {
                                isDragging = false
                                val edge = OrbPhysics.calculateSnappingEdge(animOffsetX.value, screenWidthPx)
                                currentDockEdge = edge
                                val targetX = OrbPhysics.calculateTargetX(
                                    edge = edge,
                                    screenWidth = screenWidthPx,
                                    orbSize = orbSizePx,
                                    margin = marginPx
                                )
                                scope.launch {
                                    animOffsetX.animateTo(
                                        targetValue = targetX,
                                        animationSpec = spring(
                                            dampingRatio = 0.75f,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    )
                                    val xRatio = (targetX / (screenWidthPx - orbSizePx)).coerceIn(0f, 1f)
                                    val yRatio = (animOffsetY.value / (screenHeightPx - orbSizePx)).coerceIn(0f, 1f)
                                    onPositionRatioChanged?.invoke(xRatio, yRatio)
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isFanOpen = !isFanOpen
                            },
                            onLongPress = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isFanOpen = false
                                onQuickAdd()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFanOpen) Icons.Rounded.Close else Icons.Rounded.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(spacing.large)
                        .rotate(orbIconRotation)
                )
            }
        }
    }
}
