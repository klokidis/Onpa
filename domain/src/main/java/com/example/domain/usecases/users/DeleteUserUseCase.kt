package com.example.domain.usecases.users

import com.example.domain.repositories.users.UserRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Int) = repository.deleteUser(userId)
}