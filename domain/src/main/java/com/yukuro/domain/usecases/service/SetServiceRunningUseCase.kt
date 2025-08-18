package com.yukuro.domain.usecases.service

import com.yukuro.domain.repositories.service.ServiceStateRepository
import javax.inject.Inject

class SetServiceRunningUseCase @Inject constructor(
    private val repository: ServiceStateRepository
) {
     operator fun invoke(isRunning: Boolean) = repository.setServiceRunning(isRunning)
}