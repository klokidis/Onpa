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

    // Using stateIn to retain the state across configuration changes
    val userUiState: StateFlow<UserUiState> = _userUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = UserUiState(isLoading = true)
        )

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers()
                .filterNotNull()
                .collect { users ->
                    val selectedUser = users.firstOrNull() // Pick the first user if available
                    _userUiState.value = UserUiState(
                        users = users,
                        selectedUser = selectedUser,
                        isLoading = false
                    )
                }
        }
    }

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            userRepository.getUserById(userId).collect { fetchedUser ->
                _userUiState.value = _userUiState.value.copy(
                    selectedUser = fetchedUser
                )
            }
        }
    }

    fun addUser(name: String) {
        viewModelScope.launch {
            val user = User(userName = name)
            userRepository.insertUser(user)
        }
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
