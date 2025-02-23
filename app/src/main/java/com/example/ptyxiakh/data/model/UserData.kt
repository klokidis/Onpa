package com.example.ptyxiakh.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_data",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = CASCADE  // Ensures deletion of user deletes favorites
    )],
    indices = [Index(value = ["userId"])]
)
data class UserData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,  // Foreign key linking to User table
    val category: String,
    val value: String
)
