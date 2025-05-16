package com.example.domain.repositories.service

import kotlinx.coroutines.flow.StateFlow

interface ServiceStateRepository {
    val isServiceRunning: StateFlow<Boolean>
    fun setServiceRunning(isRunning: Boolean)
}