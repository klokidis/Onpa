package com.yukuro.domain.usecases.users

import com.yukuro.domain.models.users.User
import com.yukuro.domain.repositories.users.UserRepository
import javax.inject.Inject

class InsertUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User): Long = repository.insertUser(user)
}