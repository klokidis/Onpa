package com.example.data.repositories.service

import com.example.domain.repositories.service.ServiceStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceStateRepositoryImpl @Inject constructor() : ServiceStateRepository {

    private val _isServiceRunning = MutableStateFlow(false)
    override val isServiceRunning: StateFlow<Boolean> = _isServiceRunning

    override fun setServiceRunning(isRunning: Boolean) {
        _isServiceRunning.value = isRunning
    }
}