package com.example.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.data.dao.UserDao
import com.example.data.dao.UserDataDao
import com.example.data.database.UserDatabase
import com.example.data.repositories.service.ServiceStateRepositoryImpl
import com.example.data.repositories.userdata.UserDataRepositoryImpl
import com.example.data.repositories.userpref.UserPreferencesRepositoryImpl
import com.example.data.repositories.users.UserRepositoryImpl
import com.example.domain.repositories.userdata.UserDataRepository
import com.example.domain.repositories.service.ServiceStateRepository
import com.example.domain.repositories.userpref.UserPreferencesRepository
import com.example.domain.repositories.users.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindServiceStateRepository(
        impl: ServiceStateRepositoryImpl
    ): ServiceStateRepository

    @Binds
    @Singleton
    abstract fun bindUserDataRepository(
        impl: UserDataRepositoryImpl
    ): UserDataRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): UserDatabase {
            return Room.databaseBuilder(
                context,
                UserDatabase::class.java,
                "user_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        fun provideUserDao(database: UserDatabase): UserDao {
            return database.userDao()
        }

        @Provides
        fun provideUserDataDao(database: UserDatabase): UserDataDao {
            return database.userDataDao()
        }

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.dataStore
        }
    }
}