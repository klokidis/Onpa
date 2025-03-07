package com.example.ptyxiakh.data.repository

import com.example.ptyxiakh.data.dao.UserDataDao
import com.example.ptyxiakh.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val userDataDao: UserDataDao
) {
    fun getAllUserDataById(userId: Int): Flow<List<UserData>> =
        userDataDao.getUserDataForUser(userId = userId)

    fun getAllUserData(): Flow<List<UserData>> =
        userDataDao.getUserAllData()

    suspend fun insertUserData(userData: UserData) {
        userDataDao.insertUserDataItem(userData)
    }

    suspend fun deleteOneUserData(id: Int) {
        userDataDao.deleteOneUserDataForUser(id)
    }

    suspend fun deleteAllUserDataForUser(userId: Int) {
        userDataDao.deleteAllUserDataForUser(userId)
    }
}
