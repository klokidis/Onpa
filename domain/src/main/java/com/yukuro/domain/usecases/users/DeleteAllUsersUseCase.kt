package com.yukuro.domain.usecases.users

import com.yukuro.domain.repositories.users.UserRepository
import javax.inject.Inject

class DeleteAllUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.deleteAllUser()
}