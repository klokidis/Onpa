package com.example.data.repositories.userdata

import com.example.data.dao.UserDataDao
import com.example.domain.models.userdata.UserData
import com.example.domain.repositories.userdata.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepositoryImpl @Inject constructor(
    private val userDataDao: UserDataDao
) : UserDataRepository {

    override suspend fun getAllUserDataById(userId: Int): Flow<List<UserData>> =
        userDataDao.getUserDataForUser(userId)

    override suspend fun getAllUserData(): Flow<List<UserData>> =
        userDataDao.getUserAllData()

    override suspend fun insertUserData(userData: UserData) {
        userDataDao.insertUserDataItem(userData)
    }

    override suspend fun deleteOneUserData(id: Int) {
        userDataDao.deleteOneUserDataForUser(id)
    }

    override suspend fun deleteAllUserDataForUser(userId: Int) {
        userDataDao.deleteAllUserDataForUser(userId)
    }
}