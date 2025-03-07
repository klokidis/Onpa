package com.example.ptyxiakh.viewmodels

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

    fun emptyList(){
        _userDetailsUiState.update { currentState ->
            currentState.copy(
                newUserDetails = listOf()
            )
        }
    }

    fun minusLine() {
        _userDetailsUiState.update { currentState ->
            currentState.copy(
                newUserDetails = currentState.newUserDetails.dropLast(1)
            )
        }
    }

    fun editValuesBasedOnLength(position: Int, newValueOne: String, newValueTwo: String) {
        _userDetailsUiState.update { currentState ->
            currentState.copy(
                newUserDetails = currentState.newUserDetails.mapIndexed { index, userData ->
                    if (index == position) Pair(newValueOne, newValueTwo) else userData
                }
            )
        }
    }

}