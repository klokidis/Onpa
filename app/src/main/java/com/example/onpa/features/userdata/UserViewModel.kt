package com.example.onpa.features.userdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.usecases.UserUseCases
import com.example.domain.models.users.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUiState(
    val users: List<User> = emptyList(),
    val selectedUser: User? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _userUiState = MutableStateFlow(UserUiState(isLoading = true))
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    init {
        viewModelScope.launch {
            userUseCases.getAllUsers()
                .collect { users ->
                    _userUiState.value = UserUiState(
                        users = users,
                        selectedUser = users.firstOrNull(),
                        isLoading = false
                    )
                }
        }
    }

    suspend fun addUser(name: String,language: Int): Int {
        val user = User(userName = name, voiceLanguage = language)
        val userId = userUseCases.insertUser(user).toInt()

        // Update UI state after adding a new user
        val updatedUsers = userUseCases.getAllUsers().first()
        _userUiState.value = _userUiState.value.copy(
            users = updatedUsers,
            selectedUser = _userUiState.value.selectedUser ?: updatedUsers.firstOrNull()
        )

        return userId
    }


    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            val user = userUseCases.getUserById(userId).firstOrNull()
            user?.let { userUseCases.deleteUser(it.userId) }
        }
    }

    fun changeUserLanguage(userId:Int, language: Int){
        viewModelScope.launch {
            val user = userUseCases.getUserById(userId).firstOrNull()
            user?.let { userUseCases.updateVoiceLanguage(userId,language) }
        }
    }

    fun deleteAllUser() {
        viewModelScope.launch {
            userUseCases.deleteAllUsers()
        }
    }
}
