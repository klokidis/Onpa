package com.example.ptyxiakh.features.sounddetection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.usecases.ServiceStateUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundDetectionServiceViewModel @Inject constructor(
    private val serviceStateUseCases: ServiceStateUseCases
) : ViewModel() {

    val isServiceRunning: StateFlow<Boolean> = serviceStateUseCases.observeServiceState()

    fun setServiceRunning(isRunning: Boolean) {
        viewModelScope.launch {
            serviceStateUseCases.setServiceState(isRunning)
        }
    }
}
