package com.example.domain.repositories.userdata

import com.example.domain.models.userdata.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    suspend fun getAllUserDataById(userId: Int): Flow<List<UserData>>

    suspend fun getAllUserData(): Flow<List<UserData>>

    suspend fun insertUserData(userData: UserData)

    suspend fun deleteOneUserData(id: Int)

    suspend fun deleteAllUserDataForUser(userId: Int)
}