package com.yukuro.domain.usecases.userdata

import com.yukuro.domain.models.userdata.UserData
import com.yukuro.domain.repositories.userdata.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUserDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(): Flow<List<UserData>> = repository.getAllUserData()
}