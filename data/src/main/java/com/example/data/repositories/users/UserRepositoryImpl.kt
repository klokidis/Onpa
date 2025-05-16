package com.example.data.repositories.users

import com.example.data.dao.UserDao
import com.example.domain.models.users.User
import com.example.domain.repositories.users.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getUserById(userId: Int): Flow<User?> =
        userDao.getUserByID(userId)

    override fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers()

    override suspend fun insertUser(user: User): Long =
        userDao.insertUser(user)

    override suspend fun updateUserName(userId: Int, newName: Int) {
        userDao.updateUserName(userId, newName)
    }

    override suspend fun updateVoiceLanguage(userId: Int, newLanguage: Int) {
        userDao.updateUserLanguage(userId, newLanguage)
    }

    override suspend fun deleteUser(userId: Int) {
        userDao.deleteUser(userId)
    }

    override suspend fun deleteAllUser() {
        userDao.deleteAllUser()
    }
}