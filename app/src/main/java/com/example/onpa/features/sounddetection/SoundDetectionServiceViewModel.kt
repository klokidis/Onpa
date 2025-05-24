package com.example.onpa.features.sounddetection

import androidx.lifecycle.ViewModel
import com.example.domain.models.usecases.ServiceStateUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SoundDetectionServiceViewModel @Inject constructor(
    serviceStateUseCases: ServiceStateUseCases
) : ViewModel() {
    val isServiceRunning: StateFlow<Boolean> = serviceStateUseCases.observeServiceState()
}
