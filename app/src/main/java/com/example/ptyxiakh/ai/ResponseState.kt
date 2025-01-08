package com.example.ptyxiakh.ai

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface ResponseState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : ResponseState

    /**
     * Still loading
     */
    object Loading : ResponseState

    /**
     * Text has been generated
     */
    object Success : ResponseState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : ResponseState
}