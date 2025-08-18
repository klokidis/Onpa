package com.yukuro.domain.repositories.userpref

import com.yukuro.domain.models.userpref.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun saveVibration(vibration: Boolean)

    suspend fun saveAutoMic(autoMic: Boolean)
}