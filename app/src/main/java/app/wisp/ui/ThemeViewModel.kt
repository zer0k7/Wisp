package app.wisp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.wisp.core.datastore.ThemePreferencesRepository
import app.wisp.core.designsystem.theme.ThemePreset
import app.wisp.core.designsystem.theme.ThemeSpec
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreferencesRepository: ThemePreferencesRepository
) : ViewModel() {

    val themeSpec: StateFlow<ThemeSpec> = themePreferencesRepository.themeSpec
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeSpec.Default
        )

    fun selectPreset(preset: ThemePreset) {
        viewModelScope.launch {
            themePreferencesRepository.setThemePreset(preset)
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            themePreferencesRepository.resetToDefault()
        }
    }
}
