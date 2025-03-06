package com.example.ptyxiakh.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ptyxiakh.model.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDataItem(userData: UserData)

    @Query("SELECT * FROM user_data WHERE userId = :userId")
    fun getUserDataForUser(userId: Int): Flow<List<UserData>>

    @Query("DELETE FROM user_data WHERE id = :id")
    suspend fun deleteOneUserDataForUser(id: Int)

    @Query("DELETE FROM user_data WHERE userId = :userId")
    suspend fun deleteAllUserDataForUser(userId: Int)
}
