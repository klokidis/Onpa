package com.yukuro.domain.usecases.userpref

import com.yukuro.domain.repositories.userpref.UserPreferencesRepository
import javax.inject.Inject

class SaveAutoMicPreferenceUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(autoMic: Boolean) = repository.saveAutoMic(autoMic)
}