package com.example.domain.usecases.users

import com.example.domain.repositories.users.UserRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Int, newName: Int) = repository.updateUserName(userId, newName)
}