package com.rjnr.thaiwrter.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.preferences.OnboardingPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class OnboardingStatus(val isComplete: Boolean = false, val isLoaded: Boolean = false)

class OnboardingStatusViewModel(preferences: OnboardingPreferences) : ViewModel() {

    val status: StateFlow<OnboardingStatus> =
            preferences
                    .settings
                    .map { settings ->
                        OnboardingStatus(isComplete = settings.isComplete, isLoaded = true)
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5_000),
                            initialValue = OnboardingStatus()
                    )
}
