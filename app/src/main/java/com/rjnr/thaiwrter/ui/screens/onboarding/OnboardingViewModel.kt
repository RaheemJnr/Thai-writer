package com.rjnr.thaiwrter.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.preferences.OnboardingPreferences
import com.rjnr.thaiwrter.data.preferences.OnboardingSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
        val settings: OnboardingSettings = OnboardingSettings(),
        val currentStep: Int = 0,
        val totalSteps: Int = 4,
        val isLoading: Boolean = true
)

class OnboardingViewModel(private val preferences: OnboardingPreferences) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.settings.collect { settings ->
                _uiState.value = _uiState.value.copy(settings = settings, isLoading = false)
            }
        }
    }

    fun setStep(step: Int) {
        _uiState.value =
                _uiState.value.copy(currentStep = step.coerceIn(0, _uiState.value.totalSteps - 1))
    }

    fun updateGoal(goal: String) {
        viewModelScope.launch { preferences.updateGoal(goal) }
    }

    fun updatePace(minutes: Int) {
        viewModelScope.launch { preferences.updatePace(minutes) }
    }

    fun updateConfidence(level: Int) {
        viewModelScope.launch { preferences.updateConfidence(level) }
    }

    fun completeOnboarding() {
        viewModelScope.launch { preferences.markComplete() }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            preferences.reset()
            _uiState.value = OnboardingUiState(isLoading = false)
        }
    }
}
