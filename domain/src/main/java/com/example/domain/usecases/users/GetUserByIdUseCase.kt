package com.example.domain.usecases.users

import com.example.domain.models.users.User
import com.example.domain.repositories.users.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(userId: Int): Flow<User?> = repository.getUserById(userId)
}