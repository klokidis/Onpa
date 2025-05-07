package com.example.ptyxiakh.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptyxiakh.model.UserData
import com.example.ptyxiakh.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserDataUiState(
    val userData: List<UserData> = emptyList(),
    val userId: Int? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val repository: UserDataRepository
) : ViewModel() {

    private val _userDataUiState = MutableStateFlow(UserDataUiState(isLoading = true))

    // Using stateIn to retain the state across configuration changes
    val userDataUiState: StateFlow<UserDataUiState> = _userDataUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = UserDataUiState(isLoading = true)
        )

    fun loadUserData(userId: Int) {
        viewModelScope.launch {
            repository.getAllUserDataById(userId).collect { data ->
                _userDataUiState.update { currentState ->
                    currentState.copy(
                        userData = data,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun loadAllUserData() {
        viewModelScope.launch {
            repository.getAllUserData().collect { data ->
                _userDataUiState.update { currentState ->
                    currentState.copy(
                        userData = data,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addOneUserData(userId: Int, category: String, value: String) {
        viewModelScope.launch {
            repository.insertUserData(
                UserData(
                    userId = userId,
                    category = category,
                    value = value
                )
            )
        }
    }

    fun deleteOneData(userDataId: Int) {
        viewModelScope.launch {
            repository.deleteOneUserData(userDataId)
        }
    }

    fun deleteAllUserData(userId: Int) {
        viewModelScope.launch {
            repository.deleteAllUserDataForUser(userId)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
