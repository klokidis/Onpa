package com.yukuro.domain.usecases.userpref

import com.yukuro.domain.models.userpref.UserPreferences
import com.yukuro.domain.repositories.userpref.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserPreferencesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> = repository.userPreferencesFlow
}