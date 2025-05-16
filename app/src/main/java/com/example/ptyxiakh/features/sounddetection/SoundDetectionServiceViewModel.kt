package com.example.ptyxiakh.features.sounddetection

import androidx.lifecycle.ViewModel
import com.example.ptyxiakh.data.repository.ServiceStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundDetectionServiceViewModel @Inject constructor(
    serviceStateManager: ServiceStateRepository
) : ViewModel() {
    val isServiceRunning = serviceStateManager.isServiceRunning
}