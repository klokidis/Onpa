package com.example.ptyxiakh.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptyxiakh.data.model.User
import com.example.ptyxiakh.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUiState(
    val users: List<User> = emptyList(),
    val selectedUser: User? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userUiState = MutableStateFlow(UserUiState(isLoading = true))
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getAllUsers()
                .collect { users ->
                    _userUiState.value = UserUiState(
                        users = users,
                        selectedUser = users.firstOrNull(),
                        isLoading = false
                    )
                }
        }
    }

    suspend fun addUser(name: String): Int {
        val user = User(userName = name)
        val userId = userRepository.insertUser(user).toInt()

        // Update UI state after adding a new user
        val updatedUsers = userRepository.getAllUsers().first()
        _userUiState.value = _userUiState.value.copy(
            users = updatedUsers,
            selectedUser = _userUiState.value.selectedUser ?: updatedUsers.firstOrNull()
        )

        return userId
    }


    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId).firstOrNull()
            user?.let { userRepository.deleteUser(it.userId) }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
