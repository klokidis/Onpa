package com.example.domain.models.userdata

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.models.users.User

@Entity(
    tableName = "user_data",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.Companion.CASCADE  // Ensures deletion of user deletes data
    )],
    indices = [Index(value = ["userId"])]
)
data class UserData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,  // Foreign key linking to User table
    val category: String,
    val value: String
)