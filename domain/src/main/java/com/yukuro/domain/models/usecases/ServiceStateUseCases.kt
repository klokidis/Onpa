package com.yukuro.domain.models.usecases

import com.yukuro.domain.usecases.service.ObserveServiceRunningStateUseCase
import com.yukuro.domain.usecases.service.SetServiceRunningUseCase

data class ServiceStateUseCases(
    val observeServiceState: ObserveServiceRunningStateUseCase,
    val setServiceState: SetServiceRunningUseCase
)