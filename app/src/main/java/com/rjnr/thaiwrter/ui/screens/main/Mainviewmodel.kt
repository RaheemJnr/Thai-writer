package com.rjnr.thaiwrter.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rjnr.thaiwrter.data.repository.ThaiLanguageRepository
import com.rjnr.thaiwrter.utils.ConnectionStatus
import com.rjnr.thaiwrter.utils.ConnectivityMonitor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
        private val repository: ThaiLanguageRepository,
        connectivityMonitor: ConnectivityMonitor
) : ViewModel() {
    //    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    //    val uiState = _uiState.asStateFlow()

    val dueReviews =
            repository
                    .getDueReviews()
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val networkStatus: StateFlow<ConnectionStatus> =
            connectivityMonitor.status.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    ConnectionStatus.Available
            )
}
