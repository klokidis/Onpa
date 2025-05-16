package com.example.domain.usecases.userdata

import com.example.domain.repositories.userdata.UserDataRepository
import javax.inject.Inject

class DeleteOneUserDataUseCase @Inject constructor(
    private val repository: UserDataRepository
) {
    suspend operator fun invoke(id: Int) = repository.deleteOneUserData(id)
}