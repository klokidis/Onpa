package com.example.domain.usecases.userdata

import com.example.domain.models.userdata.UserData
import com.example.domain.repositories.userdata.UserDataRepository
import javax.inject.Inject

class InsertUserDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userData: UserData) = repository.insertUserData(userData)
}