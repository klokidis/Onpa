package com.example.domain.usecases.userpref

import com.example.domain.repositories.userpref.UserPreferencesRepository
import javax.inject.Inject

class SaveVibrationPreferenceUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(vibration: Boolean) = repository.saveVibration(vibration)
}