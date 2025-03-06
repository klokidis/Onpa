package com.example.ptyxiakh.data.repository

import com.example.ptyxiakh.data.dao.UserDao
import com.example.ptyxiakh.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getUserById(userId: Int): Flow<User?> = userDao.getUserByID(userId)

    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun insertUser(user: User): Long { //maybe return?
        return userDao.insertUser(user)
    }

    suspend fun updateUserName(userId: Int, newName: String) {
        userDao.updateUserName(userId, newName)
    }

    suspend fun deleteUser(userId: Int) {
        userDao.deleteUser(userId)
    }

    suspend fun deleteAllUser() {
        userDao.deleteAllUser()
    }
}
