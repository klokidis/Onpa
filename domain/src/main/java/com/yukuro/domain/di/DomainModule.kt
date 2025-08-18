package com.yukuro.domain.di

import com.yukuro.domain.models.usecases.ServiceStateUseCases
import com.yukuro.domain.models.usecases.UserDataUseCases
import com.yukuro.domain.models.usecases.UserPreferencesUseCases
import com.yukuro.domain.models.usecases.UserUseCases
import com.yukuro.domain.repositories.service.ServiceStateRepository
import com.yukuro.domain.repositories.userdata.UserDataRepository
import com.yukuro.domain.repositories.userpref.UserPreferencesRepository
import com.yukuro.domain.repositories.users.UserRepository
import com.yukuro.domain.usecases.service.ObserveServiceRunningStateUseCase
import com.yukuro.domain.usecases.service.SetServiceRunningUseCase
import com.yukuro.domain.usecases.userdata.DeleteAllUserDataForUserUseCase
import com.yukuro.domain.usecases.userdata.DeleteOneUserDataUseCase
import com.yukuro.domain.usecases.userdata.GetAllUserDataByIdUseCase
import com.yukuro.domain.usecases.userdata.GetAllUserDataUseCase
import com.yukuro.domain.usecases.userdata.InsertUserDataUseCase
import com.yukuro.domain.usecases.userpref.ObserveUserPreferencesUseCase
import com.yukuro.domain.usecases.userpref.SaveAutoMicPreferenceUseCase
import com.yukuro.domain.usecases.userpref.SaveVibrationPreferenceUseCase
import com.yukuro.domain.usecases.users.DeleteAllUsersUseCase
import com.yukuro.domain.usecases.users.DeleteUserUseCase
import com.yukuro.domain.usecases.users.GetAllUsersUseCase
import com.yukuro.domain.usecases.users.GetUserByIdUseCase
import com.yukuro.domain.usecases.users.InsertUserUseCase
import com.yukuro.domain.usecases.users.UpdateUserNameUseCase
import com.yukuro.domain.usecases.users.UpdateVoiceLanguageUseCase
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
