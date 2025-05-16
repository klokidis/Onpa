package com.example.domain.usecases.userpref

import com.example.domain.models.userpref.UserPreferences
import com.example.domain.repositories.userpref.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserPreferencesUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> = repository.userPreferencesFlow
}