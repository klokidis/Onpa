package com.example.onpa.features.userdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.usecases.UserPreferencesUseCases
import com.example.domain.models.userpref.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStorePrefViewModel @Inject constructor(
    private val useCases: UserPreferencesUseCases
) : ViewModel() {

    val uiState: StateFlow<UserPreferences> =
        useCases.observePreferences()
            .map { preferences ->
                UserPreferences(
                    vibration = preferences.vibration,
                    autoMic = preferences.autoMic,
                    isLoading = false
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UserPreferences()
            )

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch {
            useCases.saveVibration(enabled)
        }
    }

    fun setAutoMic(enabled: Boolean) {
        viewModelScope.launch {
            useCases.saveAutoMic(enabled)
        }
    }
}
