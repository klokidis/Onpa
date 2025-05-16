package com.example.domain.models.usecases

import com.example.domain.usecases.service.ObserveServiceRunningStateUseCase
import com.example.domain.usecases.service.SetServiceRunningUseCase

data class ServiceStateUseCases(
    val observeServiceState: ObserveServiceRunningStateUseCase,
    val setServiceState: SetServiceRunningUseCase
)