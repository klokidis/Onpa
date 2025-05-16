package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.dao.UserDao
import com.example.data.dao.UserDataDao
import com.example.domain.models.userdata.UserData
import com.example.domain.models.users.User

@Database(entities = [User::class, UserData::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userDataDao(): UserDataDao

}