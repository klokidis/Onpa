package com.yukuro.domain.usecases.userdata

import com.yukuro.domain.models.userdata.UserData
import com.yukuro.domain.repositories.userdata.UserDataRepository
import javax.inject.Inject

class InsertUserDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userData: UserData) = repository.insertUserData(userData)
}