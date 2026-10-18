package app.wisp.core.designsystem.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.WispTheme
@Composable
fun WispScaffold(
    currentSpace: NavSpace,
    onSpaceSelected: (NavSpace) -> Unit,
    navStyle: NavStyleType,
    onNavStyleSelected: (NavStyleType) -> Unit,
    onQuickAdd: () -> Unit,
    onThemePresetSelected: (ThemePreset) -> Unit,
    modifier: Modifier = Modifier,
    initialXRatio: Float = 0.90f,
    initialYRatio: Float = 0.85f,
    onOrbPositionChanged: ((Float, Float) -> Unit)? = null,
    content: @Composable (NavSpace) -> Unit
) {
    val colors = WispTheme.colors
    var isCommandBarOpen by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = currentSpace.ordinal,
        pageCount = { NavSpace.spaces.size }
    )

    // Synchronize currentSpace changes to pager
    LaunchedEffect(currentSpace) {
        if (pagerState.currentPage != currentSpace.ordinal) {
            pagerState.animateScrollToPage(
                page = currentSpace.ordinal,
                animationSpec = spring(dampingRatio = 0.75f)
            )
        }
    }

    // Synchronize pager swipes to currentSpace
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
            val targetSpace = NavSpace.spaces.getOrNull(pageIndex) ?: NavSpace.BOARDS
            if (targetSpace != currentSpace) {
                onSpaceSelected(targetSpace)
            }
        }
    }

    // Predictive Back Handling
    BackHandler(enabled = isCommandBarOpen || currentSpace != NavSpace.BOARDS) {
        if (isCommandBarOpen) {
            isCommandBarOpen = false
        } else if (currentSpace != NavSpace.BOARDS) {
            onSpaceSelected(NavSpace.BOARDS)
        }
    }

    // Pull-down detection at top of screen for Command Bar
    val pullDownThreshold = 40f
    var pullDelta by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Horizontal Pager for swipe between spaces
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val space = NavSpace.spaces[page]
            content(space)
        }

        // Pull down gesture detector zone across top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.TopCenter)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        pullDelta += delta
                        if (pullDelta > pullDownThreshold && !isCommandBarOpen) {
                            isCommandBarOpen = true
                            pullDelta = 0f
                        }
                    },
                    onDragStopped = {
                        pullDelta = 0f
                    }
                )
        )

        // Active Navigation Overlay
        when (navStyle) {
            NavStyleType.ORB_DOCK -> {
                OrbDock(
                    currentSpace = currentSpace,
                    onSpaceSelected = { space ->
                        onSpaceSelected(space)
                    },
                    onQuickAdd = onQuickAdd,
                    initialXRatio = initialXRatio,
                    initialYRatio = initialYRatio,
                    onPositionRatioChanged = onOrbPositionChanged
                )
            }
            NavStyleType.EDGE_HANDLE -> {
                EdgeHandleNav(
                    currentSpace = currentSpace,
                    onSpaceSelected = onSpaceSelected
                )
            }
            NavStyleType.FLOATING_PILL -> {
                FloatingPillNav(
                    currentSpace = currentSpace,
                    onSpaceSelected = onSpaceSelected
                )
            }
            NavStyleType.GESTURE_ONLY -> {
                GestureOnlyNav()
            }
        }

        // Command Bar Overlay
        WispCommandBar(
            isOpen = isCommandBarOpen,
            onDismiss = { isCommandBarOpen = false },
            onQuickAdd = {
                isCommandBarOpen = false
                onQuickAdd()
            },
            onSpaceSelected = { space ->
                isCommandBarOpen = false
                onSpaceSelected(space)
            },
            onNavStyleSelected = { style ->
                onNavStyleSelected(style)
            },
            onThemePresetSelected = { preset ->
                onThemePresetSelected(preset)
            },
            currentNavStyle = navStyle
        )
    }
}
