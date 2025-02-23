package com.example.ptyxiakh.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ptyxiakh.data.dao.UserDao
import com.example.ptyxiakh.data.dao.UserDataDao
import com.example.ptyxiakh.data.model.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun userDataDao(): UserDataDao

}