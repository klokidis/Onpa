package com.example.data.repositories.userpref

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.domain.models.userpref.UserPreferences
import com.example.domain.repositories.userpref.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val VIBRATION = booleanPreferencesKey("vibration")
        val AUTO_MIC = booleanPreferencesKey("auto_mic")
        const val TAG = "UserPreferencesRepo"
    }

    override val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(PreferencesKeys.TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else throw exception
        }
        .map { preferences ->
            UserPreferences(
                vibration = preferences[PreferencesKeys.VIBRATION] ?: true,
                autoMic = preferences[PreferencesKeys.AUTO_MIC] ?: true,
                isLoading = true
            )
        }

    override suspend fun saveVibration(vibration: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION] = vibration
        }
    }

    override suspend fun saveAutoMic(autoMic: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_MIC] = autoMic
        }
    }
}