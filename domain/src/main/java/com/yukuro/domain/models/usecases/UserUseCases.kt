package com.yukuro.domain.models.usecases

import com.yukuro.domain.usecases.users.DeleteAllUsersUseCase
import com.yukuro.domain.usecases.users.DeleteUserUseCase
import com.yukuro.domain.usecases.users.GetAllUsersUseCase
import com.yukuro.domain.usecases.users.GetUserByIdUseCase
import com.yukuro.domain.usecases.users.InsertUserUseCase
import com.yukuro.domain.usecases.users.UpdateUserNameUseCase
import com.yukuro.domain.usecases.users.UpdateVoiceLanguageUseCase

data class UserUseCases(
    val getUserById: GetUserByIdUseCase,
    val getAllUsers: GetAllUsersUseCase,
    val insertUser: InsertUserUseCase,
    val updateUserName: UpdateUserNameUseCase,
    val updateVoiceLanguage: UpdateVoiceLanguageUseCase,
    val deleteUser: DeleteUserUseCase,
    val deleteAllUsers: DeleteAllUsersUseCase
)
