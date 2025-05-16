package com.example.domain.models.usecases

import com.example.domain.usecases.userdata.DeleteAllUserDataForUserUseCase
import com.example.domain.usecases.userdata.DeleteOneUserDataUseCase
import com.example.domain.usecases.userdata.GetAllUserDataByIdUseCase
import com.example.domain.usecases.userdata.GetAllUserDataUseCase
import com.example.domain.usecases.userdata.InsertUserDataUseCase

data class UserDataUseCases(
    val getAllUserDataById: GetAllUserDataByIdUseCase,
    val getAllUserData: GetAllUserDataUseCase,
    val insertUserData: InsertUserDataUseCase,
    val deleteOneUserData: DeleteOneUserDataUseCase,
    val deleteAllUserDataForUser: DeleteAllUserDataForUserUseCase
)
