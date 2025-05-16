package com.example.domain.di

import com.example.domain.models.usecases.ServiceStateUseCases
import com.example.domain.models.usecases.UserDataUseCases
import com.example.domain.models.usecases.UserPreferencesUseCases
import com.example.domain.models.usecases.UserUseCases
import com.example.domain.repositories.service.ServiceStateRepository
import com.example.domain.repositories.userdata.UserDataRepository
import com.example.domain.repositories.userpref.UserPreferencesRepository
import com.example.domain.repositories.users.UserRepository
import com.example.domain.usecases.service.ObserveServiceRunningStateUseCase
import com.example.domain.usecases.service.SetServiceRunningUseCase
import com.example.domain.usecases.userdata.DeleteAllUserDataForUserUseCase
import com.example.domain.usecases.userdata.DeleteOneUserDataUseCase
import com.example.domain.usecases.userdata.GetAllUserDataByIdUseCase
import com.example.domain.usecases.userdata.GetAllUserDataUseCase
import com.example.domain.usecases.userdata.InsertUserDataUseCase
import com.example.domain.usecases.userpref.ObserveUserPreferencesUseCase
import com.example.domain.usecases.userpref.SaveAutoMicPreferenceUseCase
import com.example.domain.usecases.userpref.SaveVibrationPreferenceUseCase
import com.example.domain.usecases.users.DeleteAllUsersUseCase
import com.example.domain.usecases.users.DeleteUserUseCase
import com.example.domain.usecases.users.GetAllUsersUseCase
import com.example.domain.usecases.users.GetUserByIdUseCase
import com.example.domain.usecases.users.InsertUserUseCase
import com.example.domain.usecases.users.UpdateUserNameUseCase
import com.example.domain.usecases.users.UpdateVoiceLanguageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideUserUseCases(repository: UserRepository): UserUseCases {
        return UserUseCases(
            getUserById = GetUserByIdUseCase(repository),
            getAllUsers = GetAllUsersUseCase(repository),
            insertUser = InsertUserUseCase(repository),
            updateUserName = UpdateUserNameUseCase(repository),
            updateVoiceLanguage = UpdateVoiceLanguageUseCase(repository),
            deleteUser = DeleteUserUseCase(repository),
            deleteAllUsers = DeleteAllUsersUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideUserDataUseCases(repository: UserDataRepository): UserDataUseCases {
        return UserDataUseCases(
            getAllUserDataById = GetAllUserDataByIdUseCase(repository),
            getAllUserData = GetAllUserDataUseCase(repository),
            insertUserData = InsertUserDataUseCase(repository),
            deleteOneUserData = DeleteOneUserDataUseCase(repository),
            deleteAllUserDataForUser = DeleteAllUserDataForUserUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesUseCases(repository: UserPreferencesRepository): UserPreferencesUseCases {
        return UserPreferencesUseCases(
            observePreferences = ObserveUserPreferencesUseCase(repository),
            saveVibration = SaveVibrationPreferenceUseCase(repository),
            saveAutoMic = SaveAutoMicPreferenceUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideServiceStateUseCases(repository: ServiceStateRepository): ServiceStateUseCases {
        return ServiceStateUseCases(
            observeServiceState = ObserveServiceRunningStateUseCase(repository),
            setServiceState = SetServiceRunningUseCase(repository)
        )
    }
}
