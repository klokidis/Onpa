package com.example.ptyxiakh.ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptyxiakh.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResultUiState(
    val answersList: List<String> = listOf(),
)


class GeminiViewModel : ViewModel() {

    private val _responseState: MutableStateFlow<ResponseState> =
        MutableStateFlow(ResponseState.Initial)
    val responseState: StateFlow<ResponseState> =
        _responseState.asStateFlow()

    private val _resultUiState = MutableStateFlow(ResultUiState())
    val resultUiState: StateFlow<ResultUiState> = _resultUiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(
        prompt: String
    ) {
        _responseState.value = ResponseState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(
                            "Generate three distinct responses to: $prompt, Each with a different mood. Separate with 1, 2, and 3. Avoid emojis or excess phrasing."
                        )
                    }
                )
                response.text?.let { outputContent ->
                    _responseState.value = ResponseState.Success
                    _resultUiState.update { currentState ->
                        currentState.copy(
                            answersList = currentState.answersList + editResults(outputContent)
                        )
                    }
                    Log.d("response", outputContent)
                }
            } catch (e: Exception) {
                _responseState.value = ResponseState.Error(e.localizedMessage ?: "")
            }
        }
    }

    private fun editResults(answer: String): List<String> {
        val emojiRegex = Regex("[\\p{So}\\p{Cn}]") // Matches most emojis
        return answer
            .replace("**", "") // Remove bold markers
            .replace("*", "") // Remove bold markers
            .replace("...", "") // Remove ellipses
            .replace(emojiRegex, "") // Remove emojis
            .replace(Regex("\\(.*?\\)"), "") // Remove (..)
            .split(Regex("\\d\\.?\\s*")) // Split by numbers, optional periods, and spaces
            .filter { it.isNotBlank() } // Remove empty entries
    }
}