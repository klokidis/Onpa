package com.example.domain.models.userpref

data class UserPreferences(
    val vibration: Boolean = true,
    val autoMic: Boolean = true,
    val isLoading: Boolean = true,
)