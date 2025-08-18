package com.yukuro.domain.usecases.users

import com.yukuro.domain.models.users.User
import com.yukuro.domain.repositories.users.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = repository.getAllUsers()
}