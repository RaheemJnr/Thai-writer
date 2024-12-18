package com.rjnr.thaiwrter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.repository.ThaiLanguageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val repository: ThaiLanguageRepository
) : ViewModel() {
//    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
//    val uiState = _uiState.asStateFlow()

    val dueReviews = repository.getDueReviews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}