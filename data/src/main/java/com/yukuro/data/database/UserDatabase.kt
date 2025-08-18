package com.yukuro.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yukuro.data.dao.UserDao
import com.yukuro.data.dao.UserDataDao
import com.yukuro.domain.models.userdata.UserData
import com.yukuro.domain.models.users.User

@Database(entities = [User::class, UserData::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userDataDao(): UserDataDao

}