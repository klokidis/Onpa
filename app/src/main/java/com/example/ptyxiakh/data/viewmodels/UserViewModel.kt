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

    val userUiState: StateFlow<UserUiState> =
        userRepository.getAllUsers()
            .map { user ->
                UserUiState(users = user, isLoading = false)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = UserUiState(isLoading = true)
            )


    fun addUser(name: String): Int {
        var userId = 0
        viewModelScope.launch {
            val user = User(userName = name)
            userId = userRepository.insertUser(user).toInt()
        }
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
