package com.yukuro.domain.models.usecases

import com.yukuro.domain.usecases.userpref.ObserveUserPreferencesUseCase
import com.yukuro.domain.usecases.userpref.SaveAutoMicPreferenceUseCase
import com.yukuro.domain.usecases.userpref.SaveVibrationPreferenceUseCase

data class UserPreferencesUseCases(
    val observePreferences: ObserveUserPreferencesUseCase,
    val saveVibration: SaveVibrationPreferenceUseCase,
    val saveAutoMic: SaveAutoMicPreferenceUseCase
)