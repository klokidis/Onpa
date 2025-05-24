package com.example.onpa.features.userdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.usecases.UserDataUseCases
import com.example.domain.models.userdata.UserData
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
    private val userDataUseCases: UserDataUseCases
) : ViewModel() {

    private val _userDataUiState = MutableStateFlow(UserDataUiState(isLoading = true))

    // Using stateIn to retain the state across configuration changes
    val userDataUiState: StateFlow<UserDataUiState> = _userDataUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = UserDataUiState(isLoading = true)
        )

    fun getOneUserData(userId: Int) {
        viewModelScope.launch {
            userDataUseCases.getAllUserDataById(userId).collect { data ->
                _userDataUiState.update { currentState ->
                    currentState.copy(
                        userData = data,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getAllUserData() {
        viewModelScope.launch {
            userDataUseCases.getAllUserData().collect { data ->
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
            userDataUseCases.insertUserData(
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
            userDataUseCases.deleteOneUserData(userDataId)
        }
    }

    fun deleteAllUserData(userId: Int) {
        viewModelScope.launch {
            userDataUseCases.deleteAllUserDataForUser(userId)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
