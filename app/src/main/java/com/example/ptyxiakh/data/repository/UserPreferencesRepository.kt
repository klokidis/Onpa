package com.example.ptyxiakh.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.ptyxiakh.viewmodels.UserPreferencesUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val VIBRATION = booleanPreferencesKey("vibration")
        val AUTO_MIC = booleanPreferencesKey("auto_mic")
        const val TAG = "UserPreferencesRepo"
    }

    val userPreferencesFlow: Flow<UserPreferencesUiState> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            UserPreferencesUiState(
                vibration = preferences[VIBRATION] ?: false,
                autoMic = preferences[AUTO_MIC] ?: false,
                isLoading = true
            )
        }

    suspend fun saveVibration(vibration: Boolean) {
        dataStore.edit { preferences ->
            preferences[VIBRATION] = vibration
        }
    }

    suspend fun saveAutoMic(autoMic: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_MIC] = autoMic
        }
    }
}