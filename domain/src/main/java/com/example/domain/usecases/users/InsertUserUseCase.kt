package com.example.domain.usecases.users

import com.example.domain.models.users.User
import com.example.domain.repositories.users.UserRepository
import javax.inject.Inject

class InsertUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: User): Long = repository.insertUser(user)
}