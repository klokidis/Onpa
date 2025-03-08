package com.example.ptyxiakh.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ptyxiakh.BuildConfig
import com.example.ptyxiakh.model.ResponseState
import com.example.ptyxiakh.model.UserData
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
        prompt: String,
        userData: List<UserData>
    ) {
        _responseState.value = ResponseState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userContext = userData.joinToString(", ") { "${it.category}: ${it.value}" }

                val response = generativeModel.generateContent(
                    content {
                        text(
                            "Prompt: \"$prompt\"\n\n" +
                                    "User context: $userContext\n" +
                                    "Generate three distinct responses with different moods as if you were the user. Number them (1, 2, 3). Avoid emojis and unnecessary phrasing."
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
                    Log.d("response edited", resultUiState.value.answersList.toString() )
                    Log.d("user", userContext)
                }
            } catch (e: Exception) {
                _responseState.value = ResponseState.Error(e.localizedMessage ?: "")
            }
        }
    }

    private fun editResults(answer: String): List<String> {
        val emojiRegex = Regex("[\\p{So}\\p{Cn}]") // Matches most emojis
        val cleanedAnswer = answer
            .replace("**", "") // Remove bold markers
            .replace("*", "") // Remove bold markers
            .replace("...", "") // Remove ellipses
            .replace(emojiRegex, "") // Remove emojis
            .replace(Regex(" - .*?(?=\n|$)"), "") // Remove text after " - " until the end of the line
            .replace(Regex("\\(.*?\\)"), "") // Remove (..)

        val regex = Regex("(?:^|\\n)\\s*\\d+\\.\\s*(.*?)(?=\\s*(?:\\n\\s*\\d+\\.|$))", RegexOption.DOT_MATCHES_ALL)
        return regex.findAll(cleanedAnswer)
            .map { it.groupValues[1].trim() } // Extract the matched text and trim whitespace
            .toList()
    }
}