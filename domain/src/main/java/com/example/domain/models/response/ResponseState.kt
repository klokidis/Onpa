package com.example.domain.models.response

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface ResponseState {

    /**
     * Empty state when the screen is first shown
     */
    data object Initial : ResponseState

    /**
     * Still loading
     */
    data object Loading : ResponseState

    /**
     * Text has been generated
     */
    data object Success : ResponseState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : ResponseState
}