package com.rjnr.thaiwrter.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.repository.ThaiLanguageRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(repository: ThaiLanguageRepository) : ViewModel() {
    val dueReviews: StateFlow<List<com.rjnr.thaiwrter.data.models.UserProgress>> =
            repository
                    .getDueReviews()
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
