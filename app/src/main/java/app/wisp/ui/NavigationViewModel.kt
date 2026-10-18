package app.wisp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.wisp.core.datastore.NavigationPreferencesRepository
import app.wisp.core.designsystem.navigation.NavSpace
import app.wisp.core.designsystem.navigation.NavStyleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigationPreferencesRepository: NavigationPreferencesRepository
) : ViewModel() {

    private val _currentSpace = MutableStateFlow(NavSpace.BOARDS)
    val currentSpace: StateFlow<NavSpace> = _currentSpace.asStateFlow()

    private val _quickAddTriggered = MutableStateFlow(false)
    val quickAddTriggered: StateFlow<Boolean> = _quickAddTriggered.asStateFlow()

    val navStyle: StateFlow<NavStyleType> = navigationPreferencesRepository.navStyle
        .map { NavStyleType.fromId(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NavStyleType.ORB_DOCK
        )

    val orbPositionRatio: StateFlow<Pair<Float, Float>> = navigationPreferencesRepository.orbPositionRatio
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.90f to 0.85f
        )

    fun selectSpace(space: NavSpace) {
        _currentSpace.value = space
    }

    fun setNavStyle(style: NavStyleType) {
        viewModelScope.launch {
            navigationPreferencesRepository.setNavStyle(style.id)
        }
    }

    fun setOrbPositionRatio(xRatio: Float, yRatio: Float) {
        viewModelScope.launch {
            navigationPreferencesRepository.setOrbPositionRatio(xRatio, yRatio)
        }
    }

    fun triggerQuickAdd() {
        _quickAddTriggered.value = true
    }

    fun consumeQuickAdd() {
        _quickAddTriggered.value = false
    }
}
