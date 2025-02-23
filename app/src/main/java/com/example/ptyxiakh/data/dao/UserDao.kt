package com.example.ptyxiakh.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ptyxiakh.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserByID(userId: Int): Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long  // Returns the inserted user ID

    @Query("UPDATE users SET userName = :newName WHERE userId = :userId")
    suspend fun updateUserName(userId: Int, newName: String)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUser(userId: Int)
}