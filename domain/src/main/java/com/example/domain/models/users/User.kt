package com.example.domain.models.users

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val userName: String = "",
    val voiceLanguage: Int = 0
)