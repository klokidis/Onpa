package com.example.domain.usecases.service

import com.example.domain.repositories.service.ServiceStateRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveServiceRunningStateUseCase @Inject constructor(
    private val repository: ServiceStateRepository
) {
    operator fun invoke(): StateFlow<Boolean> = repository.isServiceRunning
}