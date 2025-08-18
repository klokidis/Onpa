package com.yukuro.domain.models.usecases

import com.yukuro.domain.usecases.userdata.DeleteAllUserDataForUserUseCase
import com.yukuro.domain.usecases.userdata.DeleteOneUserDataUseCase
import com.yukuro.domain.usecases.userdata.GetAllUserDataByIdUseCase
import com.yukuro.domain.usecases.userdata.GetAllUserDataUseCase
import com.yukuro.domain.usecases.userdata.InsertUserDataUseCase

data class UserDataUseCases(
    val getAllUserDataById: GetAllUserDataByIdUseCase,
    val getAllUserData: GetAllUserDataUseCase,
    val insertUserData: InsertUserDataUseCase,
    val deleteOneUserData: DeleteOneUserDataUseCase,
    val deleteAllUserDataForUser: DeleteAllUserDataForUserUseCase
)
