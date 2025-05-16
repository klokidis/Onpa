package com.example.domain.models.usecases

import com.example.domain.usecases.users.DeleteAllUsersUseCase
import com.example.domain.usecases.users.DeleteUserUseCase
import com.example.domain.usecases.users.GetAllUsersUseCase
import com.example.domain.usecases.users.GetUserByIdUseCase
import com.example.domain.usecases.users.InsertUserUseCase
import com.example.domain.usecases.users.UpdateUserNameUseCase
import com.example.domain.usecases.users.UpdateVoiceLanguageUseCase

data class UserUseCases(
    val getUserById: GetUserByIdUseCase,
    val getAllUsers: GetAllUsersUseCase,
    val insertUser: InsertUserUseCase,
    val updateUserName: UpdateUserNameUseCase,
    val updateVoiceLanguage: UpdateVoiceLanguageUseCase,
    val deleteUser: DeleteUserUseCase,
    val deleteAllUsers: DeleteAllUsersUseCase
)
