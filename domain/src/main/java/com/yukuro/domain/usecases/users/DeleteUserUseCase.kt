package com.yukuro.domain.usecases.users

import com.yukuro.domain.repositories.users.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Int) = repository.deleteUser(userId)
}