package com.example.domain.usecases.userdata

import com.example.domain.models.userdata.UserData
import com.example.domain.repositories.userdata.UserDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUserDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(): Flow<List<UserData>> = repository.getAllUserData()
}