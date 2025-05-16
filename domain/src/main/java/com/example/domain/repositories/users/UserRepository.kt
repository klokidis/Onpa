package com.example.domain.repositories.users

import com.example.domain.models.users.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserById(userId: Int): Flow<User?>

    fun getAllUsers(): Flow<List<User>>

    suspend fun insertUser(user: User): Long

    suspend fun updateUserName(userId: Int, newName: Int)

    suspend fun updateVoiceLanguage(userId: Int, newLanguage: Int)

    suspend fun deleteUser(userId: Int)

    suspend fun deleteAllUser()
}