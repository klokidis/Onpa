package com.yukuro.domain.usecases.users

import com.yukuro.domain.repositories.users.UserRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Int, newName: Int) = repository.updateUserName(userId, newName)
}