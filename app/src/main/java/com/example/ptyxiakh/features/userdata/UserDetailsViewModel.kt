package com.example.ptyxiakh.features.userdata

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserDetailsUiState(
    val newUserDetails: List<Pair<String, String>> = emptyList()
)

class UserDetailsViewModel() : ViewModel() {

    private val _userDetailsUiState = MutableStateFlow(UserDetailsUiState())
    val userDetailsUiState: StateFlow<UserDetailsUiState> = _userDetailsUiState.asStateFlow()


    fun addLine() {
        _userDetailsUiState.update { currentState ->
            currentState.copy(
                newUserDetails = currentState.newUserDetails + Pair("", "")
            )
        }
    }

    fun emptyNewUserDetailsList() {
        _userDetailsUiState.update { currentState ->
            currentState.copy(
                newUserDetails = listOf()
            )
        }
    }

    fun minusNewUserDetailsLine(index: Int) {
        _userDetailsUiState.value = _userDetailsUiState.value.copy(
            newUserDetails = _userDetailsUiState.value.newUserDetails.toMutableList()
                .also { it.removeAt(index) }
        )
    }


    fun setValuesBasedOnLength(position: Int, newValueOne: String, newValueTwo: String) {
        _userDetailsUiState.update { currentState ->
            currentState.copy(
                newUserDetails = currentState.newUserDetails.mapIndexed { index, userData ->
                    if (index == position) Pair(newValueOne, newValueTwo) else userData
                }
            )
        }
    }
}