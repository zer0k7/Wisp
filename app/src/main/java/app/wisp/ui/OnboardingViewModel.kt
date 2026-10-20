package app.wisp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.wisp.core.datastore.OnboardingPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: OnboardingPreferencesRepository
) : ViewModel() {
    val isCompleted: StateFlow<Boolean> = repository.isCompleted.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun complete() {
        viewModelScope.launch { repository.complete() }
    }
}
