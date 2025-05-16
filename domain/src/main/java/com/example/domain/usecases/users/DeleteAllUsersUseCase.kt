package com.example.domain.usecases.users

import com.example.domain.repositories.users.UserRepository
import javax.inject.Inject

class DeleteAllUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.deleteAllUser()
}