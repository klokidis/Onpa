package com.example.ptyxiakh.di

import android.content.Context
import androidx.room.Room
import com.example.ptyxiakh.data.dao.UserDao
import com.example.ptyxiakh.data.dao.UserDataDao
import com.example.ptyxiakh.data.database.UserDatabase
import com.example.ptyxiakh.data.repository.UserDataRepository
import com.example.ptyxiakh.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

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
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideUserDataRepository(userDataDao: UserDataDao): UserDataRepository {
        return UserDataRepository(userDataDao)
    }

}