package com.yukuro.domain.usecases.service

import com.yukuro.domain.repositories.service.ServiceStateRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveServiceRunningStateUseCase @Inject constructor(
    private val repository: ServiceStateRepository
) {
    operator fun invoke(): StateFlow<Boolean> = repository.isServiceRunning
}