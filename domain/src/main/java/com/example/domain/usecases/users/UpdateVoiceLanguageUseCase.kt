package com.example.domain.usecases.users

import com.example.domain.repositories.users.UserRepository
import javax.inject.Inject

class UpdateVoiceLanguageUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Int, newLanguage: Int) = repository.updateVoiceLanguage(userId, newLanguage)
}