package com.example.domain.usecases.userdata

import com.example.domain.repositories.userdata.UserDataRepository
import javax.inject.Inject

class DeleteAllUserDataForUserUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(userId: Int) = repository.deleteAllUserDataForUser(userId)
}