package com.example.domain.models.usecases

import com.example.domain.usecases.userpref.ObserveUserPreferencesUseCase
import com.example.domain.usecases.userpref.SaveAutoMicPreferenceUseCase
import com.example.domain.usecases.userpref.SaveVibrationPreferenceUseCase

data class UserPreferencesUseCases(
    val observePreferences: ObserveUserPreferencesUseCase,
    val saveVibration: SaveVibrationPreferenceUseCase,
    val saveAutoMic: SaveAutoMicPreferenceUseCase
)