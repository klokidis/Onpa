package com.example.ptyxiakh.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptyxiakh.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserPreferencesUiState(
    val vibration: Boolean = true,
    val autoMic: Boolean = true,
    val isLoading: Boolean = true,
)


@HiltViewModel
class DataStorePrefViewModel @Inject constructor(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<UserPreferencesUiState> =
        repository.userPreferencesFlow
            .map { preferences ->
            UserPreferencesUiState(
                vibration = preferences.vibration,
                autoMic = preferences.autoMic,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferencesUiState()
        )

    fun toggleVibration(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveVibration(enabled)
        }
    }

    fun toggleAutoMic(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveAutoMic(enabled)
        }
    }
}